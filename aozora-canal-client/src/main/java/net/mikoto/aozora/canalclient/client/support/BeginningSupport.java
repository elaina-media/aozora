package net.mikoto.aozora.canalclient.client.support;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.java.Log;

/**
 * @author mikoto
 * &#064;date 2024/3/24
 * Create for aozora
 */
@Log
public class BeginningSupport extends Support {
    public BeginningSupport(Support next) {
        super(next);
    }

    @Override
    protected boolean resolve(JSONObject before, JSONObject after) {
        log.info("Do support\nbefore: " + before + "\nafter: " + after);
        return false;
    }
}
