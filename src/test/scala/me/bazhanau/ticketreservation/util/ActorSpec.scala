package me.bazhanau.ticketreservation.util

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers

abstract class ActorSpec extends TestKit(ActorSystem("ActorSpec"))
  with Matchers
  with MockFactory
  with BeforeAndAfterAll{

  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val config = ConfigFactory.load()

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally TestKit.shutdownActorSystem(system)
  }
}
