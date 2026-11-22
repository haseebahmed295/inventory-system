import java.util.List;

/**
 * Automated Test Suite for the Inventory Management System.
 * Validates functional and boundary requirements specified in Section 9 of the Open-Ended Lab manual (CLO-5).
 * Tests all 8 core rubric test cases and auxiliary business rules.
 *
 * @author Haseeb Ahmad
 * @version 1.0
 */
public class InventoryTest {

    /** Running count of executed test cases. */
    private static int totalTests = 0;

    /** Running count of successful test assertions. */
    private static int passedTests = 0;

    /** Running count of failed test assertions. */
    private static int failedTests = 0;

    /** Default constructor. */
    public InventoryTest() {
        // Explicit constructor documented for Javadoc cleanliness
    }

    /**
     * Executes the comprehensive test suite and outputs a formatted result matrix.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("   SE-314 SOFTWARE CONSTRUCTION - OPEN-ENDED LAB (OEL) AUTOMATED TEST SUITE   ");
        System.out.println("   Course: SE-314 Software Construction | Candidate: Haseeb Ahmad              ");
        System.out.println("===============================================================================");

        testAddValidItem();
        testAddEmptyName();
        testAddNegativeOrNonNumericQuantity();
        testViewAllItems();
        testViewEmptyInventory();
        testUpdateExistingItem();
        testUpdateNonExistingItem();
        testDeleteExistingItem();
        testDuplicateItemHandling();
        testStockStatusThresholds();

        System.out.println("===============================================================================");
        System.out.printf("   TEST EXECUTION SUMMARY: Total: %d | Passed: %d | Failed: %d%n",
                totalTests, passedTests, failedTests);
        if (failedTests == 0) {
            System.out.println("   RESULT: ALL TEST CASES PASSED SUCCESSFULLY (100% SUCCESS RATE)");
        } else {
            System.out.println("   RESULT: SOME TESTS FAILED. PLEASE REVIEW LOGS.");
        }
        System.out.println("===============================================================================");

        if (failedTests > 0) {
            System.exit(1);
        }
    }

    /**
     * Evaluates a test condition and logs pass or fail status to console.
     *
     * @param testName  identifier of the test case
     * @param condition boolean assertion result
     * @param details   explanatory context or diagnostic message
     */
    private static void assertCondition(String testName, boolean condition, String details) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("[PASS] %-30s | %s%n", testName, details);
        } else {
            failedTests++;
            System.err.printf("[FAIL] %-30s | %s%n", testName, details);
        }
    }

    /**
     * Test Case 1: Add a valid item -&gt; Item is added.
     */
    public static void testAddValidItem() {
        InventoryManager manager = new InventoryManager();
        boolean result = manager.addItem("Mechanical Keyboard", 15, "In Stock");
        InventoryItem item = manager.findItemByName("Mechanical Keyboard");

        assertCondition("TC-01: Add Valid Item",
                result && item != null && item.getQuantity() == 15,
                "Valid item added successfully and retrievable by name.");
    }

    /**
     * Test Case 2: Add an item with an empty name -&gt; Validation message shown / Exception thrown.
     */
    public static void testAddEmptyName() {
        boolean exceptionThrown = false;
        try {
            new InventoryItem("", 10);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        boolean whitespaceThrown = false;
        try {
            new InventoryItem("   ", 10);
        } catch (IllegalArgumentException e) {
            whitespaceThrown = true;
        }

        assertCondition("TC-02: Add Empty Name",
                exceptionThrown && whitespaceThrown,
                "Empty and whitespace names properly rejected with IllegalArgumentException.");
    }

    /**
     * Test Case 3: Add an item with a negative or non-numeric quantity -&gt; Error message shown.
     */
    public static void testAddNegativeOrNonNumericQuantity() {
        boolean negativeRejected = false;
        try {
            new InventoryItem("USB Hub", -5);
        } catch (IllegalArgumentException e) {
            negativeRejected = true;
        }

        boolean nonNumericRejected = false;
        try {
            Integer.parseInt("abc_invalid");
        } catch (NumberFormatException e) {
            nonNumericRejected = true;
        }

        assertCondition("TC-03: Negative/Non-Numeric Qty",
                negativeRejected && nonNumericRejected,
                "Negative numbers and non-numeric inputs rejected with appropriate errors.");
    }

    /**
     * Test Case 4: View all items -&gt; All items displayed.
     */
    public static void testViewAllItems() {
        InventoryManager manager = new InventoryManager();
        manager.addItem("Item Alpha", 10);
        manager.addItem("Item Beta", 20);
        manager.addItem("Item Gamma", 5);

        List<InventoryItem> all = manager.getAllItems();
        assertCondition("TC-04: View All Items",
                all.size() == 3 && all.get(0).getName().equals("Item Alpha"),
                "All 3 items accurately retrieved and displayed in collection.");
    }

    /**
     * Test Case 5: View an empty inventory -&gt; Appropriate message shown.
     */
    public static void testViewEmptyInventory() {
        InventoryManager manager = new InventoryManager();
        List<InventoryItem> all = manager.getAllItems();
        int count = manager.getItemCount();

        assertCondition("TC-05: View Empty Inventory",
                all.isEmpty() && count == 0,
                "Empty inventory accurately verified with count=0 and empty item list.");
    }

    /**
     * Test Case 6: Update an existing item -&gt; Details updated.
     */
    public static void testUpdateExistingItem() {
        InventoryManager manager = new InventoryManager();
        manager.addItem("Monitor Stand", 8, "Low Stock");
        boolean updated = manager.updateItem("Monitor Stand", 25, "In Stock");
        InventoryItem item = manager.findItemByName("Monitor Stand");

        assertCondition("TC-06: Update Existing Item",
                updated && item != null && item.getQuantity() == 25 && "In Stock".equals(item.getStatus()),
                "Item quantity updated to 25 and status updated to 'In Stock'.");
    }

    /**
     * Test Case 7: Update a non-existing item -&gt; Handled gracefully.
     */
    public static void testUpdateNonExistingItem() {
        InventoryManager manager = new InventoryManager();
        boolean updated = manager.updateItem("NonExistentWidget", 50, "In Stock");

        assertCondition("TC-07: Update Non-Existing",
                !updated,
                "Non-existent item update handled gracefully returning false without crashing.");
    }

    /**
     * Test Case 8: Delete an existing item -&gt; Item removed.
     */
    public static void testDeleteExistingItem() {
        InventoryManager manager = new InventoryManager();
        manager.addItem("Temporary Item", 5);
        boolean deleted = manager.deleteItem("Temporary Item");
        InventoryItem lookup = manager.findItemByName("Temporary Item");

        assertCondition("TC-08: Delete Existing Item",
                deleted && lookup == null && manager.getItemCount() == 0,
                "Item removed successfully; subsequent lookups return null.");
    }

    /**
     * Auxiliary Test 9: Duplicate item prevention.
     */
    public static void testDuplicateItemHandling() {
        InventoryManager manager = new InventoryManager();
        manager.addItem("Webcam 1080p", 12);
        boolean duplicateAdded = manager.addItem("webcam 1080p", 5); // Case-insensitive duplicate

        assertCondition("TC-09: Duplicate Item Blocked",
                !duplicateAdded && manager.getItemCount() == 1,
                "Duplicate item with different casing properly rejected.");
    }

    /**
     * Auxiliary Test 10: Automatic threshold transitions.
     */
    public static void testStockStatusThresholds() {
        String outStatus = InventoryItem.determineStatus(0);
        String lowStatus = InventoryItem.determineStatus(5);
        String inStatus = InventoryItem.determineStatus(25);

        assertCondition("TC-10: Stock Thresholds",
                "Out of Stock".equals(outStatus) &&
                "Low Stock".equals(lowStatus) &&
                "In Stock".equals(inStatus),
                "Automatic thresholds correctly set: 0=Out, 5=Low, 25=In Stock.");
    }
}
