package Classes

import scala.collection.mutable

class User(val name: String, val address: String, val phoneNumber: String):


  val notifications = mutable.Buffer[Notification]()
  val rents = mutable.Buffer[Rent]()

  def makeNotification(name: String, pricePerDay: Double, pricePerHour: Double, description: String, category: Category): Notification =
    Notification(name, this, pricePerDay, pricePerHour, description, category)

  def rentProduct(): Rent = ???
  //def rentEndings(): Map[Rent->Day] = ???

  override def toString: String = s"$name, $address, $phoneNumber"
end User
