package net.mikoto.aozora.crawler.service.impl;

import lombok.SneakyThrows;
import net.mikoto.aozora.crawler.AozoraCrawlerApplicationContextGetter;
import net.mikoto.aozora.crawler.manager.TaskManager;
import net.mikoto.aozora.crawler.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mikoto
 * &#064;date 2024/1/21
 * Create for aozora
 */
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskManager taskManager;
    private final AozoraCrawlerApplicationContextGetter contextGetter;
    private int lastId = 1;
    private final Map<Integer, Thread> threadMap = new ConcurrentHashMap<>();

    @Autowired
    public TaskServiceImpl(TaskManager taskManager, AozoraCrawlerApplicationContextGetter contextGetter) {
        this.taskManager = taskManager;
        this.contextGetter = contextGetter;
    }

    @Override
    @SneakyThrows
    public int scheduleTask(String taskName, String[] beanNames, Object[] params) {
        Class<? extends Runnable> taskClass = taskManager.getTask(taskName);
        if (taskClass.getConstructors().length != 1) {
            return 0;
        }
        Constructor<?> taskConstructor = taskClass.getConstructors()[0];
        Runnable task = (Runnable) taskConstructor.newInstance(params);
        for (String beanName : beanNames) {
            Class<?> beanClass = Class.forName(beanName);
            Object bean = contextGetter.getApplicationContext().getBean(beanClass);
            Method setBean = taskClass.getMethod("set" + beanClass.getSimpleName(), beanClass);
            setBean.invoke(task, bean);
        }
        return scheduleTask(task);
    }

    @Override
    public int scheduleTask(@NotNull Runnable task) {
        int id = lastId;
        lastId++;
        threadMap.put(id, new Thread(task));
        return id;
    }

    @Override
    public void run(int id) {
        threadMap.get(id).start();
    }

    @Override
    public void stop(int id) {
        threadMap.get(id).interrupt();
    }

}
