package Classes

import io.circe.Error
import io.circe.parser.{decode, *}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Scene, control}
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}
import io.circe.generic.auto.*
import io.circe.*
import javafx.collections
import javafx.collections.ObservableList
import scalafx.geometry.Pos.BottomCenter
import scalafx.scene.control.Control.sfxControl2jfx

import scala.io.Source
import java.time.*
import scala.collection.mutable

object RentigAppGui extends JFXApp3:

  val UIWidth = 800
  val UIHeight = 630
  val standardPadding = Insets.apply(5, 5, 5, 5)
  val standardSpacing = 10

  val sports = ObservableBuffer(Category("Basketball"), Category("Football"), Category("Skiing"), Category("IceHockey"), Category("Running"), Category("Cycling"))
  val home = ObservableBuffer(Category("Kitchen"), Category("Decor"), Category("Kids"), Category("Outside"), Category("Space"), Category("Other"))
  val categories = sports ++ home

  val correctChars = Array('0','1','2','3','4','5','6','7','8','9')

  var buttons1 = mutable.Buffer[Button]()
  var buttons2 = mutable.Buffer[Button]()
  var buttons3 = mutable.Buffer[Button]()
  var buttons4 = mutable.Buffer[Button]()

  private val productPackage = mutable.Set[Notification]()

  def start() =
    val stage = new JFXApp3.PrimaryStage:
      title = "Renting App"
      width = UIWidth
      height = UIHeight
      resizable = false

    //Main page
    val view1 = GridPane()

    val scene1 = new Scene(parent = view1)

    //Alerts for incorrect inputs and situations
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
    val tooManyProductsAlert = new Alert(AlertType.Error):
      title = "Too many products"
      headerText = "You can't add more products"
      contentText = "In order to add this product, first delete some older one"

    /**
     * Reads Notifications from "jsonFileNotif.txt"
     * @return List of Notifications
     */
    def readNotifications: List[Notification] =
      val source = Source.fromFile("jsonFileNotif.txt")
      val currentList = try source.mkString finally source.close()

      val currentNotifications = decode[List[Notification]](currentList) match
        case Right(notifications) => notifications
        case Left(_) => List.empty[Notification]
      currentNotifications
    end readNotifications

    /**
     * Reads Renst fron "jsonFileRent.txt"
     * @return List of Rents
     */
    def readRents: List[Rent] =
      val source = Source.fromFile("jsonFileRent.txt")
      val currentList = try source.mkString finally source.close()

      val currentRents = decode[List[Rent]](currentList) match
        case Right(rents) => rents
        case Left(_) => List.empty[Rent]
      currentRents
    end readRents

    /**
     * Check if for given notifications the given start and end days are possible to make a rent for. This method is for situations
     * when rent is desired to be for days.
     * @param notifications Notifications which user wants to rent
     * @param startDay desired start day of a rent
     * @param endDay desired end day of a rent
     * @param startH desired start hour of a rent
     * @param endH desired end hour of a rent
     * @return false if rent can be done or true if rent isn't possible for desired time
     */
    def checkRentDays(notifications: mutable.Set[Notification], startDay: LocalDate, endDay: LocalDate, startH: Int, endH: Int): Boolean =
      val now = LocalDate.now()
      val existingRents = readRents.filter( r => notifications.contains(r.notification) )
      val startDays = existingRents.map( r => (r.startDay, r.amount) )
      val endDays = existingRents.map( _.endDay )
      val startHours = existingRents.map( _.startHour )
      val endHours = existingRents.map( _.endHour )
      val hours = startHours.zip(endHours)
      val startDaysWhours = startDays.zip(startHours.zip(endHours))
      val startsWends = startDays.zip(endDays)

      if startDay == null || endDay == null then
        true
      else if startDay.isBefore(now) || endDay.isBefore(startDay) then
        true
      else if startsWends.exists( (s,e) => (startDay.isBefore(s._1) && endDay.isAfter(s._1)) || (startDay.isAfter(s._1) && endDay.isBefore(e)) || (startDay.isBefore(e) && endDay.isAfter(e)) ) ||
         startsWends.zip(hours).exists( (d,h) => (d._1._1.isEqual(startDay) && startH < h._2) || (d._2.isEqual(endDay)) || (d._1._1.isEqual(startDay) && d._2.isEqual(endDay) || (!(d._1._1.isEqual(d._2)) && (startDay.isEqual(d._2)) ))) then
        true
      else
        false
    end checkRentDays


    /**
     * Check if for given notifications the given start and end days are possible to make a rent for. This method is for situations
     * when rent is desired to be for hours
     * @param notifications Notifications which user wants to rent
     * @param date desired day of a rent
     * @param startHour desired start hour of a rent
     * @param endHour desired end hour of a rent
     * @return
     */
    def checkRentHours(notifications: mutable.Set[Notification], date: LocalDate, startHour: String, endHour: String ): Boolean =
      val dateToday = LocalDate.now()
      val hourNow = LocalTime.now().getHour

      val existingRents = readRents.filter( r => notifications.contains(r.notification) )
      val startDays = existingRents.map( r => (r.startDay,r.amount) )
      val endDays = existingRents.map( _.endDay )
      val startHours = existingRents.map( _.startHour )
      val endHours = existingRents.map( _.endHour )
      val hours = startHours.zip(endHours)
      val startDaysWhours = startDays.zip(startHours.zip(endHours))
      val endDaysWhours = endDays.zip(startHours.zip(endHours))
      val startsWends = startDays.zip(endDays)
      val daysAndHours = startsWends.zip(hours)
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
      else if startsWends.exists( (s,e) => (date.isAfter(s._1) && date.isBefore(e)) ) || daysAndHours.exists( (d,h) => (d._1._1.isEqual(date) && !(d._2.isEqual(date)) && endHour.toInt > h._1) || (!(d._1._1.isEqual(date)) && d._2.isEqual(date))
        || (d._1._1.isEqual(date) && d._2.isEqual(date) && (((startHour.toInt < h._1) && (endHour.toInt > h._1)) || ((startHour.toInt > h._1) && (endHour.toInt < h._2)) || ((startHour.toInt < h._2) && (endHour.toInt > h._2)) ))) then
        true
      else
        false
    end checkRentHours

    /**
     * Method for calculating days between given days
     * @param start start day of a rent
     * @param end end day of a rent
     * @return days between start day and end day including them
     */
    def countDays(start: LocalDate, end: LocalDate): Int =
      val period = Period.between(start, end)
      val total = period.getDays
      if total == 0 then 1 else total
    end countDays


    //Dividing screen to 2 Vboxes
    val rightBox = VBox()
    val leftBox = VBox()
    val nextAndPrevButtons = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.TopCenter)

    view1.add(leftBox, 0, 0, 1, 2)
    view1.add(rightBox, 1, 0, 1, 2)
    //screen is divided for two columns
    val column0 = new ColumnConstraints:
      percentWidth = 20
    val column1 = new ColumnConstraints:
      percentWidth = 80
    val row0 = new RowConstraints:
      percentHeight = 50
    val row1 = new RowConstraints:
      percentHeight = 50

    view1.columnConstraints = Array(column0, column1)
    view1.rowConstraints = Array(row0, row1)

    // colors for background
    leftBox.background = Background.fill(Color.White)
    rightBox.background = Background.fill(Color.White)

    //LeftBox adjusting
    leftBox.padding = standardPadding
    leftBox.spacing = standardSpacing
    leftBox.setAlignment(Pos.BaselineCenter)

    //title for starting page and package making day
    val rightTitle = new Label("Click products to see more about them"):
      font = new Font(25)
    val rightPackageTitle = new Label("Click products to add them to package"):
      font = new Font(25)
    rightBox.padding = standardPadding
    rightBox.spacing = standardSpacing
    rightBox.setAlignment(Pos.BaselineCenter)
    rightBox.children = Array(rightTitle, new Separator) ++ buttons1

    //stuff for leftbox (buttons and labels)
    val productsTitle = new Label("Products"):
      font = new Font(25)

    val allProducts = new Button("All Products")
    allProducts.font = new Font(10)
    val availableBut = new Button("Available today")
    availableBut.font = new Font(10)
    val reservedBut = new Button("Reserved today")
    reservedBut.font = new Font(10)

    val newNotificationLabel = new Label("Add new notification")
    val addNotification = new Button("Add")
    val newPackageLabel = new Label("Rent multiple products")
    val nextPage1 = new Button("2->")
    val nextPage2 = new Button("3->")
    val nextPage3 = new Button("4->")
    val previousPage2 = new Button("<-1")
    val previousPage3 = new Button("<-2")
    val previousPage4 = new Button("<-3")
    val userDataTitle = new Label("Users and their rents"):
      font = new Font(25)
    val seeUsersRentsLabel = new Label("See All Users Rents")
    val seeUsersRentsButton = new Button("Users' rents")

    //Notification creating page
    val view2 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    //Information of product for creating new notification
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

    //Rent making page
    val view3 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    //Package rent making page
    val view5 = new VBox():
      padding = standardPadding
      spacing = standardSpacing

    //Font for rent making page
    val boldFont = Font("Arial", FontWeight.Bold, 12)

    //Stuff for rent making page (package rent making page labels have "package" in front of them)
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

    val packageRentTitleLabel = new Label("Titles:")
    val packageRentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val rentDescLabel = new Label("Description:")
    rentDescLabel.padding = standardPadding
    val rentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val packageRentDescLabel = new Label("Descriptions:")
    packageRentDescLabel.padding = standardPadding
    val packageRentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val rentQuantityLabel = new Label("Quantity:")
    val rentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

    val packageRentQuantityLabel = new Label("Quantity:")
    val packageRentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

    val rentPriceLabelDay = new Label("Price per day:")
    val rentPriceLabelHour = new Label("Price per hour:")
    val rentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing

    val packageRentPriceLabelDay = new Label("Price's per day:")
    val packageRentPriceLabelHour = new Label("Price's per hour:")
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

    //Cancel button for rent making page
    val cancelButton2 = new Button("Cancel")
    //Cancel button for package rent making page
    val cancelButton5 = new Button("Cancel")

    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 15
      children = Array(cancelButton2, rentButton)

    val packageRentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 58
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

    //DatePickers for making rent
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

    //DatePickers for making rent for package
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

    // Cancelbutton for comments page
    val cancelButton3 = new Button("Cancel")

    //stuff for comment making page
    val commentField = new TextField()
    val addCommentButton = new Button("Add Comment")

    val commentsButtonBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing *60
      children = Array(cancelButton3, addCommentButton)

    /**
     * Adds a writed comment to that notifications comment, comments aren't saved to files
     * @param n Notification comment is writed to
     */
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
    end addComment

    /**
     * Clears Rent making page textfields and calendar selections after rent is made or
     * user has gone back to start page from cancel button
     */
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
    end clearView3

    /**
     * Clears Package rent making page textfields and calendar selections after rent is made or
     * user has gone back to start page from cancel button
     */
    def clearView5(): Unit =
      packageRentMakerName.text = ""
      packageRentMakerAddress.text = ""
      packageRentMakerPhone.text = ""
      packageStartDateTime.text = ""
      packageEndDateTime.text = ""
      packageStartDate.value = null
      packageEndDate.value = null
      packageStartDateForHours.value = null
      chooseTimeBox.value = choicesForTime.head
    end clearView5

    /**
     * Counts rent price depending on hour and day price, for how long period rent is and how many of this product is rented
     * @param n notifications rent is going to be
     * @param quantity  quantity of products
     * @param days  for how many days rent is
     * @param hours for how many hours rent is
     * @return price of the rent
     */
    def countRentPrice(n: mutable.Set[Notification], quantity: Int, days: Int, hours: Int): Double =
      if days != 0 then
        n.map(_.pricePerDay).sum * days * quantity
      else
        n.map(_.pricePerHour).sum * hours * quantity
    end countRentPrice

    /**
     * Creates new rent for single product if given informations are correct
     * @param n notification which is going to be rented
     */
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
      else if (forDays && checkRentDays(mutable.Set(n), startingDate, endingDate, 0, 24)) ||
        (forHours && checkRentHours(mutable.Set(n), startingDateHours, startHour, endHour)) then
        inValidDateAlert.showAndWait()
      else
        //Create rent and update page
        val durationDays = if forDays then countDays(startingDate, endingDate) else 0
        val durationHours = if forHours then endHour.toInt - startHour.toInt else 0
        val rentMaker = User(rentMakerName, rentMakerAddress, rentMakerPhone)
        val rentPrice = countRentPrice(mutable.Set(n), quantity.toInt, durationDays, durationHours)
        val rent = Rent(n, rentMaker, quantity.toInt,
          if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours,
          if forHours then startHour.toInt else 0, if forHours then endHour.toInt else 24, rentPrice)

        WriteToFile().writeRentToFile(rent)
        nextAndPrevButtons.children = nextPage1
        rightBox.children = Array(rightTitle, new Separator) ++ buttons1
        scene1.root = view1
        clearView3()
    end createNewRent

    /**
     *  Creates new rent for multiple products user has selected if given informations are correct
     * @param notifs products user has selected
     */
    def createNewRentForPackage(notifs: mutable.Set[Notification]): Unit =
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
      else if (forDays && checkRentDays(notifs, startingDate, endingDate, 0, 24)) ||
        (forHours && checkRentHours(notifs, startingDateHours, startHour, endHour)) then
        inValidDateAlert.showAndWait()
      else
        val durationDays = if forDays then countDays(startingDate, endingDate) else 0
        val durationHours = if forHours then endHour.toInt - startHour.toInt else 0
        val rentMaker = User(rentMakerName, rentMakerAddress, rentMakerPhone)
        val rentPrice = countRentPrice(notifs, quantity, durationDays, durationHours)
        val rents = notifs.map( n => Rent(n, rentMaker, quantity,
          if forDays then startingDate else startingDateHours, if forDays then endingDate else startingDateHours,
          if forHours then startHour.toInt else 0, if forHours then endHour.toInt else 24, rentPrice))

        rents.foreach( r => WriteToFile().writeRentToFile(r) )
        nextAndPrevButtons.children = nextPage1
        rightBox.children = Array(rightTitle, new Separator) ++ buttons1
        scene1.root = view1
        clearView5()
        productPackage.clear()
    end createNewRentForPackage

    //Button for deleting notification
    val deleteNotifButton = new Button("DELETE")

    //Button for when products for package has been chosen
    val packageDoneButton = new Button("Package Done"):
      font = boldFont
      this.setAlignment(Pos.BottomCenter)

    val cancelPackageMaking = new Button("Cancel")

    /**
     * Adds given notification to productpackage
     * @param notification notification to be added to package
     */
    def addToPackage(notification: Notification): Unit =
      productPackage += notification

    /**
     * Changes every notifications button onAction so that when pressing it, notification is added to productpackage.
     * Buttons are separated for 4 pages each max 6 buttons
     */
    def addPackageButtons(): Unit =
      var notifs = readNotifications
      val packageButtons = notifs.map( _.button )
      val pageSize = 6
      val buttonsIn6Groups = packageButtons.grouped(pageSize).toBuffer
      if packageButtons.isEmpty then
          packageButtons
      else if packageButtons.length <=6 then
        buttons1 = buttonsIn6Groups.head.toBuffer
      else if packageButtons.length <= 12 then
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
      else if packageButtons.length <= 18 then
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
        buttons3 = buttonsIn6Groups(2).toBuffer
      else
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
        buttons3 = buttonsIn6Groups(2).toBuffer
        buttons4 = buttonsIn6Groups.last.toBuffer
      val notifsButtons = notifs.zip(packageButtons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => addToPackage(n))
      rightBox.children = Array(rightPackageTitle, new Separator) ++ buttons1
      rightBox.setAlignment(Pos.BaselineCenter)
      leftBox.children = Array(packageDoneButton, nextAndPrevButtons, cancelPackageMaking)
    end addPackageButtons

    //Button for starting to make productpackage
    val startCreatingPackage = new Button("Create Package")
    startCreatingPackage.onAction = (event) =>
      nextAndPrevButtons.children = nextPage1
      addPackageButtons()

    /**
     * Method to handle users option to rent product for days or hours. Different select updates calendarbox childrens
     * inside rent making page
     * @param n Notification which calendar is shown
     */
    def updateView3(n: Notification): Unit =
      val selectedOption = chooseTimeBox.getValue
      startDate = n.calendarStart
      endDate = n.calendarEnd
      startDateForHours = n.calendarStart
      selectedOption match
        case "For days" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)
        case "For hours" => calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateForHoursLabel, startDateForHours, startDateTime, endDateTime)
    end updateView3

    /**
     * Creates a rent making page for given notification by adding its information to page
     * @param n notification whose iformation is added
     */
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
      startDate = n.calendarStart
      endDate = n.calendarEnd

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

    //Rent making pages children at beginning
    view3.children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterCalendarBox , new Separator, rentButtonBox)
    //Calendarbocx children at beginning
    calendarBox.children = Array(calendarHeader, chooseTimeBox, startDateLabel, startDate, endDateLabel, endDate)

    /**
     * Method to handle users option to rent package of products for days or hours. Different select updates calendarbox childrens
     * in rent making page
     * @param productPackage collection of notifications which calendar is shown. One calendar is added but its modified by every given notification
     */
    def updateView5(productPackage: ProductPackage): Unit =
      val selectedOption = packageChooseTimeBox.getValue
      packageStartDate = productPackage.combinedCalendarStart
      packageStartDateForHours = productPackage.combinedCalendarStart
      packageEndDate = productPackage.combinedCalendarEnd
      selectedOption match
        case "For days" => packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateLabel, packageStartDate, packageEndDateLabel, packageEndDate)
        case "For hours" => packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateForHoursLabel, packageStartDateForHours, packageStartDateTime, packageEndDateTime)
    end updateView5

    /**
     * Creates a rent making page for given notifications by adding their information to page
     * @param n notifications whose iformation are added
     */
    def makeRentPageForPackage(notifs: mutable.Set[Notification]) =
      val packageName = new Label(s"${notifs.map(_.name).mkString(" || ")}")
      packageName.font = boldFont
      val packageDesc = new Label(s"${notifs.map(_.description).mkString("\n")}")
      packageDesc.font = boldFont
      val packagePriceDay = new Label(s"${notifs.map(_.pricePerDay).mkString("+")} = ${notifs.map(_.pricePerDay).sum}")
      packagePriceDay.font = boldFont
      val packagePriceHour = new Label(s"${notifs.map(_.pricePerHour).mkString("+")} = ${notifs.map(_.pricePerHour).sum}")
      packagePriceHour.font = boldFont
      val packageQuantity = new Label(s"${notifs.map(_.amount).mkString(" || ")}")
      packageQuantity.font = boldFont
      val packageCategories = new Label(s"${notifs.map(_.category).mkString(" || ")}")
      packageCategories.font = boldFont
      val renterNames = new Label(s"${notifs.map(_.publisher.name).mkString("\n")}")
      renterNames.font = boldFont
      packageRentButton.onAction = (event) => createNewRentForPackage(notifs)
      val products = ProductPackage(notifs)
      val combinedCalendarStart = products.combinedCalendarStart
      val combinedCalendarEnd = products.combinedCalendarEnd
      packageStartDate = combinedCalendarStart
      packageEndDate = combinedCalendarEnd
      packageChooseTimeBox.onAction = (event) => updateView5(products)

      packageRentTitleBox.children = Array(packageRentTitleLabel, packageName)
      packageRentDescriptionBox.children = Array(packageRentDescLabel, packageDesc)
      packageRentPriceBox.children =  Array(packageRentPriceLabelDay, packagePriceDay, packageRentPriceLabelHour, packagePriceHour)
      packageRentQuantityBox.children = Array(packageRentQuantityLabel, packageQuantity)
      packageRentCategoryBox.children = Array(packageRentCategoryLabel, packageCategories)
      packageRenterBox.children = Array(packageRenterHeader, packageRenternameLabel, renterNames)
      packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateLabel, packageStartDate, packageEndDateLabel, packageEndDate)
      packageRenterCalendarBox.children = Array(packageRenterBox, packageCalendarBox, packageRentMakerBox)
      packageRentButtonBox.children = Array(cancelButton5, packageRentButton)
      scene1.root = view5
    end makeRentPageForPackage

    //Children of package renting page at beginning
    view5.children = Array(packageRentHeader, packageRentTitleBox, packageRentDescriptionBox, packageRentQuantityBox, packageRentPriceBox, packageRentCategoryBox,
      new Separator, packageRenterCalendarBox , new Separator, packageRentButtonBox)

    //Package calendarbox children at beginning
    packageCalendarBox.children = Array(packageCalendarHeader, packageChooseTimeBox, packageStartDateLabel, packageStartDate, packageEndDateLabel, packageEndDate)

    /**
     * Creates a comment making page for given notification
     * @param n notification whose comments are shown and what is going to be commented
     */
    def makeCommentsPage(n: Notification): Unit =
      val allComments = new Label(n.comments.mkString("\n"))
      commentField.promptText = "Comment this product"
      cancelButton3.onAction = (event) => makeRentPage(n)
      addCommentButton.onAction = (event) => addComment(n)
      view4.children = Array(commentsHeaderBox, allComments, commentField, commentsButtonBox)
      scene1.root = view4
    end makeCommentsPage

    /**
     * Clears the notification creating page's textfields when notification has been made
     */
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
    end clearView2

    /**
     * Deletes a given notification and updates files
     * @param notification notification that is wanted to be deleted
     */
    def deleteNotification(notification: Notification): Unit =
      WriteToFile().deleteNotification(notification)
      buttons1.clear()
      buttons2.clear()
      buttons3.clear()
      buttons4.clear()
      updateStartPage(true, false)
    end deleteNotification

    /**
     * Creates new notification if given informations are correct
     */
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
        val notif = Notification(ptitle, creator, dayPrice.toDouble, hourPrice.toDouble, desc, category.value.value, amount.toInt)
        notif.button.onAction = (event) => makeRentPage(notif)

        if readNotifications.length < 24 then
          WriteToFile().writeNotifToFile(notif)
          updateStartPage(true, false)
          clearView2()
        else
          tooManyProductsAlert.showAndWait()
          updateStartPage(true, false)
          clearView2()
    end createNewNotification


    /**
     * Adds from jsonFileNotif.txt notifications to starting screen. Notifications are filtered depending if
     * all, available today or reserved today notifications are wanted to be shown. Notifications are added to 4 pages
     * 6 notifications max per page
     * @param all true if all notifications are wanted to be shown
     * @param available false if available notifications are wanted to be shown
     */
    def updateStartPage(all: Boolean, available: Boolean): Unit =
      buttons1.clear()
      buttons2.clear()
      buttons3.clear()
      buttons4.clear()
      val today = LocalDate.now()
      var notifs = readNotifications
      if available then
        notifs = notifs.filterNot( _.allReservedDays.contains(today) )
      else if !all && !available then
        notifs = notifs.filter( _.allReservedDays.contains(today) )
      val buttons = notifs.map( _.button )
      val pageSize = 6
      val buttonsIn6Groups = buttons.grouped(pageSize).toBuffer
      if buttons.isEmpty then
          buttons
      else if buttons.length <=6 then
        buttons1 = buttonsIn6Groups.head.toBuffer
      else if buttons.length <= 12 then
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
      else if buttons.length <= 18 then
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
        buttons3 = buttonsIn6Groups(2).toBuffer
      else
        buttons1 = buttonsIn6Groups.head.toBuffer
        buttons2 = buttonsIn6Groups(1).toBuffer
        buttons3 = buttonsIn6Groups(2).toBuffer
        buttons4 = buttonsIn6Groups.last.toBuffer
      val notifsButtons = notifs.zip(buttons)
      notifsButtons.foreach( (n,b) => b.onAction = (event) => makeRentPage(n))
      rightBox.children = Array(rightTitle, new Separator) ++ buttons1
      rightBox.setAlignment(Pos.BaselineCenter)
      leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification, newPackageLabel, startCreatingPackage, seeUsersRentsLabel, seeUsersRentsButton, nextAndPrevButtons)
      nextAndPrevButtons.children = nextPage1
      scene1.root = view1
    end updateStartPage

    /**
     * Method that deletes rents from file if it has expired. This is checked everytime app is opened
     */
    def deleteExpiredRents(): Unit =
      val dayToday = LocalDate.now()
      val timeNow = LocalTime.now().getHour
      val rents = readRents
      val expiredRents = rents.filter( r => dayToday.isAfter(r.endDay) || (dayToday.isEqual(r.endDay) && timeNow >= r.endHour) )
      expiredRents.foreach( r => WriteToFile().deleteRent(r) )
    end deleteExpiredRents

    //LeftBox buttons onActions to update buttons that are shown
    allProducts.onAction = (event) => updateStartPage(true, false)
    availableBut.onAction = (event) => updateStartPage(false, true)
    reservedBut.onAction = (event) => updateStartPage(false, false)

    cancelButton2.onAction = (event) =>
      clearView3()
      updateStartPage(true, false)
    cancelButton5.onAction = (event) =>
      productPackage.clear()
      clearView5()
      updateStartPage(true, false)
    cancelPackageMaking.onAction = (event) =>
      productPackage.clear()
      updateStartPage(true, false)


    val submitButton = new Button("Submit")
    submitButton.font = Font("System", FontWeight.Bold, 15)
    submitButton.onAction = (event) =>
      nextAndPrevButtons.children = nextPage1
      createNewNotification()
    val cancelButton = new Button("Cancel")
    cancelButton.onAction = (event) => updateStartPage(true, false)
    val submitBox = new HBox():
      spacing = standardSpacing * 65
      children = Array(cancelButton, submitButton)


    packageDoneButton.onAction = (event) =>
      if productPackage.isEmpty then
        noProductsAlert.showAndWait()
      else
        updateStartPage(true, false)
        makeRentPageForPackage(productPackage)

    /**
     * Shows all users and their rents in a listView
     */
    def seeUserData(): Unit =
      val usersWithRents = readRents.groupBy( _.whoRented ).map( i => (i._1.toString, i._2.map(_.toString)) ).toSeq
      val asString = usersWithRents.flatMap( (user, rents) => user +: rents.map( r => s" - $r" ) )
      val listView = new ListView[String]():
        this.vgrow = Priority.Always
      listView.getItems.addAll(asString*)
      rightBox.children = Array(userDataTitle, new Separator, listView)
      rightBox.setAlignment(Pos.BottomCenter)
      leftBox.children -= nextAndPrevButtons
    end seeUserData

    addNotification.onAction = (event) => scene1.root = view2
    seeUsersRentsButton.onAction = (event) => seeUserData()

    //Next and previous page buttons onActions
    nextPage1.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons2
      nextAndPrevButtons.children -= nextPage1
      nextAndPrevButtons.children += previousPage2
      nextAndPrevButtons.children += nextPage2

    nextPage2.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons3
      nextAndPrevButtons.children -= previousPage2
      nextAndPrevButtons.children -= nextPage2
      nextAndPrevButtons.children += previousPage3
      nextAndPrevButtons.children += nextPage3

    nextPage3.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons4
      nextAndPrevButtons.children -= previousPage3
      nextAndPrevButtons.children -= nextPage3
      nextAndPrevButtons.children += previousPage4

    previousPage2.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons1
      nextAndPrevButtons.children -= previousPage2
      nextAndPrevButtons.children -= nextPage2
      nextAndPrevButtons.children += nextPage1

    previousPage3.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons2
      nextAndPrevButtons.children -= previousPage3
      nextAndPrevButtons.children -= nextPage3
      nextAndPrevButtons.children += previousPage2
      nextAndPrevButtons.children += nextPage2

    previousPage4.onAction = (event) =>
      rightBox.children = Array(rightTitle, new Separator) ++ buttons3
      nextAndPrevButtons.children -= previousPage4
      nextAndPrevButtons.children += previousPage3
      nextAndPrevButtons.children += nextPage3

    nextAndPrevButtons.children = nextPage1
    view2.children = Array(header, titleBox, descriptionBox, quantityBox, priceBox, categoryBox, new Separator, createrBoxHeader, createrBox, submitBox)
    leftBox.children = Array(productsTitle, allProducts, availableBut, reservedBut, newNotificationLabel, addNotification,  newPackageLabel, startCreatingPackage, seeUsersRentsLabel, seeUsersRentsButton, nextAndPrevButtons)

    //At beginning every expired rent is deleted and buttons are shown
    deleteExpiredRents()
    updateStartPage(true, false)
    
    stage.scene = scene1
  end start