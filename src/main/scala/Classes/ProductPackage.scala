package Classes
import scalafx.Includes.*
import javafx.util.Callback
import java.time.LocalDate
import scala.collection.mutable
import javafx.scene.control.{DateCell, DatePicker, Tooltip}

/**
 * Class for productpackages, basically same methods that notifications have but for calculating every this packages
 * rented days and modifying calendar based on those days and times
 * See class notifiaction for more details about methods
 * @param notifications notifications in this package
 */
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
    val startDays = rents.map(  r => (r.startDay, r.notification.name, r.amount) ).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startHours = rents.map( _.startHour).toBuffer
    val endHours = rents.map( _.endHour).toBuffer
    val days = startDays.zip(endDays)
    val hours = startHours.zip(endHours)
    days.zip(hours)

  def setCells(calendar: DatePicker): Unit =
    calendar.setDayCellFactory(new Callback[DatePicker, DateCell] {
      override def call(cal: DatePicker): DateCell = new DateCell() {
        override def updateItem(date: LocalDate, empty: Boolean): Unit =
          super.updateItem(date, empty)
          if (!empty && date != null) then
            val rentedPeriods = schedule.filter(d => d._1._1._1.isEqual(date) || d._1._2.isEqual(date))
            if (rentedPeriods.nonEmpty) then
              val tooltipText = rentedPeriods.map((i,j) =>
                if (i._1._1 == i._2) s"${i._1._3}x ${i._1._2} is rented for: ${j._1}-${j._2}"
                else if (i._1._1 == date) s"${i._1._3}x ${i._1._2} is rented from: ${j._1} for the rest of the day"
                else if (i._2 == date) s"${i._1._3}x ${i._1._2} is rented until: ${j._2}"
                else s"${i._1._3}x ${i._1._2} is rented for the whole day"
              ).mkString("\n")
              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip(tooltipText))
            else if (daysBetweenSandE.contains(date)) then
              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip("One of this package's item is rented for the whole day"))
            else
              setTooltip(new Tooltip(s"All available"))
          else
            setTooltip(new Tooltip(s"All available"))
    }
  })

  //Add reserved days to calendars
  setCells(combinedCalendarStart)
  setCells(combinedCalendarEnd)
end ProductPackage