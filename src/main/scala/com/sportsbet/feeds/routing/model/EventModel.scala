package com.sportsbet.feeds.routing.model

import akka.routing.ConsistentHashingRouter.ConsistentHashable

case class Event(id: String, name: String) extends ConsistentHashable {
  override def consistentHashKey: Any = id
}