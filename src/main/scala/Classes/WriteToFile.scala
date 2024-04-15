package Classes
import io.circe.generic.auto.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import scala.io.Source
import java.io.{File, PrintWriter}

/**
 * Class which has every method when reading something from files
 */
class WriteToFile:

  /**
   * Writes a given notification to "jsonFileNotif.txt". First reads existing notifications and decodes them to List,
   * then adds this to that list and finally writes it back to file.
   * @param notification Notification to be writed to file
   */
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
  end writeNotifToFile
  

  /**
   * Same as previous method but for Rent
   * @param rent rent to be writed to file
   */
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
  end writeRentToFile
  
  /**
   * Deletes given notification from file. Works otherwise same as WriteNotifToFile
   * @param notification notification to be deleted from files
   */
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
  end deleteNotification
  
  /**
   * Same as previous but for rents
   * @param rent rent to be deleted from files
   */
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
  end deleteRent
  
  /**
   * Reads all existing rents and returns them in a list
   * @return List of existing rents
   */
  def readRentsFromFile: List[Rent] =
    val source = Source.fromFile("jsonFileRent.txt")
    val currentList = try source.mkString finally source.close()

    val currentRents = decode[List[Rent]](currentList) match
      case Right(rents) => rents
      case Left(_) => List.empty[Rent]
    currentRents
  end readRentsFromFile 

end WriteToFile