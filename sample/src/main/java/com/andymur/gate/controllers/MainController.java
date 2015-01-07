package com.andymur.gate.controllers;

import com.andymur.gate.helpers.GateHelper;
import com.andymur.gate.helpers.TwitterHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gate.*;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Scope("request")
public class MainController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String gateHome;
    private String pluginDir;
    private String userConfig;

    gate.Controller controller;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        logger.debug("index.enter;");
        logger.debug("index.exit;");
        return "main";
    }

    @RequestMapping(value = "/annotate")
    public @ResponseBody Map<String, Set<String>> annotate(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("text") String text) throws GateException, IOException {
        logger.debug("index.enter; text = {}", text);
        Map<String, Set<String>> result = Maps.newHashMap();
        logger.debug("context path: " + request.getContextPath());
        try {
            controller= GateHelper.getController(gateHome, pluginDir, userConfig);
            Corpus corpus = createCorpusFromDocument("default corpus", text);
            logger.debug(controller.toString());
            ((ConditionalSerialAnalyserController)controller).setCorpus(corpus);

            logger.debug("corpus loaded");
            controller.execute();

            Document document = corpus.get(0);

            Map<String, String> nameToAnnotations = Maps.newHashMap(ImmutableMap.<String, String>of(
                    "position", "JobTitle", "person", "Person", "address", "Address", "organization", "Organization"));

            for (String name: nameToAnnotations.keySet()) {
                Set<String> content = getContentAnnotatedBy(document, nameToAnnotations.get(name));
                logger.debug("category: " + name + " values: " + content);
                result.put(name, content);
            }

        } catch (ResourceInstantiationException e) {
            logger.error(e.getMessage());
        }
        logger.debug("index.exit;");
        return result;
    }

    @RequestMapping(value = "/load")
    public @ResponseBody Set<String> loadFromTwitter(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestParam("twitterId") String twitterId,
                                                     @RequestParam("top") Integer top) throws TwitterException {
        logger.debug("loadFromTwitter; twitterId = {}, top = {}", twitterId, top);
        return loadTopTweets(twitterId, top);
    }

    @Value("${gate.home}")
    public void setGateHome(String gateHome) {
        this.gateHome = gateHome;
    }

    @Value("${gate.plugin_dir}")
    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    @Value("${gate.site_config}")
    public void setUserConfig(String userConfig) {
        this.userConfig = userConfig;
    }

    private static Set<String> getContentAnnotatedBy(Document document, String annotationName)
            throws InvalidOffsetException {

        Set<String> contentList = Sets.newHashSet();
        DocumentContent content = document.getContent();

        for (Annotation annotation: document.getAnnotations().get(Sets.newHashSet(annotationName))) {

            long startOffset = annotation.getStartNode().getOffset().longValue();
            long endOffset = annotation.getEndNode().getOffset().longValue();

            String annotationContent = content.getContent(startOffset, endOffset).toString();
            contentList.add(annotationContent);
        }

        return contentList;
    }

    private static Corpus createCorpusFromDocument(String name, String documentContent)
            throws ResourceInstantiationException {
        Corpus corpus = Factory.newCorpus(name);
        Document gateDocument = createDocumentFromText(documentContent);
        corpus.add(gateDocument);
        return corpus;
    }

    private static Document createDocumentFromText(String text) throws ResourceInstantiationException {
        return Factory.newDocument(text);
    }

    private static Set<String> loadTopTweets(String twitterId, int top) throws TwitterException {
        Set<String> result = Sets.newHashSet();

        Twitter twitter = TwitterHelper.getInstance();

        Paging paging = new Paging(1, top);
        List<Status> statuses = twitter.getUserTimeline(twitterId, paging);
        for (Status status: statuses) {
            result.add(status.getText());
        }
        return result;
    }
}