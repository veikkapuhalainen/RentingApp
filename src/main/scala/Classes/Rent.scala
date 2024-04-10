package Classes
import java.time.LocalDate
import scala.collection.mutable

class Rent(notification: Notification, whoRented: User, durationDays: Int, durationHours: Int, price: Double, returned: Boolean):

  val comments = mutable.Buffer[String]()

  def addComment(comment: String): Unit = comments += comment

  def returnRent(): Unit = 
    this.whoRented.rents -= this

  def cancelRent(): Unit = 
    this.whoRented.rents -= this

  override def toString = s"Rented: ${notification.name}, who rented: $whoRented, durationDays: $durationDays, durationHours: $durationHours, price: $price, returned: $returned"