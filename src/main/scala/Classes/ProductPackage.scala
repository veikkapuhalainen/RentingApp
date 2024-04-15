package Classes
import scalafx.Includes.*
import javafx.util.Callback
import java.time.LocalDate
import scala.collection.mutable
import javafx.scene.control.{DateCell, DatePicker, Tooltip}

/**
 * Class for productpackages, basically same methods that notifications have but for calculating every this packages
 * rented days and modifying calendar based on those days and times
 * @param notifications notifications in this package
 */
case class ProductPackage(notifications: mutable.Set[Notification]):

  val combinedCalendarStart: scalafx.scene.control.DatePicker = new DatePicker()
  val combinedCalendarEnd: scalafx.scene.control.DatePicker = new DatePicker()

  def left(rented: Int, n: Notification) =
    n.amount - rented

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
    val startDays = rents.map(  r => (r.startDay, r.notification.name, r.amount) ).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startHours = rents.map( _.startHour).toBuffer
    val endHours = rents.map( _.endHour).toBuffer
    val days = startDays.zip(endDays)
    val hours = startHours.zip(endHours)
    days.zip(hours)

  def setCells(calendar: DatePicker): Unit =
    calendar.setDayCellFactory(new Callback[DatePicker, DateCell] {
      override def call(param: DatePicker): DateCell = new DateCell() {
        override def updateItem(item: LocalDate, empty: Boolean): Unit =
          super.updateItem(item, empty)
          if (!empty && item != null) then
            val rentedPeriods = schedule.filter(d => d._1._1._1.isEqual(item) || d._1._2.isEqual(item))
            if (rentedPeriods.nonEmpty) then
              val tooltipText = rentedPeriods.map((i,j) =>
                if (i._1._1 == i._2) s"${i._1._3} X ${i._1._2} is rented for: ${j._1}-${j._2}"
                else if (i._1._1 == item) s"${i._1._3} X ${i._1._2} is rented from: ${j._1} for the rest of the day"
                else if (i._2 == item) s"${i._1._3} X ${i._1._2} is rented until: ${j._2}"
                else s"${i._1._3} X ${i._1._2} is rented for the whole day"
              ).mkString("\n")
              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip(tooltipText))
            else if (daysBetweenSandE.contains(item)) then
              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip(rentedPeriods.map((i,j) => s"${i._1._3} X ${i._1._2} is rented for the whole day").mkString("\n")))
            else
              setTooltip(new Tooltip(rentedPeriods.map((i,j) => s"${i._1._2} is available").mkString("\n")))
          else
            setTooltip(new Tooltip(s"All available"))
    }
  })

  setCells(combinedCalendarStart)
  setCells(combinedCalendarEnd)
end ProductPackage