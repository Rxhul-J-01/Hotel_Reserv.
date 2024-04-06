
import java.sql.*;
import java.util.*;

public class HotelReservation {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "****";

    private static void reserveRoom(Connection con, Scanner sc){

        try{
            System.out.print("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "Insert into reservations(guest_name,room_number,contact_number)" + "values('" + guestName + "','"+ roomNumber + "','" + contactNumber + "')";

            try(Statement stmt = con.createStatement();){
                int affectedRows = stmt.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation successfull");
                }else {
                    System.out.println("Reservation failed");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection con) throws SQLException{

        String sql = "select reservation_id, guest_name,room_number,contact_number,reservation_date from reservations;";

        try(Statement stmt = con.createStatement();ResultSet rs = stmt.executeQuery(sql);){

            System.out.println("Current Reservations;");
            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date         |");
            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");

            while(rs.next()){
                int id = rs.getInt("reservation_id");
                String name = rs.getString("guest_name");
                int roomNo = rs.getInt("room_number");
                String contactNo = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s    |\n",id,name,roomNo,contactNo,reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+--------------------------+");

        }
    }

    private  static void getRoomNumber(Connection con, Scanner sc){

        System.out.print("Enter reservation ID: ");
        int reservation_id = sc.nextInt();
        System.out.print("Enter guest name: ");
        String guest_name = sc.next();

        String sql = "SELECT room_number FROM reservations " +
                "WHERE reservation_id = " + reservation_id +
                " AND guest_name = '" + guest_name + "'";

        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);){
            if(rs.next()){
                int room_number = rs.getInt("room_number");
                System.out.println("Room number for Reservation ID " +reservation_id+" and Guest "+guest_name + " is: "+room_number);
            }else{
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation(Connection con, Scanner sc){

        try{
            System.out.println("Enter reservation ID to update");
            int reservationID = sc.nextInt();
            sc.nextLine();

            if(!reservationExists(con,reservationID)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "update reservations set guest_name = '" +newGuestName+"', " +
                    "room_number = " +newRoomNumber+", "+
                    "contact_number = '"+newContactNumber+"' "+
                    "where reservation_id = " + reservationID;

            try(Statement stmt = con.createStatement();){
                int affectedRows = stmt.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation updated successfully");
                }else{
                    System.out.println("Reservation update failed");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner sc){

        try{
            System.out.println("Enter reservation ID to delete: ");
            int reservation_id = sc.nextInt();

            if(!reservationExists(con,reservation_id)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "delete from reservations where reservation_id = "+reservation_id;

            try(Statement stmt = con.createStatement();){
                int affectedRows = stmt.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation deleted successfully");
                }else{
                    System.out.println("Reservation deletion failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean reservationExists(Connection con, int reservationId){
        try{
            String sql = "Select reservation_id from reservations where reservation_id = "+reservationId;

            try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)){

                return  rs.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{

        System.out.println("Exiting System");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou for Using Hotel Reservation System");
    }



    public static void main(String[] args) throws ClassNotFoundException,SQLException {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded successfully");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try(Connection con = DriverManager.getConnection(url,username,password)){

            while(true){
                System.out.println();
                System.out.println("Hotel Mangagement System");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(con,sc);
                        break;
                    case 2:
                        viewReservations(con);
                        break;
                    case 3:
                        getRoomNumber(con,sc);
                        break;
                    case 4:
                        updateReservation(con,sc);
                        break;
                    case 5:
                        deleteReservation(con,sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
