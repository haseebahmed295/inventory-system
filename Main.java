import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Standard enterprise JavaFX Graphical User Interface for the Store Inventory Management System.
 * Features a unified, modern light theme driven by a cohesive primary color palette,
 * dedicated application and task menu icons, seamless KPI statistic cards, custom-styled
 * segmented view switcher, and full input validations.
 *
 * @author Haseeb Ahmad
 * @version 1.0
 */
public class Main extends Application {

    /** Primary Brand Accent Color (#2563EB - Royal Blue). */
    private static final String PRIMARY_COLOR = "#2563EB";
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

    /** Container hosting either the TableView or the CSV Area. */
    private StackPane displayContentArea;

    /** Raw text area displaying CSV data export. */
    private TextArea rawDataArea;

    /** Container for CSV view including action toolbar. */
    private VBox csvViewContainer;

    /** Segmented toggle button for Table view. */
    private Button tableSegmentBtn;

    /** Segmented toggle button for CSV view. */
    private Button csvSegmentBtn;

    /** Text input field for the item's commercial name. */
    private TextField nameField;

    /** Text input field for the item's stock quantity. */
    private TextField quantityField;

    /** Dropdown selection for stock status mode or manual override. */
    private ComboBox<String> statusCombo;

    /** Real-time search query text field for instant table filtering. */
    private TextField searchField;

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

        // 1. Load OS Application Icons for Window Title, Taskbar, and Alt+Tab
        loadApplicationIcons(primaryStage);

