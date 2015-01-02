package com.andymur.gate;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

/**
 * Created by andymur on 1/2/15.
 */
public class TwitterRunner {
    public static void main(String[] args) throws TwitterException {

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey("JUhgYNKKsiVhmOgkOGGEOkvaI");
        configurationBuilder.setOAuthConsumerSecret("Gum07oMrsoJxk48AoWu4v1MP977ASOM20yjTMBea4zATLs6eIV");
        configurationBuilder.setOAuthAccessToken("184537064-9wFFenKKxYDYUiMJRFrtlklxNPpJSFNkaWFVmFEt");
        configurationBuilder.setOAuthAccessTokenSecret("SS2Jyoz6BpM0CcZlSOxbj8n58a91z8Moe31ICi88bYq8g");

        Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        //First param of Paging() is the page number, second is the number per page (this is capped around 200 I think.
        Paging paging = new Paging(1, 10);
        List<Status> statuses = twitter.getUserTimeline("google", paging);
        System.out.println(statuses.get(0).getText());

    }
}
