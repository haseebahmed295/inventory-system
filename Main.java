import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * Standard enterprise JavaFX Graphical User Interface for the Store Inventory Management System.
 * Built with a modern, primary-color-driven UI theme, clean visual hierarchy, reactive KPI metrics,
 * full input validation, and interactive catalog controls.
 *
 * @author Haseeb Ahmad
 * @version 1.0
 */
public class Main extends Application {

    /** Primary Brand Accent Color (#2563EB - Royal Blue). */
    private static final String PRIMARY_COLOR = "#2563EB";
    private static final String PRIMARY_HOVER = "#1D4ED8";
    private static final String SURFACE_BG = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String TEXT_MAIN = "#0F172A";
    private static final String TEXT_MUTED = "#64748B";

    /** Central inventory business controller. */
    private InventoryManager inventoryManager;

    /** Observable collection backing the JavaFX TableView. */
    private ObservableList<InventoryItem> observableItemList;

    /** Primary interactive table displaying inventory records. */
    private TableView<InventoryItem> inventoryTable;

    /** Text input field for the item's commercial name. */
    private TextField nameField;

    /** Text input field for the item's stock quantity. */
    private TextField quantityField;

    /** Dropdown selection for stock status mode or manual override. */
    private ComboBox<String> statusCombo;

    /** Real-time search query text field for instant table filtering. */
    private TextField searchField;

    /** Raw text area displaying CSV data export. */
    private TextArea rawDataArea;

    /** KPI display label for total inventory item count. */
    private Label totalKpiLabel;

    /** KPI display label for items with abundant stock. */
    private Label inStockKpiLabel;

    /** KPI display label for items with critical low stock. */
    private Label lowStockKpiLabel;

    /** KPI display label for items completely depleted. */
    private Label outOfStockKpiLabel;

    /** Inline banner displaying real-time feedback status and validation warnings. */
    private Label feedbackBanner;

    /** Default constructor initializing UI state. */
    public Main() {
        // Explicit constructor documented for Javadoc cleanliness
    }

    /**
     * Initializes the JavaFX primary application window and orchestrates the layout hierarchy.
     *
     * @param primaryStage the primary window stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {
        inventoryManager = new InventoryManager();
        observableItemList = FXCollections.observableArrayList();

        // Root BorderPane
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + SURFACE_BG + ";");

        // Top Header and KPI Dashboard (Modern Primary-Driven Design)
        VBox topContainer = new VBox(14);
        topContainer.setPadding(new Insets(16, 24, 16, 24));
        topContainer.setStyle("-fx-background-color: #0F172A; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 10, 0, 0, 3);");
        topContainer.getChildren().addAll(createHeaderBanner(), createKpiDashboard());
        root.setTop(topContainer);

        // Center Content SplitPane (Form on Left, Inventory Table on Right)
        HBox centerContent = new HBox(18);
        centerContent.setPadding(new Insets(18, 24, 18, 24));
        VBox.setVgrow(centerContent, Priority.ALWAYS);

        VBox formPanel = createFormPanel();
        VBox tablePanel = createTablePanel();

        formPanel.setPrefWidth(370);
        formPanel.setMinWidth(330);
        HBox.setHgrow(tablePanel, Priority.ALWAYS);

        centerContent.getChildren().addAll(formPanel, tablePanel);
        root.setCenter(centerContent);

        // Bottom Footer Bar
        root.setBottom(createFooterBar());

        // Configure Scene and Window
        Scene scene = new Scene(root, 1120, 740);
        primaryStage.setTitle("Store Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);
        primaryStage.show();

        // Load initial sample items for seamless demonstration
        loadSampleCatalog();
    }

    /**
     * Constructs the top title banner with standard enterprise branding.
     *
     * @return HBox container with formatted labels
     */
    private HBox createHeaderBanner() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label titleLabel = new Label("Store Inventory Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 21));
        titleLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("Real-time stock tracking, catalog control, and inventory analytics");
        subLabel.setFont(Font.font("Segoe UI", 12));
        subLabel.setTextFill(Color.web("#94A3B8"));
        titleBox.getChildren().addAll(titleLabel, subLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Clean System Online Badge
        HBox statusPill = new HBox(8);
        statusPill.setAlignment(Pos.CENTER);
        statusPill.setPadding(new Insets(6, 14, 6, 14));
        statusPill.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 20; -fx-border-color: #334155; -fx-border-radius: 20;");

        Circle dot = new Circle(4, Color.web("#10B981"));
        Label pillText = new Label("System Online");
        pillText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11.5));
        pillText.setTextFill(Color.web("#E2E8F0"));
        statusPill.getChildren().addAll(dot, pillText);

