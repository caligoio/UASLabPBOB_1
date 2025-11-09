public class CashPayment implements Pembayaran {
    private int idPembayaran;

    public CashPayment(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    @Override
    public int getIdPembayaran() {
        return idPembayaran;
    }

    @Override
    public void setIdPembayaran(int id) {
        this.idPembayaran = id;
    }
    
}
