abstract class Akun {
    protected int id;
    protected String nama;
    protected String password;

    public Akun(int id, String nama, String password) {
        this.id = id;
        this.nama = nama;
        this.password = password;
    }

    // Accessors (getters)
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getPassword() {
        return password;
    }

    // Mutators (setters)
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

