
import java.io.*;
import java.util.Scanner;

class Ticket {
    String id;
    String customer;
    String issue;

    Ticket(String id, String customer, String issue) {
        this.id = id;
        this.customer = customer;
        this.issue = issue;
    }
}

class Node {
    Ticket data;
    Node next;

    Node(Ticket data) {
        this.data = data;
        this.next = null;
    }
}

public class Main {

    static Node front = null;
    static Node rear = null;
    static int nextId = 101;

    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "tickets.txt";

    // Add ticket to queue
    static void raiseTicket() {
        System.out.print("Enter customer name: ");
        String customer = sc.nextLine();

        System.out.print("Enter issue: ");
        String issue = sc.nextLine();

        if (customer.trim().isEmpty() || issue.trim().isEmpty()) {
            System.out.println("Customer and issue cannot be empty.");
            return;
        }

        String id = "T" + nextId++;

        Ticket ticket = new Ticket(id, customer, issue);
        Node newNode = new Node(ticket);

        if (front == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        int position = getQueueSize();

        System.out.println("Ticket raised. ID: " + id +
                " | " + customer +
                " | " + issue +
                " | Position: " + position);

        saveToFile();
    }

    // Serve the first ticket (FIFO)
    static void serveNextTicket() {
        if (front == null) {
            System.out.println("No tickets in the queue.");
            return;
        }

        Ticket ticket = front.data;

        front = front.next;

        if (front == null) {
            rear = null;
        }

        int waiting = getQueueSize();

        if (waiting > 0) {
            System.out.println("Ticket " + ticket.id +
                    " served. Customer: " + ticket.customer);
            System.out.println("Tickets waiting: " + waiting);
        } else {
            System.out.println("Ticket " + ticket.id +
                    " served. Tickets waiting: 0");
        }

        saveToFile();
    }

    // Display all waiting tickets
    static void viewQueue() {
        if (front == null) {
            System.out.println("No tickets in the queue.");
            return;
        }

        Node temp = front;
        int position = 1;

        System.out.println("\nWaiting: " + getQueueSize());

        while (temp != null) {
            Ticket t = temp.data;

            System.out.println(position + ". " +
                    t.id + " | " +
                    t.customer + " | " +
                    t.issue);

            temp = temp.next;
            position++;
        }
    }

    // Search ticket using ID
    static void searchTicket() {
        System.out.print("Enter ticket ID to search: ");
        String searchId = sc.nextLine().trim();

        Node temp = front;
        int position = 1;

        while (temp != null) {
            if (temp.data.id.equalsIgnoreCase(searchId)) {
                System.out.println("Ticket Found!");
                System.out.println("ID: " + temp.data.id);
                System.out.println("Customer: " + temp.data.customer);
                System.out.println("Issue: " + temp.data.issue);
                System.out.println("Position: " + position);
                return;
            }

            temp = temp.next;
            position++;
        }

        System.out.println("Ticket not found.");
    }

    // Calculate queue size
    static int getQueueSize() {
        int count = 0;
        Node temp = front;

        while (temp != null) {
            count++;
            temp = temp.next;
        }

        return count;
    }

    // Save queue data to file
    static void saveToFile() {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(FILE_NAME))) {

            Node temp = front;

            while (temp != null) {
                Ticket t = temp.data;

                writer.write(t.id + "|" +
                        t.customer.replace("|", "/") + "|" +
                        t.issue.replace("|", "/"));

                writer.newLine();
                temp = temp.next;
            }

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Load queue data from file
    static void loadFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);

                if (parts.length == 3) {
                    Ticket ticket = new Ticket(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim()
                    );

                    Node newNode = new Node(ticket);

                    if (front == null) {
                        front = rear = newNode;
                    } else {
                        rear.next = newNode;
                        rear = newNode;
                    }

                    // Generate next available ID
                    try {
                        int number = Integer.parseInt(
                                ticket.id.substring(1));

                        if (number >= nextId) {
                            nextId = number + 1;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid ticket IDs
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {

        loadFromFile();

        int choice;

        do {
            System.out.println("\n===== SIMPLE TICKET QUEUE SYSTEM =====");
            System.out.println("1. Raise Ticket");
            System.out.println("2. Serve Next Ticket");
            System.out.println("3. View Queue");
            System.out.println("4. Search Ticket by ID");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    raiseTicket();
                    break;

                case 2:
                    serveNextTicket();
                    break;

                case 3:
                    viewQueue();
                    break;

                case 4:
                    searchTicket();
                    break;

                case 5:
                    saveToFile();
                    System.out.println("Data saved successfully.");
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (true);
    }
}
