import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RestaurantSystem {
    private static RestaurantSystem instance; // Singleton instance
    private List<MenuItem> daftarMenu = new ArrayList<>();
    private List<Pegawai> daftarPegawai = new ArrayList<>();
    private List<Pesanan> daftarPesanan = new ArrayList<>();
    private static final String PESANAN_DATA_FILE = "pesanan_data.txt";

    // Private constructor untuk Singleton pattern
    private RestaurantSystem() {
        initMenu();
        loadPesananFromFile();
    }

    // Get instance (Singleton)
    public static RestaurantSystem getInstance() {
        if (instance == null) {
            instance = new RestaurantSystem();
        }
        return instance;
    }

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
        savePesananToFile();
        System.out.println("Pesanan dengan ID " + pesanan.getIdPesanan() + " ditambahkan.");
    }

    public List<Pesanan> getDaftarPesanan() {
        return daftarPesanan;
    }

    // Public method to save current daftarPesanan to file (used after updates)
    public void saveAllPesanan() {
        savePesananToFile();
    }

    public void lihatMenu() {
        System.out.println("Daftar Menu:");
        for (MenuItem item : daftarMenu) {
            System.out.println(item.getNama() + " - Rp" + item.getHarga());
        }
    }

    public List<MenuItem> getDaftarMenu() {
        return daftarMenu;
    }

    private void initMenu() {
        tambahMenu(new Makanan("Nasi Goreng", 10000, 1, "Nasi"));
        tambahMenu(new Makanan("Gacoan", 11000, 6, "Mie"));
        tambahMenu(new Makanan("Mie Bangladesh", 15000, 2, "Mie"));

        tambahMenu(new Minuman("Sanger Pancong", 6000, "kecil", "Panas"));
        tambahMenu(new Minuman("Es Teh", 8000, "Medium", "Dingin"));
        tambahMenu(new Minuman("Kopi Hitam", 10000, "Kecil", "Panas"));
    }

    public void tambahMenu(MenuItem item) {
        daftarMenu.add(item);
        System.out.println("Menu ditambahkan: " + item.getNama());
    }

    /**
     * Simpan data pesanan ke file pesanan_data.txt
     */
    private void savePesananToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PESANAN_DATA_FILE))) {
            for (Pesanan pesanan : daftarPesanan) {
                // Format: ID|Status|Owner|NoMeja|Item1:Harga1:Jumlah1,Item2:Harga2:Jumlah2,...
                StringBuilder itemStr = new StringBuilder();
                for (DetailPesanan detail : pesanan.getDaftarItem()) {
                    if (itemStr.length() > 0) itemStr.append(",");
                    itemStr.append(detail.getItem().getNama()).append(":")
                           .append(detail.getItem().getHarga()).append(":")
                           .append(detail.getJumlah());
                }
                
                String owner = pesanan.getOwner() == null ? "" : pesanan.getOwner();
                String line = pesanan.getIdPesanan() + "|" + 
                             pesanan.getStatus() + "|" + 
                             owner + "|" + 
                             pesanan.getMeja().getNomor() + "|" + 
                             itemStr.toString();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error menyimpan pesanan: " + e.getMessage());
        }
    }

    /**
     * Load data pesanan dari file pesanan_data.txt
     */
    private void loadPesananFromFile() {
        File file = new File(PESANAN_DATA_FILE);
        if (!file.exists()) {
            System.out.println("File pesanan_data.txt tidak ditemukan. Data pesanan kosong.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PESANAN_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    String status = parts[1];
                    String owner = "";
                    int noMeja = -1;
                    String itemsStr = "";

                    // New format expects owner in parts[2]
                    if (parts.length >= 5) {
                        owner = parts[2];
                        noMeja = Integer.parseInt(parts[3]);
                        itemsStr = parts[4];
                    } else {
                        // backward compatible: old format ID|Status|NoMeja|items
                        noMeja = Integer.parseInt(parts[2]);
                        itemsStr = parts[3];
                    }

                    // Create Meja
                    Meja meja = new Meja(noMeja, "Tersedia");
                    
                    // Create Pesanan (include owner if known)
                    Pesanan pesanan = new Pesanan(id, status, owner.isEmpty() ? null : owner, meja);
                    
                    // Add items
                    if (!itemsStr.isEmpty()) {
                        String[] items = itemsStr.split(",");
                        for (String itemData : items) {
                            String[] itemParts = itemData.split(":");
                            if (itemParts.length >= 3) {
                                String itemNama = itemParts[0];
                                double itemHarga = Double.parseDouble(itemParts[1]);
                                int itemJumlah = Integer.parseInt(itemParts[2]);
                                
                                // Find MenuItem dari menu yang ada
                                MenuItem menuItem = null;
                                for (MenuItem m : daftarMenu) {
                                    if (m.getNama().equals(itemNama) && m.getHarga() == itemHarga) {
                                        menuItem = m;
                                        break;
                                    }
                                }
                                
                                if (menuItem != null) {
                                    DetailPesanan detail = new DetailPesanan(menuItem, itemJumlah, "");
                                    pesanan.getDaftarItem().add(detail);
                                }
                            }
                        }
                    }
                    
                    daftarPesanan.add(pesanan);
                }
            }
            System.out.println("Data pesanan berhasil dimuat. Total pesanan: " + daftarPesanan.size());
        } catch (IOException e) {
            System.err.println("Error membaca pesanan: " + e.getMessage());
        }
    }
}

