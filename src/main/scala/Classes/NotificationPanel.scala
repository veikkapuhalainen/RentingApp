package Classes

import Classes.User
import Classes.InformationBox
import Classes.RentigAppGui
import scalafx.beans.property.ObjectProperty
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*

import scala.collection.mutable.ListBuffer

class NotificationPanel(notification: Notification) extends HBox:

  val standardSpacing = 5
  val standardPadding =  Insets.apply(10, 10, 10, 10)

  val description = notification.description
  val user = notification.publisher
  val name = notification.name
  val dayPrice = notification.pricePerDay
  val hourPrice = notification.pricePerHour
  val category = notification.category

  val notificationBox = new HBox()
  notificationBox.spacing = standardSpacing

  val seeMoreButton = new Button(name)
  seeMoreButton.onAction = (event) => RentigAppGui

  def button() =
    seeMoreButton

  children = Array(notificationBox)

  RentigAppGui.allNotifications.onChange((_, oldValue, newValue) => addNotificationPanel(newValue.last) )

  def addNotificationPanel(notification: Notification) =
    notificationBox.children += InfoPanel(notification.description)

  children = Array(new Label(s"${name}"))

class InfoPanel(description: String) extends VBox:

  val desc = new Label(description)
  desc.wrapText = true

  val seeMoreButton = new Button("See more")

  children = Array(desc, seeMoreButton)