public class Struk {
    public void cetak(Transaksi transaksi) {
        if (transaksi == null) return;
        System.out.println("==== STRUK TRANSAKSI ====");
        System.out.println("ID Transaksi: " + transaksi.getIdTransaksi());
        Pesanan p = transaksi.getPesanan();
        if (p != null) {
            System.out.println("ID Pesanan: " + p.getIdPesanan());
            System.out.println("No Meja: " + (p.getMeja() != null ? p.getMeja().getNomor() : "-"));
            System.out.println("Status: " + p.getStatus());
            System.out.println("Items:");
            double total = 0.0;
            for (DetailPesanan d : p.getDaftarItem()) {
                double harga = d.getItem().getHarga();
                int j = d.getJumlah();
                double subtotal = harga * j;
                total += subtotal;
                System.out.println(" - " + d.getItem().getNama() + " x" + j + " @" + harga + " => " + subtotal);
            }
            System.out.println("Total: Rp " + total);
        }
        Pembayaran pay = transaksi.getPembayaran();
        if (pay != null) {
            String tipe = pay instanceof CardPayment ? "Card" : pay instanceof CashPayment ? "Cash" : pay instanceof QRISPayment ? "QRIS" : "Unknown";
            System.out.println("Pembayaran: " + tipe + " (ID: " + pay.getIdPembayaran() + ")");
        }
        System.out.println("=========================");
    }
}
