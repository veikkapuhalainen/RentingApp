package Classes

//import javafx.scene.control.{DateCell, DatePicker}
import scalafx.Includes._
import scalafx.scene.control.{Button, DateCell, DatePicker}
//import javafx.util.Callback
import java.time.{LocalDate, Period}
import scala.collection.mutable

case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int, var available: Boolean):

  val reservedDates = mutable.Buffer[Period]()
  val datePicker = new DatePicker()
  
/*
  // Set the day cell factory to customize cell appearance
  datePicker.setDayCellFactory(new Callback[DatePicker, DateCell] {
    override def call(param: DatePicker): DateCell = new DateCell() {
      override def updateItem(item: LocalDate, empty: Boolean): Unit = {
        super.updateItem(item, empty)

        // Check if the item (date) is within the rent period
        if (!empty && item != null && isWithinRentPeriod(item)) {
          // Set the background color of the cell to indicate it's within the rent period
          style = ("-fx-background-color: pink")
        }
      }

      // Function to check if a date is within the rent period
      private def isWithinRentPeriod(date: LocalDate): Boolean = {
        reservedDates.contains(date)
      }
    }
  })
  
 */
/*
  datePicker.setDayCellFactory(new Callback[DatePicker, DateCell] {
    override def call(param: javafx.scene.control.DatePicker): javafx.scene.control.DateCell = new javafx.scene.control.DateCell() {
      override def updateItem(item: LocalDate, empty: Boolean): Unit = {
        super.updateItem(item, empty)

        if (!empty && item != null) {
          if (reservedDates.contains(item)) {
            setStyle("-fx-background-color: pink")
          } else {
            setStyle("-fx-background-color: green")
          }
        }
      }
    }
  })

 */

  val damages = mutable.Buffer[String]()
  var comments = mutable.Buffer[String]()


  val seeMoreButton = new Button(s"$name ${"\n"}Price/day: ${pricePerDay.toString}€, Price/hour: ${pricePerHour.toString}€")
  seeMoreButton.minHeight = 50
  seeMoreButton.minWidth = 70

  val addToPackageButton = new Button(s"$name ${"\n"}Price/day: ${pricePerDay.toString}€, Price/hour: ${pricePerHour.toString}€")
  addToPackageButton.minHeight = 50
  addToPackageButton.minWidth = 70
  
  def packageButton = addToPackageButton
  def button = seeMoreButton

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification
