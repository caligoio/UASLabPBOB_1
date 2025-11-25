public class CardPayment implements Pembayaran {
    private int idPembayaran;

    public CardPayment(int idPembayaran) {
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
