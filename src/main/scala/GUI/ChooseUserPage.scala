package GUI
package Classes

import scalafx.application.JFXApp3
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, ChoiceBox, Label, Separator, TextField}
import scalafx.scene.effect.BlendMode.{Green, Red}
import scalafx.scene.layout.{Background, ColumnConstraints, GridPane, HBox, Pane, RowConstraints, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.White
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.text.FontWeight.Black

object ChooseUserPage extends JFXApp3:

  val UIWidth = 400
  val UIHeight = 280
  val standardPadding = Insets.apply(10, 10, 10, 10)
  val standardSpacing = 10


  def start() =
    stage = new JFXApp3.PrimaryStage:
      title = "Make an Account"
      width = UIWidth
      height = UIHeight
      resizable = false

    def changeWindow() =
      ???

    val root = new VBox():
      padding = standardPadding
      spacing = standardSpacing

      def createUser(): Unit = ???

      val header = new HBox():
        this.setAlignment(Pos.BaselineCenter)
        val headerLabel = new Label("Please fill the fields below with your information")
        children = headerLabel

      val accountName = new HBox():
        spacing = standardSpacing

        val nameLabel = new Label("Account name:")
        val accName = new TextField():
          promptText = "Your name"

        children = Array(nameLabel, accName)

      val accountPhone = new HBox():
        spacing = standardSpacing

        val phoneLabel = new Label("Phonenumber:")
        val accPhone = new TextField():
          promptText = "Your phonenumber"
        children = Array(phoneLabel, accPhone)

      val accountEmail = new HBox():
        spacing = standardSpacing * 5.7

        val emailLabel = new Label("Email:")
        val accEmail = new TextField():
          promptText = "Your email"
        children = Array(emailLabel, accEmail)

      val accountAddress = new HBox():
        spacing = standardSpacing * 4.35

        val addressLabel = new Label("Address:")
        val accAddress = new TextField():
          promptText = "Your address"
        children = Array(addressLabel, accAddress)

      val chooseButtons = new HBox():
        padding = standardPadding
        spacing = standardSpacing * 2
        this.setAlignment(Pos.BottomRight)

        val adminButton = new Button("I'm admin")
        val accButton = new Button("Create Account"):
          onAction = (event) => changeWindow()
        children = Array(adminButton, accButton)

      children = Array(header, accountName, accountPhone, accountEmail, accountAddress, chooseButtons)

    val scene = Scene(parent = root)
      stage.scene = scene