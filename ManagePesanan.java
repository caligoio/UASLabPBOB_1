import java.util.List;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * ManagePesanan - admin/manager view for managing all orders.
 * - Shows one card per Pesanan with full details (all items, qty, total, status).
 * - Supports delete, edit, and status change operations.
 * - Edit opens a modal dialog to modify order details.
 */
public class ManagePesanan extends Application {

    SessionManager session = SessionManager.getInstance();
    Akun user = session.getCurrentUser();
    Label topBox = new Label(session.getUserType() + " | " + user.getNama() + (session.getUserType().equals("Pegawai") ? " - " + session.getUserRole() : ""));
    
    private HBox ordersContainer;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        Label title = new Label("Manajemen Pesanan");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        ordersContainer = new HBox(12);
        ordersContainer.setPadding(new Insets(12));
        ordersContainer.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(ordersContainer);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setPrefHeight(420);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadOrders());

        Button backBtn = new Button("Kembali");
        backBtn.setOnAction(e -> {
            try {
                new PegawaiApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox controls = new HBox(8, refreshBtn, backBtn);
        controls.setPadding(new Insets(8));

        VBox root = new VBox(10, title, topBox, scroll, controls);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Manajemen Pesanan");
        stage.setScene(scene);
        stage.show();

        loadOrders();
    }

    /**
     * Load all pesanan and render one card per order.
     */
    private void loadOrders() {
        ordersContainer.getChildren().clear();

        List<Pesanan> semua = new ArrayList<>(RestaurantSystem.getInstance().getDaftarPesanan());

        // Sort by id ascending
        semua.sort((a, b) -> Integer.compare(a.getIdPesanan(), b.getIdPesanan()));

        if (semua.isEmpty()) {
            Label empty = new Label("Tidak ada pesanan.");
            empty.setStyle("-fx-font-size:14px;");
            ordersContainer.getChildren().add(empty);
            return;
        }

        for (Pesanan p : semua) {
            VBox card = buildOrderCard(p);
            ordersContainer.getChildren().add(card);
        }
    }

    private VBox buildOrderCard(Pesanan p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #333; -fx-border-radius:4; -fx-background-color: #f5f5f5; -fx-background-radius:4;");
        card.setPrefWidth(280);

        Label id = new Label("Pesanan #" + p.getIdPesanan());
        id.setStyle("-fx-font-weight:bold; -fx-font-size:14px;");

        Label meja = new Label("Meja: " + (p.getMeja() != null ? p.getMeja().getNomor() : "-"));

        // List all items (both makanan and minuman)
        VBox itemsBox = new VBox(4);
        itemsBox.setPadding(new Insets(4));
        itemsBox.setStyle("-fx-background-color: transparent;");

        double total = 0.0;
        for (DetailPesanan d : p.getDaftarItem()) {
            double harga = d.getItem().getHarga();
            int j = d.getJumlah();
            double subtotal = harga * j;
            total += subtotal;
            Label it = new Label("- " + d.getItem().getNama() + " x" + j + " => Rp " + subtotal);
            itemsBox.getChildren().add(it);
        }

        Label totalLabel = new Label("Total: Rp " + total);
        totalLabel.setStyle("-fx-font-weight:bold;");

        Label status = new Label("Status: " + p.getStatus());

        ScrollPane itemScroll = new ScrollPane(itemsBox);
        itemScroll.setPrefHeight(140);
        itemScroll.setFitToWidth(true);
        itemScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Action buttons
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editBtn.setOnAction(e -> openEditDialog(p));

        Button deleteBtn = new Button("Hapus");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus pesanan #" + p.getIdPesanan() + "?", ButtonType.YES, ButtonType.NO);
            if (confirm.showAndWait().get() == ButtonType.YES) {
                RestaurantSystem.getInstance().getDaftarPesanan().remove(p);
                RestaurantSystem.getInstance().saveAllPesanan();
                loadOrders();
            }
        });

        HBox btnBox = new HBox(6, editBtn, deleteBtn);
        btnBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(id, meja, new Separator(), itemScroll, totalLabel, status, btnBox);
        VBox.setVgrow(itemScroll, Priority.ALWAYS);
        return card;
    }

    /**
     * Open modal dialog to edit pesanan details.
     */
    private void openEditDialog(Pesanan p) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Pesanan #" + p.getIdPesanan());

        // Meja field
        HBox mejaBox = new HBox(5);
        Label mejaLabel = new Label("No Meja:");
        mejaLabel.setPrefWidth(100);
        TextField mejaField = new TextField();
        mejaField.setText(String.valueOf(p.getMeja() != null ? p.getMeja().getNomor() : 1));
        mejaBox.getChildren().addAll(mejaLabel, mejaField);

        // Status dropdown
        HBox statusBox = new HBox(5);
        Label statusLabel = new Label("Status:");
        statusLabel.setPrefWidth(100);
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Dipesan", "Siap", "Selesai");
        statusCombo.setValue(p.getStatus());
        statusBox.getChildren().addAll(statusLabel, statusCombo);

        // Items list with edit capability
        Label itemsLabel = new Label("Items dalam pesanan:");
        itemsLabel.setStyle("-fx-font-weight:bold;");

        ListView<String> itemsListView = new ListView<>();
        for (DetailPesanan d : p.getDaftarItem()) {
            itemsListView.getItems().add(d.getItem().getNama() + " x" + d.getJumlah());
        }
        itemsListView.setPrefHeight(200);

        Button removeItemBtn = new Button("Hapus Item (pilih dari list)");
        removeItemBtn.setOnAction(e -> {
            int idx = itemsListView.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < p.getDaftarItem().size()) {
                p.getDaftarItem().remove(idx);
                itemsListView.getItems().remove(idx);
            }
        });

        Button addItemBtn = new Button("Tambah Item");
        addItemBtn.setOnAction(e -> openAddItemDialog(p, itemsListView));

        HBox itemBtnBox = new HBox(6, removeItemBtn, addItemBtn);
        itemBtnBox.setAlignment(Pos.CENTER);

        // Save and Cancel buttons
        Button saveBtn = new Button("Simpan");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> {
            try {
                int noMeja = Integer.parseInt(mejaField.getText());
                if (noMeja < 1 || noMeja > 20) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "No meja harus 1-20");
                    a.showAndWait();
                    return;
                }
                p.getMeja().setNomor(noMeja);
                p.setStatus(statusCombo.getValue());
                RestaurantSystem.getInstance().saveAllPesanan();
                dialog.close();
                loadOrders();
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.WARNING, "No meja harus angka");
                a.showAndWait();
            }
        });

        Button cancelBtn = new Button("Batal");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(10, mejaBox, statusBox, itemsLabel, itemsListView, itemBtnBox, new Separator(), buttons);
        content.setPadding(new Insets(15));

        Scene scene = new Scene(content, 500, 550);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Open dialog to add a new item to pesanan.
     */
    private void openAddItemDialog(Pesanan p, ListView<String> itemsListView) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Tambah Item");

        RestaurantSystem rs = RestaurantSystem.getInstance();

        ComboBox<MenuItem> itemCombo = new ComboBox<>();
        itemCombo.getItems().addAll(rs.getDaftarMenu());
        itemCombo.setCellFactory(lv -> new ListCell<MenuItem>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNama() + " - Rp" + item.getHarga());
            }
        });
        itemCombo.setButtonCell(new ListCell<MenuItem>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNama() + " - Rp" + item.getHarga());
            }
        });

        HBox itemBox = new HBox(5, new Label("Item:"), itemCombo);
        itemBox.setAlignment(Pos.CENTER);

        HBox jumlahBox = new HBox(5);
        Label jumlahLabel = new Label("Jumlah:");
        jumlahLabel.setPrefWidth(80);
        Spinner<Integer> jumlahSpinner = new Spinner<>(1, 100, 1);
        jumlahBox.getChildren().addAll(jumlahLabel, jumlahSpinner);
        jumlahBox.setAlignment(Pos.CENTER);
        
        Button addBtn = new Button("Tambah");
        addBtn.setOnAction(e -> {
            if (itemCombo.getValue() == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Pilih item terlebih dahulu");
                a.showAndWait();
                return;
            }
            MenuItem selectedItem = itemCombo.getValue();
            int qty = jumlahSpinner.getValue();
            DetailPesanan detail = new DetailPesanan(selectedItem, qty, "");
            p.getDaftarItem().add(detail);
            itemsListView.getItems().add(selectedItem.getNama() + " x" + qty);
            dialog.close();
        });

        Button cancelBtn = new Button("Batal");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(8, addBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, itemBox, jumlahBox, buttons);
        content.setPadding(new Insets(15));

        Scene scene = new Scene(content, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
