package net.mikoto.aozora.crawler.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mikoto
 * &#064;date 2024/1/23
 * Create for aozora
 */
public interface Observer {
    Map<String, Object> dataMap = new HashMap<>();

    default void update(Map<String, Object> currentDataMap) {
        Set<String> existKeys = dataMap.keySet();
        for (String key : currentDataMap.keySet()) {
            if (existKeys.contains(key)) {
                dataMap.remove(key);
            }
            dataMap.put(key, currentDataMap.get(key));
        }
    }
}
