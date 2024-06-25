public class Item {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void display() {
        System.out.println("Item: " + name + ", Price: " + price);
    }

    public String getBillDetail() {
        return name + "\t" + price;
    }
}
