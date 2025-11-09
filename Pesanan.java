import java.util.List;
import java.util.ArrayList;

public class Pesanan {
    private int idPesanan;
    private String status;
    private List<DetailPesanan> daftarItem = new ArrayList<>();
    private Meja meja;

    public Pesanan(int idPesanan, String status, Meja meja) {
        this.idPesanan = idPesanan;
        this.status = status;
        this.meja = meja;
    }   

    //accessors
    public int getIdPesanan() {
        return idPesanan;
    }

    public String getStatus() {
        return status;
    }

    public List<DetailPesanan> getDaftarItem() {
        return daftarItem;
    }

    public Meja getMeja() {
        return meja;
    }

    //mutators
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMeja(Meja meja) {
        this.meja = meja;
    }
}
