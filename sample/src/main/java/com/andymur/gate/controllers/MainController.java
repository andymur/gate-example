package com.andymur.gate.controllers;

import gate.Factory;
import gate.creole.ResourceInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Scope("request")
public class MainController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    gate.Controller controller;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        logger.debug("index.enter;");
        logger.debug(controller.toString());
        try {
            Factory.newCorpus("default corpus");
        } catch (ResourceInstantiationException e) {

        }
        logger.debug("index.exit;");
        return "main";
    }
}