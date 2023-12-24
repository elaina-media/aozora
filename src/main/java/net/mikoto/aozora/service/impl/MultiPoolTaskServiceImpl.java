package net.mikoto.aozora.service.impl;

import io.vertx.sqlclient.Pool;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.model.multipleTask.SingleTask;
import net.mikoto.aozora.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@Service("MultiPoolTaskService")
@Log
public class MultiPoolTaskServiceImpl implements TaskService, AutoCloseable {
    private final ThreadPoolExecutor[] THREAD_POOLS;
    private final Set<Class<?>> classSet = new HashSet<>();
    private final Set<Class<? extends MultipleTask>> multipleTaskClassSet = new HashSet<>();
    private final Set<RunningTask> runningTaskSet = new HashSet<>();

    @SuppressWarnings("resource")
    public MultiPoolTaskServiceImpl(AozoraConfig aozoraConfig) {
        THREAD_POOLS = new ThreadPoolExecutor[3];

        for (int i = 0; i < 3; i++) {
            THREAD_POOLS[i] = new ThreadPoolExecutor(
                    aozoraConfig.getThreadPoolCorePoolSize()[i],
                    aozoraConfig.getThreadPoolMaxCorePoolSize()[i],
                    1,
                    TimeUnit.HOURS,
                    new LinkedBlockingDeque<>(),
                    new CustomizableThreadFactory("Aozora-MultiPoolTaskService-" + PoolTag.values()[i] + "Pool-Task-%d")
            );
        }
    }

    public void runTask(@NotNull MultipleTask multipleTask, @NotNull PoolTag poolTag) {
        // 计算下一线程池
        PoolTag nextPoolTag;
        if (poolTag.ordinal() + 1 != PoolTag.values().length) {
            nextPoolTag = PoolTag.values()[poolTag.ordinal() + 1];
        } else {
            nextPoolTag = PoolTag.values()[0];
        }

        RunningTask runningTask = new RunningTask() {
            private final long startTime = new Date().getTime();
            private boolean flag = false;
            private long continuedTime;

            @Override
            public TaskType taskType() {
                return TaskType.MultipleTask;
            }

            @Override
            public long taskContinuedTime() {
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
        runningTaskSet.add(runningTask);

        // 推送任务布置线程
        THREAD_POOLS[poolTag.ordinal()].execute(
                () -> {
                    SingleTask[] tasks = new SingleTask[multipleTask.getLoopCount()];

                    for (int i = 0; i < multipleTask.getLoopCount(); i++) {
                        tasks[i] = multipleTask.popTask();

                        runTask(tasks[i], nextPoolTag);
                    }
                    for (;;) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                        // 轮询子线程
                        boolean flag = true;
                        for (SingleTask task : tasks) {
                            if (!flag) {
                                break;
                            }

                            flag = task.isFinished();
                        }


                        if (!flag) {
                            continue;
                        }

                        // 执行新线程任务
                        tasks = new SingleTask[multipleTask.getLoopCount()];

                        for (int i = 0; i < multipleTask.getLoopCount(); i++) {
                            tasks[i] = multipleTask.popTask();

                            // 退出线程
                            if (tasks[i] == null) {
                                if (i == 0) {
                                    runningTask.endTask();
                                    return;
                                }

                                // 执行剩余任务
                                for (int j = 0; j < i; j++) {
                                    runTask(tasks[j], nextPoolTag);
                                }
                                runningTask.endTask();
                                return;
                            }

                            runTask(tasks[i], nextPoolTag);
                        }
                    }
                }
        );
    }

    @Override
    public RunningTask[] getRunningTasks() {
        return runningTaskSet.toArray(new RunningTask[0]);
    }

    @Override
    public void close() {
        for (ThreadPoolExecutor threadPoolExecutor : THREAD_POOLS) {
            threadPoolExecutor.close();
        }
    }

    @Override
    public void runTask(Runnable task, @NotNull MultiPoolTaskServiceImpl.PoolTag poolTag) {
        THREAD_POOLS[poolTag.ordinal()].execute(task);
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

    public enum PoolTag {
        A, B, C
    }
}
