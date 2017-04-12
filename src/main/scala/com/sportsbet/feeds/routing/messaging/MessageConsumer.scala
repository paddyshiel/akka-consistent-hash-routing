package com.sportsbet.feeds.routing.messaging

import akka.actor.ActorRef
import com.sportsbet.feeds.routing.cluster.ClusterSingletonManagementWrapper
import com.sportsbet.feeds.routing.model.Event

import scala.util.Random

trait MessageConsumer {

  val messageRoutingProxy: ActorRef

  def createMockEvent(idRange: Int, sequenceNum: Int, source: String): Event = {
    Event(s"${Random.nextInt(idRange)}", s"Source[${source}] Seq[$sequenceNum]")
  }

  def routeDummyMessages(numberOfMessages: Int, messageIntervalInMillis: Int, producerId: String) = {
    (1 to numberOfMessages).foreach(sequenceNum => {
      val event = createMockEvent(2, sequenceNum, producerId)
      Thread.sleep(messageIntervalInMillis)
      messageRoutingProxy ! event
    })
  }
}

class MockMessageConsumer(implicit val clusterSingletonManagementWrapper: ClusterSingletonManagementWrapper) extends MessageConsumer {
  override val messageRoutingProxy:ActorRef = clusterSingletonManagementWrapper.messageRoutingServiceProxy
}

