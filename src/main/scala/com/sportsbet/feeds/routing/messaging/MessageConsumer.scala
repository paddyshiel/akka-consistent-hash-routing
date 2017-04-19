package com.sportsbet.feeds.routing.messaging

import akka.actor.ActorRef
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.PlayJsonSupport._
import com.spingo.op_rabbit._
import com.sportsbet.feeds.routing.cluster.ClusterSingletonManagementWrapper
import com.sportsbet.feeds.routing.model.Event
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class MessageConsumer(implicit val clusterSingletonManagementWrapper: ClusterSingletonManagementWrapper) {
  val messageRoutingProxy: ActorRef = clusterSingletonManagementWrapper.messageRoutingServiceProxy
  val rabbitControl: ActorRef = clusterSingletonManagementWrapper.rabbitControl

  implicit val eventFormat = Json.format[Event]
  implicit val recoveryStrategy = RecoveryStrategy.nack()

  val subscriptionRef = Subscription.run(rabbitControl) {
    channel(qos = 3) {
      consume(Queue.passive("inbound.event-creation.queue")) {
        (body(as[Event]) & routingKey) { (event: Event, key) =>
          messageRoutingProxy ! event
          ack
        }
      }
    }
  }

}

