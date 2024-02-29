package Classes

import scala.collection.mutable

class User(val name: String, val phoneNumber: String, val email: String, val address: String):


  val notifications = mutable.Buffer[Notification]()
  //val rents = mutable.Buffer[Rent]()

  def makeNotification(name: String, pricePerDay: Double, description: String, categories: mutable.Buffer[Category]): Notification =
    val newNotification = Notification(name, this, pricePerDay, description, categories)
    this.notifications += newNotification
    newNotification

  //def rentProduct(): Rent = ???
  //def rentEndings(): Map[Rent->Day] = ???

  override def toString: String = s"$name, $phoneNumber, $email, $address"
end User
