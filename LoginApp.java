import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginApp extends Application {
    @Override
    public void start(Stage stage) {
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
        
        // ComboBox untuk memilih jenis pengguna
        ComboBox<String> userBox = new ComboBox<>();
        userBox.getItems().addAll("Customer", "Pegawai");
        userBox.setValue("Customer");
        userBox.setPrefWidth(200);
        formGrid.add(new Label("Jenis Pengguna:"), 0, 0);
        formGrid.add(userBox, 1, 0);

        // ComboBox untuk memilih peran (hanya untuk Pegawai)
        Label peranLabel = new Label("Peran:");
        ComboBox<String> peranBox = new ComboBox<>();
        peranBox.getItems().addAll("Kasir", "Pelayan", "Koki");
        peranBox.setValue("Kasir");
        peranBox.setPrefWidth(200);
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
        Button loginButton = new Button("Login");
        Button backButton = new Button("Kembali");

        // Aksi tombol kembali → kembali ke MainApp
        backButton.setOnAction(e -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loginButton.setOnAction(e -> {
            AkunManager akunManager = AkunManager.getInstance(); // Gunakan singleton

            String username = namaField.getText().trim();
            String password = passField.getText();
            String userType = userBox.getValue();
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

            // Cari akun dengan username dan password
            Akun akun = akunManager.cariAkun(username, password);

            if (akun != null) {
                // Validasi tipe pengguna sesuai dengan yang dipilih
                boolean isValidType = false;
                
                if (userType.equals("Customer") && akun instanceof Customer) {
                    isValidType = true;
                } else if (userType.equals("Pegawai") && akun instanceof Pegawai) {
                    Pegawai pegawai = (Pegawai) akun;
                    // Validasi peran juga cocok
                    if (pegawai.getPeran().equals(peran)) {
                        isValidType = true;
                    }
                }

                if (isValidType) {
                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil sebagai " + akun.getNama() + "!");
                    System.out.println("Login sukses: " + akun.getNama());
                    if (akun instanceof Pegawai) {
                        System.out.println("Peran: " + ((Pegawai) akun).getPeran());
                    }
                    
                    SessionManager sm = SessionManager.getInstance();
                    sm.setCurrentUser(akun);

                    // If customer already had a table occupied, restore it
                    if (akun instanceof Customer) {
                        int restoredTable = sm.getFirstTableOccupiedByCustomer(akun.getNama());
                        if (restoredTable > 0) {
                            sm.setCurrentTableNumber(restoredTable);
                        }
                    }

                    // Clear form
                    namaField.clear();
                    passField.clear();

                    if (akun instanceof Pegawai pegawai) {
                        String role = pegawai.getPeran(); // AMBIL ROLE DARI OBJECT PEGawai

                        if ("Koki".equalsIgnoreCase(role)) {
                            try {
                                new ToCookApp().start(stage);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                new PegawaiApp().start(stage);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else if (akun instanceof Customer) {
                        // Always send customer to CustomerApp; CustomerApp will show existing order or a create button
                        try {
                            new CustomerApp().start(stage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Gagal", "Tipe pengguna atau peran tidak sesuai!");
                    System.out.println("Tipe pengguna atau peran tidak sesuai.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah!");
                System.out.println("Username atau password salah.");
            }
        });

        // Layout navigasi (bawah)
        VBox navigasi = new VBox(8, loginButton, backButton);
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

        // Scene dan stage
        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Halaman Login");
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
