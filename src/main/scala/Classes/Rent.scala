package Classes

import scala.collection.mutable

class Rent(notification: Notification, whoRented: User, rentDuration: Int, returned: Boolean):

  val comments = mutable.Buffer[String]()

  def addComment(comment: String): Unit = comments += comment

  def returnRent(): Unit = 
    this.whoRented.rents -= this

  def cancelRent(): Unit = 
    this.whoRented.rents -= this