package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.AozoraApplicationContextGetter;
import net.mikoto.aozora.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/task")
@Log
public class TaskRestController {
    public final TaskService taskService;
    public final AozoraApplicationContextGetter contextGetter;

    @Autowired
    public TaskRestController(TaskService taskService, AozoraApplicationContextGetter contextGetter) {
        this.taskService = taskService;
        this.contextGetter = contextGetter;
    }

    @SneakyThrows
    @RequestMapping("/create")
    public JSONObject createTask(
            @RequestParam String taskClassName,
            @RequestParam @NotNull String paramTypes,
            @RequestParam String params,
            @RequestParam String requireServiceTypes) {
        Class<? extends Runnable> taskClass = Class.forName(taskClassName).asSubclass(Runnable.class);

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

        Runnable task = (Runnable) taskConstructor.newInstance(paramsObjectArray);

        if (requireServiceTypes != null) {
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

        taskService.runTask(task);

        return new JSONObject();
    }
}
