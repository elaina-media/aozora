package net.mikoto.aozora.service.impl;

import lombok.extern.java.Log;
import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@Service
@Log
public class TaskServiceImpl implements TaskService, AutoCloseable {
    private static final ThreadPoolExecutor[] THREAD_POOL_EXECUTORS = new ThreadPoolExecutor[] {
            new ThreadPoolExecutor(
                    6,
                    6,
                    1,
                    TimeUnit.HOURS,
                    new LinkedBlockingDeque<>(),
                    new CustomizableThreadFactory("Aozora-A-Task-%d")
            ),
            new ThreadPoolExecutor(
                    6,
                    6,
                    1,
                    TimeUnit.HOURS,
                    new LinkedBlockingDeque<>(),
                    new CustomizableThreadFactory("Aozora-B-Task-%d")
            ),
            new ThreadPoolExecutor(
            6,
            6,
            1,
            TimeUnit.HOURS,
            new LinkedBlockingDeque<>(),
            new CustomizableThreadFactory("Aozora-C-Task-%d")
            )
    };

    @Override
    public void runTask(Runnable task, @NotNull Tag tag) {
        THREAD_POOL_EXECUTORS[tag.ordinal()].execute(task);
    }

    public void runTask(@NotNull MultipleTask multipleTask, Tag tag) {
        for (int i = 0; i < multipleTask.getCacheMaxSize(); i++) {
            Runnable task = multipleTask.popTask();

            if (task == null) {
                break;
            }

            runTask(task, tag);
        }


    }

    @Override
    public void close() {
        for (ThreadPoolExecutor threadPoolExecutor : THREAD_POOL_EXECUTORS) {
            threadPoolExecutor.close();
        }
    }

    public enum Tag {
        A, B, C
    }
}
