package Classes

import io.circe.Error
import io.circe.parser.{decode, *}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.{Scene, control}
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.White
import scalafx.scene.text.{Font, FontWeight}

import scala.collection.mutable.ListBuffer
import io.circe.generic.auto.*
import cats.syntax.either.*
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.auto.*

import scala.io.Source
import java.util
import java.util.{Calendar, Date}
import java.time.*

object RentigAppGui extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 600
  val standardPadding = Insets.apply(5, 5, 5, 5)
  val standardSpacing = 10

  val sports = ObservableBuffer(Category("Basketball"), Category("Football"), Category("Skiing"), Category("IceHockey"), Category("Running"))
  val home = ObservableBuffer(Category("Kitchen"), Category("Decor"), Category("Kids"), Category("Outside"), Category("Other"))
  val categories = sports ++ home

  val correctChars = Array('0','1','2','3','4','5','6','7','8','9')

  var allNotifications = ListBuffer[Notification]()
  var availableNotifications = allNotifications.map( _.available == true )
  var reservedNotifications = allNotifications.map( _.available == false )

  def createNewRent(n: Notification): Unit =
    val correct = WriteToFile().readFromFile(n)
    println(correct.getOrElse("ei löytyny"))


  def start() =
    val stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    val view1 = GridPane()

    val scene1 = new Scene(parent = view1)

    def readFile: List[Notification] =
      val source = Source.fromFile("jsonFile.txt")
      val currentList = try source.mkString finally source.close()

      val currentNotifications = decode[List[Notification]](currentList) match
        case Right(notifications) => notifications
        case Left(_) => List.empty[Notification]
      currentNotifications


    //Dividing screen to 2 Vboxes
    val rightBox = VBox()
    val leftBox = VBox()
    val hSeparator = VBox()

    view1.add(leftBox, 0, 0, 1, 2)
    view1.add(rightBox, 2, 0, 1, 2)
    view1.add(hSeparator, 1, 0, 1, 2)

    val column0 = new ColumnConstraints:
      percentWidth = 19.75
    val column1 = new ColumnConstraints:
      percentWidth = 0.5
    val column2 = new ColumnConstraints:
      percentWidth = 79.75
    val row0 = new RowConstraints:
      percentHeight = 15
    val row1 = new RowConstraints:
      percentHeight = 85

    view1.columnConstraints = Array(column0, column1, column2)
    view1.rowConstraints = Array(row0, row1)

    // filling colors for background
    leftBox.background = Background.fill(Color.LightCoral)
    rightBox.background = Background.fill(Color.White)
    hSeparator.background = Background.fill(Color.White)

    val separator = new Separator
    separator.orientation = Orientation.Vertical
    hSeparator.children = separator

    hSeparator.padding = standardPadding
    hSeparator.spacing = standardSpacing

    //LeftBox adjusting and children:
    leftBox.padding = standardPadding
    leftBox.spacing = standardSpacing
    leftBox.setAlignment(Pos.BaselineCenter)

    val rightTitle = new Label("Click products to see more"):
      font = new Font(25)

    val productsTitle = new Label("Products"):
      font = new Font(25)

    val allProducts = new Button("All products"):
      font = new Font(10)
      //onAction = (event) =>

    val availableBut = new Button("Available"):
      font = new Font(10)
     // onAction = (event) =>
    val reservedBut = new Button("Reserved"):
      font = new Font(10)
     // onAction = (event) =>
    val newNotificationLabel = new Label("Add new notification")

    val addNotification = new Button("Add")

    /*
    // add from jsonFile.txt all notifications to starting screen
    val notifs = readFile
    val rentDatas = readFile.map( n => MakeRent(n) )

    val notifsButtons = notifs.map( n => NotificationPanel(n).button )
    val cancelButtons = rentDatas.map( r => r.cancelBut )
    val rentButtons = rentDatas.map( r => r.rentBut )

    val notifsWithButtons = notifs.zip(notifsButtons)
    val rentsWithCButtons = rentDatas.zip(cancelButtons)
    val rentsWithRButtons = rentDatas.zip(rentButtons)

    notifsWithButtons.foreach( (n,b) => b.onAction = (event) => scene1.root = MakeRent(n))
    cancelButtons.foreach( _.onAction = (event) => scene1.root = view1 )
    rentsWithRButtons.foreach( (r,b) => b.onAction = (event) => createNewRent(r.notification) )

    rightBox.children = Array(rightTitle, new Separator) ++ notifsButtons

     */


    //Creating notification page
    val view2 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    //InformationBox of product for creating new notification
    val header = new HBox():
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)

    val headerLabel = new Label("Please fill the fields below regarding your product")
    headerLabel.font = new Font(15)
    header.children = headerLabel

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

    val quantityLabel = new Label("Quantity:")
    val quantityTxt = new TextField():
      promptText = "1,2..."
      prefWidth = 50

    val quantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3
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

    // users informations when creating a new notification
    val nameLabel = new Label("Your name:")
    val createrName = new TextField():
      promptText = "Name"

    val addressLabel = new Label("Address:")
    val createrAddress = new TextField():
      promptText = "Address"

    val phoneLabel = new Label("Phone number:")
    val createrPhone = new TextField()
    createrPhone.promptText = "Phone nro"

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

    val view3 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    def clearView2: Unit =
      titleTxtField.text = ""
      priceTxtFieldDay.text = ""
      priceTxtFieldhour.text = ""
      descTxtArea.text = ""
      category.value = categories.head
      quantityTxt.text = ""
      createrName.text = ""
      createrPhone.text = ""
      createrAddress.text = ""

    def createNewNotification(): Unit =
      // product info
      val ptitle = titleTxtField.text.value
      val dayPrice = priceTxtFieldDay.text.value
      val hourPrice = priceTxtFieldhour.text.value
      val desc = descTxtArea.text.value
      // user info
      val name = createrName.text.value
      val address =  createrAddress.text.value
      val phone =  createrPhone.text.value
      //checking incorrect inputs
      val missingValues: Boolean = ptitle == "" || dayPrice == "" || hourPrice == "" || desc == "" || name == "" || address == "" || phone == ""
      var inCorrectValues: Boolean = false
      for i <- 0 until dayPrice.length do
        if !(correctChars.contains(dayPrice(i))) then
          inCorrectValues = true
      for i <- 0 until hourPrice.length do
        if !(correctChars.contains(hourPrice(i))) then
          inCorrectValues = true
      for i <- 0 until phone.length do
        if !(correctChars.contains(phone(i))) then
          inCorrectValues = true

      if missingValues then
        val missingAlert = new Alert(AlertType.Error):
          title = "Missing Information"
          headerText = "Remember to fill all information"
          contentText = "You haven't filled all fields"
          showAndWait()
      else if inCorrectValues then
        val inCorrectAlert = new Alert(AlertType.Error):
          title = "Incorrect Information"
          headerText = "Please fill informations correct"
          contentText = "You have input characters to fields in which you shouldn't"
          showAndWait()
      else
        val creator = User(name, address, phone)
        val notif = creator.makeNotification(ptitle, dayPrice.toDouble, hourPrice.toDouble, desc, category.value.value, true)
        val newPanel = NotificationPanel(notif)
        val rentData = MakeRent(notif)

        allNotifications = (allNotifications :+ notif)
        WriteToFile().writeToFile(notif)
        rightBox.children += newPanel.button
        newPanel.button.onAction = (event) => scene1.root = rentData
        rentData.cancelBut.onAction = (event) => scene1.root = view1
        rentData.rentButton.onAction = (event) => createNewRent(notif)

        scene1.root = view1
        clearView2
    end createNewNotification

   
    val submitButton = new Button("Submit")
    submitButton.font = Font("System", FontWeight.Bold, 15)
    submitButton.onAction = (event) =>
      createNewNotification()

    val cancelButton = new Button("Cancel")
    cancelButton.onAction = (event) => scene1.root = view1

    val submitBox = new HBox():
      spacing = standardSpacing * 65
      children = Array(cancelButton, submitButton)


    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification)

    view2.children = Array(header, titleBox, descriptionBox, quantityBox, priceBox, categoryBox, new Separator, createrBoxHeader, createrBox, submitBox)

    addNotification.onAction = (event) => scene1.root = view2


    //RightBox adjusting and children:
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val middleTitle = new Label("Categories"):
      font = new Font(25)


    //rightBox adjusting and children
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    
    rightBox.children = Array(rightTitle, new Separator)

    stage.scene = scene1