package net.mikoto.aozora.service;

import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.impl.MultiPoolTaskServiceImpl;

import java.util.Date;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
public interface TaskService {
    long runTask(MultipleTask task, MultiPoolTaskServiceImpl.PoolTag poolTag);

    long runTask(Runnable task, MultiPoolTaskServiceImpl.PoolTag poolTag);

    void register(Class<?> clazz);

    void registerMultipleTask(Class<? extends MultipleTask> clazz);

    Class<?>[] getRegisteredClasses();
    Class<? extends MultipleTask>[] getRegisteredMultipleTaskClasses();
    RunningTask[] getRunningTasks();
    RunningTask getRunningTask(long taskId);

    default RunningTask getDefaultTask(TaskType taskType) {
        return new RunningTask() {
            private final long startTime = new Date().getTime();
            private boolean flag = false;
            private long continuedTime;

            @Override
            public TaskType getTaskType() {
                return taskType;
            }

            @Override
            public long getTaskContinuedTime() {
                if (flag) {
                    return continuedTime;
                }
                return new Date().getTime() - startTime;
            }

            @Override
            public void endTask() {
                flag = true;
                continuedTime = new Date().getTime() - startTime;
            }

            @Override
            public boolean isEnd() {
                return flag;
            }
        };
    }

    interface RunningTask {
        TaskType getTaskType();
        long getTaskContinuedTime();
        void endTask();
        boolean isEnd();
    }

    enum TaskType {
        SingleTask,
        MultipleTask
    }
}
