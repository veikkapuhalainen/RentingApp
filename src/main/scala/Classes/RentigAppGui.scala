package Classes

import Classes.User
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
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

object RentigAppGui extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 600
  val standardPadding = Insets.apply(10, 10, 10, 10)
  val standardSpacing = 15

  val sports = ObservableBuffer("Basketball", "Football", "Skiing", "IceHockey", "Running")
  val home = ObservableBuffer("Kitchen", "Decor", "Kids", "Outside", "Other")
  val categories = sports ++ home
  
  def start() =
    val stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    val root = GridPane()

    //Dividing screen to 3 Vboxes
    val rightBox = VBox()
    val leftBox = VBox()
    root.add(leftBox, 0, 0, 1, 2)
    root.add(rightBox, 1, 0, 1, 2)

    val column0 = new ColumnConstraints:
      percentWidth = 20
    val column1 = new ColumnConstraints:
      percentWidth = 80
    val row0 = new RowConstraints:
      percentHeight = 15
    val row1 = new RowConstraints:
      percentHeight = 85

    root.columnConstraints = Array(column0, column1)
    root.rowConstraints = Array(row0, row1)

    // filling colors for testing purpose
    leftBox.background = Background.fill(Color.White)
    rightBox.background = Background.fill(Color.LightBlue)


    //LeftBox adjusting and children:
    leftBox.padding = standardPadding
    leftBox.spacing = standardSpacing
    leftBox.setAlignment(Pos.BaselineCenter)

    val productsTitle = new Label("Products"):
      font = new Font(25)
    val allProducts = new Button("All products"):
      font = new Font(10)
    val availableBut = new Button("Available"):
      font = new Font(10)
    val reservedBut = new Button("Reserved"):
      font = new Font(10)
    val newNotificationLabel = new Label("Add new notification")
    val addNotification = new Button("Add")
      //onAction = (event) => *changes page to createnotificationpage

    //Buttons for sorting the notifications
   /* val sortedByPublication = new Button("By publication")
    val sortedByCategory = new ChoiceBox(categories):
      value = categories(0)*/

    //notifications downloaded from files
    val notifications = Array("**Notifications**")

    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification)

    //MiddleBox adjusting and children:
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val middleTitle = new Label("Categories"):
      font = new Font(25)
/*
    val categorySportBox = new HBox():
      spacing = standardSpacing

      val sportLabel = new Label("Sports:")
      val sportCategories = new ChoiceBox(sports):
        value = sports(0)
      children = Array(sportLabel, sportCategories)

    val categoryHomeBox = new HBox():
      spacing = standardSpacing

      val homeLabel = new Label("Home:")
      val homeCategories = new ChoiceBox(home):
        value = home(0)
      children = Array(homeLabel, homeCategories)

 */

    rightBox.children = Array(middleTitle) //, categorySportBox, categoryHomeBox)


    //rightBox adjusting and children
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val rightTitle = new Label("Click products you are interested in"):
      font = new Font(25)

    rightBox.children = Array(rightTitle)

    val scene = new Scene(parent = root)
    stage.scene = scene


  end start