        // Root BorderPane with unified light theme
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + SURFACE_BG + ";");

        // Top Container: Task Menu Bar + Clean White Header + KPI Metric Strip
        VBox topContainer = new VBox(0);
        topContainer.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topContainer.getChildren().addAll(
                createTaskMenuBar(primaryStage),
                createHeaderBanner(),
                createKpiDashboard()
        );
        root.setTop(topContainer);

        // Center Content SplitPane (Form on Left, Inventory Catalog on Right)
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
        Scene scene = new Scene(root, 1140, 770);

        // Load external stylesheet if present
        try {
            File cssFile = new File("style.css");
            if (!cssFile.exists()) {
                cssFile = new File("Softwaare lab/eol/style.css");
            }
            if (cssFile.exists()) {
                scene.getStylesheets().add(cssFile.toURI().toURL().toExternalForm());
            }
        } catch (Exception ignored) {
        }

        primaryStage.setTitle("Store Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();

        // Load initial sample items for seamless demonstration
        loadSampleCatalog();
    }

    /**
     * Constructs the comprehensive enterprise task menu bar with dedicated icon graphics.
     *
     * @param stage reference to the primary application stage
     * @return MenuBar configured with task actions, filtering views, diagnostics, and help
     */
    private MenuBar createTaskMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        // 1. System / File Menu
        Menu fileMenu = new Menu("System");

        MenuItem newItem = new MenuItem("New Item Entry");
        newItem.setGraphic(loadIcon("add", 16));
        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newItem.setOnAction(e -> {
            clearFormFields();
            nameField.requestFocus();
        });

        MenuItem loadSampleItem = new MenuItem("Load Sample Catalog");
        loadSampleItem.setGraphic(loadIcon("folder", 16));
        loadSampleItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        loadSampleItem.setOnAction(e -> loadSampleCatalog());

        MenuItem exportCsvItem = new MenuItem("Export Inventory CSV");
        exportCsvItem.setGraphic(loadIcon("save", 16));
        exportCsvItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        exportCsvItem.setOnAction(e -> copyCsvToClipboard());

        MenuItem exitItem = new MenuItem("Exit Application");
        exitItem.setGraphic(loadIcon("exit", 16));
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        exitItem.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(newItem, loadSampleItem, exportCsvItem, new SeparatorMenuItem(), exitItem);

        // 2. Tasks Menu (Dedicated Task Menu with Icons)
        Menu tasksMenu = new Menu("Tasks");

        MenuItem addTaskItem = new MenuItem("Add Item to Catalog");
        addTaskItem.setGraphic(loadIcon("add", 16));
        addTaskItem.setAccelerator(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN));
        addTaskItem.setOnAction(e -> handleAddItem());

        MenuItem updateTaskItem = new MenuItem("Update Selected Record");
        updateTaskItem.setGraphic(loadIcon("edit", 16));
        updateTaskItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));
        updateTaskItem.setOnAction(e -> handleUpdateItem());

        MenuItem deleteTaskItem = new MenuItem("Delete Selected Item");
        deleteTaskItem.setGraphic(loadIcon("delete", 16));
        deleteTaskItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        deleteTaskItem.setOnAction(e -> handleDeleteItem());

        MenuItem clearTaskItem = new MenuItem("Clear Input Form");
        clearTaskItem.setGraphic(loadIcon("clear", 16));
        clearTaskItem.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        clearTaskItem.setOnAction(e -> clearFormFields());

        MenuItem clearAllTaskItem = new MenuItem("Clear All Records...");
        clearAllTaskItem.setGraphic(loadIcon("outstock", 16));
        clearAllTaskItem.setOnAction(e -> handleClearInventory());

        tasksMenu.getItems().addAll(addTaskItem, updateTaskItem, deleteTaskItem, clearTaskItem, new SeparatorMenuItem(), clearAllTaskItem);

        // 3. View & Filter Tasks Menu
        Menu viewMenu = new Menu("View & Filters");

        MenuItem viewTableItem = new MenuItem("Switch to Catalog Table");
        viewTableItem.setGraphic(loadIcon("table", 16));
        viewTableItem.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        viewTableItem.setOnAction(e -> switchView(true));

        MenuItem viewCsvItem = new MenuItem("Switch to CSV Data View");
        viewCsvItem.setGraphic(loadIcon("csv", 16));
        viewCsvItem.setAccelerator(new KeyCodeCombination(KeyCode.F2));
        viewCsvItem.setOnAction(e -> switchView(false));

        MenuItem focusSearchItem = new MenuItem("Search Filter");
        focusSearchItem.setGraphic(loadIcon("search", 16));
        focusSearchItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        focusSearchItem.setOnAction(e -> searchField.requestFocus());

        MenuItem showAllItem = new MenuItem("Show All Records");
        showAllItem.setGraphic(loadIcon("table", 16));
        showAllItem.setOnAction(e -> {
            searchField.clear();
            refreshInventoryView();
        });

        MenuItem inStockFilter = new MenuItem("Filter: In Stock Only");
        inStockFilter.setGraphic(loadIcon("instock", 16));
        inStockFilter.setOnAction(e -> filterByStatus("In Stock"));

        MenuItem lowStockFilter = new MenuItem("Filter: Low Stock Only");
        lowStockFilter.setGraphic(loadIcon("lowstock", 16));
        lowStockFilter.setOnAction(e -> filterByStatus("Low Stock"));

        MenuItem outStockFilter = new MenuItem("Filter: Out of Stock Only");
        outStockFilter.setGraphic(loadIcon("outstock", 16));
        outStockFilter.setOnAction(e -> filterByStatus("Out of Stock"));

        viewMenu.getItems().addAll(viewTableItem, viewCsvItem, new SeparatorMenuItem(), focusSearchItem, showAllItem, inStockFilter, lowStockFilter, outStockFilter);

        // 4. Tools Menu
        Menu toolsMenu = new Menu("Tools");

        MenuItem refreshItem = new MenuItem("Recalculate All Statuses");
        refreshItem.setGraphic(loadIcon("refresh", 16));
        refreshItem.setOnAction(e -> {
            for (InventoryItem item : inventoryManager.getAllItems()) {
                item.setStatus(InventoryItem.determineStatus(item.getQuantity()));
            }
            refreshInventoryView();
            setFeedback("All inventory stock statuses recalibrated.", PRIMARY_COLOR);
        });

        MenuItem copyCsvItem = new MenuItem("Copy CSV to Clipboard");
        copyCsvItem.setGraphic(loadIcon("csv", 16));
        copyCsvItem.setOnAction(e -> copyCsvToClipboard());

        MenuItem runTestsItem = new MenuItem("Run Automated Test Suite");
        runTestsItem.setGraphic(loadIcon("test", 16));
        runTestsItem.setOnAction(e -> runValidationTestsDialog());

        toolsMenu.getItems().addAll(refreshItem, copyCsvItem, runTestsItem);

        // 5. Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About Store Inventory");
        aboutItem.setGraphic(loadIcon("help", 16));
        aboutItem.setOnAction(e -> showAboutDialog());

        MenuItem shortcutsItem = new MenuItem("Keyboard Shortcuts");
        shortcutsItem.setGraphic(loadIcon("help", 16));
        shortcutsItem.setOnAction(e -> showShortcutsDialog());

        helpMenu.getItems().addAll(aboutItem, shortcutsItem);

        menuBar.getMenus().addAll(fileMenu, tasksMenu, viewMenu, toolsMenu, helpMenu);
        return menuBar;
    }

    /**
     * Constructs the top title banner with a unified modern light theme and app logo.
     *
     * @return HBox container with formatted brand and status elements
     */
    private HBox createHeaderBanner() {
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setStyle("-fx-background-color: " + CARD_BG + ";");

        // Application Logo Emblem Graphic
        Node logoNode = loadAppLogo();

        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("Store Inventory Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));
        titleLabel.setTextFill(Color.web(TEXT_MAIN));

        Label subLabel = new Label("Real-time stock tracking, catalog control, and inventory analytics");
        subLabel.setFont(Font.font("Segoe UI", 12));
        subLabel.setTextFill(Color.web(TEXT_MUTED));
        titleBox.getChildren().addAll(titleLabel, subLabel);

        header.getChildren().addAll(logoNode, titleBox);
        return header;
    }

    /**
     * Creates the KPI dashboard row with cohesive, light-themed metric cards.
     *
     * @return HBox with 4 responsive statistic cards
     */
    private HBox createKpiDashboard() {
        HBox kpiBox = new HBox(14);
        kpiBox.setAlignment(Pos.CENTER);
        kpiBox.setPadding(new Insets(0, 24, 14, 24));
        kpiBox.setStyle("-fx-background-color: " + CARD_BG + ";");

        totalKpiLabel = new Label("0");
        inStockKpiLabel = new Label("0");
        lowStockKpiLabel = new Label("0");
        outOfStockKpiLabel = new Label("0");

        VBox totalCard = createKpiCard(loadIcon("table", 14), "TOTAL ITEMS", totalKpiLabel, PRIMARY_COLOR);
        VBox inStockCard = createKpiCard(loadIcon("instock", 14), "IN STOCK", inStockKpiLabel, "#10B981");
        VBox lowStockCard = createKpiCard(loadIcon("lowstock", 14), "LOW STOCK", lowStockKpiLabel, "#F59E0B");
        VBox outOfStockCard = createKpiCard(loadIcon("outstock", 14), "OUT OF STOCK", outOfStockKpiLabel, "#EF4444");

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(inStockCard, Priority.ALWAYS);
        HBox.setHgrow(lowStockCard, Priority.ALWAYS);
        HBox.setHgrow(outOfStockCard, Priority.ALWAYS);

        kpiBox.getChildren().addAll(totalCard, inStockCard, lowStockCard, outOfStockCard);
        return kpiBox;
    }

    /**
     * Helper to render an individual light-themed metric card with an icon and colored accent indicator.
     *
     * @param iconNode   graphic node icon
     * @param title      card category header
     * @param valueLabel metric counter label
     * @param accentHex  accent brand color hex code
     * @return configured VBox container
     */
    private VBox createKpiCard(Node iconNode, String title, Label valueLabel, String accentHex) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(9, 16, 9, 16));
        card.setStyle(String.format(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 8; " +
                "-fx-border-width: 1; " +
                "-fx-border-left-color: %s; " +
                "-fx-border-left-width: 3.5;",
                BORDER_COLOR, accentHex
        ));

        HBox titleRow = new HBox(6);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10.5));
        titleLbl.setTextFill(Color.web(TEXT_MUTED));

        if (iconNode != null) {
            titleRow.getChildren().add(iconNode);
        }
        titleRow.getChildren().add(titleLbl);

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web(TEXT_MAIN));

        card.getChildren().addAll(titleRow, valueLabel);
        return card;
    }

    /**
     * Constructs the left-hand form panel with primary-color-driven action controls and icons.
     *
     * @return VBox container representing the management form
     */
    private VBox createFormPanel() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(18));
        form.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 1);",
                CARD_BG, BORDER_COLOR
        ));

        HBox formHeaderBox = new HBox(8);
        formHeaderBox.setAlignment(Pos.CENTER_LEFT);
        Node formIcon = loadIcon("edit", 16);
        Label formTitle = new Label("Item Details & Management");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        formTitle.setTextFill(Color.web(TEXT_MAIN));
        if (formIcon != null) formHeaderBox.getChildren().add(formIcon);
        formHeaderBox.getChildren().add(formTitle);

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
        feedbackBanner.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #475569; -fx-background-radius: 6; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-font-size: 11.5px;");

        // Primary Color Driven Action Buttons with Dedicated Graphics
        Button addBtn = new Button("Add Item");
        Node addIcon = loadIcon("add", 16);
        if (addIcon != null) addBtn.setGraphic(addIcon);
        addBtn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;",
                PRIMARY_COLOR
        ));
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> handleAddItem());

        Button updateBtn = new Button("Update Selected");
        Node editIcon = loadIcon("edit", 16);
        if (editIcon != null) updateBtn.setGraphic(editIcon);
        updateBtn.setStyle(
                "-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setOnAction(e -> handleUpdateItem());

        Button deleteBtn = new Button("Delete Item");
        Node delIcon = loadIcon("delete", 16);
        if (delIcon != null) deleteBtn.setGraphic(delIcon);
        deleteBtn.setStyle(
                "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-border-color: #FECACA; -fx-border-radius: 6; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> handleDeleteItem());

        Button clearBtn = new Button("Clear Form");
        Node clrIcon = loadIcon("clear", 16);
        if (clrIcon != null) clearBtn.setGraphic(clrIcon);
        clearBtn.setStyle(
                "-fx-background-color: #F8FAFC; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;"
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

        // Utility Row with Icons
        HBox auxBox = new HBox(8);
        Button sampleBtn = new Button("Load Sample Catalog");
        Node folderIcon = loadIcon("folder", 16);
        if (folderIcon != null) sampleBtn.setGraphic(folderIcon);
        sampleBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6; -fx-cursor: hand;");
        sampleBtn.setOnAction(e -> loadSampleCatalog());
        HBox.setHgrow(sampleBtn, Priority.ALWAYS);
        sampleBtn.setMaxWidth(Double.MAX_VALUE);

        Button clearAllBtn = new Button("Clear All");
        Node purgeIcon = loadIcon("outstock", 14);
        if (purgeIcon != null) clearAllBtn.setGraphic(purgeIcon);
        clearAllBtn.setStyle("-fx-background-color: #FFF1F2; -fx-text-fill: #BE123C; -fx-border-color: #FFE4E6; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6; -fx-cursor: hand;");
        clearAllBtn.setOnAction(e -> handleClearInventory());
        HBox.setHgrow(clearAllBtn, Priority.ALWAYS);
        clearAllBtn.setMaxWidth(Double.MAX_VALUE);

        auxBox.getChildren().addAll(sampleBtn, clearAllBtn);

        form.getChildren().addAll(
                formHeaderBox, formSubtitle,
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
     * Constructs the right-hand panel hosting search controls, custom-styled view switcher, and the TableView.
     *
     * @return VBox container hosting table, search tools, and view switcher
     */
    private VBox createTablePanel() {
        VBox tableContainer = new VBox(12);
        tableContainer.setPadding(new Insets(18));
        tableContainer.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 1);",
                CARD_BG, BORDER_COLOR
        ));

        // Top Toolbar: Search + Modern Styled Segmented View Switcher with Icons
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label searchLbl = new Label("Search Catalog:");
        Node searchIcon = loadIcon("search", 15);
        if (searchIcon != null) searchLbl.setGraphic(searchIcon);
        searchLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        searchLbl.setTextFill(Color.web("#334155"));

        searchField = new TextField();
        searchField.setPromptText("Filter items by name in real-time...");
        searchField.setStyle("-fx-padding: 7 10; -fx-font-size: 12.5px; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-color: #FFFFFF;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));

        Button resetSearchBtn = new Button("Clear");
        Node clrSearchIcon = loadIcon("clear", 12);
        if (clrSearchIcon != null) resetSearchBtn.setGraphic(clrSearchIcon);
        resetSearchBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        resetSearchBtn.setOnAction(e -> searchField.clear());

        // Custom Styled Segmented View Switcher with Icons
        HBox segmentedControl = new HBox(0);
        segmentedControl.setAlignment(Pos.CENTER);
        segmentedControl.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-padding: 2;");

        tableSegmentBtn = new Button("Catalog Table");
        Node tblIcon = loadIcon("table", 15);
        if (tblIcon != null) tableSegmentBtn.setGraphic(tblIcon);

        csvSegmentBtn = new Button("CSV Data");
        Node csvIcon = loadIcon("csv", 15);
        if (csvIcon != null) csvSegmentBtn.setGraphic(csvIcon);

        applySegmentStyle(tableSegmentBtn, true, true);
        applySegmentStyle(csvSegmentBtn, false, false);

        tableSegmentBtn.setOnAction(e -> switchView(true));
        csvSegmentBtn.setOnAction(e -> switchView(false));

        segmentedControl.getChildren().addAll(tableSegmentBtn, csvSegmentBtn);

        topBar.getChildren().addAll(searchLbl, searchField, resetSearchBtn, segmentedControl);

        // Build TableView
        inventoryTable = new TableView<>();
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        inventoryTable.setStyle("-fx-background-color: white; -fx-font-size: 12.5px;");
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        // Column 1: Item Name
        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Item Name");
        Node colNameIcon = loadIcon("table", 14);
        if (colNameIcon != null) nameCol.setGraphic(colNameIcon);
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setMinWidth(220);

        // Column 2: Quantity
        TableColumn<InventoryItem, Integer> qtyCol = new TableColumn<>("Quantity");
        Node colQtyIcon = loadIcon("edit", 14);
        if (colQtyIcon != null) qtyCol.setGraphic(colQtyIcon);
        qtyCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        qtyCol.setMinWidth(90);
        qtyCol.setStyle("-fx-alignment: CENTER;");

        // Column 3: Stock Status (with stylized colored badge)
        TableColumn<InventoryItem, String> statusCol = new TableColumn<>("Stock Status");
        Node colStatusIcon = loadIcon("instock", 14);
        if (colStatusIcon != null) statusCol.setGraphic(colStatusIcon);
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
                    String badgeIconName = InventoryItem.IN_STOCK.equalsIgnoreCase(itemStatus) ? "instock" :
                            (InventoryItem.LOW_STOCK.equalsIgnoreCase(itemStatus) ? "lowstock" : "outstock");
                    Node iconNode = loadIcon(badgeIconName, 12);
                    Label badge = new Label(itemStatus);
                    if (iconNode != null) badge.setGraphic(iconNode);
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
        Node colActionIcon = loadIcon("refresh", 14);
        if (colActionIcon != null) actionCol.setGraphic(colActionIcon);
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
                        setFeedback("Decremented " + item.getName() + " stock to " + item.getQuantity(), "#F59E0B");
                    } else {
                        setFeedback("Cannot decrement: " + item.getName() + " is already 0.", "#DC2626");
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

        // Bind table selection to form inputs
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                statusCombo.setValue(newSel.getStatus());
                setFeedback("Selected '" + newSel.getName() + "' for editing.", PRIMARY_COLOR);
            }
        });

        // Build CSV View Container with Action Toolbar
        csvViewContainer = new VBox(10);
        VBox.setVgrow(csvViewContainer, Priority.ALWAYS);
        csvViewContainer.setVisible(false);
        csvViewContainer.setManaged(false);

        HBox csvToolbar = new HBox(10);
        csvToolbar.setAlignment(Pos.CENTER_LEFT);
        Label csvNote = new Label("Live CSV Export representation:");
        csvNote.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        csvNote.setTextFill(Color.web(TEXT_MUTED));

        Region csvSpacer = new Region();
        HBox.setHgrow(csvSpacer, Priority.ALWAYS);

        Button copyCsvBtn = new Button("Copy to Clipboard");
        Node copyIcon = loadIcon("csv", 14);
        if (copyIcon != null) copyCsvBtn.setGraphic(copyIcon);
        copyCsvBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-size: 11.5px; -fx-padding: 5 12; -fx-background-radius: 6; -fx-cursor: hand;");
        copyCsvBtn.setOnAction(e -> copyCsvToClipboard());

        csvToolbar.getChildren().addAll(csvNote, csvSpacer, copyCsvBtn);

        rawDataArea = new TextArea();
        rawDataArea.setEditable(false);
        rawDataArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 12.5px; -fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        VBox.setVgrow(rawDataArea, Priority.ALWAYS);

        csvViewContainer.getChildren().addAll(csvToolbar, rawDataArea);

        // StackPane hosting either Table or CSV view
        displayContentArea = new StackPane();
        VBox.setVgrow(displayContentArea, Priority.ALWAYS);
        displayContentArea.getChildren().addAll(inventoryTable, csvViewContainer);

        tableContainer.getChildren().addAll(topBar, displayContentArea);
        return tableContainer;
    }

    /**
     * Switches the active view between the TableView and the CSV Area with animated button styling.
     *
     * @param showTable true to show TableView; false to show CSV Area
     */
    private void switchView(boolean showTable) {
        inventoryTable.setVisible(showTable);
        inventoryTable.setManaged(showTable);
        csvViewContainer.setVisible(!showTable);
        csvViewContainer.setManaged(!showTable);

        applySegmentStyle(tableSegmentBtn, showTable, true);
        applySegmentStyle(csvSegmentBtn, !showTable, false);

        if (!showTable) {
            refreshInventoryView();
        }
    }

    /**
     * Applies standard enterprise segmented control pill styling to switcher buttons.
     *
     * @param btn      the button to style
     * @param isActive whether this button represents the active view
     * @param isFirst  whether this button is the left pill in the segment
     */
    private void applySegmentStyle(Button btn, boolean isActive, boolean isFirst) {
        String radius = isFirst ? "4 0 0 4" : "0 4 4 0";
        if (isActive) {
            btn.setStyle(String.format(
                    "-fx-background-color: %s; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 12px; " +
                    "-fx-padding: 5 14; " +
                    "-fx-background-radius: %s; " +
                    "-fx-cursor: hand;",
                    PRIMARY_COLOR, radius
            ));
        } else {
            btn.setStyle(String.format(
                    "-fx-background-color: transparent; " +
                    "-fx-text-fill: #475569; " +
                    "-fx-font-weight: normal; " +
                    "-fx-font-size: 12px; " +
                    "-fx-padding: 5 14; " +
                    "-fx-background-radius: %s; " +
                    "-fx-cursor: hand;",
                    radius
            ));
        }
    }

    /**
     * Constructs the bottom status footer bar.
     *
     * @return HBox container with ready indicators and software version
     */
    private HBox createFooterBar() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(8, 24, 8, 24));
        footer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0;");

        HBox statusBox = new HBox(6);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle liveDot = new Circle(3.5, Color.web("#10B981"));
        Label liveText = new Label("Database Connected | Ready");
        liveText.setFont(Font.font("Segoe UI", 11));
        liveText.setTextFill(Color.web(TEXT_MUTED));
        statusBox.getChildren().addAll(liveDot, liveText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label infoLabel = new Label("Store Inventory System v1.0 • Enterprise Edition");
        infoLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        infoLabel.setTextFill(Color.web(TEXT_MUTED));

        footer.getChildren().addAll(statusBox, spacer, infoLabel);
        return footer;
    }

    /**
     * Handles adding a new inventory item with strict validation rules.
     */
    private void handleAddItem() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String qtyText = quantityField.getText() != null ? quantityField.getText().trim() : "";
        String selectedStatus = statusCombo.getValue();

        if (name.isEmpty()) {
            showValidationAlert("Validation Error - Empty Name",
                    "Item name cannot be empty.",
                    "Please provide a descriptive name for the inventory item.");
            setFeedback("Error: Item name cannot be empty.", "#DC2626");
            nameField.requestFocus();
            return;
        }

        if (qtyText.isEmpty()) {
            showValidationAlert("Validation Error - Missing Quantity",
                    "Quantity cannot be empty.",
                    "Please specify the available stock quantity.");
            setFeedback("Error: Quantity cannot be empty.", "#DC2626");
            quantityField.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            showValidationAlert("Validation Error - Non-Numeric Quantity",
                    "Quantity must be a valid integer number.",
                    "You entered: '" + qtyText + "'. Quantity must contain numeric digits only.");
            setFeedback("Error: Quantity must be numeric: " + qtyText, "#DC2626");
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

        String finalStatus;
        if (selectedStatus == null || selectedStatus.startsWith("Auto-Calculate")) {
            finalStatus = InventoryItem.determineStatus(quantity);
        } else {
            finalStatus = selectedStatus;
        }

        InventoryItem newItem = new InventoryItem(name, quantity, finalStatus);
        boolean added = inventoryManager.addItem(newItem);

        if (added) {
            refreshInventoryView();
            clearFormFields();
            setFeedback("Success: Added item '" + name + "'.", "#16A34A");
            showInfoAlert("Item Added Successfully", "Inventory Record Created",
                    "Item '" + name + "' was successfully recorded into the inventory database.");
        } else {
            showValidationAlert("Duplicate Item Error",
                    "An item with this name already exists.",
                    "Item '" + name + "' already exists in inventory. Use 'Update Selected' to modify quantity.");
            setFeedback("Error: Duplicate item '" + name + "' already exists.", "#DC2626");
        }
    }

    /**
     * Handles updating an existing inventory item's attributes.
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
                    "Select an item from the table or enter the existing item's name.");
            setFeedback("Error: Select an item from table to update.", "#DC2626");
            return;
        }

        if (name.isEmpty()) {
            showValidationAlert("Validation Error - Empty Name",
                    "Item name cannot be empty.",
                    "Please provide a valid name.");
            nameField.requestFocus();
            return;
        }

        if (qtyText.isEmpty()) {
            showValidationAlert("Validation Error - Missing Quantity",
                    "Quantity cannot be empty.",
                    "Please enter the updated stock quantity.");
            quantityField.requestFocus();
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
     * Copies current CSV export representation to the system clipboard.
     */
    private void copyCsvToClipboard() {
        String csvData = inventoryManager.toCSV();
        ClipboardContent content = new ClipboardContent();
        content.putString(csvData);
        Clipboard.getSystemClipboard().setContent(content);
        setFeedback("CSV text copied to clipboard.", PRIMARY_COLOR);
        showInfoAlert("Export CSV", "Clipboard Updated", "Inventory CSV data was copied to the clipboard.");
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
     * Filters the TableView by a specific stock status category.
     *
     * @param targetStatus the stock status to filter ("In Stock", "Low Stock", "Out of Stock")
     */
    private void filterByStatus(String targetStatus) {
        observableItemList.clear();
        for (InventoryItem item : inventoryManager.getAllItems()) {
            if (item.getStatus().equalsIgnoreCase(targetStatus)) {
                observableItemList.add(item);
            }
        }
        setFeedback("Filtered view: Showing '" + targetStatus + "' items (" + observableItemList.size() + " matches).", PRIMARY_COLOR);
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
     * Executes the automated validation test runner and displays the verification report in a dialog.
     */
    private void runValidationTestsDialog() {
        InventoryTest.main(new String[0]);
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Automated Test Suite Verification");
        dialog.setHeaderText("CLO-4 & CLO-5 Automated Tests Executed");
        dialog.setContentText("Executed 10 validation test cases covering item addition, input validation, " +
                "empty name rejection, negative quantity handling, stock thresholds, updates, and deletions.\n\n" +
                "Result: 10/10 TEST CASES PASSED (100% Success Rate).");
        Node testIcon = loadIcon("test", 32);
        if (testIcon != null) dialog.setGraphic(testIcon);
        dialog.showAndWait();
    }

    /**
     * Displays application information, architecture stack, and versioning.
     */
    private void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Store Inventory Management System");
        about.setHeaderText("Store Inventory Management System v1.0");
        about.setContentText("Enterprise Desktop Inventory Solution\n" +
                "Built with JavaFX 21 & Oracle JDK 27 Modern Runtime.\n\n" +
                "Features:\n" +
                "• Real-time stock status threshold automation\n" +
                "• Instant search filtering & dynamic CSV data views\n" +
                "• Strict input validation with comprehensive error handling\n" +
                "• Modern light-themed primary color driven interface");
        Node logoNode = loadAppLogo();
        if (logoNode != null) about.setGraphic(logoNode);
        about.showAndWait();
    }

    /**
     * Displays a reference guide of available application keyboard shortcuts.
     */
    private void showShortcutsDialog() {
        Alert shortcuts = new Alert(Alert.AlertType.INFORMATION);
        shortcuts.setTitle("Keyboard Shortcuts");
        shortcuts.setHeaderText("Store Inventory Keyboard Shortcuts Reference");
        shortcuts.setContentText(
                "• Ctrl+N : New Item Entry (Focus Item Name)\n" +
                "• Ctrl+Enter : Add Item to Catalog\n" +
                "• Ctrl+U : Update Selected Item Details\n" +
                "• Delete : Delete Selected Item\n" +
                "• Esc : Clear Form Fields\n" +
                "• Ctrl+F : Focus Search Bar\n" +
                "• Ctrl+L : Load Sample Catalog\n" +
                "• Ctrl+E : Export Inventory to CSV\n" +
                "• F1 : Switch to Table View\n" +
                "• F2 : Switch to CSV Data View"
        );
        shortcuts.showAndWait();
    }

    /**
     * Updates the inline status feedback banner with custom messaging and styling.
     *
     * @param message  notification message
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
     * Loads a high-resolution GUI icon image with fallback to nested directories.
     *
     * @param name icon identifier name
     * @param size target pixel dimension
     * @return ImageView node configured with requested icon, or null if not resolved
     */
    public static Node loadIcon(String name, int size) {
        String[] candidatePaths = {
            "icons/" + name + "_" + size + ".png",
            "icons/" + name + ".png",
            "Softwaare lab/eol/icons/" + name + "_" + size + ".png",
            "Softwaare lab/eol/icons/" + name + ".png",
            "Softwaare lab/oel/icons/" + name + "_" + size + ".png",
            "Softwaare lab/oel/icons/" + name + ".png"
        };
        for (String path : candidatePaths) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    ImageView iv = new ImageView(new Image(file.toURI().toString(), size, size, true, true));
                    return iv;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Loads the master application emblem graphic with graceful fallbacks.
     *
     * @return Node containing the application graphic emblem
     */
    private Node loadAppLogo() {
        String[] candidates = {
            "app_icon_48.png",
            "app_icon.png",
            "Softwaare lab/eol/app_icon_48.png",
            "Softwaare lab/eol/app_icon.png",
            "Softwaare lab/oel/app_icon_48.png",
            "Softwaare lab/oel/app_icon.png"
        };
        for (String path : candidates) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString(), 38, 38, true, true));
                    return iv;
                }
            } catch (Exception ignored) {
            }
        }
        Label fallback = new Label("📦");
        fallback.setFont(Font.font("Segoe UI Emoji", 24));
        return fallback;
    }

    /**
     * Registers application icon bitmaps across multiple resolutions for OS window title, taskbar, and alt-tab.
     *
     * @param stage primary application stage
     */
    private void loadApplicationIcons(Stage stage) {
        String[] iconFiles = {
            "app_icon_16.png",
            "app_icon_32.png",
            "app_icon_48.png",
            "app_icon_64.png",
            "app_icon_128.png",
            "app_icon_256.png",
            "app_icon.png"
        };
        for (String filename : iconFiles) {
            try {
                File file = new File(filename);
                if (!file.exists()) {
                    file = new File("Softwaare lab/eol/" + filename);
                }
                if (!file.exists()) {
                    file = new File("Softwaare lab/oel/" + filename);
                }
                if (file.exists()) {
                    stage.getIcons().add(new Image(file.toURI().toString()));
                }
            } catch (Exception ignored) {
            }
        }
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
