package Classes

import scalafx.Includes.*
import scalafx.scene.control.Button
import javafx.util.Callback
import java.time.LocalDate
import scala.collection.mutable
import javafx.scene.control.{DateCell, DatePicker, Tooltip}

/**
 * Class representing notification
 * @param name name of this notifiaction
 * @param publisher user who made this notifiaction
 * @param pricePerDay price for one day
 * @param pricePerHour pricar for one hour
 * @param description some text of this product
 * @param category for what category is this product
 * @param amount how many of this product's are
 */
case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category, amount: Int):

  /**
   * Two calendars of this product to be shown when making a rent of this notification
   */
  val calendarStart: scalafx.scene.control.DatePicker = new DatePicker()
  val calendarEnd: scalafx.scene.control.DatePicker = new DatePicker()

  val comments = mutable.Buffer[String]()

  def left(rented: Int) =
    this.amount - rented

  def rentedDaysAndAmount: Unit =
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val quantitys  = rents.map( _.amount )
    val startWendWQúant = startDays.zip(endDays).zip(quantitys)

  /**
   * Counts every day between given days
   * @param s start day
   * @param e end day
   * @return buffer of days between s and e
   */
  def countDays(s: LocalDate, e: LocalDate): mutable.Buffer[LocalDate] =
    val count = java.time.temporal.ChronoUnit.DAYS.between(s,e)
    (0L to count).map(s.plusDays).toBuffer

  /**
   * Gives days between every this notifications rents start and end day
   * @return buffer of these middle of rent days
   */
  def daysBetweenSandE: mutable.Buffer[LocalDate] =
    //reserved days but not including start and end days
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startWend = startDays.zip(endDays)
    startWend.flatMap( i => countDays(i._1, i._2) ).filterNot( d => startDays.contains(d) || endDays.contains(d) )

  /**
   * Gives all reserved days of this product including start and end days
   * @return buffer of all reserved days
   */
  def allReservedDays: mutable.Buffer[LocalDate] =
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map( _.startDay).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startWend = startDays.zip(endDays)
    startWend.flatMap( i => countDays(i._1, i._2) )

  /**
   * Brings together this notifications rents' start days, end days, name, start hour and end hour.
   * This way its easy to show in calendar start and end times of this notifs rents
   * @return buffer
   */
  def schedule =
    val rents = WriteToFile().readRentsFromFile.filter( _.notification == this )
    val startDays = rents.map(  r => (r.startDay, r.amount) ).toBuffer
    val endDays = rents.map( _.endDay).toBuffer
    val startHours = rents.map( _.startHour).toBuffer
    val endHours = rents.map( _.endHour).toBuffer
    val days = startDays.zip(endDays)
    val hours = startHours.zip(endHours)
    days.zip(hours)

  /**
   * Modifies given calendar days (cells) to pink if its rented for that day and shows message for
   * detailed information of particular rent start and end times
   * @param calendar calendar that will be modified
   */
  def setCells(calendar: DatePicker): Unit =
    calendar.setDayCellFactory(new Callback[DatePicker, DateCell] {
      override def call(cal: DatePicker): DateCell = new DateCell() {
        override def updateItem(date: LocalDate, empty: Boolean): Unit =
          super.updateItem(date, empty)
          if (!empty && date != null) then
            val rentedPeriods = schedule.filter(d => d._1._1._1.isEqual(date) || d._1._2.isEqual(date))
            if (rentedPeriods.nonEmpty) then
              val tooltipText = rentedPeriods.map( (i,j) =>
                if (i._1._1 == i._2) then s"${i._1._2} X $name is rented for: ${j._1}-${j._2}"
                else if (i._1._1 == date) then s"${i._1._2} X $name is rented from: ${j._1} for the rest of the day"
                else if (i._2 == date) then s"${i._1._2} X $name is rented until: ${j._2}"
                else s"${i._1._2} X $name is rented for the whole day"
              ).mkString("\n")

              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip(tooltipText))
            else if (daysBetweenSandE.contains(date)) then
              setStyle("-fx-background-color: pink")
              setTooltip(new Tooltip(s"$name is rented for the whole day"))
            else
              setTooltip(new Tooltip(s"$name is available"))
          else
            setTooltip(new Tooltip(s"$name is available"))
    }
  })

  /**
   * modifie this notifications calendars
   */
  setCells(calendarStart)
  setCells(calendarEnd)

  /**
   * Button to be shown at starting page
   */
  val seeMoreButton = new Button(s"$name ${"\n"}Price/day: ${pricePerDay.toString}€, Price/hour: ${pricePerHour.toString}€")
  seeMoreButton.minHeight = 60
  seeMoreButton.minWidth = 250
  seeMoreButton.maxWidth = 250
  seeMoreButton.maxHeight = 60

  def button = seeMoreButton

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification