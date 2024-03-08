package Classes

import Classes.CreateNotification.{categories, standardPadding, standardSpacing}
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, ChoiceBox, Label, TextArea, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.{Font, FontWeight}

class InformationBox(parentWidth: Int, parentHeight: Int) extends VBox:

  val titleBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing * 5

    val titleLabel = new Label("Title:")
    val titleTxtField = new TextField():
      promptText = "Name of your product"
    children = Array(titleLabel, titleTxtField)

  val descriptionBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing

    val descLabel = new Label("Description:")
    val descTxtArea = new TextArea():
      promptText = "Tell something about your product"
    children = Array(descLabel, descTxtArea)

  val quantityBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing*3

    val quantityLabel = new Label("Quantity:")
    val quantityTxt = new TextField():
      promptText = "1,2..."
      prefWidth = 50

    children = Array(quantityLabel, quantityTxt)

  val priceBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing

    val priceLabelDay = new Label("Price per day:")
    val priceTxtFieldDay = new TextField():
      promptText = "€"
      prefWidth = 30

    val priceLabelHour = new Label("Price per hour:")
    val priceTxtFieldhour = new TextField():
      promptText = "€"
      prefWidth = 30

    children = Array(priceLabelDay, priceTxtFieldDay, priceLabelHour, priceTxtFieldhour)

  val categoryBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing
    val categoryLabel = new Label("Choose category:")
    val category = new ChoiceBox[String](categories):
      value = "Category"

    children = Array(categoryLabel, category)

  val durationBox = new HBox():
    padding = standardPadding
    spacing = standardSpacing

    val durationLabel = new Label("Set max duration (days) for renting this product:")
    val maxDuration = new TextField():
      promptText = "MAX"
      prefWidth = 50

    children = Array(durationLabel, maxDuration)

  val submitBox = new HBox():
    this.setAlignment(Pos.BottomRight)
    padding = standardPadding
    spacing = standardSpacing

    val submitButton = new Button("Submit"):
      font = Font("System", FontWeight.Bold, 15)
      //onAction = (event) => createNewNotification(information) *created a new notification*
    children = submitButton

  //children of informationBox
  children = Array(titleBox, descriptionBox, quantityBox, priceBox, categoryBox, durationBox, submitBox)

