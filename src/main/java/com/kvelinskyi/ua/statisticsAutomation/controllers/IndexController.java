package com.kvelinskyi.ua.statisticsAutomation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/")
    public ModelAndView getIndexSlash() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class IndexController - IndexController(/) has started !");
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = "/index")
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class IndexController - IndexController(/index) has started !");
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
