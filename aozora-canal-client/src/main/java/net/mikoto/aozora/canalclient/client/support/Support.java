package net.mikoto.aozora.canalclient.client.support;

import com.alibaba.fastjson2.JSONObject;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * @author mikoto
 * &#064;date 2024/3/24
 * Create for aozora
 */
@Setter
@Log
public abstract class Support {
    private Support next;

    public Support(Support next) {
        this.next = next;
    }

    public final void support(JSONObject before, JSONObject after) {
        if (!resolve(before, after) && next != null) {
            next.support(before, after);
        }
    }

    protected abstract boolean resolve(JSONObject before, JSONObject after);
}
