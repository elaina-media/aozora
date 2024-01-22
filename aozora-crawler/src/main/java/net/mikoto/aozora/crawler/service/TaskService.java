package net.mikoto.aozora.crawler.service;

import cn.hutool.cron.task.Task;

/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
public interface TaskService {
    String scheduleTask(String pattern, String taskName, String[] beanNames, Object[] params);
    String scheduleTask(String pattern, Task task);
    String scheduleTask(String pattern, Runnable task);
    void run();
    void stop();
}
