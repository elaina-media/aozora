package net.mikoto.aozora.crawler.controller;

import com.alibaba.fastjson2.JSONObject;
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
    @RequestMapping("/create")
    public JSONObject create(String taskName, String taskParamTypes, String taskParams) {

    }
}
