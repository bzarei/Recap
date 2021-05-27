package de.telekom.bzarei.sst;

public interface EventSubscription {

	void subscribe(EventListener listener);
	void unsubscribe(EventListener listener);
}

