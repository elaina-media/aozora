package net.mikoto.aozora.service;

import net.mikoto.aozora.model.multipleTask.MultipleTask;
import net.mikoto.aozora.service.impl.TaskServiceImpl;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
public interface TaskService {
    void runTask(MultipleTask task, TaskServiceImpl.Tag tag);

    void runTask(Runnable task, TaskServiceImpl.Tag tag);
}
