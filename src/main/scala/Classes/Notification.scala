package Classes

import scalafx.scene.control.{Button, DatePicker, TextField}

import java.time.LocalDate
import java.util.Date
import scala.collection.mutable

case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int, var available: Boolean):

  val reservedDates = mutable.Buffer[Date]()
  val calendar = new DatePicker()

  val comments = mutable.Buffer[String]()
  val damages = mutable.Buffer[String]()

  def addComment(description: String): Unit =
    this.comments += description

  val seeMoreButton = new Button(s"$name ${"\n"}Price/day: ${pricePerDay.toString}€, Price/hour: ${pricePerHour.toString}€")
  seeMoreButton.minHeight = 50
  seeMoreButton.minWidth = 70

  def button =
    seeMoreButton

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification
