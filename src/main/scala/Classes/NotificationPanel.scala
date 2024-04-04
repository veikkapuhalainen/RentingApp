package Classes

import Classes.User
import Classes.RentigAppGui
import scalafx.beans.property.ObjectProperty
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.scene.text.{Font, FontWeight}

import scala.collection.mutable.ListBuffer

class NotificationPanel(notification: Notification) extends HBox:

  val buttonWidth = 70
  val buttonHeight = 50
  val standardSpacing = 5
  val standardPadding =  Insets.apply(10, 10, 10, 10)

  val description = notification.description
  val user = notification.publisher
  val name = notification.name
  val dayPrice = notification.pricePerDay
  val hourPrice = notification.pricePerHour
  val category = notification.category

  val seeMoreButton1 = new Button(s"$name ${"\n"}Price/day: ${dayPrice.toString}€, Price/hour: ${hourPrice.toString}€")
  seeMoreButton1.minHeight = buttonHeight.toDouble
  seeMoreButton1.minWidth = buttonWidth.toDouble

  def button =
    seeMoreButton1

