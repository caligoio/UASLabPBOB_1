import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PesananApp extends Application {

    // Data untuk menyimpan item pesanan (gunakan DetailPesanan yang sudah ada)
    private List<DetailPesanan> daftarItemPesanan = new ArrayList<>();
    private TableView<DetailPesanan> itemTable;
    private static int pesananCounter = 1;

    @Override
    public void start(Stage stage) {
        SessionManager session = SessionManager.getInstance();
        Button backButton = new Button("Kembali");
        backButton.setOnAction(e -> {
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

        // leftbox
        RestaurantSystem menu = RestaurantSystem.getInstance();

        ListView<String> makananList = new ListView<>();
        ListView<String> minumanList = new ListView<>();

        // Fill each section
        List<MenuItem> daftarMenu = menu.getDaftarMenu();
        for (MenuItem item : daftarMenu) {
            if (item instanceof Makanan) {
                makananList.getItems().add(item.getInfo());
            } else if (item instanceof Minuman) {
                minumanList.getItems().add(item.getInfo());
            }
        }

        // Apply custom cells with button inside each row
        applyTambahButtonCellFactory(makananList, "Makanan");
        applyTambahButtonCellFactory(minumanList, "Minuman");

        // Labels
        Label makananLabel = new Label("Daftar Makanan");
        Label minumanLabel = new Label("Daftar Minuman");

        makananLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        minumanLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // VBox sections
        VBox makananSection = new VBox(5, makananLabel, makananList);
        VBox minumanSection = new VBox(5, minumanLabel, minumanList, backButton);

        // Main layout
        VBox leftBox = new VBox(20, makananSection, minumanSection);
        leftBox.setPrefSize(424, 560);
        leftBox.setStyle("-fx-border-color: black; -fx-alignment: top-left; -fx-padding: 10;");

        // ===== RIGHT BOX (Order Form) =====
        VBox rightBox = createOrderFormBox(menu, session, stage);
        rightBox.setPrefSize(320, 560);
        rightBox.setStyle("-fx-border-color: black; -fx-alignment: top-center; -fx-padding: 10;");

        HBox root = new HBox(20, leftBox, rightBox);
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Buat Pesanan");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Create order form box di sebelah kanan
     */
    private VBox createOrderFormBox(RestaurantSystem menu, SessionManager session, Stage stage) {
        VBox orderBox = new VBox(10);
        orderBox.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label("Form Pesanan");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Table Number Input - show for both Pegawai and Customer
        HBox mejaBox = new HBox(5);
        Label mejaLabel = new Label("No Meja:");
        mejaLabel.setPrefWidth(60);
        TextField mejaField = new TextField();
        mejaField.setPromptText("1-20");
        mejaField.setPrefWidth(80);
        mejaField.setText("1");
        mejaBox.getChildren().addAll(mejaLabel, mejaField);
        
        // If customer has a table number in session, prefill the field
        if (session.getUserType().equals("Customer") && session.getCurrentTableNumber() > 0) {
            mejaField.setText(String.valueOf(session.getCurrentTableNumber()));
        }

        // Order Items Table
        itemTable = new TableView<>();
        itemTable.setPrefHeight(300);
        
        // Column: Nama
        TableColumn<DetailPesanan, String> namaCol = new TableColumn<>("Item");
        namaCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getItem().getNama()));
        namaCol.setPrefWidth(80);
        
        // Column: Harga
        TableColumn<DetailPesanan, String> hargaCol = new TableColumn<>("Harga");
        hargaCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp " + cellData.getValue().getItem().getHarga()));
        hargaCol.setPrefWidth(80);
        
        // Column: Jumlah (with Spinner)
        TableColumn<DetailPesanan, Integer> jumlahCol = new TableColumn<>("Qty");
        jumlahCol.setCellFactory(param -> new TableCell<DetailPesanan, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1);
            {
                spinner.setPrefWidth(60);
                spinner.setEditable(true);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().isEmpty()) {
                    setGraphic(null);
                } else {
                    DetailPesanan detail = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(detail.getJumlah());
                    
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                        detail.setJumlah(newVal);
                        getTableView().refresh();
                    });
                    
                    setGraphic(spinner);
                }
            }
        });
        jumlahCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getJumlah()).asObject());
        jumlahCol.setPrefWidth(80);
        
        // Column: Action (Delete)
        TableColumn<DetailPesanan, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<DetailPesanan, Void>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3px 8px;");
                deleteBtn.setOnAction(e -> {
                    DetailPesanan data = getTableView().getItems().get(getIndex());
                    itemTable.getItems().remove(data);
                    daftarItemPesanan.remove(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        actionCol.setPrefWidth(60);

        itemTable.getColumns().add(namaCol);
        itemTable.getColumns().add(hargaCol);
        itemTable.getColumns().add(jumlahCol);
        itemTable.getColumns().add(actionCol);
        itemTable.setItems(javafx.collections.FXCollections.observableArrayList(daftarItemPesanan));

        // Button "Buat Pesanan"
        Button buatButton = new Button("Buat Pesanan");
        buatButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px;");
        buatButton.setPrefWidth(200);
        buatButton.setOnAction(e -> {
            String mejaText = mejaField.getText().trim();
            if (mejaText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Nomor meja harus diisi!");
                return;
            }
            
            int noMeja = 0;
            try {
                noMeja = Integer.parseInt(mejaText);
                if (noMeja < 1 || noMeja > 20) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", "Nomor meja harus antara 1-20!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Nomor meja harus berupa angka!");
                return;
            }
            
            int tableNum = noMeja;

            if (daftarItemPesanan.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih minimal 1 item!");
                return;
            }

            // Check if table is occupied by someone else
            SessionManager sm = SessionManager.getInstance();
            if (sm.isTableOccupied(tableNum)) {
                String owner = sm.getTableOccupiedBy(tableNum);
                String currentName = session.getCurrentUser() != null ? session.getCurrentUser().getNama() : "";
                if (session.getUserType().equals("Customer") && !currentName.equals(owner)) {
                    showAlert(Alert.AlertType.ERROR, "Meja Terpakai", "Meja ini sudah ditempati oleh: " + owner);
                    return;
                }
            }

            // Check whether there's already an existing pesanan for this table
            List<Pesanan> all = RestaurantSystem.getInstance().getDaftarPesanan();
            List<Pesanan> existing = all.stream()
                .filter(p -> p.getMeja() != null && p.getMeja().getNomor() == tableNum)
                .collect(java.util.stream.Collectors.toList());

            if (!existing.isEmpty() && session.getUserType().equals("Customer")) {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Anda sudah memiliki pesanan untuk meja ini. Buka pesanan?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> res = a.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.YES) {
                    try {
                        new CustomerApp().start(stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            }

            Meja meja = new Meja(tableNum, "Tersedia");
            Pesanan pesanan;
            if (session.getUserType().equals("Customer") && session.getCurrentUser() != null) {
                pesanan = new Pesanan(pesananCounter++, "Dipesan", session.getCurrentUser().getNama(), meja);
            } else {
                pesanan = new Pesanan(pesananCounter++, "Dipesan", meja);
            }
            
            // Add items to pesanan (daftarItemPesanan already holds DetailPesanan)
            for (DetailPesanan detail : daftarItemPesanan) {
                // create new DetailPesanan instance to avoid sharing mutable objects
                DetailPesanan d = new DetailPesanan(detail.getItem(), detail.getJumlah(), detail.getCatatan() == null ? "" : detail.getCatatan());
                pesanan.getDaftarItem().add(d);
            }

            // Save to RestaurantSystem
            RestaurantSystem.getInstance().tambahPesanan(pesanan);
            
            // If customer created order, occupy table and set session
            if (session.getUserType().equals("Customer")) {
                if (session.getCurrentUser() != null) {
                    sm.occupyTable(tableNum, session.getCurrentUser().getNama());
                } else {
                    sm.occupyTable(tableNum, "Guest");
                }
                session.setCurrentTableNumber(tableNum);
            }

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Pesanan berhasil dibuat!");

            if (session.getUserType().equals("Customer")) {
                try {
                    // Navigate to CustomerApp to view/edit their order
                    new CustomerApp().start(stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return; 
            } 
            
            // Clear form for Pegawai (staff)
            daftarItemPesanan.clear();
            itemTable.refresh();
            mejaField.setText("1");
        });

        orderBox.getChildren().addAll(titleLabel, mejaBox, new Separator(), itemTable, buatButton);
        VBox.setVgrow(itemTable, Priority.ALWAYS);
        
        return orderBox;
    }

    /**
     * Apply custom cell factory dengan "Tambah" button untuk ListView
     */
    private void applyTambahButtonCellFactory(ListView<String> listView, String tipe) {
        listView.setCellFactory(param -> new ListCell<String>() {
            private final Label label = new Label();
            private final Button btnTambah = new Button("+");
            private final HBox hbox = new HBox(10);
            private MenuItem menuItem = null;

            {
                HBox.setHgrow(label, Priority.ALWAYS);
                label.setMaxWidth(Double.MAX_VALUE);

                hbox.getChildren().addAll(label, btnTambah);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPadding(new Insets(5));

                btnTambah.setOnAction(e -> {
                    if (menuItem != null) {
                        addItemToPesanan(menuItem);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    menuItem = null;
                } else {
                    label.setText(item);
                    setGraphic(hbox);

                    // Cari MenuItem yang sesuai dari RestaurantSystem
                    RestaurantSystem menu = RestaurantSystem.getInstance();
                    for (MenuItem m : menu.getDaftarMenu()) {
                        if (m.getInfo().equals(item)) {
                            menuItem = m;
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Tambah item ke pesanan
     */
    private void addItemToPesanan(MenuItem item) {
        System.out.println("Adding item: " + item.getNama());
        
        // Cek apakah item sudah ada di daftar
        for (DetailPesanan data : daftarItemPesanan) {
            if (data.getItem().getNama().equals(item.getNama())) {
                data.setJumlah(data.getJumlah() + 1);
                System.out.println("Item quantity updated to: " + data.getJumlah());
                if (itemTable != null) {
                    itemTable.refresh();
                }
                return;
            }
        }

        // Jika belum ada, tambah item baru (catatan kosong)
        DetailPesanan newItem = new DetailPesanan(item, 1, "");
        daftarItemPesanan.add(newItem);
        System.out.println("New item added. Total items now: " + daftarItemPesanan.size());
        
        if (itemTable != null) {
            itemTable.setItems(javafx.collections.FXCollections.observableArrayList(daftarItemPesanan));
            itemTable.refresh();
        }
    }

    /**
     * Helper method untuk menampilkan Alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

// Removed ItemPesananData helper — using domain class DetailPesanan instead
