public class Minuman extends MenuItem {
    private String ukuran;
    private String suhu;

    public Minuman(String nama, double harga, String ukuran, String suhu) {
        super(nama, harga);
        this.ukuran = ukuran;
        this.suhu = suhu;
    }

    // Accessor (getter)
    public String getUkuran() {
        return ukuran;
    }

    public String getSuhu() {
        return suhu;
    }

    // Mutator (setter)
    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

     public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    @Override
    public String getInfo() {
        super.getInfo();
        return ", Ukuran: " + ukuran + ", Suhu: " + suhu;
    } 
}
