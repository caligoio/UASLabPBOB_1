public class Meja {
   private int nomor; 
   private String status;

   public Meja(int nomor, String status) {
       this.nomor = nomor;
       this.status = status;
   }

   //accessors
   public int getNomor() {
       return nomor;
   }

    public String getStatus() {
         return status;
    }

    //mutators
    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
