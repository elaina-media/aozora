package net.mikoto.aozora.canalclient.client;

import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.model.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author mikoto
 * &#064;date 2024/1/14
 * Create for aozora
 */
@Component
@Log
public class CanalClient implements InitializingBean {
    private final CanalClientThread canalClientThread;

    @Autowired
    public CanalClient(CanalClientThread canalClientThread) {
        this.canalClientThread = canalClientThread;
    }

    @Override
    public void afterPropertiesSet() {
        Thread thread = new Thread(canalClientThread);
        thread.start();
    }
}
