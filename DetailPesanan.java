public class DetailPesanan {
    private MenuItem item;
    private int jumlah;
    private String catatan;

    public DetailPesanan(MenuItem item, int jumlah, String catatan) {
        this.item = item;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    // Accessors (getters)
    public MenuItem getItem() {
        return item;
    }   

    public int getJumlah() {
        return jumlah;
    }

    public String getCatatan() {
        return catatan;
    }

    // Mutators (setters)
    public void setItem(MenuItem item) {
        this.item = item;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
