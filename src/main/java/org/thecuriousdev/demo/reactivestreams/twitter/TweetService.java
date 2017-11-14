package org.thecuriousdev.demo.reactivestreams.twitter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class TweetService {
	
	@Autowired
	private TweetPublisher tweetPublisher;
	
	@PostConstruct
	public void subscribeToTweets() {
		TweetSubscriber subscriber = new TweetSubscriber("1");
		tweetPublisher.subscribe(subscriber);
	}

}
