package com.sportsbet.feeds.routing.cluster

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.spingo.op_rabbit.{ConnectionParams, RabbitControl}
import com.sportsbet.feeds.routing.actor.MessageRoutingService

class ClusterSingletonManagementWrapper(implicit actorSystem: ActorSystem) {

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

  val rabbitControl = actorSystem.actorOf(
    Props(new RabbitControl(Left(ConnectionParams.fromConfig()))),
    "rabbitmqControl")

}
