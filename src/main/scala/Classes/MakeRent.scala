package Classes

import Classes.RentigAppGui.{standardPadding, standardSpacing}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, DatePicker, Label, Separator}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.{Font, FontWeight}

class MakeRent(n: Notification) extends VBox:

    val notification = n

    padding = Insets.apply(5,5,5,5)
    spacing = 10
  
    val boldFont = Font("Arial", FontWeight.Bold, 14)

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

    val renterBox = new VBox():
      padding = standardPadding
      spacing = standardSpacing

      children = Array(renternameLabel, renterName, renteraddressLabel, renterAddress, renterphoneLabel, renterPhone)

    val renterBoxHeader = new HBox():
      padding = standardPadding
      this.setAlignment(Pos.BaselineCenter)

      val header = new Label("Renters contact information and calendar to see products availability"):
        font = new Font(15)
      children = header

    val rentButton = new Button("Make a rent")
    rentButton.font = Font("System", FontWeight.Bold, 15)

    val rentCancelButton = new Button("Cancel")

    val rentButtonBox = new HBox():
      padding = Insets.apply(30,5,5,5)
      spacing = standardSpacing * 60
      children = Array(rentCancelButton, rentButton)

    val notifsCalendar = n.calendar

    val calendarBox = new VBox:
      padding = standardPadding
      spacing = standardSpacing
      this.setAlignment(Pos.BaselineCenter)

    calendarBox.children = Array(notifsCalendar)

    val renterCalendarBox = new HBox():
      children = Array(renterBox,calendarBox)


    children = Array(rentHeader, rentTitleBox, rentDescriptionBox, rentQuantityBox, rentPriceBox, rentCategoryBox,
      new Separator, renterBoxHeader, renterCalendarBox , new Separator, rentButtonBox)
    
    def cancelBut = rentCancelButton
    def rentBut = rentButton
    
end MakeRent
