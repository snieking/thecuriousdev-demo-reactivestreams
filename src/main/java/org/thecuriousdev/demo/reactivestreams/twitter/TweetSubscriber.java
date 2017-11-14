package org.thecuriousdev.demo.reactivestreams.twitter;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import twitter4j.Status;

public class TweetSubscriber implements Subscriber<Status> {
	
	private final String id;
	private Flow.Subscription subscription;
	
	public TweetSubscriber(String id) {
		this.id = id;
	}

	@Override
	public void onComplete() {
		System.out.println("Completed!");
	}

	@Override
	public void onError(Throwable throwable) {
		System.out.println("Error: " + throwable);
	}

	@Override
	public void onNext(Status status) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder()
				.append(status.getCreatedAt())
				.append(" [").append(status.getUser().getName()).append("]: ")
				.append(status.getText());
				
		System.out.println(sb);
		subscription.request(2);
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		System.out.println("SUB " + id + " -> Subscribed");
		this.subscription = subscription;
		subscription.request(2);
	}

}
