public class CardPayment implements Pembayaran {
  private int idPembayaran;

  public CardPayment(int idPembayaran) {
    this.idPembayaran = idPembayaran;
  }

  @override
  public int getIdPembayaran() {
    return idPembayaran;
  }

  @override
  public void setIdPembayaran(int id) {
    this.idPembayaran = id;
  }

}
