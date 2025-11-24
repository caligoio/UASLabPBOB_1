import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.List;
import java.util.ArrayList;

/**
 * CustomerApp - customer dashboard showing their pesanan as cards.
 * - Displays user info at the top
 * - Shows pesanan as horizontal scrollable cards with full details
 * - Provides options to create new order, manage pesanan, and view menu
 */
public class CustomerApp extends Application {

    private HBox ordersContainer;
    private Stage primaryStage;
    private Button editOrderBtn;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        SessionManager session = SessionManager.getInstance();
        Akun user = session.getCurrentUser();

        // Top section - User info
        Label topBox = new Label(session.getUserType() + " | " + user.getNama());
        topBox.setMinHeight(60);
        topBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-font-size:14px; -fx-font-weight:bold;");

        // Title
        Label titleLabel = new Label("Pesanan Anda");
        titleLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-alignment: center;");

        // Orders container (horizontal, no scroll)
        ordersContainer = new HBox(12);
        ordersContainer.setPadding(new Insets(12));
        ordersContainer.setAlignment(Pos.TOP_CENTER);
        ordersContainer.setPrefHeight(320);
        ordersContainer.setMaxHeight(320);

        loadOrders();

        // Button layout - 4 action buttons
        editOrderBtn = new Button("Edit Pesanan");
        editOrderBtn.setPrefWidth(180);
        editOrderBtn.setOnAction(e -> openEditOrderPopup(stage));

