package Classes
import java.time.LocalDate
import scala.collection.mutable

/**
 * Class representing a single rent
 * @param notification notification that is rented
 * @param whoRented user who rented this notification
 * @param amount how many products is rented
 * @param startDay start day of rent
 * @param endDay end day of rent
 * @param startHour start hour of rent
 * @param endHour end hour of rent
 * @param price price of the rent
 */
case class Rent(notification: Notification, whoRented: User, amount: Int, startDay: LocalDate, endDay: LocalDate, startHour: Int, endHour: Int, price: Double):
  
  val comments = mutable.Buffer[String]()

  def addComment(comment: String): Unit = comments += comment

  override def toString = s"Rented: ${notification.name}, who rented: $whoRented, amount: $amount, start: d:$startDay, h:$startHour, d:end: $endDay, h:$endHour, price: $price"