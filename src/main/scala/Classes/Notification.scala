package Classes

import scalafx.scene.control.DatePicker

import scala.collection.mutable

case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, available: Boolean):


  val comments = mutable.Buffer[String]()
  val damages = mutable.Buffer[String]()
  
  val calendar = new DatePicker()

  def addComment(description: String): Unit =
    this.comments += description

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification
