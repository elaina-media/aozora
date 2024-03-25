package net.mikoto.aozora.crawler.manager.impl;

import net.mikoto.aozora.crawler.manager.TaskManager;
import net.mikoto.aozora.crawler.tasks.PatchArtworkTask;
import net.mikoto.aozora.crawler.tasks.UpdateArtworkCountTask;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
@Component
public class TaskManagerImpl implements TaskManager, InitializingBean {
    private static final Map<String, Class<? extends Runnable>> taskClassMap = new HashMap<>();

    @Override
    public Class<? extends Runnable> getTask(String name) {
        return taskClassMap.get(name);
    }

    @Override
    public void registerTask(String name, Class<? extends Runnable> clazz) {
        taskClassMap.put(name, clazz);
    }

    @Override
    public void afterPropertiesSet() {
        registerTask(PatchArtworkTask.class.getSimpleName(), PatchArtworkTask.class);
        registerTask(UpdateArtworkCountTask.class.getSimpleName(), UpdateArtworkCountTask.class);
    }
}
