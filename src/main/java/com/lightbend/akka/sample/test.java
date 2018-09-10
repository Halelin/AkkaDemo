package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class test {
	public static void main(String[] args) {
		 ActorSystem system = ActorSystem.create("testSystem");
		ActorRef first = system.actorOf(Props.create(StartStopActor1.class), "first");
		first.tell("stop", ActorRef.noSender());
	}
}