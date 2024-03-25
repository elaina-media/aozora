package net.mikoto.aozora.crawler.service;


/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
public interface TaskService {
    int scheduleTask(String taskName, String[] beanNames, Object[] params);
    int scheduleTask(Runnable task);
    void run(int taskId);
    void stop(int taskId);
}
