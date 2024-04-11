package Classes
import java.time.{LocalDate, Period}
import scala.collection.mutable

case class Rent(notification: Notification, whoRented: User, amount: Int, startDay: LocalDate, endDay: LocalDate, startHour: Int, endHour: Int, price: Double):
  
  val comments = mutable.Buffer[String]()

  def addComment(comment: String): Unit = comments += comment

  def returnRent(): Unit = 
    this.whoRented.rents -= this

  def cancelRent(): Unit = 
    this.whoRented.rents -= this

  override def toString = s"Rented: ${notification.name}, who rented: $whoRented, amount: $amount, start: d:$startDay, h:$startHour, d:end: $endDay, h:$endHour, price: $price"