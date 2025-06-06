import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class CashRegister {

    static Scanner scan = new Scanner(System.in);

    static ArrayList<String> usernames = new ArrayList<>();
    static ArrayList<String> passwords = new ArrayList<>();
    static ArrayList<String> productNames = new ArrayList<>();
    static ArrayList<Integer> productQuantities = new ArrayList<>();
    static ArrayList<Double> productPrices = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("========== HOME PAGE ==========");

        while (true) {
            System.out.println("\n[1] Sign Up");
            System.out.println("[2] Log In");
            System.out.println("[3] Exit");
            System.out.print("OPTION: ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1":
                    signup();
                    break;
                case "2":
                    String loggedInUser = login();
                    if (loggedInUser != null) {
                        accessCashRegister(loggedInUser);
                    }
                    break;
                case "3":
                    System.out.println("Thank you! Exiting the system now.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void signup() {
        System.out.println("\n---- SIGN UP ----");

        String username;
        while (true) {
            System.out.print("Enter username (Must be alphanumeric and 5-15 characters): ");
            username = scan.nextLine();

            if (username.matches("^[a-zA-Z0-9]{5,15}$")) {
                if (usernames.contains(username)) {
                    System.out.println("Username already taken.");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid username format.");
            }
        }

        String password;
        while (true) {
            System.out.print("Enter password (8-20 chars, 1 uppercase, 1 digit): ");
            password = scan.nextLine();

            if (password.matches("^(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$")) {
                break;
            } else {
                System.out.println("Invalid password format. Try again.");
            }
        }

        usernames.add(username);
        passwords.add(password);
        System.out.println("Sign Up successful! You can now log in.");
    }

    public static String login() {
        System.out.println("\n---- LOG IN ----");

        while (true) {
            System.out.print("Username: ");
            String inputUser = scan.nextLine();
            System.out.print("Password: ");
            String inputPass = scan.nextLine();

            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(inputUser) && passwords.get(i).equals(inputPass)) {
                    System.out.println("Login successful!");
                    System.out.println("Welcome, " + inputUser + "!");
                    return inputUser;
                }
            }

            System.out.println("Incorrect username or password. Try again.");
        }
    }

    public static void accessCashRegister(String username) {
        boolean keepGoing = true;

        while (keepGoing) {
            productNames.clear();
            productQuantities.clear();
            productPrices.clear();

            System.out.println("--------------------------------------------");
            System.out.println("       WELCOME TO THE ICE CREAM SHOP!      ");
            System.out.println("        lick it. love it. crave it!        ");
            System.out.println("--------------------------------------------");

            System.out.print("Customer Name: ");
            String customerName = scan.nextLine();

            // Optional: Auto-generate date/time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm:ss");
            String date = dtfDate.format(now);
            String time = dtfTime.format(now);

            // Uncomment the lines below if you prefer user input for date/time
            // System.out.print("Date: ");
            // String date = scan.nextLine();
            // System.out.print("Time: ");
            // String time = scan.nextLine();

            System.out.println();

            inputProducts();

            System.out.print("Do you want to proceed with the checkout? (yes/no): ");
            String confirm = scan.nextLine();
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Transaction canceled.");
            } else {
                double totalPrice = checkout();
                processPayment(totalPrice);
                logTransaction(username, customerName, date, time, totalPrice);
                System.out.println("Transaction recorded successfully.\n");
            }

            System.out.print("Do you want to perform another transaction? (yes/no): ");
            String response = scan.nextLine();
            if (!response.equalsIgnoreCase("yes")) {
                keepGoing = false;
                System.out.println();
                System.out.println("----------------------------------------------------");
                System.out.println("             THANKS FOR YOUR PURCHASE!");
                System.out.println("              ENJOY YOUR SWEET TREAT!");
                System.out.println("      LOOKING FORWARD TO OUR NEXT TRANSACTION!  ");
                System.out.println("----------------------------------------------------");
            }
        }
    }

    public static void inputProducts() {
        while (true) {
            System.out.print("Product Name ('done' to pay, 'delete' to remove): ");
            String pn = scan.nextLine();

            if (pn.equalsIgnoreCase("done")) break;
            if (pn.equalsIgnoreCase("delete")) {
                deleteProduct();
                continue;
            }

            try {
                System.out.print("Price: ");
                double price = Double.parseDouble(scan.nextLine());
                System.out.print("Quantity: ");
                int quantity = Integer.parseInt(scan.nextLine());

                if (price < 0 || quantity < 0) throw new NumberFormatException();
                addProduct(pn, quantity, price);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    public static void addProduct(String name, int quantity, double price) {
        productNames.add(name);
        productQuantities.add(quantity);
        productPrices.add(price);
        System.out.println(name + " added successfully!\n");
    }

    public static void deleteProduct() {
        if (productNames.isEmpty()) {
            System.out.println("No products to delete.");
            return;
        }

        System.out.print("Enter product name to delete: ");
        String deleteProduct = scan.nextLine();
        int index = productNames.indexOf(deleteProduct);

        if (index != -1) {
            productNames.remove(index);
            productQuantities.remove(index);
            productPrices.remove(index);
            System.out.println(deleteProduct + " has been removed from the list.");
        } else {
            System.out.println("Product not found.");
        }
    }

    public static double checkout() {
        double totalPrice = 0;
        System.out.println("\n----------------------------");
        System.out.println("      Items Purchased:     ");
        System.out.println("----------------------------");

        for (int i = 0; i < productNames.size(); i++) {
            double itemTotal = productQuantities.get(i) * productPrices.get(i);
            totalPrice += itemTotal;
            System.out.printf("%d x %s\n@ %.2f = %.2f\n\n", productQuantities.get(i), productNames.get(i),
                    productPrices.get(i), itemTotal);
        }

        System.out.println("----------------------------");
        System.out.printf("Total Price: PHP %.2f%n", totalPrice);
        System.out.println();
        return totalPrice;
    }

    public static void processPayment(double totalPrice) {
        String paymentMethod;
        while (true) {
            System.out.println("Select mode of payment (Cash, Debit, Credit, GCash)");
            System.out.print("Enter your choice: ");
            paymentMethod = scan.nextLine().toLowerCase();

            if (paymentMethod.equals("cash") || paymentMethod.equals("debit") ||
                paymentMethod.equals("credit") || paymentMethod.equals("gcash")) {
                break;
            } else {
                System.out.println("Invalid payment method. Please choose Cash, Debit, Credit, or GCash.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter payment amount: PHP ");
                double payment = Double.parseDouble(scan.nextLine());

                if (payment < totalPrice) {
                    System.out.println("Insufficient payment. Please enter a valid amount.");
                    continue;
                }

                double change = payment - totalPrice;
                System.out.printf("Change: PHP %.2f%n", change);
                System.out.println();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }
    }

    public static void logTransaction(String username, String customerName, String date, String time, double totalPrice) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write("\n========= NEW TRANSACTION =========\n");
            writer.write("Cashier: " + username + "\n");
            writer.write("Customer: " + customerName + "\n");
            writer.write("Date: " + date + "\n");
            writer.write("Time: " + time + "\n");
            writer.write("Items:\n");

            for (int i = 0; i < productNames.size(); i++) {
                int qty = productQuantities.get(i);
                double price = productPrices.get(i);
                double total = qty * price;
                writer.write(qty + " x " + productNames.get(i) + " @ " + String.format("%.2f", price) +
                             " = " + String.format("%.2f", total) + "\n");
            }

            writer.write("Total: PHP " + String.format("%.2f", totalPrice) + "\n");
            writer.write("===================================\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error writing transaction to file: " + e.getMessage());
        }
    }
}
