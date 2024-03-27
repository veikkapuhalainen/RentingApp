package Classes
import io.circe.generic.auto.*
import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import scala.io.Source

import java.io.{BufferedWriter, File, FileWriter}

class FileReader(n: Notification):

  val notif = n
  val notifinJson = notif.asJson

  val notifJsonString = notifinJson.spaces2

  val file = new File("jsonFile.txt")
  val writer = new BufferedWriter(new FileWriter(file, true))
  writer.write(notifJsonString + "\n" + "\n")
  writer.close()


end FileReader
