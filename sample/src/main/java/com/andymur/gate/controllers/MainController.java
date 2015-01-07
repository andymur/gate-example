package com.andymur.gate.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gate.*;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Scope("request")
public class MainController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    gate.Controller controller;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        logger.debug("index.enter;");

        try {
            Factory.newCorpus("default corpus");
        } catch (ResourceInstantiationException e) {

        }
        logger.debug("index.exit;");
        return "main";
    }

    @RequestMapping(value = "/annotate")
    public @ResponseBody Map<String, Set<String>> annotate(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("text") String text) throws ExecutionException, InvalidOffsetException {
        logger.debug("index.enter; text = {}", text);
        Map<String, Set<String>> result = Maps.newHashMap();
        try {
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
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey("JUhgYNKKsiVhmOgkOGGEOkvaI");
        configurationBuilder.setOAuthConsumerSecret("Gum07oMrsoJxk48AoWu4v1MP977ASOM20yjTMBea4zATLs6eIV");
        configurationBuilder.setOAuthAccessToken("184537064-9wFFenKKxYDYUiMJRFrtlklxNPpJSFNkaWFVmFEt");
        configurationBuilder.setOAuthAccessTokenSecret("SS2Jyoz6BpM0CcZlSOxbj8n58a91z8Moe31ICi88bYq8g");

        Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();

        Paging paging = new Paging(1, top);
        List<Status> statuses = twitter.getUserTimeline(twitterId, paging);
        for (Status status: statuses) {
            result.add(status.getText());
        }
        return result;
    }
}