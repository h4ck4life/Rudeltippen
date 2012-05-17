package services;

import models.Settings;

import org.apache.commons.lang.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import play.Logger;
import play.libs.Codec;
import play.mvc.Http;

import utils.AppUtils;

public class TwitterService {
    public static void updateStatus(String message) {
    	Settings settings = AppUtils.getSettings();
        if (settings.isTweetable() && StringUtils.isNotBlank(message) && !Codec.hexMD5(message).equalsIgnoreCase(settings.getLastTweet())) {
            OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1/statuses/update.json");
            request.addQuerystringParameter("status", message);
            sendRequest(request);
            settings.setLastTweet(message);
            settings._save();
            Logger.info("Updating Twitter-Status.");
        } else if (StringUtils.isNotBlank(message) && !Codec.hexMD5(message).equalsIgnoreCase(settings.getLastTweet())) {
            settings.setLastTweet(message);
            settings._save();
            Logger.info("Mocked Twitter-Request. Message: " + message);
        } else {
        	Logger.info("Sending no new Tweet, since message is null or last Tweet matches new Tweet.");
        }
    }
    
    private static void sendRequest(OAuthRequest request) {
        OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
                .apiKey(AppUtils.getSettings().getTwitterConsumerkey())
                .apiSecret(AppUtils.getSettings().getTwitterConsumersecret())
                .build();

        Token accessToken = new Token(AppUtils.getSettings().getTwitterToken(), AppUtils.getSettings().getTwitterSecret());
        service.signRequest(accessToken, request);
        Response response = request.send();

        int responseCode = response.getCode();
        if (response.getCode() == Http.StatusCode.OK) {
        	Logger.info("Twitter Request successful. Response-Code: " + responseCode);
        } else {
        	Logger.error("Twitter Request failed. Response-Code: " + responseCode);
        }
    }
}