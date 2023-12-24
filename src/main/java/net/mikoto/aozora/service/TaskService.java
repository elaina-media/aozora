package net.mikoto.aozora.service;

import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.impl.MultiPoolTaskServiceImpl;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
public interface TaskService {
    void runTask(MultipleTask task, MultiPoolTaskServiceImpl.PoolTag poolTag);

    void runTask(Runnable task, MultiPoolTaskServiceImpl.PoolTag poolTag);

    void register(Class<?> clazz);

    void registerMultipleTask(Class<? extends MultipleTask> clazz);

    Class<?>[] getRegisteredClasses();
    Class<? extends MultipleTask>[] getRegisteredMultipleTaskClasses();
    RunningTask[] getRunningTasks();

    interface RunningTask {
        TaskType taskType();
        long taskContinuedTime();
        void endTask();
        boolean isEnd();
    }

    enum TaskType {
        Task,
        MultipleTask
    }
}
