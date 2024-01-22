package net.mikoto.aozora.crawler.manager;

import cn.hutool.cron.task.Task;

/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
public interface TaskManager {
    Class<? extends Task> getTask(String name);
    void registerTask(String name, Class<? extends Task> clazz);
}
