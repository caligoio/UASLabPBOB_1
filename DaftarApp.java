import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DaftarApp extends Application {
    @Override
    public void start(Stage stage) {

        // ComboBox untuk memilih jenis pengguna
        ComboBox<String> userBox = new ComboBox<>();
        userBox.getItems().addAll("Customer", "Pegawai");
        userBox.setValue("Customer");
        userBox.setPrefWidth(200);

        // ComboBox untuk memilih peran (hanya untuk Pegawai)
        Label peranLabel = new Label("Peran:");
        ComboBox<String> peranBox = new ComboBox<>();
        peranBox.getItems().addAll("Kasir", "Pelayan", "Koki");
        peranBox.setValue("Kasir");
        peranBox.setPrefWidth(200);

        // Elemen-elemen form
        Label namaLabel = new Label("Nama:");
        TextField namaField = new TextField();
        namaField.setPrefWidth(200);

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPrefWidth(200);

        // Menggunakan GridPane untuk layout yang lebih terstruktur dan centered
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(20));
        
        // Jenis Pengguna field
        formGrid.add(new Label("Jenis Pengguna:"), 0, 0);
        formGrid.add(userBox, 1, 0);

        // Peran field (awalnya hidden)
        formGrid.add(peranLabel, 0, 1);
        formGrid.add(peranBox, 1, 1);
        peranLabel.setVisible(false);
        peranBox.setVisible(false);

        // Nama field
        formGrid.add(namaLabel, 0, 2);
        formGrid.add(namaField, 1, 2);

        // Password field
        formGrid.add(passLabel, 0, 3);
        formGrid.add(passField, 1, 3);

        // Tombol navigasi
        Button daftarButton = new Button("Daftar");
        Button backButton = new Button("Kembali");

        // Aksi tombol kembali → kembali ke MainApp
        backButton.setOnAction(e -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Layout navigasi (bawah)
        VBox navigasi = new VBox(8, daftarButton, backButton);
        navigasi.setStyle("-fx-alignment: center;");

        // Layout utama
        VBox layout = new VBox(20, formGrid, navigasi);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Logika: jika user memilih "Pegawai", tampilkan peranBox
        userBox.setOnAction(e -> {
            if (userBox.getValue().equals("Pegawai")) {
                peranLabel.setVisible(true);
                peranBox.setVisible(true);
            } else {
                peranLabel.setVisible(false);
                peranBox.setVisible(false);
            }
        });

        daftarButton.setOnAction(e -> {
            AkunManager akunManager = AkunManager.getInstance(); // Gunakan singleton

            String username = namaField.getText().trim();
            String password = passField.getText();
            String peran = peranBox.getValue();

            // Validasi input kosong
            if (username.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Username tidak boleh kosong!");
                return;
            }

            if (password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Password tidak boleh kosong!");
                return;
            }

            // Validasi panjang password minimal 6 karakter
            if (password.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Password minimal 6 karakter!");
                return;
            }

            // Validasi peran dipilih
            if (userBox.getValue().equals("Pegawai") && (peran == null || peran.isEmpty())) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Peran harus dipilih untuk Pegawai!");
                return;
            }

            // Cek apakah username sudah ada
            boolean sudahAda = false;
            for (Akun a : akunManager.getDaftarAkun()) {
                if (a.getNama().equalsIgnoreCase(username)) {
                    sudahAda = true;
                    break;
                }
            }

            if (sudahAda) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Username telah dipakai!");
            } else {
                try {
                    if (userBox.getValue().equals("Pegawai")) {
                        akunManager.tambahAkun(new Pegawai(0, username, password, peran));
                    } else {
                        akunManager.tambahAkun(new Customer(0, username, password));
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Akun berhasil dibuat!");

                    System.out.println("Daftar akun saat ini:");
                    for (Akun a : akunManager.getDaftarAkun()) {
                        System.out.println("- " + a.getNama() + " (" + a.getClass().getSimpleName() + ")");
                    }

                    try {
                        new LoginApp().start(stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // Clear form fields
                    namaField.clear();
                    passField.clear();
                    userBox.setValue("Customer");

                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuat akun: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });


        // Scene dan stage
        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Halaman Daftar");
        stage.setScene(scene);
        stage.show();
    }

    // Method helper untuk menampilkan Alert dengan format konsisten
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
