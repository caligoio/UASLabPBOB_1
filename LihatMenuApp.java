import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * LihatMenu - UI to display all available menu items.
 * - Shows menu organized by category (Makanan / Minuman)
 * - Displays item details: name, price, and relevant attributes
 * - Similar layout to PesananApp for consistency
 */
public class LihatMenuApp extends Application {

    @Override
    public void start(Stage stage) {
        RestaurantSystem rs = RestaurantSystem.getInstance();

        Label title = new Label("Daftar Menu");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        // Makanan section
        Label makananLabel = new Label("Daftar Makanan");
        makananLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        ListView<String> makananList = new ListView<>();
        makananList.setPrefHeight(200);

        List<MenuItem> daftarMenu = rs.getDaftarMenu();
        for (MenuItem item : daftarMenu) {
            if (item instanceof Makanan) {
                Makanan m = (Makanan) item;
                String info = m.getNama() + " - Rp " + m.getHarga() 
                    + " (" + m.getKategori() + ", Level: " + m.getTingkatPedas() + ")";
                makananList.getItems().add(info);
            }
        }

        VBox makananSection = new VBox(10, makananLabel, makananList);
        makananSection.setPadding(new Insets(10));
        makananSection.setStyle("-fx-border-color: #ddd; -fx-border-radius:4; -fx-background-color: #fafafa;");

        // Minuman section
        Label minumanLabel = new Label("Daftar Minuman");
        minumanLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        ListView<String> minumanList = new ListView<>();
        minumanList.setPrefHeight(200);

        for (MenuItem item : daftarMenu) {
            if (item instanceof Minuman) {
                Minuman mm = (Minuman) item;
                String info = mm.getNama() + " - Rp " + mm.getHarga() 
                    + " (" + mm.getUkuran() + ", " + mm.getSuhu() + ")";
                minumanList.getItems().add(info);
            }
        }

        VBox minumanSection = new VBox(10, minumanLabel, minumanList);
        minumanSection.setPadding(new Insets(10));
        minumanSection.setStyle("-fx-border-color: #ddd; -fx-border-radius:4; -fx-background-color: #fafafa;");

        // Back button
        SessionManager session = SessionManager.getInstance();
        Button backBtn = new Button("Kembali");
        backBtn.setPrefWidth(100);
        backBtn.setOnAction(e -> {
             if (session.getUserType().equals("Customer")) {
                try {
                    new CustomerApp().start(stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return; 
            } else {
                try {
                    new PegawaiApp().start(stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox buttonBox = new HBox(10, backBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10));

        // Main layout
        VBox mainContent = new VBox(15, makananSection, minumanSection);
        mainContent.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(15, title, scrollPane, buttonBox);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Lihat Menu");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
