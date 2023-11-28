package net.mikoto.aozora.forward.service;

import com.alibaba.fastjson2.JSONObject;

/**
 * @author mikoto
 * &#064;date 2023/11/28
 * Create for aozora
 */
public interface ArtworkService {
    Object parseRawData(AvailableRoute availableRoute, JSONObject rawData);
}
