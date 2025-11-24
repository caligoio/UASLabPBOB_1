import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static SessionManager instance;
    private Akun currentUser;
    private String previousScreen; // Track previous screen for navigation
    private int currentTableNumber = -1; // Track customer's table number
    private Map<Integer, String> occupiedTables = new HashMap<>(); // Track occupied tables: table number -> customer name

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Akun akun) {
        this.currentUser = akun;
    }

    public Akun getCurrentUser() {
        return currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }

    // Set previous screen before navigating
    public void setPreviousScreen(String screenName) {
        this.previousScreen = screenName;
    }

    // Get previous screen
    public String getPreviousScreen() {
        return previousScreen;
    }

    // Set table number for customer
    public void setCurrentTableNumber(int tableNumber) {
        this.currentTableNumber = tableNumber;
    }

    // Get table number
    public int getCurrentTableNumber() {
        return currentTableNumber;
    }

    // method untuk ambil tipe user
    public String getUserType() {
        if (currentUser instanceof Pegawai) {
            return "Pegawai";
        } else if (currentUser instanceof Customer) {
            return "Customer";
        } else {
            return "Tidak diketahui";
        }
    }

    // method untuk ambil peran jika user adalah pegawai
    public String getUserRole() {
        if (currentUser instanceof Pegawai) {
            Pegawai p = (Pegawai) currentUser;
            return p.getPeran();
        }
        return "-";
    }

    // method untuk debugging
    public void printSessionInfo() {
        if (currentUser == null) {
            System.out.println("Belum ada user yang login.");
            return;
        }

        System.out.println("Nama: " + currentUser.getNama());
        System.out.println("UserType: " + getUserType());

        if (currentUser instanceof Pegawai) {
            System.out.println("Peran: " + getUserRole());
        }
    }

    // Check if a table is currently occupied
    public boolean isTableOccupied(int tableNumber) {
        return occupiedTables.containsKey(tableNumber);
    }

    // Get the customer name occupying a table
    public String getTableOccupiedBy(int tableNumber) {
        return occupiedTables.get(tableNumber);
    }

    // Occupy a table for a customer
    public void occupyTable(int tableNumber, String customerName) {
        occupiedTables.put(tableNumber, customerName);
        System.out.println("Table " + tableNumber + " occupied by " + customerName);
        System.out.println("Current occupied tables: " + occupiedTables);
    }

    // Release a table (when customer pays)
    public void releaseTable(int tableNumber) {
        if (occupiedTables.containsKey(tableNumber)) {
            String customer = occupiedTables.remove(tableNumber);
            System.out.println("Table " + tableNumber + " released by " + customer);
        }
        System.out.println("Current occupied tables: " + occupiedTables);
    }

    // Release all tables for a customer (when they logout)
    public void releaseTableByCustomer(String customerName) {
        occupiedTables.entrySet().removeIf(entry -> entry.getValue().equals(customerName));
        System.out.println("All tables released for customer: " + customerName);
        System.out.println("Current occupied tables: " + occupiedTables);
    }

    // Get a table number occupied by a specific customer (first found), or -1 if none
    public int getFirstTableOccupiedByCustomer(String customerName) {
        for (Map.Entry<Integer, String> e : occupiedTables.entrySet()) {
            if (e.getValue().equals(customerName)) {
                return e.getKey();
            }
        }
        return -1;
    }
}
