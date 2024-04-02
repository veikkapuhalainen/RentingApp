package Classes
import io.circe.generic.auto.*
import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import scala.io.Source

import java.io.{BufferedWriter, File, FileWriter}

class WriteToFile:

  def writeToFile(n: Notification) =

    val notif = n
    val notifinJson = notif.asJson

    val notifJsonString = notifinJson.spaces2

    val file = new File("jsonFile.txt")
    val writer = new BufferedWriter(new FileWriter(file, true))
    writer.write(notifJsonString + "\n" + "\n")
    writer.close()


  def readFromFile(notificationName: String): Option[Notification] =
    val source = Source.fromFile("jsonFile.txt")
    val jsonString =
      try
        source.mkString
      catch
        case error: Error => throw Exception().getCause
      end try
      source.close()

    val notifications: Array[Notification] =
      decode[Array[Notification]](jsonString.asJson.spaces2) match
        case Right(value) => value
        case Left(error) => throw error

    notifications.find(_.name == notificationName)

end WriteToFile