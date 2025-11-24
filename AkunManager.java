import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AkunManager {
    private static AkunManager instance; // Singleton instance
    private List<Akun> daftarAkun = new ArrayList<>();
    private int nextId = 1;
    private static final String DATA_FILE = "akun_data.txt";

    // Private constructor untuk Singleton pattern
    private AkunManager() {
        loadDataFromFile();
    }

    // Get instance (Singleton)
    public static AkunManager getInstance() {
        if (instance == null) {
            instance = new AkunManager();
        }
        return instance;
    }

    public void tambahAkun(Akun akun) {
        akun.id = nextId++;
        daftarAkun.add(akun);
        saveDataToFile(); // Simpan ke file setiap ada akun baru
    }

    public Akun cariAkun(String nama, String password) {
        for (Akun akun : daftarAkun) {
            if (akun.getNama().equals(nama) && akun.getPassword().equals(password)) {
                return akun;
            }
        }
        return null; // not found
    }

    public List<Akun> getDaftarAkun() {
        return daftarAkun;
    }

    // Simpan data akun ke file
    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write(String.valueOf(nextId)); // Simpan nextId
            writer.newLine();
            
            for (Akun akun : daftarAkun) {
                String type = akun.getClass().getSimpleName();
                if (type.equals("Customer")) {
                    writer.write("CUSTOMER|" + akun.id + "|" + akun.getNama() + "|" + akun.getPassword());
                } else if (type.equals("Pegawai")) {
                    Pegawai pegawai = (Pegawai) akun;
                    writer.write("PEGAWAI|" + akun.id + "|" + akun.getNama() + "|" + akun.getPassword() + "|" + pegawai.getPeran());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error menyimpan data: " + e.getMessage());
        }
    }

    // Load data akun dari file
    private void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            nextId = 1;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                nextId = Integer.parseInt(line);
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String type = parts[0];
                    int id = Integer.parseInt(parts[1]);
                    String nama = parts[2];
                    String password = parts[3];

                    if (type.equals("CUSTOMER")) {
                        Customer customer = new Customer(id, nama, password);
                        daftarAkun.add(customer);
                    } else if (type.equals("PEGAWAI") && parts.length >= 5) {
                        String peran = parts[4];
                        Pegawai pegawai = new Pegawai(id, nama, password, peran);
                        daftarAkun.add(pegawai);
                    }
                }
            }
            System.out.println("Data akun berhasil dimuat dari file. Total akun: " + daftarAkun.size());
        } catch (IOException e) {
            System.err.println("Error membaca data: " + e.getMessage());
        }
    }

    // Clear semua data (untuk testing)
    public void clearAllData() {
        daftarAkun.clear();
        nextId = 1;
        new File(DATA_FILE).delete();
    }
}


