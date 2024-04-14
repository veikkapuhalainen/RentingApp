package Classes
import scalafx.Includes.*
import javafx.util.Callback
import java.time.LocalDate
import scala.collection.mutable
import javafx.scene.control.{DateCell, DatePicker, Tooltip}

case class ProductPackage(notifications: mutable.Set[Notification]):

  val combinedCalendarStart: scalafx.scene.control.DatePicker = new DatePicker()
  val combinedCalendarEnd: scalafx.scene.control.DatePicker = new DatePicker()

  def countDays(s: LocalDate, e: LocalDate): mutable.Buffer[LocalDate] =
    val count = java.time.temporal.ChronoUnit.DAYS.between(s,e)
    (0L to count).map(s.plusDays).toBuffer

  def daysBetweenSandE: mutable.Buffer[LocalDate] =
    //reserved days but not including start and end days
    val rents = WriteToFile().readRentsFromFile.filter( r => notifications.contains(r.notification) )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startWend = startDays.zip(endDays)
    startWend.flatMap( i => countDays(i._1, i._2) ).filterNot( d => startDays.contains(d) || endDays.contains(d) )

  def allReservedDays: mutable.Buffer[LocalDate] =
    val rents = WriteToFile().readRentsFromFile.filter( r => notifications.contains(r.notification) )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startWend = startDays.zip(endDays)
    startWend.flatMap( i => countDays(i._1, i._2) )

  def schedule =
    val rents = WriteToFile().readRentsFromFile.filter( r => notifications.contains(r.notification) )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startHours = rents.map( _.startHour).toBuffer
    val endHours = rents.map( _.endHour).toBuffer
    val days = startDays.zip(endDays)
    val hours = startHours.zip(endHours)
    days.zip(hours)

  def setCells(calendar: DatePicker): Unit =
    calendar.setDayCellFactory(new Callback[DatePicker, DateCell] {
      override def call(param: DatePicker): DateCell = new DateCell() {
        override def updateItem(item: LocalDate, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          if (!empty && item != null) {
          val rentedPeriods = schedule.filter(d => d._1._1.isEqual(item) || d._1._2.isEqual(item))

          if (rentedPeriods.nonEmpty) {
            val tooltipText = rentedPeriods.map { case ((start, end), (startH, endH)) =>
              if (start == end) s"Rented for: $startH-$endH"
              else if (start == item) s"Rented from: $startH for the rest of the day"
              else if (end == item) s"Rented until: $endH"
              else "Rented for the whole day"
            }.mkString("\n")

            setStyle("-fx-background-color: pink")
            setTooltip(new Tooltip(tooltipText))
          } else if (daysBetweenSandE.contains(item)) {
            setStyle("-fx-background-color: pink")
            setTooltip(new Tooltip("Rented for the whole day"))
          } else {
            setTooltip(new Tooltip("Available"))
          }
        } else {
          setTooltip(new Tooltip("Available"))
        }
      }
    }
  })

  setCells(combinedCalendarStart)
  setCells(combinedCalendarEnd)