package Classes
import io.circe.generic.auto.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

import scala.io.Source
import java.io.{File, PrintWriter}
import scala.collection.mutable

class WriteToFile:

  def writeNotifToFile(notification: Notification): Unit =
    val source = Source.fromFile("jsonFileNotif.txt")
    val currentList = try source.mkString finally source.close()

    val currentNotifications = decode[List[Notification]](currentList) match 
      case Right(notifications) => notifications
      case Left(_) => List.empty[Notification]

    val newNotifications = currentNotifications :+ notification
    val newList = newNotifications.asJson.spaces2

    val writer = new PrintWriter(new File("jsonFileNotif.txt"))
    writer.write(newList)
    writer.close()


  def writeRentToFile(rent: Rent): Unit =
    val source = Source.fromFile("jsonFileRent.txt")
    val currentList = try source.mkString finally source.close()

    val currentRents = decode[List[Rent]](currentList) match
      case Right(rents) => rents
      case Left(_) => List.empty[Rent]

    val newRents = currentRents :+ rent
    val newList = newRents.asJson.spaces2

    val writer = new PrintWriter(new File("jsonFileRent.txt"))
    writer.write(newList)
    writer.close()

  def deleteNotification(notification: Notification): Unit =
    val source = Source.fromFile("jsonFileNotif.txt")
    val currentList = try source.mkString finally source.close()

    val currentNotifications = decode[List[Notification]](currentList) match
      case Right(notifications) => notifications
      case Left(_) => List.empty[Notification]

    val updatedNotifications = currentNotifications.filterNot( _ == notification )
    val newList = updatedNotifications.asJson.spaces2

    val writer = new PrintWriter(new File("jsonFileNotif.txt"))
    writer.write(newList)
    writer.close()
    
  def deleteRent(rent: Rent): Unit =
    val source = Source.fromFile("jsonFileRent.txt")
    val currentList = try source.mkString finally source.close()

    val currentRents = decode[List[Rent]](currentList) match
      case Right(rents) => rents
      case Left(_) => List.empty[Rent]

    val updatedRents = currentRents.filterNot( _ == rent )
    val newList = updatedRents.asJson.spaces2

    val writer = new PrintWriter(new File("jsonFileRent.txt"))
    writer.write(newList)
    writer.close()


  def readRentsFromFile: List[Rent] =
      val source = Source.fromFile("jsonFileRent.txt")
      val currentList = try source.mkString finally source.close()

      val currentRents = decode[List[Rent]](currentList) match
        case Right(rents) => rents
        case Left(_) => List.empty[Rent]
      currentRents
      
  def readNotifFromFile(n: Notification): Option[Notification] =
    val source = Source.fromFile("jsonFileNotif.txt")
    try 
      val currentList = source.mkString
      val allNotifications = decode[List[Notification]](currentList) match 
        case Right(list) => list
        case Left(_) => List.empty[Notification]

      allNotifications.find( c => c==n)//c.name == n.name && c.publisher == n.publisher && c.pricePerDay == n.pricePerDay
        //&& c.pricePerHour == n.pricePerHour && c.amount == n.amount && c.description == n.description && c.category == n.category)
    finally 
      source.close()
  
end WriteToFile