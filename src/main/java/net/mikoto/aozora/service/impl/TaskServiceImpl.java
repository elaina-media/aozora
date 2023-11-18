package net.mikoto.aozora.service.impl;

import net.mikoto.aozora.service.TaskService;
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
public class TaskServiceImpl implements TaskService {
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            1,
            1,
            1,
            TimeUnit.HOURS,
            new LinkedBlockingDeque<>(),
            new CustomizableThreadFactory("Aozora-Task-%d")
    );

    @Override
    public void runTask(Runnable task) {
        THREAD_POOL.execute(task);
    }
}
