package GUI
package Classes

import scalafx.application.JFXApp3
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, ChoiceBox, Label, Separator}
import scalafx.scene.effect.BlendMode.{Green, Red}
import scalafx.scene.layout.{Background, ColumnConstraints, GridPane, HBox, Pane, RowConstraints, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.White
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.text.FontWeight.Black

object RentigAppGui extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 600
  val standardPadding = Insets.apply(10, 10, 10, 10)
  val standardSpacing = 10

  val sports = ObservableBuffer("Basketball", "Football", "Skiing", "IceHockey", "Running")
  val home = ObservableBuffer("Kitchen", "Decor", "Kids", "Outside", "Other")

  def start() =
    stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    val root = GridPane()

    //Dividing screen to 3 Vboxes
    val middleBox = VBox()
    val leftBox = VBox()
    val rightBox = VBox()
    root.add(leftBox, 0, 0, 1, 2)
    root.add(middleBox, 1, 1, 1, 2)
    root.add(rightBox, 2, 0, 1, 2)

    val column0 = new ColumnConstraints:
      percentWidth = 40
    val column1 = new ColumnConstraints:
      percentWidth = 20
    val column2 = new ColumnConstraints:
      percentWidth = 40
    val row0 = new RowConstraints:
      percentHeight = 15
    val row1 = new RowConstraints:
      percentHeight = 85

    root.columnConstraints = Array(column0, column1, column2)
    root.rowConstraints = Array(row0, row1)

    // filling colors for testing purpose
    leftBox.background = Background.fill(Color.White)
    middleBox.background = Background.fill(Color.LightBlue)
    rightBox.background = Background.fill(Color.LightGreen)


    //LeftBox adjusting and children:
    leftBox.padding = standardPadding
    leftBox.spacing = standardSpacing
    leftBox.setAlignment(Pos.BaselineCenter)

    val leftTitle = new Label("For Rent"):
      font = new Font(30)

    //Buttons for sorting the notifications
   /* val sortedByPublication = new Button("By publication")
    val sortedByCategory = new ChoiceBox(categories):
      value = categories(0)*/

    //notifications downloaded from files
    val notifications = Array("**Notifications**")

    leftBox.children = Array(leftTitle)

    //MiddleBox adjusting and children:
    middleBox.padding = standardPadding
    middleBox.spacing = standardSpacing
    middleBox.setAlignment(Pos.BaselineCenter)
    middleBox.visible

    val middleTitle = new Label("Categories"):
      font = new Font(25)

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

    middleBox.children = Array(middleTitle, categorySportBox, categoryHomeBox)

    //rightBox adjusting and children
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val rightTitle = new Label("Your rents"):
      font = new Font(30)

    rightBox.children = Array(rightTitle)

    val scene = Scene(parent = root)
    stage.scene = scene

  end start
end RentigAppGui