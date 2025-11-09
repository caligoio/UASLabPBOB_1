public class Transaksi {
  private int idTransaksi;
  private Pesanan pesanan;
  private Pembayaran pembayaran;
  
  public Transaksi(int idTransaksi, Pesanan pesanan, Pembayaran pembayaran) {
      this.idTransaksi = idTransaksi;
      this.pesanan = pesanan;
      this.pembayaran = pembayaran;
  }

  //accessor 
    public int getIdTransaksi() {
        return idTransaksi;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    //mutator
    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
    
    public void setPesanan(Pesanan pesanan) {
        this.pesanan = pesanan;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    public void konfirmasi() {
        
    }
}
