import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Selamat Datang!\n Restoran Lezat siap melayani");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-alignment:center;");

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(160);
        Button daftarButton = new Button("DAFTAR");
        daftarButton.setPrefWidth(160);

        // tombol login → buka LoginApp
        loginButton.setOnAction(e -> {
            try {
                new LoginApp().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // tombol daftar → buka DaftarApp
        daftarButton.setOnAction(e -> {
            try {
                new DaftarApp().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // OVERLAY PANEL (RIGHT SIDE)
        VBox layout = new VBox(15, title, loginButton, daftarButton);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
    
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Restoran Lezat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
