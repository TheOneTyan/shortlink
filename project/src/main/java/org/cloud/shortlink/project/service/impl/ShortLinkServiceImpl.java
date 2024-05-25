package org.cloud.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.convention.exception.ServiceException;
import org.cloud.shortlink.project.dao.entity.*;
import org.cloud.shortlink.project.dao.mapper.*;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.project.service.ShortLinkService;
import org.cloud.shortlink.project.toolkit.HashUtil;
import org.cloud.shortlink.project.toolkit.LinkUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.cloud.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;
import static org.cloud.shortlink.project.common.constant.RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY;
import static org.cloud.shortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final ShortLinkAccessStatsMapper shortLinkAccessStatsMapper;
    private final ShortLinkLocaleStatsMapper shortLinkLocaleStatsMapper;
    private final ShortLinkOsStatsMapper shortLinkOsStatsMapper;
    private final ShortLinkBrowserStatsMapper shortLinkBrowserStatsMapper;
    private final ShortLinkAccessLogsMapper shortLinkAccessLogsMapper;
    private final ShortLinkDeviceStatsMapper shortLinkDeviceStatsMapper;
    private final ShortLinkNetworkStatsMapper shortLinkNetworkStatsMapper;
    private final ShortLinkStatsTodayMapper shortLinkStatsTodayMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Value("${short-link.stats.locale.a-map-key}")
    private String statsLocaleAMapKey;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUri = generateShortUri(requestParam);
        // TODO 从原始链接中获取 协议字段
        String fullShortUrl = "https://" +
                requestParam.getDomain() +
                "/" +
                shortUri;

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortUri)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .fullShortUrl(fullShortUrl)
                .build();

        ShortLinkGotoDo shortLinkGotoDo = ShortLinkGotoDo.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDo);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException("短链接已存在，请勿重复生成");
        }

        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS
        );
        shortLinkCreateCachePenetrationBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkDO> resultPage = baseMapper.pageShortLink(requestParam);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> listGroupShortLinkCount(List<String> gidList) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid", "count(*) as shortLinkCount")
                .in("gid", gidList)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> resultMaps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(resultMaps, ShortLinkGroupCountRespDTO.class);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 和示例不同，我不允许修改gid，所以不需要先删后插
        // TODO 在【功能扩展@短链接变更分组记录功能】章节后补全更新短链接功能
    }

    /**
     * 根据短链接跳转至原链接
     * @param shortUri 6位短链接
     */
    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String domain = request.getServerName();
        // TODO 获取原始链接中的协议字段
        String fullShortUrl = "https://" +
                domain +
                "/" +
                shortUri;
        String originUrl = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originUrl)) {
            //TODO: Q
            shortLinkStats(fullShortUrl, null, request, response);
            try {
                response.sendRedirect(originUrl);
            } catch (IOException e) {
                throw new ServiceException("跳转失败");
            }
            return;
        }

        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originUrl = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originUrl)) {
                shortLinkStats(fullShortUrl, null, request, response);
                try {
                    response.sendRedirect(originUrl);
                } catch (IOException e) {
                    throw new ServiceException("跳转失败");
                }
                return;
            }

            LambdaQueryWrapper<ShortLinkGotoDo> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDo.class)
                    .eq(ShortLinkGotoDo::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDo shortLinkGotoDo = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDo == null) {
                throw new ServiceException("此完整短链接在路由表中不存在，需要重新插入");
            }

            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDo.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(linkQueryWrapper);
            if (shortLinkDO == null) {
                response.sendRedirect("/page/notfound");
                return;
            } else if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date())) {
                throw new ServiceException("短链接已过期");
            }

            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    shortLinkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()),
                    TimeUnit.MILLISECONDS
            );
            shortLinkStats(fullShortUrl, shortLinkDO.getGid(), request, response);
            try {
                response.sendRedirect(shortLinkDO.getOriginUrl());
            } catch (IOException e) {
                throw new ServiceException("跳转失败");
            }
        } finally {
            lock.unlock();
        }
    }

    // uv, pv, uip, locale
    private void shortLinkStats(String fullShortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean(false);
        Cookie[] cookies = request.getCookies();
        try {
            AtomicReference<String> uv = new AtomicReference<>();
            Runnable addResponseCookieTask = () -> {
                uv.set(UUID.randomUUID().toString());
                Cookie uvCookie = new Cookie("uv", uv.get());
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                // 取短链接
                uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.lastIndexOf("/"), fullShortUrl.length()));
                response.addCookie(uvCookie);
                uvFirstFlag.set(Boolean.TRUE);
                stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv.get());
            };

            // 若用户携带cookie，则判断是否有uv，若没有则添加uv
            // 若有则判断set中是否存在此uv
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(uvValue -> {
                            uv.set(uvValue);
                            Long uvAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uvValue);
                            uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                        }, addResponseCookieTask);
            } else {
                addResponseCookieTask.run();
            }

            // 统计 uip
            String remoteAddr = LinkUtil.getActualIp(((HttpServletRequest) request));
            Long uipAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, remoteAddr);
            boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;

            // 补齐 gid
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDo.class)
                        .eq(ShortLinkGotoDo::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDo shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }

            Date date = new Date();
            int hour = DateUtil.hour(date, true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getIso8601Value();
            ShortLinkAccessStatsDO shortLinkAccessStatsDO = ShortLinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(uvFirstFlag.get() ? 1 : 0)
                    .uip(uipFirstFlag ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(date)
                    .build();
            shortLinkAccessStatsMapper.shortLinkAccessStats(shortLinkAccessStatsDO);

            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocaleAMapKey);
            localeParamMap.put("ip", remoteAddr);
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "";
            String actualCity = "";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                ShortLinkLocaleStatsDO shortLinkLocaleStatsDO = ShortLinkLocaleStatsDO.builder()
                        .province(actualProvince = unknownFlag ? "未知" : province)
                        .city(actualCity = unknownFlag ? "未知" : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .gid(gid)
                        .date(new Date())
                        .build();
                shortLinkLocaleStatsMapper.shortLinkLocaleStats(shortLinkLocaleStatsDO);
            }

            // 统计OS
            String os = LinkUtil.getOs(request);
            ShortLinkOsStatsDO shortLinkOsStatsDO = ShortLinkOsStatsDO.builder()
                    .os(os)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .build();
            shortLinkOsStatsMapper.shortLinkOsStats(shortLinkOsStatsDO);

            // 统计浏览器
            String browser = LinkUtil.getBrowser(request);
            ShortLinkBrowserStatsDO shortLinkBrowserStatsDO = ShortLinkBrowserStatsDO.builder()
                    .browser(browser)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .build();
            shortLinkBrowserStatsMapper.shortLinkBrowserStats(shortLinkBrowserStatsDO);

            // 统计访问设备
            String device = LinkUtil.getDevice(request);
            ShortLinkDeviceStatsDO shortLinkDeviceStatsDO = ShortLinkDeviceStatsDO.builder()
                    .device(device)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .build();
            shortLinkDeviceStatsMapper.shortLinkDeviceStats(shortLinkDeviceStatsDO);

            // 访问网络
            String network = LinkUtil.getNetwork(request);
            ShortLinkNetworkStatsDO linkNetworkStatsDO = ShortLinkNetworkStatsDO.builder()
                    .network(network)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .build();
            shortLinkNetworkStatsMapper.shortLinkNetworkStats(linkNetworkStatsDO);

            // 统计日志
            ShortLinkAccessLogsDO shortLinkAccessLogsDO = ShortLinkAccessLogsDO.builder()
                    .user(uv.get())
                    .ip(remoteAddr)
                    .browser(browser)
                    .os(os)
                    .network(network)
                    .device(device)
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .build();
            shortLinkAccessLogsMapper.insert(shortLinkAccessLogsDO);

            // 汇总统计增加
            baseMapper.incrementStats(gid, fullShortUrl, 1, uvFirstFlag.get() ? 1 : 0, uipFirstFlag ? 1 : 0);

            // 今日统计增加
            ShortLinkStatsTodayDO shortLinkStatsTodayDO = ShortLinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(uvFirstFlag.get() ? 1 : 0)
                    .todayUip(uipFirstFlag ? 1 : 0)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .build();
            shortLinkStatsTodayMapper.shortLinkTodayState(shortLinkStatsTodayDO);
        } catch (Exception ex) {
            throw new ServiceException("统计失败");
        }
    }

    private String generateShortUri(ShortLinkCreateReqDTO requestParam) {
        String shortUri = "";
        int maxTryCount = 10;
        while (true) {
            if (maxTryCount <= 0) {
                throw new ServiceException("短链接生成过于频繁，稍后重试");
            }
            // 防止高并发时生成相同短链接并插入
            String originUrlAndUuid = requestParam.getOriginUrl() + UUID.randomUUID();
            shortUri = HashUtil.hashToBase62(originUrlAndUuid);
            String fullShortUrl = requestParam.getDomain() + "/" + shortUri;
            // 布隆过滤器：不存在则一定不存在，此时生成的shortUri满足要求
            if (!shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                break;
            }
            maxTryCount--;
        }
        return shortUri;
    }

    private String parseProtocolFromUrl(String url) {
        if (url.startsWith("http://")) {
            return "http://";
        } else if (url.startsWith("https://")) {
            return "https://";
        } else {
            return "";
        }
    }
}
