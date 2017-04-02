package com.paddyshiel.starscream.actor

import akka.actor.{Actor, ActorLogging}

class EchoActor extends Actor with ActorLogging {
  def receive: Receive = {
    case message =>
      log.info("Received Message {} in Actor {}", message, self.path.name)
  }
}