        Button viewMenuBtn = new Button("Lihat Menu");
        viewMenuBtn.setPrefWidth(180);
        viewMenuBtn.setOnAction(e -> {
            try {
                new LihatMenuApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(180);
        refreshBtn.setOnAction(e -> loadOrders());

        HBox buttonRow1 = new HBox(12, editOrderBtn, viewMenuBtn);
        buttonRow1.setAlignment(Pos.CENTER);
        
        HBox buttonRow2 = new HBox(12, refreshBtn);
        buttonRow2.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, buttonRow1, buttonRow2);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Kembali");
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox backBox = new HBox(10, backButton);
        backBox.setAlignment(Pos.CENTER);
        backBox.setPadding(new Insets(10));

        // Main layout
        VBox contentBox = new VBox(15, titleLabel, ordersContainer, buttonBox);
        contentBox.setPadding(new Insets(15));
        contentBox.setStyle("-fx-border-color: #000000ff; -fx-background-color: #fafafa;");
        contentBox.setMaxWidth(400);
        contentBox.setMaxHeight(500);
        contentBox.setAlignment(Pos.CENTER);
            
        VBox root = new VBox(15, topBox, contentBox, backBox);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Customer Dashboard");
        stage.setScene(scene);
        stage.show();

        // Initial load of orders (will show existing order or a create prompt)
        loadOrders();
    }

    /**
     * Load pesanan and render as cards - filtered by table number
     */
    private void loadOrders() {
        ordersContainer.getChildren().clear();

        SessionManager sm = SessionManager.getInstance();
        Akun user = sm.getCurrentUser();

        int tableNumber = sm.getCurrentTableNumber();
        // If session doesn't have a table, try restore by occupied table mapping
        if (tableNumber <= 0 && user != null) {
            int found = sm.getFirstTableOccupiedByCustomer(user.getNama());
            if (found > 0) {
                tableNumber = found;
                sm.setCurrentTableNumber(found);
            }
        }

        List<Pesanan> allPesanan = new ArrayList<>(RestaurantSystem.getInstance().getDaftarPesanan());

        List<Pesanan> daftarPesanan = new ArrayList<>();
        if (tableNumber > 0) {
            final int tableNum = tableNumber;
            daftarPesanan = allPesanan.stream()
                .filter(p -> p.getMeja() != null && p.getMeja().getNomor() == tableNum)
                .collect(java.util.stream.Collectors.toList());
        }

        if (daftarPesanan.isEmpty()) {
            // Show a centered big "+" button with "Buat Pesanan" text
            Button plus = new Button("+");
            plus.setStyle("-fx-font-size:36px; -fx-pref-width:80px; -fx-pref-height:80px; -fx-background-radius:40px; -fx-background-color: #AC9055; -fx-text-fill: white;");
            Label label = new Label("Buat Pesanan");
            label.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");

            VBox center = new VBox(8, plus, label);
            center.setAlignment(Pos.CENTER);
            center.setPrefWidth(240);

            plus.setOnAction(e -> {
                try {
                    new PesananApp().start(primaryStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            ordersContainer.getChildren().add(center);

            // Disable edit button when no order
            if (editOrderBtn != null) editOrderBtn.setDisable(true);
            return;
        }

       Pesanan p = daftarPesanan.get(0);
       VBox card = buildOrderCard(p);
       ordersContainer.getChildren().add(card);

       if (editOrderBtn != null) editOrderBtn.setDisable(false);
    }

    /**
     * Build a card for each pesanan
     */
    private VBox buildOrderCard(Pesanan p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #444; -fx-border-radius:4; -fx-background-color: #fff7e6; -fx-background-radius:4;");
        card.setPrefWidth(240);
        card.setMinHeight(280);

        Label id = new Label("Pesanan #" + p.getIdPesanan());
        id.setStyle("-fx-font-weight:bold; -fx-font-size:13px;");

        Label meja = new Label("Meja: " + (p.getMeja() != null ? p.getMeja().getNomor() : "-"));

        // Calculate total
        double total = 0.0;
        VBox itemsBox = new VBox(3);
        itemsBox.setPadding(new Insets(4));
        itemsBox.setStyle("-fx-background-color: transparent;");

        for (DetailPesanan d : p.getDaftarItem()) {
            double subtotal = d.getItem().getHarga() * d.getJumlah();
            total += subtotal;
            Label it = new Label("- " + d.getItem().getNama() + " x" + d.getJumlah());
            it.setStyle("-fx-font-size:11px;");
            itemsBox.getChildren().add(it);
        }

        Label totalLabel = new Label("Total: Rp " + total);
        totalLabel.setStyle("-fx-font-weight:bold;");

        Label status = new Label("Status: " + p.getStatus());
        status.setStyle("-fx-font-size:12px;");

        ScrollPane itemScroll = new ScrollPane(itemsBox);
        itemScroll.setPrefHeight(120);
        itemScroll.setFitToWidth(true);
        itemScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        card.getChildren().addAll(id, meja, new Separator(), itemScroll, totalLabel, status);
        VBox.setVgrow(itemScroll, Priority.ALWAYS);
        return card;
    }
    private void openEditOrderPopup(Stage owner) {
        SessionManager sm = SessionManager.getInstance();
        Akun user = sm.getCurrentUser();

        int tableNumber = sm.getCurrentTableNumber();
        if (tableNumber <= 0 && user != null) {
            // try restore by owner
            List<Pesanan> all = RestaurantSystem.getInstance().getDaftarPesanan();
            List<Pesanan> byOwner = all.stream()
                .filter(p -> p.getOwner() != null && p.getOwner().equals(user.getNama()))
                .collect(java.util.stream.Collectors.toList());
            if (!byOwner.isEmpty()) {
                tableNumber = byOwner.get(0).getMeja().getNomor();
                sm.setCurrentTableNumber(tableNumber);
            }
        }

        List<Pesanan> allPesanan = RestaurantSystem.getInstance().getDaftarPesanan();
        List<Pesanan> daftarPesanan = new ArrayList<>();
        if (tableNumber > 0) {
            final int tableNum = tableNumber;
            daftarPesanan = allPesanan.stream()
                .filter(p -> p.getMeja() != null && p.getMeja().getNomor() == tableNum)
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (daftarPesanan.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Anda belum memiliki pesanan.", ButtonType.OK);
            a.showAndWait();
            return;
        }

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Pesanan");

        // Automatically get the first (only) pesanan for this table
        Pesanan selected = daftarPesanan.get(0);

        // Display pesanan info
        Label pesananInfo = new Label("Pesanan #" + selected.getIdPesanan() + " - Meja " + selected.getMeja().getNomor());
        pesananInfo.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        // Items list
        ListView<String> itemsListView = new ListView<>();
        itemsListView.setPrefHeight(200);

        // Load items from the selected pesanan
        for (DetailPesanan d : selected.getDaftarItem()) {
            itemsListView.getItems().add(d.getItem().getNama() + " x" + d.getJumlah());
        }

        // Buttons
        Button removeItemBtn = new Button("Hapus Item");
        removeItemBtn.setOnAction(e -> {
            int idx = itemsListView.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < selected.getDaftarItem().size()) {
                selected.getDaftarItem().remove(idx);
                itemsListView.getItems().remove(idx);
            }
        });

        Button addItemBtn = new Button("Tambah Item");
        addItemBtn.setOnAction(e -> {
            openAddItemDialog(selected, itemsListView);
        });

        HBox itemBtnBox = new HBox(8, removeItemBtn, addItemBtn);
        itemBtnBox.setAlignment(Pos.CENTER);

        Button saveBtn = new Button("Simpan");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> {
            RestaurantSystem.getInstance().saveAllPesanan();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Pesanan berhasil disimpan.", ButtonType.OK);
            a.showAndWait();
            dialog.close();
            loadOrders();
        });

        Button cancelBtn = new Button("Batal");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, pesananInfo, new Label("Items:"), itemsListView, itemBtnBox, new Separator(), buttons);
        content.setPadding(new Insets(15));

        Scene scene = new Scene(content, 450, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Open dialog to add a new item to pesanan
     */
    private void openAddItemDialog(Pesanan p, ListView<String> itemsListView) {
        Stage dialog = new Stage();
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
