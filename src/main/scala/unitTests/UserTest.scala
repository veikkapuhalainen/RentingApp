/*package unitTests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import Classes.*
import java.time.Period
import java.time.LocalDate

class UserTest extends AnyFlatSpec with Matchers:
 "making rent" should "behave as expected when making a new rent" in {
    val User1 = User("veikka", "osoite", "7787798")
    val User2 = User("veijo", "osoite", "7787798")
    val categ = Category("sport")
    val notif = Notification("testi", User1, 3.0, 4.0, "hyvä tuote", Category("sport"), 1,true)
    val rent = Rent(notif, User2, 1, LocalDate.now(), LocalDate.now(), 0, 0, notif.pricePerDay )
    notif.reservedDates += Period.between(LocalDate.now(), LocalDate.now() )
    notif.reservedDates.head shouldEqual(Period.between(LocalDate.now(), LocalDate.now() ))
    //User1.makeNotification("testi", 3.0, 4.0, "hyvä tuote", Category("sport"), 1,true) shouldEqual(notif)

 }
 
 */
