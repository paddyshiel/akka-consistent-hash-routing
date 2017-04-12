package com.sportsbet.feeds.routing.actor

import akka.actor.{Actor, ActorLogging}
import com.sportsbet.feeds.routing.model.Event

class EventProcessingActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case event: Event => log.info(s"Actor ${self.path.name} - $event")
    case _ => log.warning(s"Unhandled message type received. ${self.path}")
  }

}
