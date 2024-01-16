package net.mikoto.aozora.crawler.model;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author mikoto
 * &#064;date 2024/1/11
 * Create for aozora
 */
public class TaskMessageQueue {
    private final Queue<String> queue = new LinkedTransferQueue<>()

    public void in(String msg) {

    }

    public String out() {

    }
}
