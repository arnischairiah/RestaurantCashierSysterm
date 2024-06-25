import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Image;

public class RestaurantCashierSystem {
    private JFrame frame;
    private JComboBox<String> itemComboBox;
    private JTextField quantityField, paymentField;
    private JTextArea orderArea;
    private OrderProcessor orderProcessor;
    private Map<String, Double> itemPrices;
    private StringBuilder paymentInput;
    private boolean paymentCompleted = false;
     private JPanel keypadPanel;


    public RestaurantCashierSystem() {
        orderProcessor = new OrderProcessor(0.1); // 10% tax
        frame = new JFrame("Restaurant Cashier System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.getContentPane().setBackground(Color.decode("#FFE4C4"));
        frame.setLayout(null);

        itemPrices = new HashMap<>();
        itemPrices.put("Nasi Lemak", 10.0);
        itemPrices.put("Roti Canai", 2.0);
        itemPrices.put("Char Kway Teow", 7.0);
        itemPrices.put("Ayam Geprek Indonesia", 7.0);
        itemPrices.put("Satay", 12.0);
        itemPrices.put("Laksa", 8.0);
        itemPrices.put("Hainan Chicken Rice", 10.0);
        itemPrices.put("Mee Goreng Mamak", 6.0);
        itemPrices.put("Teh Tarik", 3.0);
        itemPrices.put("Ais Kacang", 8.0);
        itemPrices.put("Cendol", 5.0);
        itemPrices.put("Rendang", 20.0);
        itemPrices.put("Nasi Kandar", 13.0);
        itemPrices.put("Kuih-muih", 3.0);

        // Load and display the image
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/logo.png"));
        imageLabel.setIcon(imageIcon);
        imageLabel.setBounds(150, 0, 200, 200);
        frame.add(imageLabel);

        JLabel itemLabel = new JLabel("Select Item:");
        itemLabel.setBounds(20, 210, 100, 30);
        frame.add(itemLabel);

        itemComboBox = new JComboBox<>(itemPrices.keySet().toArray(new String[0]));
        itemComboBox.setEditable(true); // Allow user input
        itemComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = ((JTextField) itemComboBox.getEditor().getEditorComponent()).getText().trim().toLowerCase();
                filterItems(input);
            }
        });
        itemComboBox.setBounds(120, 210, 150, 30);
        frame.add(itemComboBox);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 250, 100, 30);
        frame.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(120, 250, 150, 30);
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addItemFromFields();
                }
            }
        });
        frame.add(quantityField);

        JButton addButton = new JButton("Add Item");
        addButton.setBounds(360, 233, 100, 30);
        frame.add(addButton);

        orderArea = new JTextArea();
        orderArea.setBounds(20, 300, 500, 100);
        orderArea.setEditable(false); // Set JTextArea as non-editable
        orderArea.setLineWrap(true); // Enable text wrapping
        orderArea.setWrapStyleWord(true); // Wrap at word boundaries
        frame.add(orderArea);

        JLabel paymentLabel = new JLabel("Cash:");
        paymentLabel.setBounds(20, 560, 150, 30);
        frame.add(paymentLabel);

        paymentField = new JTextField();
        paymentField.setBounds(170, 560, 150, 30);
        frame.add(paymentField);

        // Add KeyListener to paymentField to handle Enter key
        paymentField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processEnterCommand();
                }
            }
        });

        paymentInput = new StringBuilder();

        JPanel keypadPanel = new JPanel();
        keypadPanel.setLayout(null);
        keypadPanel.setBounds(400, 450, 400, 300);
        frame.add(keypadPanel);

        String[] buttons = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "", "0", "C", "Del", "↵"};

        int x = 0, y = 0;
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setBounds(x * 50, y * 50, 50, 50);
            button.addActionListener(new KeypadListener());
            keypadPanel.add(button);
            x++;
            if (x > 2) {
                x = 0;
                y++;
            }
        }

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemFromFields();
            }
        });

        frame.setVisible(true);
    }

    private void addItemFromFields() {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if quantity is invalid
        }

        // Check if the selected item exists in itemPrices
        if (!itemPrices.containsKey(selectedItem)) {
            JOptionPane.showMessageDialog(frame, "Selected item is not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if item is not found
        }

        double price = itemPrices.get(selectedItem);
        FoodItem foodItem = new FoodItem(selectedItem, price, quantity);
        orderProcessor.addItem(foodItem);
        updateOrderArea();

        // Reset fields after adding item
        itemComboBox.setSelectedIndex(-1); // Unselect item
        quantityField.setText("");
    }

    private void updateOrderArea() {
        orderArea.setText("");
        orderArea.append("Item\tPrice\tQuantity\tTotal\n");
        for (FoodItem item : orderProcessor.orderList) {
            orderArea.append(item.getBillDetail() + "\n");
        }
        double subtotal = orderProcessor.calculateSubtotal();

        orderArea.append("\nSubtotal:\t" + subtotal);
        orderArea.append("\nTax:\t" + orderProcessor.calculateTax(subtotal));
        orderArea.append("\nTotal:\t" + orderProcessor.calculateTotal());

        // Check if payment received is greater than 0
        if (orderProcessor.getPaymentReceived() > 0) {
            orderArea.append("\nCash:\t" + orderProcessor.getPaymentReceived());
            orderArea.append("\nChange:\t" + orderProcessor.calculateChange());
        }

        // Adjust JTextArea size based on preferred size
        orderArea.setSize(orderArea.getPreferredSize());
    }

    private void filterItems(String input) {
        List<String> filteredItems = itemPrices.keySet().stream()
                .filter(item -> item.toLowerCase().contains(input))
                .collect(Collectors.toList());

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(filteredItems.toArray(new String[0]));
        itemComboBox.setModel(model);
        itemComboBox.setSelectedItem(input); // Keep user's input
        itemComboBox.showPopup(); // Show the dropdown
    }

    private void processEnterCommand() {
    try {
        double paymentReceived = Double.parseDouble(paymentInput.toString());
        orderProcessor.setPaymentReceived(paymentReceived);

        // Check if payment is sufficient
        double totalAmount = orderProcessor.calculateTotal();
        if (paymentReceived < totalAmount) {
            JOptionPane.showMessageDialog(frame, "Insufficient payment. Please enter more cash.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit method if payment is insufficient
        }

        updateOrderArea();
        paymentField.setText("");
        paymentInput.setLength(0); // Clear input after processing

        // Set paymentCompleted to true and disable input fields
        paymentCompleted = true;
        itemComboBox.setEnabled(false);
        quantityField.setEnabled(false);
        paymentField.setEnabled(false); // Disable paymentField after payment is completed

        // Disable keypad buttons after payment
        disableKeypadButtons();

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(frame, "Please enter a valid payment amount.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void disableKeypadButtons() {
        Component[] components = keypadPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                component.setEnabled(false);
            }
        }
    }


    private class KeypadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("C")) {
                paymentInput.setLength(0);
            } else if (command.equals("Del")) {
                if (paymentInput.length() > 0) {
                    paymentInput.setLength(paymentInput.length() - 1);
                }
            } else if (command.equals("↵")) {
                processEnterCommand();
            } else {
                paymentInput.append(command);
            }
            paymentField.setText(paymentInput.toString());
        }
    }

    public static void main(String[] args) {
        new RestaurantCashierSystem();
    }
}

