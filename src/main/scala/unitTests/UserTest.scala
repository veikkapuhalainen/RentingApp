package unitTests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import Classes.Notification
import Classes.User
import Classes.Category
import scala.collection.mutable

class UserTest extends AnyFlatSpec with Matchers:
 "makeNotification" should "behave as expected when making a new notification" in {
    val User1 = User("Veijo", "0440110649", "veijo.puhalainen@gmail.com", "Liponkuja 15")
    val categ = Category("sport")
    val notif = Notification("bike", User1, 10.0, "new bike", mutable.Buffer(categ))
    User1.makeNotification("bike", 10.0, "new bike", mutable.Buffer(categ)) shouldEqual(notif)
 }
