package com.flow.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class UserCreatedPUBSUB extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(UserCreatedPUBSUB.class);

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			logger.debug("@ UC - Input received in the actor:" + msg);

			// call the pub sub component such that it can deliver the messages to all of its subscribers.
			callMessagingPubSubComponent((String) msg);

		} else {
			throw new RuntimeException("Failed : unsupported input format, input needs to be in JSON format.");
		}

		// we are not sending anything to the sender/caller of this actor because this actor/component is designed
		// to be ASYNC always.
		// getSender().tell(response, getSender());
	}

	/***
	 * mocked method to depict a call to a real component, I have not created the real component in this sample project,
	 * but idea is to hook the component call here
	 ***/
	public void callMessagingPubSubComponent(String jsonInput) {
		// presuming a messaging component will be called from here to publish this message to all of its subscribers.
		return;
	}

}
