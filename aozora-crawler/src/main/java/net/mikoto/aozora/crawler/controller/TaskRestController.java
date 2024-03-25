package net.mikoto.aozora.crawler.controller;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.aozora.crawler.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikoto
 * &#064;date 2024/1/7
 * Create for aozora
 */
@RestController
@RequestMapping("/task")
public class TaskRestController {
    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping("/create")
    public JSONObject create(String taskName, String beanNames, String taskParams) {
        JSONObject result = new JSONObject();
        int id = taskService.scheduleTask(taskName, beanNames.split(","), taskParams.split(","));
        result.put("taskId", id);
        return result;
    }

    @RequestMapping("/run")
    public void run(int taskId) {
        taskService.run(taskId);
    }


    @RequestMapping("/stop")
    public void stop(int taskId) {
        taskService.stop(taskId);
    }
}
