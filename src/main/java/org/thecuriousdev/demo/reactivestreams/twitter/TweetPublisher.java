package org.thecuriousdev.demo.reactivestreams.twitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@EnableScheduling
@Configuration
@Service
public class TweetPublisher {
	
	@Value("${oauth.consumerKey}")
	private String consumerKey;
	
	@Value("${oauth.consumerSecret}")
	private String consumerSecret;
	
	@Value("${oauth.accessToken}")
	private String accessToken;
	
	@Value("${oauth.accessTokenSecret}")
	private String accesessTokenSecret;

	private static final Query TRUMP_QUERY = new Query("trump");
	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

	private Twitter twitter;
	private SubmissionPublisher<Status> sb = new SubmissionPublisher<>(EXECUTOR, Flow.defaultBufferSize());

	private Map<Long, Object> cache = new HashMap<>();

	@Autowired
	public void setup() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accesessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	@Scheduled(fixedRate = 10000)
	public void scanTweets() throws TwitterException {
		twitter.search(TRUMP_QUERY).getTweets()
			.stream()
			.filter(status -> !cache.containsKey(status.getId()))
			.forEach(status -> {
				cache.put(status.getId(), null);
				sb.submit(status);
			});
	}

	public void subscribe(TweetSubscriber subscriber) {
		sb.subscribe(subscriber);
	}

}
