public class FoodItem extends Item {
    private int quantity;

    public FoodItem(String name, double price, int quantity) {
        super(name, price);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void display() {
        System.out.println("Item: " + getName() + ", Price: " + getPrice() + ", Quantity: " + quantity);
    }

    @Override
    public String getBillDetail() {
    String itemName = getName();
    String formattedItemName = "";
    int maxLength = 14;

    // Wrap item name into lines of maxLength characters
    for (int i = 0; i < itemName.length(); i += maxLength) {
        formattedItemName += itemName.substring(i, Math.min(itemName.length(), i + maxLength)) + "\n";
    }

    // Remove trailing newline character if present
    if (formattedItemName.endsWith("\n")) {
        formattedItemName = formattedItemName.substring(0, formattedItemName.length() - 1);
    }

    // Format the bill detail string
    String billDetail = formattedItemName; // Start a new line for price, quantity, and total
    billDetail += "\t         " + getPrice() + "\t" + quantity + "\t" + (getPrice() * quantity);

    return billDetail;
}

}
