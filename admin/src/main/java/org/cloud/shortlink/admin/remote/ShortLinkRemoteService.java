package org.cloud.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

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
        String responseJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/link/page", paramMap);
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }

    default Result<List<ShortLinkGroupCountRespDTO>> listGroupCount(List<String> gidList) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", gidList);
        String responseJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/link/count", requestMap);
        return JSON.parseObject(responseJsonStr, new TypeReference<>() {});
    }
}
