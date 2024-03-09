package Classes

import Classes.InformationBox
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

  val sports = ObservableBuffer(Category("Basketball"), Category("Football"), Category("Skiing"), Category("IceHockey"), Category("Running"))
  val home = ObservableBuffer(Category("Kitchen"), Category("Decor"), Category("Kids"), Category("Outside"), Category("Other"))
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

      children = Array(header, InformationBox(UIWidth, UIHeight))

    val scene = Scene(parent = root)
    stage.scene = scene