        header.getChildren().addAll(titleBox, spacer, statusPill);
        return header;
    }

    /**
     * Creates the KPI dashboard row with cohesive, theme-driven metric cards.
     *
     * @return HBox with 4 responsive statistic cards
     */
    private HBox createKpiDashboard() {
        HBox kpiBox = new HBox(14);
        kpiBox.setAlignment(Pos.CENTER);

        totalKpiLabel = new Label("0");
        inStockKpiLabel = new Label("0");
        lowStockKpiLabel = new Label("0");
        outOfStockKpiLabel = new Label("0");

        VBox totalCard = createKpiCard("TOTAL ITEMS", totalKpiLabel, PRIMARY_COLOR);
        VBox inStockCard = createKpiCard("IN STOCK", inStockKpiLabel, "#10B981");
        VBox lowStockCard = createKpiCard("LOW STOCK", lowStockKpiLabel, "#F59E0B");
        VBox outOfStockCard = createKpiCard("OUT OF STOCK", outOfStockKpiLabel, "#EF4444");

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(inStockCard, Priority.ALWAYS);
        HBox.setHgrow(lowStockCard, Priority.ALWAYS);
        HBox.setHgrow(outOfStockCard, Priority.ALWAYS);

        kpiBox.getChildren().addAll(totalCard, inStockCard, lowStockCard, outOfStockCard);
        return kpiBox;
    }

    /**
     * Helper to render an individual metric card adhering to the unified theme.
     *
     * @param title      card category header
     * @param valueLabel metric counter label
     * @param accentHex  accent brand color hex code
     * @return configured VBox container
     */
    private VBox createKpiCard(String title, Label valueLabel, String accentHex) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setStyle(String.format(
                "-fx-background-color: #1E293B; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #334155; " +
                "-fx-border-radius: 8; " +
                "-fx-border-width: 1;"
        ));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Circle indicator = new Circle(3.5, Color.web(accentHex));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        titleLbl.setTextFill(Color.web("#94A3B8"));
        titleRow.getChildren().addAll(indicator, titleLbl);

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleRow, valueLabel);
        return card;
    }

    /**
     * Constructs the left-hand form panel with primary-color-driven action controls.
     *
     * @return VBox container representing the management form
     */
    private VBox createFormPanel() {
        VBox form = new VBox(13);
        form.setPadding(new Insets(18));
        form.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);",
                CARD_BG, BORDER_COLOR
        ));

        Label formTitle = new Label("Item Details & Management");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        formTitle.setTextFill(Color.web(TEXT_MAIN));

        Label formSubtitle = new Label("Add new store inventory items or update existing records.");
        formSubtitle.setWrapText(true);
        formSubtitle.setFont(Font.font("Segoe UI", 11.5));
        formSubtitle.setTextFill(Color.web(TEXT_MUTED));

        // Name input
        Label nameLbl = new Label("Item Name *");
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        nameLbl.setTextFill(Color.web("#334155"));
        nameField = new TextField();
        nameField.setPromptText("e.g. Wireless Ergonomic Mouse");
        nameField.setStyle("-fx-padding: 8 10; -fx-font-size: 13px; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-color: #FFFFFF;");

        // Quantity input
        Label qtyLbl = new Label("Quantity (Units) *");
        qtyLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        qtyLbl.setTextFill(Color.web("#334155"));
        quantityField = new TextField();
        quantityField.setPromptText("Non-negative integer (e.g. 25)");
        quantityField.setStyle("-fx-padding: 8 10; -fx-font-size: 13px; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-color: #FFFFFF;");

        // Status Combo Box
        Label statusLbl = new Label("Stock Status");
        statusLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        statusLbl.setTextFill(Color.web("#334155"));
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Auto-Calculate (from Qty)", "In Stock", "Low Stock", "Out of Stock");
        statusCombo.setValue("Auto-Calculate (from Qty)");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setStyle("-fx-padding: 4 8; -fx-font-size: 12.5px; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-color: #FFFFFF;");

        // Feedback Banner
        feedbackBanner = new Label("Ready. Enter item attributes above.");
        feedbackBanner.setWrapText(true);
        feedbackBanner.setMaxWidth(Double.MAX_VALUE);
        feedbackBanner.setPadding(new Insets(8, 12, 8, 12));
        feedbackBanner.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 6; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-font-size: 11.5px;");

        // Primary Color Driven Buttons Grid
        Button addBtn = new Button("Add Item");
        addBtn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 9 14; -fx-background-radius: 6; -fx-cursor: hand;",
                PRIMARY_COLOR
        ));
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> handleAddItem());

        Button updateBtn = new Button("Update Selected");
        updateBtn.setStyle(
                "-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 9 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setOnAction(e -> handleUpdateItem());

        Button deleteBtn = new Button("Delete Item");
        deleteBtn.setStyle(
                "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-border-color: #FECACA; -fx-border-radius: 6; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 9 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> handleDeleteItem());

        Button clearBtn = new Button("Clear Form");
        clearBtn.setStyle(
                "-fx-background-color: #F8FAFC; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 9 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> clearFormFields());

        GridPane btnGrid = new GridPane();
        btnGrid.setHgap(8);
        btnGrid.setVgap(8);
        btnGrid.add(addBtn, 0, 0);
        btnGrid.add(updateBtn, 1, 0);
        btnGrid.add(deleteBtn, 0, 1);
        btnGrid.add(clearBtn, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        btnGrid.getColumnConstraints().addAll(col1, col2);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #E2E8F0;");

        // Utility Row
        HBox auxBox = new HBox(8);
        Button sampleBtn = new Button("Load Sample Catalog");
        sampleBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6; -fx-cursor: hand;");
        sampleBtn.setOnAction(e -> loadSampleCatalog());
        HBox.setHgrow(sampleBtn, Priority.ALWAYS);
        sampleBtn.setMaxWidth(Double.MAX_VALUE);

        Button clearAllBtn = new Button("Clear All");
        clearAllBtn.setStyle("-fx-background-color: #FFF1F2; -fx-text-fill: #BE123C; -fx-border-color: #FFE4E6; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6; -fx-cursor: hand;");
        clearAllBtn.setOnAction(e -> handleClearInventory());
        HBox.setHgrow(clearAllBtn, Priority.ALWAYS);
        clearAllBtn.setMaxWidth(Double.MAX_VALUE);

        auxBox.getChildren().addAll(sampleBtn, clearAllBtn);

        form.getChildren().addAll(
                formTitle, formSubtitle,
                nameLbl, nameField,
                qtyLbl, quantityField,
                statusLbl, statusCombo,
                feedbackBanner,
                btnGrid,
                sep,
                auxBox
        );

        return form;
    }

    /**
     * Constructs the right-hand panel hosting search controls and the interactive TableView.
     *
     * @return VBox container hosting table and search tools
     */
    private VBox createTablePanel() {
        VBox tableContainer = new VBox(13);
        tableContainer.setPadding(new Insets(18));
        tableContainer.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);",
                CARD_BG, BORDER_COLOR
        ));

        // Top Search Bar
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label searchLbl = new Label("Search Catalog:");
        searchLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        searchLbl.setTextFill(Color.web("#334155"));

        searchField = new TextField();
        searchField.setPromptText("Filter items by name in real-time...");
        searchField.setStyle("-fx-padding: 8 10; -fx-font-size: 12.5px; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-color: #FFFFFF;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));

        Button resetSearchBtn = new Button("Clear Search");
        resetSearchBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-size: 12px; -fx-padding: 7 12; -fx-background-radius: 6; -fx-cursor: hand;");
        resetSearchBtn.setOnAction(e -> searchField.clear());

        topBar.getChildren().addAll(searchLbl, searchField, resetSearchBtn);

        // Build TableView
        inventoryTable = new TableView<>();
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        inventoryTable.setStyle("-fx-background-color: white; -fx-font-size: 12.5px;");
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        // Column 1: Item Name
        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Item Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setMinWidth(220);

        // Column 2: Quantity
        TableColumn<InventoryItem, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        qtyCol.setMinWidth(90);
        qtyCol.setStyle("-fx-alignment: CENTER;");

        // Column 3: Stock Status (with stylized colored badge)
        TableColumn<InventoryItem, String> statusCol = new TableColumn<>("Stock Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setMinWidth(130);
        statusCol.setStyle("-fx-alignment: CENTER;");
        statusCol.setCellFactory(col -> new TableCell<InventoryItem, String>() {
            @Override
            protected void updateItem(String itemStatus, boolean empty) {
                super.updateItem(itemStatus, empty);
                if (empty || itemStatus == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(itemStatus);
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                    badge.setPadding(new Insets(3, 10, 3, 10));
                    if (InventoryItem.IN_STOCK.equalsIgnoreCase(itemStatus)) {
                        badge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-background-radius: 12; -fx-border-color: #A7F3D0; -fx-border-radius: 12;");
                    } else if (InventoryItem.LOW_STOCK.equalsIgnoreCase(itemStatus)) {
                        badge.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #D97706; -fx-background-radius: 12; -fx-border-color: #FDE68A; -fx-border-radius: 12;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-background-radius: 12; -fx-border-color: #FECACA; -fx-border-radius: 12;");
                    }
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Column 4: Quick Stock Adjust Actions
        TableColumn<InventoryItem, Void> actionCol = new TableColumn<>("Quick Adjust");
        actionCol.setMinWidth(110);
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<InventoryItem, Void>() {
            private final Button incBtn = new Button("+");
            private final Button decBtn = new Button("-");
            private final HBox btnBox = new HBox(6, incBtn, decBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                incBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2 9; -fx-background-radius: 4; -fx-cursor: hand;");
                decBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2 9; -fx-background-radius: 4; -fx-cursor: hand;");

                incBtn.setOnAction(e -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    item.addQuantity(1);
                    refreshInventoryView();
                    setFeedback("Incremented " + item.getName() + " stock to " + item.getQuantity(), PRIMARY_COLOR);
                });

                decBtn.setOnAction(e -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    if (item.getQuantity() > 0) {
                        item.subtractQuantity(1);
                        refreshInventoryView();
                        setFeedback("Decremented " + item.getName() + " stock to " + item.getQuantity(), "#D97706");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });

        inventoryTable.getColumns().addAll(nameCol, qtyCol, statusCol, actionCol);
        inventoryTable.setItems(observableItemList);

        // Empty Table Placeholder
        Label emptyPlaceholder = new Label("No inventory records found.\nEnter item details on the left or load the sample catalog.");
        emptyPlaceholder.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-font-style: italic; -fx-text-alignment: center;");
        inventoryTable.setPlaceholder(emptyPlaceholder);

        // Click row to populate form for instant editing
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                statusCombo.setValue(newSel.getStatus());
                setFeedback("Selected '" + newSel.getName() + "' for editing.", PRIMARY_COLOR);
            }
        });

        // Tabbed Display to switch between Table and CSV Export
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tableTab = new Tab("Catalog Table", inventoryTable);

        rawDataArea = new TextArea();
        rawDataArea.setEditable(false);
        rawDataArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 12px;");
        Tab csvTab = new Tab("CSV Data View", rawDataArea);

        tabPane.getTabs().addAll(tableTab, csvTab);
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == csvTab) {
                rawDataArea.setText(inventoryManager.toCSV());
            }
        });

        tableContainer.getChildren().addAll(topBar, tabPane);
        return tableContainer;
    }

    /**
     * Constructs the bottom status and metadata footer bar.
     *
     * @return HBox containing version, runtime, and status notes
     */
    private HBox createFooterBar() {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(8, 24, 8, 24));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");

        Label statusInfo = new Label("System Status: Online | Database Ready");
        statusInfo.setFont(Font.font("Segoe UI", 11.5));
        statusInfo.setTextFill(Color.web("#475569"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label systemInfo = new Label("Store Inventory System v1.0 • Enterprise Edition");
        systemInfo.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11.5));
        systemInfo.setTextFill(Color.web("#64748B"));

        footer.getChildren().addAll(statusInfo, spacer, systemInfo);
        return footer;
    }

    /**
     * Handles adding a new inventory item, enforcing rigorous input validations.
     */
    private void handleAddItem() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String qtyText = quantityField.getText() != null ? quantityField.getText().trim() : "";
        String selectedStatus = statusCombo.getValue();

        // Validation Rule 1: Item name cannot be empty
        if (name.isEmpty()) {
            showValidationAlert("Validation Error - Empty Name",
                    "Item name cannot be empty.",
                    "Please provide a descriptive commercial name for the inventory item.");
            setFeedback("Error: Item name cannot be empty.", "#DC2626");
            nameField.requestFocus();
            return;
        }

        // Validation Rule 2: Quantity must be non-negative integer
        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            showValidationAlert("Validation Error - Invalid Quantity",
                    "Quantity must be a valid non-negative integer.",
                    "You entered: '" + qtyText + "'. Please enter numeric digits only.");
            setFeedback("Error: Quantity must be numeric digits only.", "#DC2626");
            quantityField.requestFocus();
            return;
        }

        if (quantity < 0) {
            showValidationAlert("Validation Error - Negative Quantity",
                    "Quantity cannot be negative.",
                    "Stock counts cannot be negative. You specified: " + quantity);
            setFeedback("Error: Quantity cannot be negative: " + quantity, "#DC2626");
            quantityField.requestFocus();
            return;
        }

        // Determine stock status
        String finalStatus;
        if (selectedStatus == null || selectedStatus.startsWith("Auto-Calculate")) {
            finalStatus = InventoryItem.determineStatus(quantity);
        } else {
            finalStatus = selectedStatus;
        }

        // Business Logic Insertion
        boolean added = inventoryManager.addItem(name, quantity, finalStatus);
        if (added) {
            refreshInventoryView();
            clearFormFields();
            setFeedback("Added item '" + name + "' (Qty: " + quantity + ", Status: " + finalStatus + ").", PRIMARY_COLOR);
            showInfoAlert("Item Added Successfully", "Inventory Record Created",
                    "Item '" + name + "' was successfully recorded into the inventory database.");
        } else {
            showValidationAlert("Duplicate Item Error",
                    "Item '" + name + "' already exists in inventory.",
                    "To modify existing stock, please select the item and use the 'Update Selected' action.");
            setFeedback("Error: Duplicate item '" + name + "' rejected.", "#DC2626");
        }
    }

    /**
     * Handles updating an existing inventory item's details and stock status.
     */
    private void handleUpdateItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String qtyText = quantityField.getText() != null ? quantityField.getText().trim() : "";
        String selectedStatus = statusCombo.getValue();

        String targetName = (selected != null) ? selected.getName() : name;

        if (targetName.isEmpty()) {
            showValidationAlert("Update Error - Missing Target",
                    "No item selected for update.",
                    "Please select an item from the table or specify an existing item name.");
            setFeedback("Error: Select an item from the table to update.", "#DC2626");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            showValidationAlert("Validation Error - Invalid Quantity",
                    "Quantity must be a valid integer.",
                    "Enter a non-negative number for the updated stock.");
            quantityField.requestFocus();
            return;
        }

        if (quantity < 0) {
            showValidationAlert("Validation Error - Negative Quantity",
                    "Quantity cannot be negative.",
                    "Stock counts cannot be negative.");
            quantityField.requestFocus();
            return;
        }

        String finalStatus;
        if (selectedStatus == null || selectedStatus.startsWith("Auto-Calculate")) {
            finalStatus = InventoryItem.determineStatus(quantity);
        } else {
            finalStatus = selectedStatus;
        }

        boolean updated = inventoryManager.updateItem(targetName, name, quantity, finalStatus);
        if (updated) {
            refreshInventoryView();
            setFeedback("Updated item '" + name + "' details.", PRIMARY_COLOR);
            showInfoAlert("Update Successful", "Inventory Updated",
                    "Item '" + targetName + "' was successfully updated to quantity " + quantity + " (" + finalStatus + ").");
        } else {
            showValidationAlert("Update Failure",
                    "Item '" + targetName + "' not found.",
                    "The specified item does not exist in inventory records.");
            setFeedback("Error: Target item '" + targetName + "' not found.", "#DC2626");
        }
    }

    /**
     * Handles deleting the selected inventory item with user confirmation.
     */
    private void handleDeleteItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String targetName = (selected != null) ? selected.getName() : name;

        if (targetName.isEmpty()) {
            showValidationAlert("Delete Error - Missing Target",
                    "No item selected for deletion.",
                    "Please select an item from the table to delete.");
            setFeedback("Error: Select an item to delete.", "#DC2626");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Inventory Item: '" + targetName + "'?");
        confirm.setContentText("Are you sure you wish to permanently remove this item from the store inventory?");

        ButtonType btnYes = new ButtonType("Yes, Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnYes, btnCancel);

        if (confirm.showAndWait().orElse(btnCancel) == btnYes) {
            boolean removed = inventoryManager.deleteItem(targetName);
            if (removed) {
                refreshInventoryView();
                clearFormFields();
                setFeedback("Item '" + targetName + "' removed from inventory.", "#DC2626");
                showInfoAlert("Item Removed", "Deletion Complete",
                        "Item '" + targetName + "' has been successfully removed from the system.");
            } else {
                showValidationAlert("Delete Failure",
                        "Item '" + targetName + "' not found.",
                        "The item could not be found in the current inventory.");
            }
        }
    }

    /**
     * Loads standard demo catalog items for rapid testing and demonstrations.
     */
    private void loadSampleCatalog() {
        inventoryManager.loadSampleData();
        refreshInventoryView();
        clearFormFields();
        setFeedback("Loaded 5 store catalog items into inventory.", PRIMARY_COLOR);
    }

    /**
     * Clears all items from the inventory to demonstrate empty state handling.
     */
    private void handleClearInventory() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear Entire Inventory");
        confirm.setHeaderText("Wipe all inventory records?");
        confirm.setContentText("This will empty the inventory database to test the empty inventory state.");

        ButtonType btnWipe = new ButtonType("Yes, Clear All", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnWipe, btnCancel);

        if (confirm.showAndWait().orElse(btnCancel) == btnWipe) {
            inventoryManager.clearInventory();
            refreshInventoryView();
            clearFormFields();
            setFeedback("Inventory completely emptied.", "#64748B");
        }
    }

    /**
     * Clears the input form fields and resets selection to default state.
     */
    private void clearFormFields() {
        nameField.clear();
        quantityField.clear();
        statusCombo.setValue("Auto-Calculate (from Qty)");
        inventoryTable.getSelectionModel().clearSelection();
    }

    /**
     * Filters the TableView in real-time based on the user's search query.
     *
     * @param query substring search query
     */
    private void filterTable(String query) {
        List<InventoryItem> filtered = inventoryManager.searchItems(query);
        observableItemList.setAll(filtered);
    }

    /**
     * Refreshes table items, KPI dashboard statistics, and raw text representations.
     */
    private void refreshInventoryView() {
        String query = searchField != null ? searchField.getText() : "";
        if (query != null && !query.trim().isEmpty()) {
            filterTable(query);
        } else {
            observableItemList.setAll(inventoryManager.getAllItems());
        }

        // Update KPI metrics
        totalKpiLabel.setText(String.valueOf(inventoryManager.getItemCount()));
        inStockKpiLabel.setText(String.valueOf(inventoryManager.getInStockCount()));
        lowStockKpiLabel.setText(String.valueOf(inventoryManager.getLowStockCount()));
        outOfStockKpiLabel.setText(String.valueOf(inventoryManager.getOutOfStockCount()));

        // Update Raw CSV area if initialized
        if (rawDataArea != null) {
            rawDataArea.setText(inventoryManager.toCSV());
        }
    }

    /**
     * Updates the inline status feedback banner with custom messaging and styling.
     *
     * @param message notification message
     * @param hexColor color hex code
     */
    private void setFeedback(String message, String hexColor) {
        feedbackBanner.setText(message);
        feedbackBanner.setStyle(String.format(
                "-fx-background-color: #F8FAFC; -fx-text-fill: %s; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-border-width: 1; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 8 12;",
                hexColor
        ));
    }

    /**
     * Displays a validation warning alert dialog.
     *
     * @param title   alert window title
     * @param header  short summary header
     * @param content detailed instructional text
     */
    private void showValidationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an informational notification alert dialog.
     *
     * @param title   alert window title
     * @param header  short summary header
     * @param content detailed message
     */
    private void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Application entry point launching the JavaFX runtime.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
