package Classes

import scala.collection.mutable

case class User(val name: String, val address: String, val phoneNumber: String):

  val notifications = mutable.Buffer[Notification]()
  val rents = mutable.Set[Rent]()

  def makeNotification(name: String, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int): Notification =
    Notification(name, this, pricePerDay, pricePerHour, description, category, amount)

  override def toString: String = s"Name: $name, address: $address, phonenumber: $phoneNumber"
end User
