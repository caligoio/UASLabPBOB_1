import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TransaksiApp extends Application {

    private TableView<Pesanan> pesananTable;
    private static int transaksiCounter = 1;

    @Override
    public void start(Stage stage) {

        Label title = new Label("Manajemen Pesanan");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        SessionManager session = SessionManager.getInstance();
        Akun user = session.getCurrentUser();
        Label topBox = new Label(session.getUserType() + " | " + user.getNama() + (session.getUserType().equals("Pegawai") ? " - " + session.getUserRole() : ""));

        pesananTable = new TableView<>();
        pesananTable.setPrefHeight(320);
        pesananTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pesanan, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdPesanan()).asObject());
        idCol.setPrefWidth(60);

        TableColumn<Pesanan, String> mejaCol = new TableColumn<>("No Meja");
        mejaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getMeja().getNomor())));
        mejaCol.setPrefWidth(80);

        TableColumn<Pesanan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        statusCol.setPrefWidth(120);

        TableColumn<Pesanan, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<Pesanan, Void>() {
            private final Button btn = new Button("Detail / Bayar");
            {
                btn.setOnAction(e -> {
                    Pesanan p = getTableView().getItems().get(getIndex());
                    showPaymentWindow(p, stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        actionCol.setPrefWidth(140);

        pesananTable.getColumns().addAll(idCol, mejaCol, statusCol, actionCol);

        // Load pesanan from system
        refreshPesananList();

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshPesananList());

        Button backButton = new Button("Kembali");
        backButton.setOnAction(e -> {
            try {
                new PegawaiApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox controls = new HBox(8, refreshBtn, backButton);
        controls.setPadding(new Insets(8));

        VBox box = new VBox(10, pesananTable, controls);
        box.setPadding(new Insets(10));
        box.setPrefSize(760, 450);
        box.setStyle("-fx-border-color: black; -fx-alignment: top-left; -fx-padding: 10;");

        VBox root = new VBox(8, title, topBox, box);
        root.setStyle("-fx-alignment: top-center; -fx-padding: 10;");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Transaksi");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshPesananList() {
        List<Pesanan> list = RestaurantSystem.getInstance().getDaftarPesanan();
        ObservableList<Pesanan> obs = FXCollections.observableArrayList(list);
        pesananTable.setItems(obs);
        pesananTable.refresh();
    }

    private void showPaymentWindow(Pesanan pesanan, Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Pembayaran - Pesanan " + pesanan.getIdPesanan());

        // List items
        ListView<String> itemsView = new ListView<>();
        double total = 0.0;
        for (DetailPesanan d : pesanan.getDaftarItem()) {
            double harga = d.getItem().getHarga();
            int j = d.getJumlah();
            double subtotal = harga * j;
            total += subtotal;
            itemsView.getItems().add(d.getItem().getNama() + " x" + j + " => Rp " + subtotal);
        }

        Label totalLabel = new Label("Total: Rp " + total);

        ToggleGroup tg = new ToggleGroup();
        RadioButton cashRb = new RadioButton("Cash");
        RadioButton cardRb = new RadioButton("Card");
        RadioButton qrisRb = new RadioButton("QRIS");
        cashRb.setToggleGroup(tg);
        cardRb.setToggleGroup(tg);
        qrisRb.setToggleGroup(tg);

        HBox payBox = new HBox(10, cashRb, cardRb, qrisRb);

        Button bayarBtn = new Button("Bayar");
        bayarBtn.setOnAction(e -> {
            Toggle selected = tg.getSelectedToggle();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Pilih metode pembayaran terlebih dahulu.", ButtonType.OK);
                a.showAndWait();
                return;
            }

            Pembayaran pembayaran = null;
            int payId = transaksiCounter; // use counter as payment id
            if (selected == cashRb) pembayaran = new CashPayment(payId);
            else if (selected == cardRb) pembayaran = new CardPayment(payId);
            else if (selected == qrisRb) pembayaran = new QRISPayment(payId);

            Transaksi transaksi = new Transaksi(transaksiCounter++, pesanan, pembayaran);
            transaksi.konfirmasi();
            // persist change
            RestaurantSystem.getInstance().saveAllPesanan();
            // print struk
            new Struk().cetak(transaksi);

            // Release the table from occupancy
            int mejaNumber = pesanan.getMeja().getNomor();
            SessionManager.getInstance().releaseTable(mejaNumber);

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Pembayaran berhasil. Struk dicetak ke console.", ButtonType.OK);
            ok.showAndWait();

            dialog.close();
            refreshPesananList();
        });

        Button closeBtn = new Button("Tutup");
        closeBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, bayarBtn, closeBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(10, new Label("Items:"), itemsView, totalLabel, new Separator(), payBox, buttons);
        content.setPadding(new Insets(10));

        Scene scene = new Scene(content, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}


