package Classes

import Classes.User
import Classes.CreateNotification.{categories, standardPadding, standardSpacing}
import Classes.RentigAppGui
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, ChoiceBox, Label, TextArea, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.{Font, FontWeight}

class InformationBox(parentWidth: Int, parentHeight: Int) extends VBox:
  
  def createNewNotification() =
    val creator = User(createrName.text.value, createrAddress.text.value, createrPhone.text.value)
    val notif = creator.makeNotification(titleTxtField.text.value, priceTxtFieldDay.text.value.toDouble, priceTxtFieldhour.text.value.toDouble,
      descTxtArea.text.value, category.value.value)

    RentigAppGui.allNotifications.setValue(RentigAppGui.allNotifications.value :+ notif)
    RentigAppGui.allNotifications.value.foreach( println(_) )

  val titleLabel = new Label("Title:")
  val titleTxtField = new TextField():
    promptText = "Name of your product"

  val titleBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing * 5
    children = Array(titleLabel, titleTxtField)

  val descLabel = new Label("Description:")
  val descTxtArea = new TextArea():
    promptText = "Tell something about your product"

  val descriptionBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing
    children = Array(descLabel, descTxtArea)

  val quantityBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing*3

    val quantityLabel = new Label("Quantity:")
    val quantityTxt = new TextField():
      promptText = "1,2..."
      prefWidth = 50

    children = Array(quantityLabel, quantityTxt)

  val priceLabelDay = new Label("Price per day:")
  val priceTxtFieldDay = new TextField():
    promptText = "€"
    prefWidth = 50

  val priceLabelHour = new Label("Price per hour:")
  val priceTxtFieldhour = new TextField():
    promptText = "€"
    prefWidth = 50

  val priceBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing
    children = Array(priceLabelDay, priceTxtFieldDay, priceLabelHour, priceTxtFieldhour)


  val categoryLabel = new Label("Choose category:")
  val category = new ChoiceBox[Category](categories):
    value = categories(0)

  val categoryBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing
    children = Array(categoryLabel, category)


  val durationLabel = new Label("Set max duration (days) for renting this product:")
  val maxDuration = new TextField():
    promptText = "max"
    prefWidth = 50

  val durationBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing
    children = Array(durationLabel, maxDuration)


  val nameLabel = new Label("Your name:")
  val createrName = new TextField():
    promptText = "Name"

  val addressLabel = new Label("Address:")
  val createrAddress = new TextField():
    promptText = "Address"

  val phoneLabel = new Label("Phone number:")
  val createrPhone = new TextField():
    promptText = "Phone nro"

  val createrBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing

    children = Array(nameLabel, createrName, addressLabel, createrAddress, phoneLabel, createrPhone)

  val createrBoxHeader = new HBox():
    padding = standardPadding
    this.setAlignment(Pos.BaselineCenter)

    val header = new Label("Please fill the fields below regarding you"):
      font = new Font(15)
    children = header

  val submitBox = new HBox():
    this.setAlignment(Pos.BottomRight)
    padding = standardPadding
    spacing = standardSpacing

    val submitButton = new Button("Submit")
    submitButton.font = Font("System", FontWeight.Bold, 15)
    submitButton.onAction = (event) => createNewNotification()
     //get back to windowwwwe
    children = submitButton

  //children of informationBox
  children = Array(titleBox, descriptionBox, quantityBox, priceBox, categoryBox, durationBox, createrBoxHeader, createrBox, submitBox)

