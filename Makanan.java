public class Makanan extends MenuItem {
    private int tingkatPedas;
    private String kategori;

    public Makanan(String nama, double harga, int tingkatPedas, String kategori) {
        super(nama, harga);
        this.tingkatPedas = tingkatPedas;
        this.kategori = kategori;
    }

    // Accessor (getter)
    public int getTingkatPedas() {
        return tingkatPedas;
    }

    public String getKategori() {
        return kategori;
    }

    // Mutator (setter)
    public void setTingkatPedas(int tingkatPedas) {
        this.tingkatPedas = tingkatPedas;
    }

     public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    @Override
    public String getInfo() {
        super.getInfo();
        return ", Tingkat pedas: " + tingkatPedas + ", Kategori: " + kategori;
    } 
}
