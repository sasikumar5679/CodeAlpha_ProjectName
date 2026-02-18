import java.io.*;
import java.util.*;

public class Hotel_reservation {

    // ---------------- Room Class ----------------
    static class Room implements Serializable {
        private int roomId;
        private String category;
        private double price;
        private boolean isAvailable;

        public Room(int roomId, String category, double price) {
            this.roomId = roomId;
            this.category = category;
            this.price = price;
            this.isAvailable = true;
        }

        public int getRoomId() { return roomId; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }

        @Override
        public String toString() {
            return "Room ID: " + roomId +
                    ", Category: " + category +
                    ", Price: $" + price +
                    ", Available: " + isAvailable;
        }
    }

    // ---------------- Booking Class ----------------
    static class Booking implements Serializable {
        private String bookingId;
        private String customerName;
        private int roomId;
        private String category;
        private double amount;

        public Booking(String bookingId, String customerName,
                       int roomId, String category, double amount) {
            this.bookingId = bookingId;
            this.customerName = customerName;
            this.roomId = roomId;
            this.category = category;
            this.amount = amount;
        }

        public String getBookingId() { return bookingId; }
        public int getRoomId() { return roomId; }

        @Override
        public String toString() {
            return "\nBooking ID: " + bookingId +
                    "\nCustomer: " + customerName +
                    "\nRoom ID: " + roomId +
                    "\nCategory: " + category +
                    "\nAmount Paid: $" + amount;
        }
    }

    // ---------------- Data Storage ----------------
    private static List<Room> rooms = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();

    private static final String ROOM_FILE = "rooms.dat";
    private static final String BOOKING_FILE = "bookings.dat";

    // ---------------- Main Method ----------------
    public static void main(String[] args) {

        loadRooms();
        loadBookings();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- HOTEL RESERVATION SYSTEM ---");
            System.out.println("1. Search Room");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter category (Standard/Deluxe/Suite): ");
                    searchRooms(sc.nextLine());
                    break;

                case 2:
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter category: ");
                    bookRoom(name, sc.nextLine());
                    break;

                case 3:
                    System.out.print("Enter Booking ID: ");
                    cancelBooking(sc.nextLine());
                    break;

                case 4:
                    viewBookings();
                    break;

                case 5:
                    saveRooms();
                    saveBookings();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------- Load & Save Methods ----------------

    private static void loadRooms() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(ROOM_FILE))) {
            rooms = (List<Room>) ois.readObject();
        } catch (Exception e) {
            rooms.add(new Room(101, "Standard", 100));
            rooms.add(new Room(102, "Standard", 100));
            rooms.add(new Room(201, "Deluxe", 200));
            rooms.add(new Room(202, "Deluxe", 200));
            rooms.add(new Room(301, "Suite", 350));
            saveRooms();
        }
    }

    private static void saveRooms() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(ROOM_FILE))) {
            oos.writeObject(rooms);
        } catch (Exception e) {
            System.out.println("Error saving rooms.");
        }
    }

    private static void loadBookings() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(BOOKING_FILE))) {
            bookings = (List<Booking>) ois.readObject();
        } catch (Exception e) {
            bookings = new ArrayList<>();
        }
    }

    private static void saveBookings() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(BOOKING_FILE))) {
            oos.writeObject(bookings);
        } catch (Exception e) {
            System.out.println("Error saving bookings.");
        }
    }

    // ---------------- Features ----------------

    private static void searchRooms(String category) {
        boolean found = false;
        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category)
                    && room.isAvailable()) {
                System.out.println(room);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No rooms available.");
        }
    }

    private static void bookRoom(String name, String category) {
        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category)
                    && room.isAvailable()) {

                room.setAvailable(false);

                String bookingId =
                        UUID.randomUUID().toString().substring(0, 8);

                Booking booking = new Booking(
                        bookingId,
                        name,
                        room.getRoomId(),
                        category,
                        room.getPrice()
                );

                bookings.add(booking);
                saveRooms();
                saveBookings();

                System.out.println("Payment Successful ✅");
                System.out.println("Booking ID: " + bookingId);
                return;
            }
        }
        System.out.println("No rooms available.");
    }

    private static void cancelBooking(String bookingId) {
        Iterator<Booking> iterator = bookings.iterator();

        while (iterator.hasNext()) {
            Booking booking = iterator.next();

            if (booking.getBookingId().equals(bookingId)) {

                for (Room room : rooms) {
                    if (room.getRoomId() == booking.getRoomId()) {
                        room.setAvailable(true);
                    }
                }

                iterator.remove();
                saveRooms();
                saveBookings();

                System.out.println("Booking Cancelled Successfully ❌");
                return;
            }
        }

        System.out.println("Booking ID not found.");
    }

    private static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }
}
