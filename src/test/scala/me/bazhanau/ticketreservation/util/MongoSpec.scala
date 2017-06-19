package me.bazhanau.ticketreservation.util

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import org.scalatest._

abstract class MongoSpec extends AsyncFlatSpec
  with Matchers
  with BeforeAndAfterAll{

  private var client: MongoClient = _
  protected var db: MongoDatabase = _

  override protected def beforeAll(): Unit = {
    try super.beforeAll()
    finally {
      val config = ConfigFactory.load()
      client = MongoClient(config.getString("mongodb.connectionString"))
      db = client.getDatabase(config.getString("mongodb.dbName"))
    }
  }

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally client.close()
  }
}