import java.util.Objects;

/**
 * Represents an inventory item entity within the Inventory Management System.
 * Encapsulates item attributes including name, quantity, and stock availability status,
 * enforcing business validation rules and threshold-based status evaluation.
 *
 * @author Haseeb Ahmad
 * @version 1.0
 */
public class InventoryItem {

    /** Constant representing an abundant stock status. */
    public static final String IN_STOCK = "In Stock";

    /** Constant representing an item with depleted inventory below the critical threshold. */
    public static final String LOW_STOCK = "Low Stock";

    /** Constant representing an exhausted inventory with zero quantity. */
    public static final String OUT_OF_STOCK = "Out of Stock";

    /** Threshold boundary for Low Stock classification. */
    public static final int LOW_STOCK_THRESHOLD = 10;

    /** Unique commercial name of the inventory item. */
    private String name;

    /** Current non-negative stock quantity. */
    private int quantity;

    /** Stock classification status ("In Stock", "Low Stock", or "Out of Stock"). */
    private String status;

    /**
     * Constructs an InventoryItem with explicit name, quantity, and stock status.
     *
     * @param name     the item name (must not be null or whitespace-only)
     * @param quantity the stock quantity (must be a non-negative integer &gt;= 0)
     * @param status   the stock status ("In Stock", "Low Stock", or "Out of Stock")
     * @throws IllegalArgumentException if the name is invalid, quantity is negative, or status is invalid
     */
    public InventoryItem(String name, int quantity, String status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
        this.name = name.trim();
        this.quantity = quantity;
        this.status = validateAndNormalizeStatus(status, quantity);
    }

    /**
     * Constructs an InventoryItem with name and quantity, automatically deriving the stock status.
     *
     * @param name     the item name (must not be null or empty)
     * @param quantity the stock quantity (must be non-negative)
     * @throws IllegalArgumentException if name is invalid or quantity is negative
     */
    public InventoryItem(String name, int quantity) {
        this(name, quantity, determineStatus(quantity));
    }

    /**
     * Evaluates and returns the appropriate stock status based on current quantity thresholds.
     * <ul>
     *   <li>quantity &lt;= 0: "Out of Stock"</li>
     *   <li>1 &lt;= quantity &lt; 10: "Low Stock"</li>
     *   <li>quantity &gt;= 10: "In Stock"</li>
     * </ul>
     *
     * @param quantity the stock count to evaluate
     * @return the corresponding stock status string
     */
    public static String determineStatus(int quantity) {
        if (quantity <= 0) {
            return OUT_OF_STOCK;
        } else if (quantity < LOW_STOCK_THRESHOLD) {
            return LOW_STOCK;
        } else {
            return IN_STOCK;
        }
    }

    /**
     * Normalizes and validates the status string against permitted constants.
     * If null or empty, falls back to automatic derivation based on quantity.
     *
     * @param status   the status string candidate
     * @param quantity the item's quantity
     * @return the normalized valid status string
     * @throws IllegalArgumentException if an invalid custom status is supplied
     */
    private static String validateAndNormalizeStatus(String status, int quantity) {
        if (status == null || status.trim().isEmpty()) {
            return determineStatus(quantity);
        }
        String clean = status.trim();
        if (clean.equalsIgnoreCase(IN_STOCK)) {
            return IN_STOCK;
        } else if (clean.equalsIgnoreCase(LOW_STOCK)) {
            return LOW_STOCK;
        } else if (clean.equalsIgnoreCase(OUT_OF_STOCK)) {
            return OUT_OF_STOCK;
        } else {
            throw new IllegalArgumentException("Invalid status: '" + status +
                    "'. Allowed values: 'In Stock', 'Low Stock', 'Out of Stock'");
        }
    }

    /**
     * Retrieves the item name.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item name with validation.
     *
     * @param name the new item name (must not be null or whitespace)
     * @throws IllegalArgumentException if name is null or whitespace
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    /**
     * Retrieves the current stock quantity.
     *
     * @return the stock quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity and automatically updates the stock status according to thresholds.
     *
     * @param quantity the new non-negative quantity
     * @throws IllegalArgumentException if quantity is negative
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
        this.quantity = quantity;
        this.status = determineStatus(quantity);
    }

    /**
     * Sets both quantity and explicit status simultaneously.
     *
     * @param quantity the new non-negative quantity
     * @param status   the new stock status
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void setQuantityAndStatus(int quantity, String status) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
        this.quantity = quantity;
        this.status = validateAndNormalizeStatus(status, quantity);
    }

    /**
     * Retrieves the current stock status.
     *
     * @return the stock status ("In Stock", "Low Stock", or "Out of Stock")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Manually updates the stock status.
     *
     * @param status the new stock status ("In Stock", "Low Stock", or "Out of Stock")
     * @throws IllegalArgumentException if the status is unrecognized
     */
    public void setStatus(String status) {
        this.status = validateAndNormalizeStatus(status, this.quantity);
    }

    /**
     * Increments the inventory quantity by the specified amount and recalibrates status.
     *
     * @param amount the positive amount to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void addQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increment amount cannot be negative: " + amount);
        }
        this.quantity += amount;
        this.status = determineStatus(this.quantity);
    }

    /**
     * Decrements the inventory quantity by the specified amount and recalibrates status.
     * Prevents quantity from falling below zero.
     *
     * @param amount the positive amount to deduct
     * @throws IllegalArgumentException if amount is negative or exceeds available stock
     */
    public void subtractQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Decrement amount cannot be negative: " + amount);
        }
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Cannot deduct " + amount + " from current stock of " + this.quantity);
        }
        this.quantity -= amount;
        this.status = determineStatus(this.quantity);
    }

    /**
     * Returns a CSS hexadecimal color string corresponding to the stock status.
     *
     * @return "#10B981" for In Stock, "#F59E0B" for Low Stock, "#EF4444" for Out of Stock
     */
    public String getStatusHexColor() {
        switch (status) {
            case IN_STOCK:
                return "#10B981"; // Emerald Green
            case LOW_STOCK:
                return "#F59E0B"; // Amber
            case OUT_OF_STOCK:
            default:
                return "#EF4444"; // Rose Red
        }
    }

    /**
     * Formats this inventory record as a comma-separated values (CSV) string.
     *
     * @return comma-separated representation (Name,Quantity,Status)
     */
    public String toCSV() {
        return String.format("%s,%d,%s", name, quantity, status);
    }

    /**
     * Returns a structured string representation of this inventory item.
     *
     * @return descriptive string of item attributes
     */
    @Override
    public String toString() {
        return String.format("InventoryItem[name='%s', quantity=%d, status='%s']", name, quantity, status);
    }

    /**
     * Compares two InventoryItems for equality based on case-insensitive item name.
     *
     * @param o the comparison object
     * @return true if both items share the identical commercial name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return name.equalsIgnoreCase(that.name);
    }

    /**
     * Computes the hash code based on the lowercase item name.
     *
     * @return hash code integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
