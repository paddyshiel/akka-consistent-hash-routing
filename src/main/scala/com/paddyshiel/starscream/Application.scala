import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.ConsistentHashingRouter.{ConsistentHashMapping, ConsistentHashableEnvelope}
import akka.routing.{ConsistentHashingPool, FromConfig}
import com.paddyshiel.starscream.actor.{EchoActor, Event, EventProcessor}
import com.typesafe.config.ConfigValueFactory.fromAnyRef
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Random

object Application {

  def main(args: Array[String]): Unit = {
    val config: Config = createApplicationConfig(args)
    val clusterName = config.getString("akka.cluster.name")
    val system = ActorSystem(clusterName, config)

    val designatedProducerId = "2553"
    val isDesignatedRouter = args(0) == "2553"

    if (isDesignatedRouter) produceAndRouteMessages(system)
  }

  def produceAndRouteMessages(system: ActorSystem): Unit = {
    def hashMapping: ConsistentHashMapping = {
      case e: Event => e.id
    }

    val consistentHashRouterActor: ActorRef = system.actorOf(FromConfig.props(Props[EventProcessor]),
      name = "ClusterAwareConsistentHashPoolRoutingActor")

    (1 to 1000).foreach(i => {
      Thread.sleep(1000)
      val id = Random.nextInt(10)
      val event = Event(s"${id}", s"Event[${id}] Seq[$i]")
      consistentHashRouterActor ! ConsistentHashableEnvelope(event, event.id)
    })
  }

  private def createApplicationConfig(args: Array[String]) = {
    val config = ConfigFactory.load

    val nodeTCPPort = args.length match {
      case 0 => throw new ExceptionInInitializerError("No args specified.")
      case _ => args(0)
    }

    config
      .withValue("akka.remote.netty.tcp.port", fromAnyRef(nodeTCPPort))
  }
}