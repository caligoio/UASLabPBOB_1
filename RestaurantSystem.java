import java.util.List;
import java.util.ArrayList;

public class RestaurantSystem {
    private List<MenuItem> daftarMenu = new ArrayList<>();
    private List<Pegawai> daftarPegawai = new ArrayList<>();
    private List<Pesanan> daftarPesanan = new ArrayList<>();

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
        System.out.println("Pesanan dengan ID " + pesanan.getIdPesanan() + " ditambahkan.");
    }

    public void lihatMenu() {
        System.out.println("Daftar Menu:");
        for (MenuItem item : daftarMenu) {
            System.out.println(item.getNama() + " - Rp" + item.getHarga());
        }
    }
}
