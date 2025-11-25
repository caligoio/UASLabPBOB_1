import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ImageView title = new ImageView(new Image("Images/logo.png"));
        title.setFitWidth(720);
        title.setPreserveRatio(true);
         
        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(160);
        loginButton.setStyle(
            "-fx-background-color: transparent;" +    // no fill
            "-fx-text-fill: #E1C582;" +               // text color
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #E1C582;" +            // border color
            "-fx-border-width: 3;" +                  // adjust thickness
            "-fx-font-family: 'Palatino Linotype';" + 
            "-fx-padding: 8 20;"
        );
        Button daftarButton = new Button("DAFTAR");
        daftarButton.setStyle(
            "-fx-background-color: transparent;" +    // no fill
            "-fx-text-fill: #E1C582;" +               // text color
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #E1C582;" +            // border color
            "-fx-border-width: 3;" +                  // adjust thickness
            "-fx-font-family: 'Palatino Linotype';" + 
            "-fx-padding: 8 20;"
        );
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

        HBox buttonBox = new HBox(20, loginButton, daftarButton);
        buttonBox.setAlignment(Pos.CENTER);

        // OVERLAY PANEL (RIGHT SIDE)
        VBox layout = new VBox(15, title, buttonBox);
        layout.setPadding(new Insets(40));
        layout.setPrefSize(800, 600);
        
        layout.setStyle(
            "-fx-background-color: rgba(22, 31, 72, 0.6);"
        );
        layout.setAlignment(Pos.CENTER);

        
        VBox root = new VBox(layout);
        // Load background image from resources if available, else fallback to file path
        Image backgroundImg = null;
        try {
            java.net.URL url = getClass().getResource("Images/landing.jpg");
            if (url != null) {
                backgroundImg = new Image(url.toExternalForm());
            } else {
                // fallback to working directory file
                backgroundImg = new Image("file:landing.jpg");
            }
        } catch (Exception ex) {
            // if anything fails, leave backgroundImg null
            backgroundImg = null;
        }

        if (backgroundImg != null && !backgroundImg.isError()) {
            BackgroundImage backgroundImage = new BackgroundImage(
                backgroundImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                    100, 100,           // width, height
                    true, true,         // widthAsPercentage, heightAsPercentage
                    false, true         // contain=false, cover=true
                )
            );
            root.setBackground(new Background(backgroundImage));
        }
    
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Restoran Lezat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
