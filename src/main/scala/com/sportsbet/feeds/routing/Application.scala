package com.sportsbet.feeds.routing

import akka.actor.ActorSystem
import com.sportsbet.feeds.routing.cluster.{ClusterSingletonManagementWrapper, ClusterSingletonManagerWrapper}
import com.sportsbet.feeds.routing.messaging.MockMessageConsumer
import com.typesafe.config.ConfigValueFactory.fromAnyRef
import com.typesafe.config.{Config, ConfigFactory}

object Application {

  def main(args: Array[String]): Unit = {
    val config: Config = createApplicationConfig(args)
    val clusterName = config.getString("akka.cluster.name")

    implicit val system = ActorSystem(clusterName, config)

    implicit val clusterSingletonManagementWrapper: ClusterSingletonManagementWrapper = new ClusterSingletonManagerWrapper

    val nodeId = s"Node ${config.getString("akka.remote.netty.tcp.port").toInt - 2550}"
    val mockMessageConsumer = new MockMessageConsumer()



    Thread.sleep(1000);

    mockMessageConsumer.routeDummyMessages(1000, 1000, nodeId)

  }

  private def createApplicationConfig(args: Array[String]) = {
    val config = ConfigFactory.load

    val portFromArgs = args.length match {
      case 0 => throw new ExceptionInInitializerError("No args specified. Require arguement for PORT.")
      case _ => args(0)
    }

    config.withValue("akka.remote.netty.tcp.port", fromAnyRef(portFromArgs))
  }
}