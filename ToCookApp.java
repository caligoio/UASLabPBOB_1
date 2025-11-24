import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ToCookApp - simple chef view listing orders that need to be cooked.
 * - Shows one box per Pesanan whose status is "Dipesan".
 * - Boxes are ordered left→right with more urgent (older) orders on the left.
 * - Each box shows: Pesanan ID, Meja, list of food items and qty, current status.
 * - Chef can mark an order as cooked; that updates status to "Siap" and persists it.
 */
public class ToCookApp extends Application {

    SessionManager session = SessionManager.getInstance();
    Akun user = session.getCurrentUser();
    Label topBox = new Label(session.getUserType() + " | " + user.getNama() + (session.getUserType().equals("Pegawai") ? " - " + session.getUserRole() : ""));
    
    private HBox ordersContainer;

    @Override
    public void start(Stage stage) {
        // access RestaurantSystem when needed via RestaurantSystem.getInstance()

        Label title = new Label("Dapur - Pesanan Untuk Dimasak");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        ordersContainer = new HBox(12);
        ordersContainer.setPadding(new Insets(12));
        ordersContainer.setAlignment(Pos.CENTER_LEFT);

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
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox controls = new HBox(8, refreshBtn, backBtn);
        controls.setPadding(new Insets(8));

        VBox root = new VBox(10, title, topBox, scroll, controls);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Scene scene = new Scene(root, 900, 520);
        stage.setTitle("To Cook - Dapur");
        stage.setScene(scene);
        stage.show();

        loadOrders();
    }

    /**
     * Load pesanan with status "Dipesan" and render one card per order.
     */
    private void loadOrders() {
        ordersContainer.getChildren().clear();

        List<Pesanan> semua = new ArrayList<>(RestaurantSystem.getInstance().getDaftarPesanan());

        // Filter only orders that need cooking (status "Dipesan") and sort by id ascending (older = more urgent)
        List<Pesanan> toCook = semua.stream()
            .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase("Dipesan"))
            .sorted((a, b) -> Integer.compare(a.getIdPesanan(), b.getIdPesanan()))
            .collect(Collectors.toList());

        if (toCook.isEmpty()) {
            Label empty = new Label("Tidak ada pesanan yang perlu dimasak.");
            empty.setStyle("-fx-font-size:14px;");
            ordersContainer.getChildren().add(empty);
            return;
        }

        for (Pesanan p : toCook) {
            VBox card = buildOrderCard(p);
            ordersContainer.getChildren().add(card);
        }
    }

    private VBox buildOrderCard(Pesanan p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #444; -fx-border-radius:4; -fx-background-color: #fff7e6; -fx-background-radius:4;");
        card.setPrefWidth(240);

        Label id = new Label("Pesanan #" + p.getIdPesanan());
        id.setStyle("-fx-font-weight:bold; -fx-font-size:14px;");

        Label meja = new Label("Meja: " + (p.getMeja() != null ? p.getMeja().getNomor() : "-"));

        // List food items (show only Makanan as chef focuses on cooking foods)
        VBox itemsBox = new VBox(4);
        itemsBox.setPadding(new Insets(4));
        itemsBox.setStyle("-fx-background-color: transparent;");

        boolean hasFood = false;
        for (DetailPesanan d : p.getDaftarItem()) {
            if (d.getItem() instanceof Makanan) {
                hasFood = true;
                Label it = new Label("- " + d.getItem().getNama() + " x" + d.getJumlah());
                itemsBox.getChildren().add(it);
            }
        }

        if (!hasFood) {
            Label noFood = new Label("(Tidak ada item makanan)");
            itemsBox.getChildren().add(noFood);
        }

        Label status = new Label("Status: " + p.getStatus());

        Button doneBtn = new Button("Sudah Masak / Siap");
        doneBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        doneBtn.setOnAction(e -> {
            p.setStatus("Siap");
            RestaurantSystem.getInstance().saveAllPesanan();
            ordersContainer.getChildren().remove(card);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Pesanan #" + p.getIdPesanan() + " ditandai siap.", ButtonType.OK);
            a.showAndWait();
        });

        // If there are many items, allow expanding
        ScrollPane itemScroll = new ScrollPane(itemsBox);
        itemScroll.setPrefHeight(120);
        itemScroll.setFitToWidth(true);
        itemScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        card.getChildren().addAll(id, meja, new Separator(), itemScroll, status, doneBtn);
        VBox.setVgrow(itemScroll, Priority.ALWAYS);
        return card;
    }

    public static void main(String[] args) {
        launch();
    }
}

