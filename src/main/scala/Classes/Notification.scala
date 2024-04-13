package Classes

import scalafx.Includes.*
import scalafx.scene.control.Button
import javafx.util.Callback

import java.time.{LocalDate, Period}
import scala.collection.mutable
import com.calendarfx.view.CalendarView
import javafx.collections.{FXCollections, ObservableList}
import scalafx.Includes.*
import javafx.scene.control.{DateCell, DatePicker, Tooltip}
import scalafx.scene.Node

case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int, var available: Boolean):

  val startAndEndDays = mutable.Buffer[(LocalDate, LocalDate)]()

  val calendar: scalafx.scene.control.DatePicker = new DatePicker()
  val cale: Node = new CalendarView()

  def countDays(s: LocalDate, e: LocalDate): mutable.Buffer[LocalDate] =
    val count = java.time.temporal.ChronoUnit.DAYS.between(s,e)
    (0L to count).map(s.plusDays).toBuffer

  def getRents =
    WriteToFile().readRentsFromFile.filter( _.notification == this )

  def reservedDays: mutable.Buffer[LocalDate] =
    //not including start and end days
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startWend = startDays.zip(endDays)
    startWend.flatMap( i => countDays(i._1, i._2) ).filterNot( d => startDays.contains(d) || endDays.contains(d) )

  def schedule =
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startHours = rents.map( _.startHour).toBuffer
    val endHours = rents.map( _.endHour).toBuffer
    val days = startDays.zip(endDays)
    val hours = startHours.zip(endHours)
    days.zip(hours)


  calendar.setDayCellFactory(new Callback[DatePicker, DateCell] {
    override def call(param: DatePicker): DateCell = new DateCell() {
      override def updateItem(item: LocalDate, empty: Boolean): Unit = {
        super.updateItem(item, empty)

        if (!empty && item != null && ((schedule.exists( d => item.isEqual(d._1._1) && item.isEqual(d._1._2) )))) {
          val i = schedule.indexWhere( d => item.isEqual(d._1._1) && item.isEqual(d._1._2))
          setStyle("-fx-background-color: pink")
          setTooltip(new Tooltip(s"Rented for: ${schedule(i)._2._1}-${schedule(i)._2._2}"))
        }
        else if (!empty && item != null && ((schedule.exists( d => item.isEqual(d._1._1) )))) then
          val i = schedule.indexWhere( d => item.isEqual(d._1._1) )
          setStyle("-fx-background-color: pink")
          setTooltip(new Tooltip(s"Rented from: ${schedule(i)._2._1} for the whole day"))
        else if (!empty && item != null && ((schedule.exists( d => item.isEqual(d._1._2 )))) ) then
          val i = schedule.indexWhere( d => item.isEqual(d._1._2) )
          setStyle("-fx-background-color: pink")
          setTooltip(new Tooltip(s"Rented until: ${schedule(i)._2._2}"))
        else if (!empty && item != null && reservedDays.contains(item) ) then
          setStyle("-fx-background-color: pink")
          setTooltip(new Tooltip("Rented for the whole day"))
        else
          setTooltip(new Tooltip("Available"))
      }
    }
  })


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
