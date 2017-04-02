package com.paddyshiel.starscream.actor

import akka.actor.{Actor, ActorLogging}

class EventProcessor extends Actor with ActorLogging {
  def receive = {
    case event: Event => log.info("Received Event {} in Actor {}", event, self.path.name)
  }
}

case class Event(id: String, name: String)

