package com.sportsbet.feeds.routing.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig
import com.sportsbet.feeds.routing.model.Event

class MessageRoutingService extends Actor with ActorLogging {

  override def preStart(): Unit = log.info(s"Starting MessageRoutingService @ ${self.path}")

  val eventProcessingActor = context.actorOf(FromConfig.props(Props[EventProcessingActor]), name = "eventProcessingActor")

  override def receive = {
    case event: Event => eventProcessingActor ! event
    case _ => log.warning(s"Unhandled message type received. ${self.path}")
  }

}

