package Classes

import scala.collection.mutable

/**
 * Class representig user. User can either be rent maker or renter
 * @param name name of user
 * @param address address of user
 * @param phoneNumber phonenumber of user
 */
case class User(val name: String, val address: String, val phoneNumber: String):

  def makeNotification(name: String, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int): Notification =
    Notification(name, this, pricePerDay, pricePerHour, description, category, amount)

  override def toString: String = s"Name: $name, Address: $address, Phonenumber: $phoneNumber."
end User