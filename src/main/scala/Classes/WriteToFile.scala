package Classes
import io.circe.generic.auto.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import scala.io.Source
import java.io.{File, PrintWriter}

class WriteToFile:

  def writeToFile(notification: Notification): Unit = 
    val source = Source.fromFile("jsonFile.txt")
    val currentList = try source.mkString finally source.close()

    val currentNotifications = decode[List[Notification]](currentList) match 
      case Right(notifications) => notifications
      case Left(_) => List.empty[Notification]

    val newNotifications = currentNotifications :+ notification
    val newList = newNotifications.asJson.spaces2

    val writer = new PrintWriter(new File("jsonFile.txt"))
    writer.write(newList)
    writer.close()

  def readFromFile(n: Notification): Option[Notification] = 
    val source = Source.fromFile("jsonFile.txt")
    try 
      val currentList = source.mkString
      val allNotifications = decode[List[Notification]](currentList) match 
        case Right(list) => list
        case Left(_) => List.empty[Notification]

      allNotifications.find( c => c==n)//c.name == n.name && c.publisher == n.publisher && c.pricePerDay == n.pricePerDay
        //&& c.pricePerHour == n.pricePerHour && c.description == n.description && c.category == n.category)
    finally 
      source.close()
  
end WriteToFile