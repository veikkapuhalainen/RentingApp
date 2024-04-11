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
      contentText = "You have input characters to fields in which you shouldn't"
    val missingDateAlert = new Alert(AlertType.Error):
      title = "Missing Date"
      headerText = "Remember to choose time for your rent"
      contentText = "You haven't chosen a date"
    val inValidDateAlert = new Alert(AlertType.Error):
      title = "Invalid Date"
      headerText = "Check dates or time you have chosen"
      contentText = "You can't make rent for this period of time"

    def readFile: List[Notification] =
      val source = Source.fromFile("jsonFileNotif.txt")
      val currentList = try source.mkString finally source.close()

      val currentNotifications = decode[List[Notification]](currentList) match
        case Right(notifications) => notifications
        case Left(_) => List.empty[Notification]
      currentNotifications


    def checkRentDays(start: LocalDate, end: LocalDate): Boolean =
      val now = LocalDate.now()
      if start == null || end == null then
        true
      else if start.isBefore(now) || end.isBefore(start) then
        true
      else
        false

    def checkRentTime(date: LocalDate, startHour: String, endHour: String ): Boolean =
      val dateToday = LocalDate.now()
      val hourNow = LocalTime.now().getHour
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
      else
        false

    def countDays(start: LocalDate, end: LocalDate): Int =
      val period = Period.between(start, end)
      val years = end.getYear - start.getYear
      val months = end.getMonthValue - start.getMonthValue
      val days = end.getDayOfMonth - start.getDayOfMonth
      val total = period.getDays
      if total == 0 then 1 else total

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
    leftBox.background = Background.fill(Color.White)
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

    val allProducts = new Button("All products")
    allProducts.font = new Font(10)
    val availableBut = new Button("Available")
    availableBut.font = new Font(10)
    val reservedBut = new Button("Reserved")
    reservedBut.font = new Font(10)

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

    val boldFont = Font("Arial", FontWeight.Bold, 12)

    val rentHeader = new HBox():
        padding = standardPadding
        this.setAlignment(Pos.BaselineCenter)

    val rentHeaderLabel = new Label("Information of the product"):
      font = new Font(15)
    rentHeader.children = rentHeaderLabel

    val rentTitleLabel = new Label("Title:")

    val rentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing * 5

    val rentDescLabel = new Label("Description:")
    rentDescLabel.padding = standardPadding

    val rentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*2

    val rentQuantityLabel = new Label("Quantity:")
    val rentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

    val rentPriceLabelDay = new Label("Price per day:")
    val rentPriceLabelHour = new Label("Price per hour:")

    val rentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing


    val rentCategoryLabel = new Label("Category:")

    val rentCategoryBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    // users informations labels when creating a new notification
    val renternameLabel = new Label("Renter name:")
    val renteraddressLabel = new Label("Renter address:")
    val renterphoneLabel = new Label("Phone number:")

    val renterHeader = new Label("Renters contact information"):
        font = new Font(14)

    val renterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.CenterLeft)


    val rentButton = new Button("Make a rent"):
      font = Font("System", FontWeight.Bold, 15)



    val rentMakerHeader = new Label("Input your information"):
        font = new Font(14)
    val rentMakerNameField = new TextField():
      promptText = "Name"
    val rentMakerAddressField = new TextField():
      promptText = "Address"
    val rentMakerPhoneField = new TextField():
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

    val cancelButton2 = new Button("Cancel")

    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 26
      children = Array(cancelButton2, rentButton)


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

    def makeCommentsPage(n: Notification): Unit =
      val allComments = new Label(n.comments.mkString("\n"))
      commentField.promptText = "Comment this product"
      cancelButton3.onAction = (event) => makeRentPage(n)
      addCommentButton.onAction = (event) => addComment(n)
      view4.children = Array(commentsHeaderBox, allComments, commentField, commentsButtonBox)
      scene1.root = view4

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

    def countRentPrice(n: Notification, quantity: Int, days: Int, hours: Int): Double =
      if days != 0 then
        n.pricePerDay * days * quantity
      else
        n.pricePerHour * hours * quantity

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
      if quantity.toInt == 0 || quantity.toInt > n.amount then
        inCorrectValue = true

      if missingValue then
        missingInformationAlert.showAndWait()
      else if inCorrectValue then
        inCorrectInputAlert.showAndWait()
      //see if user has made renting for days or hours and handle wrong inputs
      else if (chooseTimeBox.value.value == choicesForTime.head && checkRentDays(startingDate, endingDate)) ||
        (chooseTimeBox.value.value == choicesForTime.last && checkRentTime(startingDateHours, startHour, endHour)) then
        inValidDateAlert.showAndWait()
      else
        val durationDays = if forDays then countDays(startingDate, endingDate) else 0
        val durationHours = if forHours then endHour.toInt - startHour.toInt else 0
        val rentMaker = User(rentMakerName, rentMakerAddress, rentMakerPhone)
        val rentPrice = countRentPrice(n, quantity.toInt, durationDays, durationHours)
        val rent = Rent(n, rentMaker, quantity.toInt,
          if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours,
          if forHours then startHour.toInt else 0, if forHours then endHour.toInt else 0, rentPrice)

        n.reservedDates += Period.between(if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours)
        WriteToFile().writeRentToFile(rent)
        scene1.root = view1
        clearView3()
    end createNewRent

    def makeRentPage(n: Notification) =
      val productName = new Label(s"${n.name}")
      productName.font = boldFont
      val desc = new Label(s"${n.description}")
      desc.font = boldFont
      val priceDay = new Label(s"${n.pricePerDay}")
      priceDay.font = boldFont
      val priceHour = new Label(s"${n.pricePerHour}")
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
      //startDate = n.datePicker
      //endDate = n.datePicker
      //startDayForHours = n.datePicker

      rentTitleBox.children = Array(rentTitleLabel, productName)
      rentDescriptionBox.children = Array(rentDescLabel, desc)
      rentQuantityBox.children = Array(rentQuantityLabel, rentQuantity)
      rentPriceBox.children =  Array(rentPriceLabelDay, priceDay, rentPriceLabelHour, priceHour)
      rentCategoryBox.children = Array(rentCategoryLabel, rentCategory)
      renterBox.children = Array(renterHeader, renternameLabel, renterName, renteraddressLabel, renterAddress, renterphoneLabel, renterPhone)
      renterCalendarBox.children = Array(renterBox, calendarBox, rentMakerBox)
      rentButtonBox.children = Array(cancelButton2, seeComments, rentButton)
      scene1.root = view3

    view3.children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterCalendarBox , new Separator, rentButtonBox)


    def updateView3(): Unit =
      val selectedOption = chooseTimeBox.getValue
      selectedOption match
        case "For days" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)
        case "For hours" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateForHoursLabel, startDateForHours, startDateTime, endDateTime)

    chooseTimeBox.onAction = (event) => updateView3()
    calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)


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
      if dayPrice.toInt == 0 ||hourPrice.toInt == 0 ||amount.toInt == 0 then
        inCorrectValues = true

      if missingValues then
        missingInformationAlert.showAndWait()
      else if inCorrectValues then
        inCorrectInputAlert.showAndWait()
      else
        val creator = User(name, address, phone)
        val notif = Notification(ptitle, creator, dayPrice.toDouble, hourPrice.toDouble, desc, category.value.value, amount.toInt,  true)

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

    view2.children = Array(header, titleBox, descriptionBox, quantityBox, priceBox, categoryBox, new Separator, createrBoxHeader, createrBox, submitBox)

    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification)
    addNotification.onAction = (event) => scene1.root = view2


    // add from jsonFileNotif.txt wanted notifications to starting screen
    def updateStartPage(all: Boolean, available: Boolean): Unit =
      var notifs = readFile
      if available then
        notifs = notifs.filter(_.available)
      else if !available && !all then
        notifs = notifs.filterNot(_.available)
      val buttons = notifs.map( _.button )
      val notifsButtons = notifs.zip(buttons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => makeRentPage(n))
      rightBox.children = Array(rightTitle, new Separator) ++ buttons

    // Which products are shown in starting page
    allProducts.onAction = (event) => updateStartPage(true, false)
    availableBut.onAction = (event) => updateStartPage(false, true)
    reservedBut.onAction = (event) => updateStartPage(false, false)

    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)
    rightBox.children = Array(rightTitle, new Separator)

    view3.padding = Insets.apply(5,5,5,5)
    view3.spacing = 5

    stage.scene = scene1