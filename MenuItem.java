abstract class MenuItem {
    protected String nama;
    protected double harga;

    public MenuItem(String nama, double harga) {
        this.nama = nama;
        this.harga = harga;
    }

    // Accessors (getters)
    public String getNama() { 
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    // Mutators (setters)
    public void setNama(String nama) {  
        this.nama = nama;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getInfo() {
        return "Nama: " + nama + ", Harga: " + harga;
    }
}
