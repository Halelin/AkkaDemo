package com.lightbend.akka.sample;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

class StartStopActor2 extends AbstractActor {
  @Override
  public void preStart() {
    System.out.println("second started");
  }

  @Override
  public void postStop() {
    System.out.println("second stopped");
  }

  // Actor.emptyBehavior is a useful placeholder when we don't
  // want to handle any messages in the actor.
  @Override
  public Receive createReceive() {
	  
    return receiveBuilder()
        .build();
  }
}

