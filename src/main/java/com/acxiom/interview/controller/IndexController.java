package com.acxiom.interview.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangzhiliang
 */
@RestController
@RequestMapping("/")
public class IndexController extends BaseRestController {
    @ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    public String welcome() {
        return "welcome";
    }
}
