package net.mikoto.aozora.crawler.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.SneakyThrows;
import net.mikoto.aozora.crawler.AozoraCrawlerApplicationContextGetter;
import net.mikoto.aozora.crawler.manager.TaskManager;
import net.mikoto.aozora.crawler.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskManager taskManager;
    private final AozoraCrawlerApplicationContextGetter contextGetter;

    @Autowired
    public TaskServiceImpl(TaskManager taskManager, AozoraCrawlerApplicationContextGetter contextGetter) {
        this.taskManager = taskManager;
        this.contextGetter = contextGetter;
    }

    @Override
    @SneakyThrows
    public String scheduleTask(String pattern, String taskName, String[] beanNames, Object[] params) {
        Class<? extends Task> taskClass = taskManager.getTask(taskName);
        if (taskClass.getConstructors().length != 1) {
            return null;
        }
        Constructor<?> taskConstructor = taskClass.getConstructors()[0];
        Task task = (Task) taskConstructor.newInstance(params);
        for (String beanName : beanNames) {
            Object bean = contextGetter.getApplicationContext().getBean(beanName);
            Method setBean = taskClass.getMethod("set" + beanName, bean.getClass());
            setBean.invoke(task, bean);
        }
        return scheduleTask(pattern, task);
    }

    @Override
    public String scheduleTask(String pattern, Task task) {
        String id = IdUtil.nanoId();
        CronUtil.schedule(id, pattern, task);
        return id;
    }

    @Override
    public String scheduleTask(String pattern, @NotNull Runnable task) {
        String id = IdUtil.nanoId();
        CronUtil.schedule(id, pattern, task::run);
        return id;
    }

    @Override
    public void run() {
        CronUtil.start(true);
    }

    @Override
    public void stop() {
        CronUtil.stop();
    }

}
