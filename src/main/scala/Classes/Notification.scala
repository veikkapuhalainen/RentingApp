package Classes

import scala.collection.mutable

class Notification(var name: String, var publisher: User, var pricePerDay: Double, var pricePerHour: Double, var description: String, val category: Category):

  val comments = mutable.Buffer[String]()
  val damages = mutable.Buffer[String]()
/*
  val reserved = Buffer[TimePeriod]()
  val calendar =  Calendar
  */

  def addComment(description: String): Unit =
    this.comments += description
    
  def deleteNotification(): Unit = ???

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification
