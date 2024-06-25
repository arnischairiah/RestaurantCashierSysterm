import java.io.*;
import java.util.ArrayList;

public class OrderProcessor {
    ArrayList<FoodItem> orderList;
    private double taxRate;
    private double paymentReceived;

    public OrderProcessor(double taxRate) {
        this.orderList = new ArrayList<>();
        this.taxRate = taxRate;
        this.paymentReceived = 0.0;
    }

    // Add item to the order
    public void addItem(FoodItem item) {
        boolean found = false;
        for (FoodItem fi : orderList) {
            if (fi.getName().equals(item.getName())) {
                fi.setQuantity(fi.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            orderList.add(item);
        }
    }

    // Remove item from the order
    public void removeItem(String itemName) {
        orderList.removeIf(item -> item.getName().equals(itemName));
    }

    // Calculate total price before tax
    public double calculateSubtotal() {
        double subtotal = 0;
        for (FoodItem item : orderList) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return Math.round(subtotal * 100.0) / 100.0; // Round to 2 decimal places
    }

    // Calculate tax
    public double calculateTax(double subtotal) {
        return Math.round(subtotal * taxRate * 100.0) / 100.0; // Round to 2 decimal places
    }

    // Calculate final total
    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double tax = calculateTax(subtotal);
        return Math.round((subtotal + tax) * 100.0) / 100.0; // Round to 2 decimal places
    }

    
    // Save detailed bill to file
    public void saveBill(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Item\tPrice\tQuantity\tTotal\n");
            for (FoodItem item : orderList) {
                writer.write(item.getBillDetail() + "\n");
            }
            double subtotal = calculateSubtotal();
            writer.write("\nSubtotal:\t" + subtotal);
            writer.write("\nTax:\t" + calculateTax(subtotal));
            writer.write("\nTotal:\t" + calculateTotal());
            writer.write("\nPayment Received:\t" + paymentReceived);
            writer.write("\nChange:\t" + calculateChange());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle payment
    public void setPaymentReceived(double paymentReceived) {
        this.paymentReceived = paymentReceived;
    }
     public double getPaymentReceived() {
        return paymentReceived;
    }

    public double calculateChange() {
        return Math.round((paymentReceived - calculateTotal()) * 100.0) / 100.0; // Round to 2 decimal places
    }

    // Display order
    public void displayOrder() {
        for (FoodItem item : orderList) {
            item.display();
        }
    }
    
public void editItem(int index, FoodItem item) {
    orderList.set(index, item);
}

public void deleteItem(int index) {
    orderList.remove(index);
}
}


