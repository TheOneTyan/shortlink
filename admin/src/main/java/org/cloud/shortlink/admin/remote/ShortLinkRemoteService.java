package org.cloud.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.dto.req.*;
import org.cloud.shortlink.admin.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkStatsRespDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkStatsReqDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ShortLinkRemoteService {
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String responseJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/link", JSON.toJSONString(requestParam));
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gid", requestParam.getGid());
        paramMap.put("current", requestParam.getCurrent());
        paramMap.put("size", requestParam.getSize());
        paramMap.put("orderTag", requestParam.getOrderTag());
        String responseJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/link/page", paramMap);
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }

    default Result<List<ShortLinkGroupCountRespDTO>> listGroupCount(List<String> gidList) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", gidList);
        String responseJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/link/count", requestMap);
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }

    default Result<Void> moveIntoRecycleBin(RecycleBinMoveIntoReqDTO requestParam) {
        String responseJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/move-into", JSON.toJSONString(requestParam));
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }

    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
    }

    default void clearRecycleBin(RecycleBinClearReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/clear", JSON.toJSONString(requestParam));
    }

    @PostMapping("/api/short-link/v1/recycle-bin/page")
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam) {
        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParam.getGidList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultJsonStr, new TypeReference<>() {});
    }

    /**
     * 访问单个短链接指定时间内监控数据
     *
     * @param requestParam 访问短链接监控请求参数
     * @return 短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 访问单个短链接指定时间内监控访问记录数据
     *
     * @param requestParam 访问短链接监控访问记录请求参数
     * @return 短链接监控访问记录信息
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
