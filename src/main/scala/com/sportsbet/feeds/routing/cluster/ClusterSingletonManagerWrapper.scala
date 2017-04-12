package com.sportsbet.feeds.routing.cluster

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.sportsbet.feeds.routing.actor.MessageRoutingService

trait ClusterSingletonManagementWrapper {
  val messageRoutingServiceSingleton: ActorRef
  val messageRoutingServiceProxy: ActorRef
}

class ClusterSingletonManagerWrapper(implicit actorSystem: ActorSystem) extends ClusterSingletonManagementWrapper {

  val messageRoutingServiceSingleton = actorSystem.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props[MessageRoutingService],
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(actorSystem)),
    "messageRoutingService")

  val messageRoutingServiceProxy = actorSystem.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/messageRoutingService",
      settings = ClusterSingletonProxySettings(actorSystem)),
    "messageRoutingServiceProxy")

}
