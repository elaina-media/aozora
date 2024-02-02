package net.mikoto.aozora.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.java.Log;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mikoto
 * &#064;date 2023/12/24
 * Create for aozora
 */
@Service("SinglePoolTaskService")
@Log
public class SinglePoolTaskServiceImpl implements TaskService {
    private final ThreadPoolExecutor THREAD_POOL;
    private final Set<Class<?>> classSet = new HashSet<>();
    private final Set<Class<? extends MultipleTask>> multipleTaskClassSet = new HashSet<>();
    private final Map<Long, RunningTask> runningTasksMap = new HashMap<>();

    public SinglePoolTaskServiceImpl(@NotNull AozoraConfig aozoraConfig) {
        THREAD_POOL = new ThreadPoolExecutor(
                aozoraConfig.getThreadPoolCorePoolSize()[0],
                aozoraConfig.getThreadPoolMaxCorePoolSize()[0],
                1,
                TimeUnit.HOURS,
                new LinkedBlockingDeque<>(),
                new CustomizableThreadFactory("Aozora-SinglePoolTaskService-Task-%d")
        );
    }

    @Override
    public long runTask(@NotNull MultipleTask multipleTask, MultiPoolTaskServiceImpl.PoolTag poolTag) {
        RunningTask runningTask = getDefaultTask(TaskType.MultipleTask);
        runningTasksMap.put(multipleTask.getTaskId(), runningTask);

        THREAD_POOL.execute(
                () -> {
                    Runnable singleTask = multipleTask.popTask();
                    while (singleTask != null) {
                        singleTask.run();
                        singleTask = multipleTask.popTask();
                    }
                }
        );

        return multipleTask.getTaskId();
    }

    @Override
    public long runTask(Runnable task, MultiPoolTaskServiceImpl.PoolTag poolTag) {
        long taskId = IdUtil.getSnowflakeNextId();
        RunningTask runningTask = getDefaultTask(TaskType.SingleTask);
        runningTasksMap.put(taskId, runningTask);
        THREAD_POOL.execute(task);
        return taskId;
    }

    @Override
    public RunningTask[] getRunningTasks() {
        return runningTasksMap.values().toArray(new RunningTask[0]);
    }

    @Override
    public RunningTask getRunningTask(long taskId) {
        return runningTasksMap.get(taskId);
    }

    @Override
    public void register(Class<?> clazz) {
        classSet.add(clazz);
    }

    @Override
    public void registerMultipleTask(Class<? extends MultipleTask> clazz) {
        multipleTaskClassSet.add(clazz);
    }

    @Override
    public Class<?>[] getRegisteredClasses() {
        return classSet.toArray(new Class[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends MultipleTask>[] getRegisteredMultipleTaskClasses() {
        return multipleTaskClassSet.toArray(new Class[0]);
    }
}
