package net.mikoto.aozora.crawler.manager.impl;

import cn.hutool.cron.task.Task;
import net.mikoto.aozora.crawler.manager.TaskManager;
import net.mikoto.aozora.crawler.model.tasks.PatchArtworkTask;
import net.mikoto.aozora.crawler.model.tasks.UpdateArtworkCountTask;
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
    private static final Map<String, Class<? extends Task>> taskClassMap = new HashMap<>();

    @Override
    public Class<? extends Task> getTask(String name) {
        return taskClassMap.get(name);
    }

    @Override
    public void registerTask(String name, Class<? extends Task> clazz) {
        taskClassMap.put(name, clazz);
    }

    @Override
    public void afterPropertiesSet() {
        registerTask(PatchArtworkTask.class.getSimpleName(), PatchArtworkTask.class);
        registerTask(UpdateArtworkCountTask.class.getSimpleName(), UpdateArtworkCountTask.class);
    }
}
