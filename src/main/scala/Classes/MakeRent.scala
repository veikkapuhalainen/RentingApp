/*package Classes

import Classes.RentigAppGui.{standardPadding, standardSpacing}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, DatePicker, Label, Separator, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.{Font, FontWeight}

case class MakeRent(n: Notification) extends VBox:

    val notification = n

    padding = Insets.apply(5,5,5,5)
    spacing = 5
  
    val boldFont = Font("Arial", FontWeight.Bold, 12)

    val rentHeader = new HBox():
        padding = standardPadding
        this.setAlignment(Pos.BaselineCenter)

    val rentHeaderLabel = new Label("Information of the product"):
      font = new Font(15)
    rentHeader.children = rentHeaderLabel

    val rentTitleLabel = new Label("Title:")
    val productName = new Label(s"${n.name}")
    productName.font = boldFont

    val rentTitleBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing * 5
      children = Array(rentTitleLabel, productName)

    val rentDescLabel = new Label("Description:")
    val desc = new Label(s"${n.description}")
    desc.font = boldFont
    rentDescLabel.padding = standardPadding

    val rentDescriptionBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*2
      children = Array(rentDescLabel, desc)

    val rentQuantityBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing*3

      val rentQuantityLabel = new Label("Quantity:")
      val rentQuantity = new Label("**")

      children = Array(rentQuantityLabel, rentQuantity)

    val rentPriceLabelDay = new Label("Price per day:")
    val priceDay = new Label(s"${n.pricePerDay}")
    priceDay.font = boldFont

    val rentPriceLabelHour = new Label("Price per hour:")
    val priceHour = new Label(s"${n.pricePerHour}")
    priceHour.font = boldFont

    val rentPriceBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(rentPriceLabelDay, priceDay, rentPriceLabelHour, priceHour)


    val rentCategoryLabel = new Label("Category:")
    val rentCategory = new Label(s"${n.category}")
    rentCategory.font = boldFont

    val rentCategoryBox = new HBox():
      padding = standardPadding
      spacing = standardSpacing
      children = Array(rentCategoryLabel, rentCategory)

    // users informations when creating a new notification
    val renternameLabel = new Label("Renter name:")
    val renterName = new Label(s"${n.publisher.name}")
    renterName.font = boldFont

    val renteraddressLabel = new Label("Renter address:")
    val renterAddress = new Label(s"${n.publisher.address}")
    renterAddress.font = boldFont

    val renterphoneLabel = new Label("Phone number:")
    val renterPhone = new Label(s"${n.publisher.phoneNumber}")
    renterPhone.font = boldFont

    val renterHeader = new Label("Renters contact information"):
        font = new Font(14)

    val renterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.CenterLeft)

      children = Array(renterHeader, renternameLabel, renterName, renteraddressLabel, renterAddress, renterphoneLabel, renterPhone)



    val rentButton = new Button("Make a rent"):
      font = Font("System", FontWeight.Bold, 15)
    val cancelButton = new Button("Cancel")



    val rentMakerHeader = new Label("Input your information"):
        font = new Font(14)
    val rentMakerNameField = new TextField():
      promptText = "Name"
    val rentMakerAddressField = new TextField():
      promptText = "Address"
    val rentMakerPhoneField = new TextField():
      promptText = "Phone number"

    val rentMakerBox = new VBox:
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineRight)
      children = Array(rentMakerHeader, rentMakerNameField, rentMakerAddressField, rentMakerPhoneField)


    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 60
      children = Array(cancelButton, rentButton)



    val notifsCalendar = n.calendar

    val calendarBox = new VBox:
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.BottomCenter)

    val calendarHeader = new Label("Select days you want to rent this product"):
        font = new Font(14)

    val renterCalendarBox = new HBox():
      spacing = standardSpacing
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)
      children = Array(renterBox,calendarBox, rentMakerBox)

    val comments = n.comments
    val commentsLabel = new Label(n.comments.mkString("\n"))

    val commentField = new TextField()
    commentField.promptText = "Comment this product"

    val addCommentButton = new Button("Add Comment")
    addCommentButton.onAction = (event) =>
    if commentField.text.value != "" then
      n.comments += commentField.text.value
      comments += commentField.text.value

      commentField.text = ""
    else
      val emptyAlert = new Alert(AlertType.Error):
        title = "Empty Comment"
        headerText = "You can't add empty comment"
        showAndWait()

    calendarBox.children = Array(calendarHeader, notifsCalendar)

    children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterCalendarBox , new Separator, rentButtonBox)

    def cancelBut = cancelButton
    def rentBut = rentButton
    
end MakeRent


*/