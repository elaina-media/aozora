package net.mikoto.aozora.service.impl;

import lombok.extern.java.Log;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    private final Set<RunningTask> runningTaskSet = new HashSet<>();

    public SinglePoolTaskServiceImpl(AozoraConfig aozoraConfig) {
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
    public void runTask(MultipleTask task, MultiPoolTaskServiceImpl.PoolTag poolTag) {
        THREAD_POOL.execute(
                () -> {
                    Runnable singleTask = task.popTask();
                    while (singleTask != null) {
                        singleTask.run();
                        singleTask = task.popTask();
                    }
                }
        );
    }

    @Override
    public void runTask(Runnable task, MultiPoolTaskServiceImpl.PoolTag poolTag) {
        THREAD_POOL.execute(task);
    }

    @Override
    public RunningTask[] getRunningTasks() {
        return runningTaskSet.toArray(new RunningTask[0]);
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
