package Classes

import Classes.User
import scalafx.application.JFXApp3
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.*
import scalafx.scene.effect.BlendMode.{Green, Red}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.White
import scalafx.scene.text.FontWeight.Black
import scalafx.scene.text.{Font, FontWeight}

object CreateNotification extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 600
  val standardPadding = Insets.apply(10, 10, 10, 10)
  val standardSpacing = 10

  val sports = ObservableBuffer("Basketball", "Football", "Skiing", "IceHockey", "Running")
  val home = ObservableBuffer("Kitchen", "Decor", "Kids", "Outside", "Other")
  val categories = sports ++ home


  def start() =
    stage = new JFXApp3.PrimaryStage:
      title = "Create a new notification"
      width = UIWidth
      height = UIHeight
      resizable = false

    val root = new VBox():
      padding = standardPadding
      spacing = standardSpacing

      val header = new HBox():
        padding = standardPadding
        this.setAlignment(Pos.BaselineCenter)

        val headerLabel = new Label("Please fill the fields below regarding your product"):
          font = new Font(15)
        children = headerLabel

      val informationBox = new VBox():
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


      //children of root
      children = Array(header, informationBox)


    val scene = Scene(parent = root)
    stage.scene = scene