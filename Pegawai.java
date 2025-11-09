public class Pegawai extends Akun {
    private String peran;

    public Pegawai(int id, String nama, String password, String peran) {
        super(id, nama, password);
        this.peran = peran;
    }

    // Accessors & Mutators for peran
    public String getPeran() {
        return peran;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public void updateStatusPesanan() {
        System.out.println(peran + " " + nama + " memperbarui status pesanan.");
    }
}
