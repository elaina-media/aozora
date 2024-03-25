package net.mikoto.aozora.crawler.manager;


/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
public interface TaskManager {
    Class<? extends Runnable> getTask(String name);
    void registerTask(String name, Class<? extends Runnable> clazz);
}
