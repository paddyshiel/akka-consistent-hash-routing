package com.sportsbet.feeds.routing.cluster

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.spingo.op_rabbit.RabbitControl
import com.sportsbet.feeds.routing.actor.MessageRoutingService

trait ClusterSingletonManagementWrapper {
  val messageRoutingServiceSingleton: ActorRef
  val messageRoutingServiceProxy: ActorRef
  val rabbitControl: ActorRef
}

class ClusterSingletonManagerWrapper(implicit actorSystem: ActorSystem) extends ClusterSingletonManagementWrapper {

  override val messageRoutingServiceSingleton = actorSystem.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props[MessageRoutingService],
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(actorSystem)),
    "messageRoutingService")

  override val messageRoutingServiceProxy = actorSystem.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/messageRoutingService",
      settings = ClusterSingletonProxySettings(actorSystem)),
    "messageRoutingServiceProxy")

  override val rabbitControl = actorSystem.actorOf(
    Props[RabbitControl],
    "rabbitmqControl")

}
