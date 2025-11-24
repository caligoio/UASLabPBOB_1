import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PegawaiApp extends Application {

    @Override
    public void start(Stage stage) {
        // Top section (wide box)
        SessionManager session = SessionManager.getInstance();
        Akun user = session.getCurrentUser();
        Label topBox = new Label(session.getUserType() + " | " + user.getNama() + (session.getUserType().equals("Pegawai") ? " - " + session.getUserRole() : ""));
        topBox.setMinHeight(60);
        topBox.setMinWidth(490);
        topBox.setStyle("-fx-border-color: black; -fx-padding: 20; -fx-alignment: center;");

        // Bottom three boxes (same size)
        Label box1 = new Label("Buatlah pesanan\nuntuk menikmati hidangan kami!");
        Label box2 = new Label("Kelola pesanan jika ada perubahan!");
        Label box3 = new Label("Sudah selesai dengan pesanan Anda?\nbayar di sini!");
        Label box4 = new Label("Silahkan lihat menu\nuntuk memilih hidangan favorit Anda!");

        Button backButton = new Button("Kembali");
        backButton.setOnAction(e -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button box1Button = new Button("Buat Pesanan");
        box1Button.setOnAction(e -> {
            try {
                new PesananApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button box2Button = new Button("Kelola Pesanan");
        box2Button.setOnAction(e -> {
            try {
                new ManagePesanan().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    
        Button box3Button = new Button("Transaksi");
        box3Button.setOnAction(e -> {
            try {
                new TransaksiApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button box4Button = new Button("Lihat Menu");
        box4Button.setOnAction(e -> {
            try {
                new LihatMenuApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
      
        VBox box1Container = new VBox(5, box1, box1Button);
        VBox box2Container = new VBox(5, box2, box2Button);
        VBox box3Container = new VBox(5, box3, box3Button);
        VBox box4Container = new VBox(5, box4, box4Button);

        // HBox for bottom three boxes
         for (VBox box : new VBox[]{box1Container, box2Container, box3Container, box4Container}) {
            box.setPrefSize(235, 120);
            box.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-padding: 10;");
        }

        HBox row1 = new HBox(20, box1Container, box2Container);
        row1.setStyle("-fx-alignment: center; -fx-padding: 20;");

        HBox row2 = new HBox(20, box3Container, box4Container);
        row2.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // VBox for the whole layout
        VBox root = new VBox(20, topBox, row1, row2, backButton);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Interface Pegawai");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
