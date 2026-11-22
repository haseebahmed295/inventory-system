import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller service managing the central repository of inventory items.
 * Provides high-level business capabilities including item persistence, updates,
 * deletions, case-insensitive searches, stock statistical aggregates, and CSV serialization.
 *
 * @author Haseeb Ahmad
 * @version 1.0
 */
public class InventoryManager {

    /** Internal collection holding inventory records in memory. */
    private final List<InventoryItem> items;

    /**
     * Constructs a new InventoryManager instance initialized with an empty item list.
     */
    public InventoryManager() {
        this.items = new ArrayList<>();
    }

    /**
     * Constructs a new InventoryManager pre-populated with an existing list of items.
     *
     * @param initialItems collection of initial items to populate (non-null)
     * @throws IllegalArgumentException if initialItems collection is null
     */
    public InventoryManager(List<InventoryItem> initialItems) {
        if (initialItems == null) {
            throw new IllegalArgumentException("Initial items collection cannot be null.");
        }
        this.items = new ArrayList<>(initialItems);
    }

    /**
     * Adds an InventoryItem to the inventory after validating non-null and duplicate checks.
     *
     * @param item the inventory item to insert
     * @return true if successfully inserted; false if an item with the same name already exists
     * @throws IllegalArgumentException if item is null
     */
    public boolean addItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null inventory item.");
        }
        if (containsItem(item.getName())) {
            return false; // Prevent duplicate inventory records
        }
        return items.add(item);
    }

    /**
     * Adds a new item using name and quantity, auto-deriving the stock status.
     *
     * @param name     item name (must be non-empty)
     * @param quantity non-negative stock quantity
     * @return true if added; false if duplicate exists
     */
    public boolean addItem(String name, int quantity) {
        return addItem(new InventoryItem(name, quantity));
    }

    /**
     * Adds a new item using explicit name, quantity, and stock status.
     *
     * @param name     item name
     * @param quantity non-negative stock quantity
     * @param status   stock status string ("In Stock", "Low Stock", "Out of Stock")
     * @return true if added; false if duplicate exists
     */
    public boolean addItem(String name, int quantity, String status) {
        return addItem(new InventoryItem(name, quantity, status));
    }

    /**
     * Determines whether an item with the given commercial name exists in the inventory (case-insensitive).
     *
     * @param name the item name to check
     * @return true if found; false otherwise
     */
    public boolean containsItem(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return items.stream().anyMatch(i -> i.getName().equalsIgnoreCase(trimmed));
    }

    /**
     * Retrieves a defensive, unmodifiable copy of all inventory items.
     *
     * @return list containing all current inventory items
     */
    public List<InventoryItem> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    /**
     * Searches for an item by name using a case-insensitive match.
     *
     * @param name the name to look up
     * @return the matched InventoryItem instance, or null if not found
     */
    public InventoryItem findItemByName(String name) {
        if (name == null) return null;
        String query = name.trim();
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(query))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves an item by its zero-based index in the collection.
     *
     * @param index zero-based index
     * @return the InventoryItem at the index, or null if index is out of bounds
     */
    public InventoryItem findItemByIndex(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    /**
     * Updates an existing item's quantity and stock status.
     *
     * @param name        the name of the existing item to update
     * @param newQuantity the new non-negative quantity
     * @param newStatus   the new stock status
     * @return true if the item was found and updated; false if item does not exist
     * @throws IllegalArgumentException if quantity is negative or status is invalid
     */
    public boolean updateItem(String name, int newQuantity, String newStatus) {
        InventoryItem existing = findItemByName(name);
        if (existing == null) {
            return false;
        }
        existing.setQuantityAndStatus(newQuantity, newStatus);
        return true;
    }

    /**
     * Updates an existing item's name, quantity, and status.
     *
     * @param oldName     the current name of the item
     * @param newName     the new name (must not conflict with another existing item)
     * @param newQuantity the new non-negative quantity
     * @param newStatus   the new stock status
     * @return true if updated successfully; false if item not found or new name conflicts
     */
    public boolean updateItem(String oldName, String newName, int newQuantity, String newStatus) {
        InventoryItem existing = findItemByName(oldName);
        if (existing == null) {
            return false;
        }
        if (newName != null && !newName.trim().equalsIgnoreCase(oldName.trim())) {
            if (containsItem(newName)) {
                return false; // New name collides with another item
            }
            existing.setName(newName);
        }
        existing.setQuantityAndStatus(newQuantity, newStatus);
        return true;
    }

    /**
     * Updates only the quantity of an existing item, automatically updating status.
     *
     * @param name        the name of the item
     * @param newQuantity the new non-negative quantity
     * @return true if updated; false if not found
     */
    public boolean updateQuantity(String name, int newQuantity) {
        InventoryItem existing = findItemByName(name);
        if (existing == null) {
            return false;
        }
        existing.setQuantity(newQuantity);
        return true;
    }

    /**
     * Deletes an item from the inventory by its name.
     *
     * @param name the name of the item to delete
     * @return true if removed; false if not found
     */
    public boolean deleteItem(String name) {
        InventoryItem existing = findItemByName(name);
        if (existing == null) {
            return false;
        }
        return items.remove(existing);
    }

    /**
     * Deletes an item by its zero-based index.
     *
     * @param index zero-based index to remove
     * @return true if removed; false if index is invalid
     */
    public boolean deleteItemByIndex(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        items.remove(index);
        return true;
    }

    /**
     * Performs a substring search across item names.
     *
     * @param keyword the search query
     * @return filtered list of matching items
     */
    public List<InventoryItem> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        String lowerQuery = keyword.trim().toLowerCase();
        return items.stream()
                .filter(i -> i.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the total count of inventory items.
     *
     * @return integer item count
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Retrieves the count of items currently marked as "In Stock".
     *
     * @return count of in-stock items
     */
    public int getInStockCount() {
        return (int) items.stream()
                .filter(i -> InventoryItem.IN_STOCK.equals(i.getStatus()))
                .count();
    }

    /**
     * Retrieves the count of items currently marked as "Low Stock".
     *
     * @return count of low-stock items
     */
    public int getLowStockCount() {
        return (int) items.stream()
                .filter(i -> InventoryItem.LOW_STOCK.equals(i.getStatus()))
                .count();
    }

    /**
     * Retrieves the count of items currently marked as "Out of Stock".
     *
     * @return count of out-of-stock items
     */
    public int getOutOfStockCount() {
        return (int) items.stream()
                .filter(i -> InventoryItem.OUT_OF_STOCK.equals(i.getStatus()))
                .count();
    }

    /**
     * Removes all items from the inventory, resulting in an empty state.
     */
    public void clearInventory() {
        items.clear();
    }

    /**
     * Pre-populates the inventory with realistic sample hardware catalog items.
     */
    public void loadSampleData() {
        items.clear();
        items.add(new InventoryItem("Logitech MX Master 3S Mouse", 24, InventoryItem.IN_STOCK));
        items.add(new InventoryItem("Keychron K2 Mechanical Keyboard", 6, InventoryItem.LOW_STOCK));
        items.add(new InventoryItem("Dell UltraSharp 27\" 4K Monitor", 0, InventoryItem.OUT_OF_STOCK));
        items.add(new InventoryItem("Anker 100W USB-C Fast Cable", 45, InventoryItem.IN_STOCK));
        items.add(new InventoryItem("Sony WH-1000XM5 ANC Headphones", 3, InventoryItem.LOW_STOCK));
    }

    /**
     * Serializes the entire inventory collection into standard CSV format.
     *
     * @return formatted CSV string with headers
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("Item Name,Quantity,Stock Status\n");
        for (InventoryItem item : items) {
            sb.append(item.toCSV()).append("\n");
        }
        return sb.toString();
    }
}
