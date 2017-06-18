package me.bazhanau.ticketreservation.dao.util

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
      client = MongoClient()
      db = client.getDatabase("test")
    }
  }

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally client.close()
  }
}