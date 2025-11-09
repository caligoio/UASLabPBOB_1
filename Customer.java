public class Customer extends Akun {

    public Customer(int id, String nama, String password) {
        super(id, nama, password);
    }   

    public void buatPesanan() {
        System.out.println(nama + " membuat pesanan baru.");
    }
}
