package com.andymur.gate.helpers;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by andymur on 1/8/15.
 */
public class TwitterHelper {

    private static Twitter twitter;

    public static Twitter getInstance() {

        if (twitter == null) {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey("JUhgYNKKsiVhmOgkOGGEOkvaI");
            configurationBuilder.setOAuthConsumerSecret("Gum07oMrsoJxk48AoWu4v1MP977ASOM20yjTMBea4zATLs6eIV");
            configurationBuilder.setOAuthAccessToken("184537064-9wFFenKKxYDYUiMJRFrtlklxNPpJSFNkaWFVmFEt");
            configurationBuilder.setOAuthAccessTokenSecret("SS2Jyoz6BpM0CcZlSOxbj8n58a91z8Moe31ICi88bYq8g");
            twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        }

        return twitter;
    }
}
