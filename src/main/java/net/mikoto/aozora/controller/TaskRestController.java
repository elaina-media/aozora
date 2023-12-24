package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.AozoraApplicationContextGetter;
import net.mikoto.aozora.service.TaskService;
import net.mikoto.aozora.service.impl.MultiPoolTaskServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/api/task")
@Log
public class TaskRestController {
    public final TaskService multipleTaskService;
    public final TaskService singleTaskService;
    public final Map<String, TaskService> taskServiceMap = new HashMap<>();
    public final AozoraApplicationContextGetter contextGetter;

    @Autowired
    public TaskRestController(@Qualifier("MultiPoolTaskService") TaskService multipleTaskService,
                              @Qualifier("SinglePoolTaskService") TaskService singleTaskService,
                              AozoraApplicationContextGetter contextGetter) {
        this.multipleTaskService = multipleTaskService;
        this.singleTaskService = singleTaskService;
        this.contextGetter = contextGetter;
        taskServiceMap.put("MultiPoolTaskService", multipleTaskService);
        taskServiceMap.put("SinglePoolTaskService", singleTaskService);
    }

    @SneakyThrows
    @RequestMapping("/create")
    public JSONObject createTask(
            @RequestParam String taskClassName,
            @RequestParam @NotNull String paramTypes,
            @RequestParam String params,
            @RequestParam String requireServiceTypes,
            @RequestParam String poolId,
            @RequestParam String taskServiceType) {
        Class<? extends Runnable> taskClass = Class.forName(taskClassName).asSubclass(Runnable.class);

        Runnable task;
        if (paramTypes.isEmpty()) {
            Constructor<?> taskConstructor = taskClass.getConstructor();

            task = (Runnable) taskConstructor.newInstance();
        } else {
            String[] paramTypesArray = paramTypes.split(",");
            Class<?>[] paramTypeClasses = new Class<?>[paramTypesArray.length];
            for (int i = 0; i < paramTypesArray.length; i++) {
                paramTypeClasses[i] = Class.forName(paramTypesArray[i]);
            }
            Constructor<?> taskConstructor = taskClass.getConstructor(paramTypeClasses);

            String[] paramsArray = params.split(",");
            Object[] paramsObjectArray = new Object[paramsArray.length];
            for (int i = 0; i < paramsObjectArray.length; i++) {
                Method method = paramTypeClasses[i].getMethod("valueOf", String.class);
                paramsObjectArray[i] = method.invoke(null, paramsArray[i]);
            }

            task = (Runnable) taskConstructor.newInstance(paramsObjectArray);

        }

        if (!requireServiceTypes.isEmpty()) {
            String[] requireServicesArray = requireServiceTypes.split(",");
            for (String requireService : requireServicesArray) {
                Class<?> requireServiceType = Class.forName(requireService);
                log.info("set" + requireServiceType.getSimpleName());
                Method setServiceMethod = taskClass.getMethod(
                        "set" + requireServiceType.getSimpleName(),
                        requireServiceType
                );
                setServiceMethod.invoke(task, contextGetter.getBean(requireServiceType));
            }
        }

        long taskId = taskServiceMap.get(taskServiceType).runTask(task, MultiPoolTaskServiceImpl.PoolTag.valueOf(poolId));
        JSONObject result = new JSONObject();
        result.put("taskId", taskId);

        return result;
    }

    @RequestMapping(
            "/getCurrentTasks"
    )
    public JSONObject getTasks(@RequestParam String taskServiceType) {
        JSONObject result = new JSONObject();
        TaskService.RunningTask[] runningTasks = taskServiceMap.get(taskServiceType).getRunningTasks();

        result.put("body", runningTasks);
        return result;
    }

    @RequestMapping(
            "/getRegisteredTasks"
    )
    public JSONObject getRegisteredTasksList(@RequestParam String taskServiceType) {
        JSONObject result = new JSONObject();
        JSONObject body = new JSONObject();
        body.put("singleTaskClasses", new JSONArray());
        body.put("multipleTaskClasses", new JSONArray());

        Class<?>[] registeredClasses = taskServiceMap.get(taskServiceType).getRegisteredClasses();
        Class<?>[] registeredMultipleTaskClasses = taskServiceMap.get(taskServiceType).getRegisteredMultipleTaskClasses();

        JSONArray singleTaskClasses = body.getJSONArray("singleTaskClasses");
        JSONArray multipleTaskClasses = body.getJSONArray("multipleTaskClasses");
        for (Class<?> clazz:registeredClasses) {
            singleTaskClasses.add(clazz.getCanonicalName());
        }
        for (Class<?> clazz:registeredMultipleTaskClasses) {
            multipleTaskClasses.add(clazz.getCanonicalName());
        }

        result.put("body", body);

        return result;
    }
}
