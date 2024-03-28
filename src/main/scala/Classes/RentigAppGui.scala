package Classes

import Classes.User
import Classes.InformationBox
import Classes.NotificationPanel
import io.circe.Error
import io.circe.parser.decode
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.collections.{ObservableBuffer, ObservableBufferBase}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.effect.BlendMode.{Green, Red}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.White
import scalafx.scene.text.FontWeight.Black
import scalafx.scene.text.{Font, FontWeight}

import scala.collection.mutable.ListBuffer
import io.circe.generic.auto.*
import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import scala.io.Source
import java.io.{BufferedWriter, File, FileWriter}

object RentigAppGui extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 600
  val standardPadding = Insets.apply(5, 5, 5, 5)
  val standardSpacing = 10

  val sports = ObservableBuffer(Category("Basketball"), Category("Football"), Category("Skiing"), Category("IceHockey"), Category("Running"))
  val home = ObservableBuffer(Category("Kitchen"), Category("Decor"), Category("Kids"), Category("Outside"), Category("Other"))
  val categories = sports ++ home

  val correctChars = Array('0','1','2','3','4','5','6','7','8','9')//('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö')

  var allNotifications = ObjectProperty(ListBuffer[Notification]())
  var availableNotifications = ObjectProperty(ListBuffer[Notification]())
  var reservedNotifications = ObjectProperty(ListBuffer[Notification]())

  def start() =
    val stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    val view1 = GridPane()

    val scene1 = new Scene(parent = view1)


    //Dividing screen to 2 Vboxes
    val rightBox = VBox()
    val leftBox = VBox()
    view1.add(leftBox, 0, 0, 1, 2)
    view1.add(rightBox, 1, 0, 1, 2)

    val column0 = new ColumnConstraints:
      percentWidth = 20
    val column1 = new ColumnConstraints:
      percentWidth = 80
    val row0 = new RowConstraints:
      percentHeight = 15
    val row1 = new RowConstraints:
      percentHeight = 85

    view1.columnConstraints = Array(column0, column1)
    view1.rowConstraints = Array(row0, row1)

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
      //onAction = (event) => *show all products*
    val availableBut = new Button("Available"):
      font = new Font(10)
      //onAction = (event) => *show currently available products*
    val reservedBut = new Button("Reserved"):
      font = new Font(10)
      //onAction = (event) => *show currently reserved products*
    val newNotificationLabel = new Label("Add new notification")

    val addNotification = new Button("Add")

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
/*
    val durationLabel = new Label("Set max duration (days) for renting this product:")
    val maxDuration = new TextField():
      promptText = "max"
      prefWidth = 50

    val durationBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(durationLabel, maxDuration)

 */
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

    val seeMoreButton = new Button("See more")


    val newScene = new Scene(parent = view3)
    seeMoreButton.onAction = (event) => RentigAppGui.stage.scene = newScene

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
        val notif = creator.makeNotification(ptitle, dayPrice.toDouble, hourPrice.toDouble, desc, category.value.value)

        allNotifications.setValue(allNotifications.value :+ notif)
        WriteToFile(notif)
        rightBox.children += NotificationPanel(notif).button()
        scene1.root = view1
    end createNewNotification

   /*
    def notificationClick(notification: Notification) =
      val alert = new Alert(AlertType.Information):
        title = "Product's information"
        headerText = notification.name
        contentText = notification.description
      alert.showAndWait()
    */
   
    val submitButton = new Button("Submit")
    submitButton.font = Font("System", FontWeight.Bold, 15)
    submitButton.onAction = (event) =>
      createNewNotification()

    val cancelButton = new Button("Cancel")
    cancelButton.onAction = (event) => scene1.root = view1

    val submitBox = new HBox():
      spacing = standardSpacing * 65
      children = Array(cancelButton, submitButton)

    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification, seeMoreButton)

    view2.children = Array(header, titleBox, descriptionBox, quantityBox, priceBox, categoryBox, new Separator, createrBoxHeader, createrBox, submitBox)

    addNotification.onAction = (event) => scene1.root = view2


    //RightBox adjusting and children:
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val middleTitle = new Label("Categories"):
      font = new Font(25)

    rightBox.children = Array(middleTitle)


    //rightBox adjusting and children
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)

    val rightTitle = new Label("Click 'see more' button to see more about product"):
      font = new Font(25)
    
    rightBox.children = Array(rightTitle, new Separator)

    stage.scene = scene1


    //Information about product that may be rented
    val rentHeader = new HBox():
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)

    val rentHeaderLabel = new Label("Information of the product and the renter"):
      font = new Font(15)
    rentHeader.children = rentHeaderLabel

    val rentTitleLabel = new Label("Title:")
    val productName = new Label(s"name")//${notification.name}")

    val rentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing * 5
      children = Array(rentTitleLabel, productName)

    val rentDescLabel = new Label("Description:")
    val desc = new Label(s"desc")//${notification.description}")

    val rentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(rentDescLabel, desc)

    val rentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

      val rentQuantityLabel = new Label("Quantity:")
      val rentQuantity = new Label("*quantity*")

      children = Array(rentQuantityLabel, rentQuantity)

    val rentPriceLabelDay = new Label("Price per day:")
    val priceDay = new Label(s"dayprice")//${notification.pricePerDay}")

    val rentPriceLabelHour = new Label("Price per hour:")
    val priceHour = new Label("hourprice")//s"${notification.pricePerHour}")

    val rentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(rentPriceLabelDay, priceDay, rentPriceLabelHour, priceHour)


    val rentCategoryLabel = new Label("Category:")
    val rentCategory = new Label("category")//s"${notification.category}")

    val rentCategoryBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(rentCategoryLabel, rentCategory)

    // users informations when creating a new notification
    val renternameLabel = new Label("Renter name:")
    val renterName = new Label("rentername")//s"${user.name}")

    val renteraddressLabel = new Label("Renter address:")
    val renterAddress = new Label("renter address")//s"${user.address}")

    val renterphoneLabel = new Label("Phone number:")
    val renterPhone = new Label("renter phone")//s"${user.phoneNumber}")

    val renterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing

      children = Array(renternameLabel, renterName, renteraddressLabel, renterAddress, renterphoneLabel, renterPhone)

    val renterBoxHeader = new HBox():
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)

      val header = new Label("Renters contact information"):
        font = new Font(15)
      children = header

    val rentButton = new Button("Make a rent")
    rentButton.font = Font("System", FontWeight.Bold, 15)
  //  rentButton.onAction = (event) => *make a rent*

    val rentCancelButton = new Button("Cancel")
    rentCancelButton.onAction = (event) => scene1.root = view1

    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 60
      children = Array(rentCancelButton, rentButton)

    view3.children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterBoxHeader, renterBox, new Separator, rentButtonBox)