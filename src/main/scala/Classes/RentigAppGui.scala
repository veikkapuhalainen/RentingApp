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
import java.time.temporal.ChronoUnit
import scala.collection.mutable
import scalafx.Includes.jfxDatePicker2sfx


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
  var availableNotifications = ListBuffer[Notification]()
  var reservedNotifications = ListBuffer[Rent]()

  private val productPackage = mutable.Set[Notification]()



  def start() =
    val stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    val view1 = GridPane()

    val scene1 = new Scene(parent = view1)

    //Alerts for incorrect inputs
    val missingInformationAlert = new Alert(AlertType.Error):
      title = "Missing Information"
      headerText = "Remember to fill all information"
      contentText = "You haven't filled all fields"
    val inCorrectInputAlert = new Alert(AlertType.Error):
      title = "Incorrect Information"
      headerText = "Please fill informations correct"
    val missingDateAlert = new Alert(AlertType.Error):
      title = "Missing Date"
      headerText = "Remember to choose time for your rent"
      contentText = "You haven't chosen a date"
    val inValidDateAlert = new Alert(AlertType.Error):
      title = "Invalid Date"
      headerText = "Check dates or time you have chosen"
      contentText = "You can't make rent for this period of time"
    val noProductsAlert = new Alert(AlertType.Error):
      title = "No products"
      headerText = "You can't make an empty package"
      contentText = "Choose some products to your package by pressing them"

    def readNotifications: List[Notification] =
      val source = Source.fromFile("jsonFileNotif.txt")
      val currentList = try source.mkString finally source.close()

      val currentNotifications = decode[List[Notification]](currentList) match
        case Right(notifications) => notifications
        case Left(_) => List.empty[Notification]
      currentNotifications

    def readRents: List[Rent] =
      val source = Source.fromFile("jsonFileRent.txt")
      val currentList = try source.mkString finally source.close()

      val currentRents = decode[List[Rent]](currentList) match
        case Right(rents) => rents
        case Left(_) => List.empty[Rent]
      currentRents


    def checkRentDays(notifications: mutable.Set[Notification], startDay: LocalDate, endDay: LocalDate, startH: Int, endH: Int): Boolean =
      val now = LocalDate.now()
      val existingRents = readRents.filter( r => notifications.contains(r.notification) )
      val startDays = existingRents.map( _.startDay )
      val endDays = existingRents.map( _.endDay )
      val startHours = existingRents.map( _.startHour )
      val endHours = existingRents.map( _.endHour )
      val hours = startHours.zip(endHours)
      val startDaysWhours = startDays.zip(startHours.zip(endHours))
      val startsWends = startDays.zip(endDays)
      val overLapDays = startsWends.exists( (s,e) => (startDay.isBefore(s) && endDay.isAfter(s)) || (startDay.isAfter(s) && endDay.isBefore(e)) || (startDay.isBefore(e) && endDay.isAfter(e)) )
      val overLapHours = startsWends.zip(hours).exists( (d,h) => (d._1.isEqual(startDay) && startH < h._2) || (d._2.isEqual(endDay)) || (d._1.isEqual(startDay) && d._2.isEqual(endDay) || (!(d._1.isEqual(d._2)) && (startDay.isEqual(d._2)) )))

      if startDay == null || endDay == null then
        true
      else if startDay.isBefore(now) || endDay.isBefore(startDay) then
        true
      else if overLapDays || overLapHours then
        true
      else
        false

    def checkRentHours(notifications: mutable.Set[Notification], date: LocalDate, startHour: String, endHour: String ): Boolean =
      val dateToday = LocalDate.now()
      val hourNow = LocalTime.now().getHour

      val existingRents = readRents.filter( r => notifications.contains(r.notification) )
      val startDays = existingRents.map( _.startDay )
      val endDays = existingRents.map( _.endDay )
      val startHours = existingRents.map( _.startHour )
      val endHours = existingRents.map( _.endHour )
      val hours = startHours.zip(endHours)
      val startDaysWhours = startDays.zip(startHours.zip(endHours))
      val endDaysWhours = endDays.zip(startHours.zip(endHours))
      val startsWends = startDays.zip(endDays)
      val daysAndHours = startsWends.zip(hours)

      val overLapDays = startsWends.exists( (s,e) => (date.isAfter(s) && date.isBefore(e)) )
      val overLapHours = daysAndHours.exists( (d,h) => (d._1.isEqual(date) && !(d._2.isEqual(date)) && endHour.toInt > h._1) || (!(d._1.isEqual(date)) && d._2.isEqual(date))
        || (d._1.isEqual(date) && d._2.isEqual(date) && (((startHour.toInt < h._1) && (endHour.toInt > h._1)) || ((startHour.toInt > h._1) && (endHour.toInt < h._2)) || ((startHour.toInt < h._2) && (endHour.toInt > h._2)) )))
      var inCorrectValue: Boolean = false

      for i <- 0 until startHour.length do
        if !(correctChars.contains(startHour(i))) then
          inCorrectValue = true
      for i <- 0 until endHour.length do
        if !(correctChars.contains(endHour(i))) then
          inCorrectValue = true

      if inCorrectValue || date == null || startHour == "" || endHour == "" || (startHour.toInt < 0 || startHour.toInt >= 24) || (endHour.toInt <= 0 || endHour.toInt > 24) then
        true
      else if date.isBefore(dateToday) || startHour.toInt == endHour.toInt || startHour.toInt > endHour.toInt || (date.isEqual(dateToday) && startHour.toInt < hourNow) then
        true
      else if overLapDays || overLapHours then
        true
      else
        false

    def countDays(start: LocalDate, end: LocalDate): Int =
      val period = Period.between(start, end)
      val total = period.getDays
      if total == 0 then 1 else total

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

    // filling colors for background
    leftBox.background = Background.fill(Color.White)
    rightBox.background = Background.fill(Color.White)



    //LeftBox adjusting and children:
    leftBox.padding = standardPadding
    leftBox.spacing = standardSpacing
    leftBox.setAlignment(Pos.BaselineCenter)

    val rightTitle = new Label("Click products to see more about them"):
      font = new Font(25)
    val rightPackageTitle = new Label("Click products to add them to package"):
      font = new Font(25)
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)
    rightBox.children = Array(rightTitle, new Separator)

    val productsTitle = new Label("Products"):
      font = new Font(25)

    val allProducts = new Button("All Products")
    allProducts.font = new Font(10)
    val availableBut = new Button("Available")
    availableBut.font = new Font(10)
    val reservedBut = new Button("Reserved")
    reservedBut.font = new Font(10)

    val newNotificationLabel = new Label("Add new notification")
    val addNotification = new Button("Add")
    val newPackageLabel = new Label("Rent multiple products")

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
    //rent making page
    val view3 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    val view5 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    val boldFont = Font("Arial", FontWeight.Bold, 12)

    val rentHeaderLabel = new Label("Information of this product"):
      font = new Font(15)
    val rentHeader = new HBox():
        padding = standardPadding
        this.setAlignment(Pos.BaselineCenter)
    rentHeader.children = rentHeaderLabel

    val packageRentHeader = new HBox():
        padding = standardPadding
        this.setAlignment(Pos.BaselineCenter)
    val packageRentHeaderLabel = new Label("Information of the products"):
      font = new Font(15)
    packageRentHeader.children = packageRentHeaderLabel

    val rentTitleLabel = new Label("Title:")
    val rentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing * 5

    val packageRentTitleLabel = new Label("Title:")
    val packageRentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val rentDescLabel = new Label("Description:")
    rentDescLabel.padding = standardPadding
    val rentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val packageRentDescLabel = new Label("Description:")
    packageRentDescLabel.padding = standardPadding
    val packageRentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val rentQuantityLabel = new Label("Quantity:")
    val rentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

    val rentPriceLabelDay = new Label("Price per day:")
    val rentPriceLabelHour = new Label("Price per hour:")
    val rentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val packageRentPriceLabelDay = new Label("Price per day:")
    val packageRentPriceLabelHour = new Label("Price per hour:")
    val packageRentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val rentCategoryLabel = new Label("Category:")
    val rentCategoryBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val packageRentCategoryLabel = new Label("Categories:")
    val packageRentCategoryBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    // users informations labels when creating a new rent
    val renternameLabel = new Label("Renter name:")
    val renteraddressLabel = new Label("Renter address:")
    val renterphoneLabel = new Label("Phone number:")
    val renterHeader = new Label("Renters contact information"):
        font = new Font(14)

    val renterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.CenterLeft)

    val packageRenternameLabel = new Label("Names:")
    val packageRenterHeader = new Label("All renters names"):
        font = new Font(14)

    val packageRenterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.CenterLeft)


    val rentButton = new Button("Make a rent"):
      font = Font("System", FontWeight.Bold, 15)

    val packageRentButton = new Button("Make a rent"):
      font = Font("System", FontWeight.Bold, 15)


    val rentMakerHeader = new Label("Input your information"):
        font = new Font(14)
    val rentMakerNameField = new TextField():
      promptText = "Name"
    val rentMakerAddressField = new TextField():
      promptText = "Address"
    val rentMakerPhoneField = new TextField():
      promptText = "Phone number"

    val packageRentMakerHeader = new Label("Input your information"):
        font = new Font(14)
    val packageRentMakerName = new TextField():
      promptText = "Name"
    val packageRentMakerAddress = new TextField():
      promptText = "Address"
    val packageRentMakerPhone = new TextField():
      promptText = "Phone number"

    val amountLabel = new Label("How many you want:")
    amountLabel.setAlignment(Pos.BaselineCenter)
    val amountText = new TextField():
      promptText = "Amount (e.g. 1,2,3...)"

    val rentMakerBox = new VBox:
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineRight)
      children = Array(rentMakerHeader, rentMakerNameField, rentMakerAddressField, rentMakerPhoneField, amountLabel, amountText)

    val packageRentMakerBox = new VBox:
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineRight)
      children = Array(packageRentMakerHeader, packageRentMakerName, packageRentMakerAddress, packageRentMakerPhone)

    val cancelButton2 = new Button("Cancel")
    val cancelButton5 = new Button("Cancel")

    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 15
      children = Array(cancelButton2, rentButton)

    val packageRentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 55
      children = Array(cancelButton5, packageRentButton)


    val calendarBox = new VBox:
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.BottomCenter)
    val calendarHeader = new Label("Choose whether you want the rental for specific days or hours"):
        font = new Font(14)

    val choicesForTime = ObservableBuffer("For days", "For hours")
    val chooseTimeBox = new ChoiceBox[String](choicesForTime)
    chooseTimeBox.value = choicesForTime.head

    var startDate = new DatePicker()
    val startDateLabel = new Label("Choose starting date:")
    var endDate = new DatePicker()
    val endDateLabel = new Label("Choose ending date:")
    var startDateForHours = new DatePicker()
    val startDateForHoursLabel = new Label("Choose date:")
    val startDateTime = new TextField():
      minWidth = 175
      maxWidth = 175
      promptText = "Write start time (e.g. 12)"
    val endDateTime = new TextField():
      minWidth = 175
      maxWidth = 175
      promptText = "Write end time (e.g. 17)"

    val renterCalendarBox = new HBox():
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)
      children = Array(renterBox, calendarBox, rentMakerBox)


    val packageCalendarBox = new VBox:
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.BottomCenter)
    val packageCalendarHeader = new Label("Choose whether you want the rental for specific days or hours"):
        font = new Font(14)

    val packageChoicesForTime = ObservableBuffer("For days", "For hours")
    val packageChooseTimeBox = new ChoiceBox[String](choicesForTime)
    packageChooseTimeBox.value = choicesForTime.head

    var packageStartDate = new DatePicker()
    val packageStartDateLabel = new Label("Choose starting date:")
    var packageEndDate = new DatePicker()
    val packageEndDateLabel = new Label("Choose ending date:")
    var packageStartDateForHours = new DatePicker()
    val packageStartDateForHoursLabel = new Label("Choose date:")
    val packageStartDateTime = new TextField():
      minWidth = 175
      maxWidth = 175
      promptText = "Write start time (e.g. 12)"
    val packageEndDateTime = new TextField():
      minWidth = 175
      maxWidth = 175
      promptText = "Write end time (e.g. 17)"

    val packageRenterCalendarBox = new HBox():
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)
      children = Array(packageRenterBox, packageCalendarBox, packageRentMakerBox)

    // comments page
    val view4 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    val commentsHeader = new Label("Comments of this product")
    commentsHeader.font = Font("Arial", FontWeight.Bold, 20)
    commentsHeader.setAlignment(Pos.BaselineCenter)
    val commentsHeaderBox = new HBox():
      spacing = standardSpacing
      this.setAlignment(Pos.BaselineCenter)
      children = commentsHeader

    val cancelButton3 = new Button("Cancel")

    val commentField = new TextField()
    val addCommentButton = new Button("Add Comment")

    val commentsButtonBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing *60
      children = Array(cancelButton3, addCommentButton)

    def addComment(n: Notification) =
      if commentField.text.value == "" then
        val emptyAlert = new Alert(AlertType.Error):
          title = "Empty Comment"
          headerText = "You can't add empty comment"
          showAndWait()
      else
        n.comments += commentField.text.value
        commentField.text = ""
        val allComments = new Label(n.comments.mkString("\n"))
        view4.children = Array(commentsHeaderBox, allComments, commentField, commentsButtonBox)


    def clearView3(): Unit =
      rentMakerNameField.text = ""
      rentMakerAddressField.text = ""
      rentMakerPhoneField.text = ""
      amountText.text = ""
      startDateTime.text = ""
      endDateTime.text = ""
      startDate.value = null
      endDate.value = null
      startDateForHours.value = null
      chooseTimeBox.value = choicesForTime.head

    def clearView5(): Unit =
      packageRentMakerName.text = ""
      packageRentMakerAddress.text = ""
      packageRentMakerPhone.text = ""
      packageStartDateTime.text = ""
      packageEndDateTime.text = ""
      packageStartDate.value = null
      packageEndDate.value = null
      packageStartDateForHours.value = null


    def countRentPrice(n: mutable.Set[Notification], quantity: Int, days: Int, hours: Int): Double =
      if days != 0 then
        n.map(_.pricePerDay).sum * days * quantity
      else
        n.map(_.pricePerHour).sum * hours * quantity

    def createNewRent(n: Notification): Unit =
      //rentmaker data
      val rentMakerName = rentMakerNameField.text.value
      val rentMakerAddress = rentMakerAddressField.text.value
      val rentMakerPhone = rentMakerPhoneField.text.value
      val quantity = amountText.text.value
      //time data of renting for days
      val forDays = chooseTimeBox.value.value == choicesForTime.head
      val forHours = chooseTimeBox.value.value == choicesForTime.last
      val startingDate = startDate.getValue
      val endingDate = endDate.getValue
      //time data of renting for hours
      val startingDateHours = startDateForHours.getValue
      val startHour = startDateTime.text.value
      val endHour = endDateTime.text.value
      //Make sure all info has been added
      val missingValue: Boolean = rentMakerName == "" || rentMakerPhone == "" || rentMakerAddress == "" || quantity == ""
      var inCorrectValue: Boolean = false

      for i <- 0 until rentMakerPhone.length do
        if !(correctChars.contains(rentMakerPhone(i))) then
          inCorrectValue = true
      for i <- 0 until quantity.length do
        if !(correctChars.contains(quantity(i))) then
          inCorrectValue = true
      if quantity == "0" then
        inCorrectValue = true
        inCorrectInputAlert.showAndWait()

      if missingValue then
        missingInformationAlert.showAndWait()
      else if inCorrectValue || quantity.toInt > n.amount then
        inCorrectInputAlert.showAndWait()
      //see if user has made renting for days or hours and handle wrong inputs
      else if (chooseTimeBox.value.value == choicesForTime.head && checkRentDays(mutable.Set(n), startingDate, endingDate, 0, 24)) ||
        (chooseTimeBox.value.value == choicesForTime.last && checkRentHours(mutable.Set(n), startingDateHours, startHour, endHour)) then
        inValidDateAlert.showAndWait()
      else
        val durationDays = if forDays then countDays(startingDate, endingDate) else 0
        val durationHours = if forHours then endHour.toInt - startHour.toInt else 0
        val rentMaker = User(rentMakerName, rentMakerAddress, rentMakerPhone)
        val rentPrice = countRentPrice(mutable.Set(n), quantity.toInt, durationDays, durationHours)
        val rent = Rent(n, rentMaker, quantity.toInt,
          if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours,
          if forHours then startHour.toInt else 0, if forHours then endHour.toInt else 24, rentPrice)

        rentMaker.rents += rent
        WriteToFile().writeRentToFile(rent)
        scene1.root = view1
        clearView3()
    end createNewRent

    def createNewRentForPackage(notifs: mutable.Set[Notification]) =
      //rentmaker data
      val rentMakerName = packageRentMakerName.text.value
      val rentMakerAddress = packageRentMakerAddress.text.value
      val rentMakerPhone = packageRentMakerPhone.text.value
      val quantity = 1
      //time data of renting for days
      val forDays = packageChooseTimeBox.value.value == choicesForTime.head
      val forHours = packageChooseTimeBox.value.value == choicesForTime.last
      val startingDate = packageStartDate.getValue
      val endingDate = packageEndDate.getValue
      //time data of renting for hours
      val startingDateHours = packageStartDateForHours.getValue
      val startHour = packageStartDateTime.text.value
      val endHour = packageEndDateTime.text.value
      //Make sure all info has been added
      val missingValue: Boolean = rentMakerName == "" || rentMakerPhone == "" || rentMakerAddress == ""
      var inCorrectValue: Boolean = false

      for i <- 0 until rentMakerPhone.length do
        if !(correctChars.contains(rentMakerPhone(i))) then
          inCorrectValue = true

      if missingValue then
        missingInformationAlert.showAndWait()
      else if inCorrectValue then
        inCorrectInputAlert.showAndWait()
      //see if user has made renting for days or hours and handle wrong inputs
      else if (chooseTimeBox.value.value == choicesForTime.head && checkRentDays(notifs, startingDate, endingDate, 0, 24)) ||
        (chooseTimeBox.value.value == choicesForTime.last && checkRentHours(notifs, startingDateHours, startHour, endHour)) then
        inValidDateAlert.showAndWait()
      else
        val durationDays = if forDays then countDays(startingDate, endingDate) else 0
        val durationHours = if forHours then endHour.toInt - startHour.toInt else 0
        val rentMaker = User(rentMakerName, rentMakerAddress, rentMakerPhone)
        val rentPrice = countRentPrice(notifs, quantity, durationDays, durationHours)
        val rents = notifs.map( n => Rent(n, rentMaker, quantity,
          if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours,
          if forHours then startHour.toInt else 0, if forHours then endHour.toInt else 24, rentPrice))

        rentMaker.rents ++= rents
        rents.foreach( r => WriteToFile().writeRentToFile(r) )
        scene1.root = view1
        clearView5()
        productPackage.clear()
    end createNewRentForPackage


    val deleteNotifButton = new Button("DELETE")
    def deleteNotification(notification: Notification): Unit =
      WriteToFile().deleteNotification(notification)
      var notifs = readNotifications
      val buttons = notifs.map( _.button )
      val notifsButtons = notifs.zip(buttons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => makeRentPage(n))
      rightBox.children = Array(rightTitle, new Separator) ++ buttons
      scene1.root = view1


    //val view6 = new VBox()

    def updateView3(n: Notification): Unit =
      val selectedOption = chooseTimeBox.getValue
      startDate = n.calendar
      startDateForHours = n.calendar
      selectedOption match
        case "For days" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)
        case "For hours" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateForHoursLabel, startDateForHours, startDateTime, endDateTime)

    def makeRentPage(n: Notification) =
      val productName = new Label(s"${n.name}")
      productName.font = boldFont
      val desc = new Label(s"${n.description}")
      desc.font = boldFont
      val priceDay = new Label(s"${n.pricePerDay}€")
      priceDay.font = boldFont
      val priceHour = new Label(s"${n.pricePerHour}€")
      priceHour.font = boldFont
      val rentQuantity = new Label(s"${n.amount}")
      rentQuantity.font = boldFont
      val rentCategory = new Label(s"${n.category}")
      rentCategory.font = boldFont
      val renterName = new Label(s"${n.publisher.name}")
      renterName.font = boldFont
      val renterAddress = new Label(s"${n.publisher.address}")
      renterAddress.font = boldFont
      val renterPhone = new Label(s"${n.publisher.phoneNumber}")
      renterPhone.font = boldFont
      rentButton.onAction = (event) => createNewRent(n)
      val seeComments = new Button("Comments")
      seeComments.onAction = (event) => makeCommentsPage(n)
      deleteNotifButton.onAction = (event) => deleteNotification(n)
      chooseTimeBox.onAction = (event) => updateView3(n)
      startDate = n.calendar
      val cale = n.cale
      /*
      val opencale = new Button("calendar")
      val canc = new Button("Cancel")
      opencale.onAction = (event) =>
        view6.children = Array(cale, canc)
        scene1.root = view6

       */

      rentTitleBox.children = Array(rentTitleLabel, productName)
      rentDescriptionBox.children = Array(rentDescLabel, desc)
      rentQuantityBox.children = Array(rentQuantityLabel, rentQuantity)
      rentPriceBox.children =  Array(rentPriceLabelDay, priceDay, rentPriceLabelHour, priceHour)
      rentCategoryBox.children = Array(rentCategoryLabel, rentCategory)
      renterBox.children = Array(renterHeader, renternameLabel, renterName, renteraddressLabel, renterAddress, renterphoneLabel, renterPhone)
      calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)
      renterCalendarBox.children = Array(renterBox, calendarBox, rentMakerBox)
      rentButtonBox.children = Array(cancelButton2, deleteNotifButton, seeComments, rentButton)
      scene1.root = view3
    end makeRentPage

    view3.children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterCalendarBox , new Separator, rentButtonBox)



    calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)

    def updateView5(): Unit =
      val selectedOption = packageChooseTimeBox.getValue
      selectedOption match
        case "For days" => packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateLabel, packageStartDate, packageEndDateLabel, packageEndDate)
        case "For hours" => packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateForHoursLabel, packageStartDateForHours, packageStartDateTime, packageEndDateTime)

    packageChooseTimeBox.onAction = (event) => updateView5()
    packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateLabel, packageStartDate, packageEndDateLabel, packageEndDate)


    def makeRentPageForPackage(notifs: mutable.Set[Notification]) =
      val packageName = new Label(s"${notifs.map(_.name).mkString(" || ")}")
      packageName.font = boldFont
      val packageDesc = new Label(s"${notifs.map(_.description).mkString(" || ")}")
      packageDesc.font = boldFont
      val packagePriceDay = new Label(s"${notifs.map(_.pricePerDay).sum}")
      packagePriceDay.font = boldFont
      val packagePriceHour = new Label(s"${notifs.map(_.pricePerHour).sum}")
      packagePriceHour.font = boldFont
      val packageQuantity = new Label(s"${notifs.map(_.amount).mkString(" || ")}")
      packageQuantity.font = boldFont
      val packageCategories = new Label(s"${notifs.map(_.category).mkString(" || ")}")
      packageCategories.font = boldFont
      val renterNames = new Label(s"${notifs.map(_.publisher.name).mkString("\n")}")
      renterNames.font = boldFont
      packageRentButton.onAction = (event) => createNewRentForPackage(notifs)

      packageRentTitleBox.children = Array(packageRentTitleLabel, packageName)
      packageRentDescriptionBox.children = Array(packageRentDescLabel, packageDesc)
      packageRentPriceBox.children =  Array(packageRentPriceLabelDay, packagePriceDay, packageRentPriceLabelHour, packagePriceHour)
      packageRentCategoryBox.children = Array(packageRentCategoryLabel, packageCategories)
      packageRenterBox.children = Array(packageRenterHeader, packageRenternameLabel, renterNames)
      packageRenterCalendarBox.children = Array(packageRenterBox, packageCalendarBox, packageRentMakerBox)
      packageRentButtonBox.children = Array(cancelButton5, packageRentButton)
      scene1.root = view5

    view5.children = Array(packageRentHeader, packageRentTitleBox, packageRentDescriptionBox, packageRentPriceBox, packageRentCategoryBox,
      new Separator, packageRenterCalendarBox , new Separator, packageRentButtonBox)

    def makeCommentsPage(n: Notification): Unit =
      val allComments = new Label(n.comments.mkString("\n"))
      commentField.promptText = "Comment this product"
      cancelButton3.onAction = (event) => makeRentPage(n)
      addCommentButton.onAction = (event) => addComment(n)
      view4.children = Array(commentsHeaderBox, allComments, commentField, commentsButtonBox)
      scene1.root = view4

    def clearView2(): Unit =
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
      val amount = quantityTxt.text.value
      // user info
      val name = createrName.text.value
      val address =  createrAddress.text.value
      val phone =  createrPhone.text.value
      //checking incorrect inputs
      val missingValues: Boolean = ptitle == "" || dayPrice == "" || hourPrice == "" || desc == ""  || amount == "" || name == "" || address == "" || phone == ""
      var inCorrectValues: Boolean = false
      for i <- 0 until dayPrice.length do
        if !(correctChars.contains(dayPrice(i))) then
          inCorrectValues = true
      for i <- 0 until hourPrice.length do
        if !(correctChars.contains(hourPrice(i))) then
          inCorrectValues = true
      for i <- 0 until amount.length do
        if !(correctChars.contains(amount(i))) then
          inCorrectValues = true
      for i <- 0 until phone.length do
        if !(correctChars.contains(phone(i))) then
          inCorrectValues = true

      if missingValues then
        missingInformationAlert.showAndWait()
      else if inCorrectValues || dayPrice.toInt == 0 || hourPrice.toInt == 0 || amount.toInt == 0 then
        inCorrectInputAlert.showAndWait()
      else
        val creator = User(name, address, phone)
        val notif = Notification(ptitle, creator, dayPrice.toDouble, hourPrice.toDouble, desc, category.value.value, amount.toInt, true)

        creator.notifications += notif
        allNotifications = (allNotifications :+ notif)
        WriteToFile().writeNotifToFile(notif)
        rightBox.children += notif.button
        notif.button.onAction = (event) => makeRentPage(notif)

        scene1.root = view1
        clearView2()
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


    cancelButton2.onAction = (event) =>
      clearView3()
      scene1.root = view1
    cancelButton5.onAction = (event) =>
      clearView5()
      productPackage.clear()
      scene1.root = view1


    def addToPackage(notification: Notification): Unit =
      productPackage += notification
      println(s"added ${notification.name} to package")

    val packageDoneButton = new Button("Package Done"):
      font = boldFont
      this.setAlignment(Pos.BottomCenter)

    def addPackageButtons() =
      val notifs = readNotifications
      val packageButtons = notifs.map( _.button )
      val notifsButtons = notifs.zip(packageButtons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => addToPackage(n))
      rightBox.children = Array(rightPackageTitle, new Separator) ++ packageButtons
      leftBox.children = packageDoneButton


    val startCreatingPackage = new Button("Create Package")
    startCreatingPackage.onAction = (event) =>
      addPackageButtons()

    // add from jsonFileNotif.txt wanted notifications to starting screen
    def updateStartPage(all: Boolean, available: Boolean): Unit =
      val today = LocalDate.now()
      var notifs = readNotifications
      if available then
        notifs = notifs.filterNot( _.allReservedDays.contains(today) )
      else if !available && !all then
        notifs = notifs.filter( _.allReservedDays.contains(today) )
      val buttons = notifs.map( _.button )
      val notifsButtons = notifs.zip(buttons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => makeRentPage(n))
      rightBox.children = Array(rightTitle, new Separator) ++ buttons
      leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification, newPackageLabel, startCreatingPackage)

    // Which products are shown in starting page
    allProducts.onAction = (event) => updateStartPage(true, false)
    availableBut.onAction = (event) => updateStartPage(false, true)
    reservedBut.onAction = (event) => updateStartPage(false, false)


    packageDoneButton.onAction = (event) =>
      if productPackage.isEmpty then
        noProductsAlert.showAndWait()
      else
        println("package is done")
        updateStartPage(true, false)
        leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification,  newPackageLabel, startCreatingPackage)
        makeRentPageForPackage(productPackage)

    addNotification.onAction = (event) => scene1.root = view2

    view2.children = Array(header, titleBox, descriptionBox, quantityBox, priceBox, categoryBox, new Separator, createrBoxHeader, createrBox, submitBox)
    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification,  newPackageLabel, startCreatingPackage)


    stage.scene = scene1