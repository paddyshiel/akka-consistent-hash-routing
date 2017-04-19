package com.sportsbet.feeds.routing.messaging

import akka.actor.ActorRef
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.PlayJsonSupport._
import com.spingo.op_rabbit._
import com.sportsbet.feeds.routing.cluster.ClusterSingletonManagementWrapper
import com.sportsbet.feeds.routing.model.Event
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait MessageConsumer {

  val messageRoutingProxy: ActorRef

  val rabbitControl: ActorRef

  implicit val eventFormat = Json.format[Event]
  implicit val recoveryStrategy = RecoveryStrategy.none

  val subscriptionRef = Subscription.run(rabbitControl) {
    channel(qos = 3) {
      consume(Queue.passive("event.inbound")) {
        (body(as[Event]) & routingKey) { (event: Event, key) =>
          /* do work; this body is executed in a separate thread, as
             provided by the implicit execution context */
          println(s"""A event named '${event.name}'was received over '${key}'.""")
          ack
        }
      }
    }
  }

  /*def createMockEvent(idRange: Int, sequenceNum: Int, source: String): Event = {
    Event(s"${Random.nextInt(idRange)}", s"Source[${source}] Seq[$sequenceNum]")
  }

  def routeDummyMessages(numberOfMessages: Int, messageIntervalInMillis: Int, producerId: String) = {
    (1 to numberOfMessages).foreach(sequenceNum => {
      val event = createMockEvent(1, sequenceNum, producerId)
      Thread.sleep(messageIntervalInMillis)
      messageRoutingProxy ! event
    })
  }*/
}

class MockMessageConsumer(implicit val clusterSingletonManagementWrapper: ClusterSingletonManagementWrapper) extends MessageConsumer {
  override val messageRoutingProxy: ActorRef = clusterSingletonManagementWrapper.messageRoutingServiceProxy
  override val rabbitControl = clusterSingletonManagementWrapper.rabbitControl
}

