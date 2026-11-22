# Store Inventory Management System (JavaFX)
**Course:** SE-314: Software Construction  
**Assessment:** Open-Ended Lab (OEL)  
**Candidate Name:** Haseeb Ahmad  
**Institution:** Military College of Signals (MCS), National University of Sciences and Technology (NUST)  
**CLO Mapping:** CLO-4 (PLO-5 P-3, 25%) & CLO-5 (PLO-5 P-4, 75%)

---

## 1. Project Overview
The **Store Inventory Management System** is an enterprise-grade desktop inventory tracking application constructed in Java 27 and JavaFX. The application enables small-business store managers to track physical catalog stock, receive real-time visibility into inventory levels, dynamically adjust and update stock counts, filter items on the fly, and prevent stock-outs through automated status indicators.

### Key Capabilities:
- **Add Items:** Insert items with commercial name, quantity, and optional custom or auto-calculated stock status.
- **View All Items:** Real-time dual display offering both an interactive `TableView` with status badges and quick-increment action buttons, plus a raw text/CSV export view.
- **Update Item Details:** Effortlessly update an item's name, quantity, and status. Selecting any table row automatically populates the form for editing.
- **Delete Items:** Safely remove items from the catalog with confirmation dialogs.
- **Automated Stock Status Classification:**
  - `Out of Stock`: Quantity $\le 0$
  - `Low Stock`: $1 \le \text{Quantity} < 10$
  - `In Stock`: $\text{Quantity} \ge 10$
- **Rigorously Validated Input:** Rejects empty or whitespace names, prevents negative or non-numeric quantities, blocks duplicate records, and displays user-friendly warning alerts.
- **KPI Metrics Dashboard:** Real-time counter tiles displaying Total Items, In Stock, Low Stock, and Out of Stock aggregates.

---

## 2. Tools & Environment
- **JDK:** Java SE Development Kit 27 (Oracle HotSpot 64-bit)
- **GUI Framework:** OpenJFX 21 (JavaFX Controls & FXML)
- **Version Control:** Git 2.x & GitHub (`git status`, `git log`, `git diff`)
- **Documentation:** Javadoc Tool (generating HTML documentation with 0 warnings)
- **IDE Support:** Apache NetBeans / IntelliJ IDEA / Eclipse / Terminal CLI

---

## 3. Directory Structure
```
eol/
├── .gitignore               # Strict exclusion of compiled bytecode & binaries
├── InventoryItem.java       # Core entity model with validation & threshold logic
├── InventoryManager.java    # Business logic controller & collection repository
├── Main.java                # Modern JavaFX desktop GUI application
├── InventoryApp.java        # Application alias & launcher
├── InventoryTest.java       # Standalone automated CLO-5 test suite (10 test cases)
├── pom.xml                  # Maven project descriptor
├── README.md                # Project architecture & execution instructions
├── run.bat / run.ps1        # One-click execution launcher
├── build.bat                # One-click project compilation script
├── test.bat                 # One-click automated test runner
├── generate_javadoc.bat     # One-click Javadoc HTML generator
├── lib/                     # Bundled JavaFX runtime libraries (controls, base, graphics, fxml)
└── docs/                    # Complete generated Javadoc HTML documentation
```

---

## 4. Execution & Quick Start

### Option A: One-Click Execution (Recommended)
Simply double-click `run.bat` (or run `.\run.ps1` in PowerShell).  
The script automatically locates the JDK 27 runtime and launches the JavaFX GUI with all required module paths.

### Option B: Command Line (PowerShell)
```powershell
& "..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe" --module-path "lib" --add-modules javafx.controls,javafx.fxml -cp "." Main
```

### Option C: Running the Automated Test Suite
Double-click `test.bat` or run:
```powershell
& "..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe" -cp "." InventoryTest
```

### Option D: Generating Javadoc Documentation
Double-click `generate_javadoc.bat` or run:
```powershell
& "..\jdk-27_windows-x64_bin\jdk-27\bin\javadoc.exe" --module-path "lib" --add-modules javafx.controls,javafx.fxml -d docs -author -version -private InventoryItem.java InventoryManager.java Main.java InventoryApp.java InventoryTest.java
```

---

## 5. Automated Test Cases Matrix (Section 9 CLO-5)
| Test ID | Test Scenario | Input Data | Expected Result | Actual Result | Status |
|:---:|:---|:---|:---|:---|:---:|
| **TC-01** | Add a valid item | "Mechanical Keyboard", 15 | Item added to inventory; status "In Stock" | Added successfully | **PASS** |
| **TC-02** | Add empty or whitespace name | `""` or `"   "` | `IllegalArgumentException` thrown; validation alert | Rejected with validation message | **PASS** |
| **TC-03** | Add negative or non-numeric quantity | `quantity = -5` or `"abc"` | `IllegalArgumentException` / `NumberFormatException` | Error message shown; rejected | **PASS** |
| **TC-04** | View all items | 3 items inserted | All 3 items returned with exact attributes | Displayed in TableView & list | **PASS** |
| **TC-05** | View an empty inventory | Empty inventory | Total count is 0; empty placeholder shown | Appropriate empty message shown | **PASS** |
| **TC-06** | Update an existing item | "Monitor Stand" from 8 to 25 | Quantity updated to 25, status becomes "In Stock" | Updated successfully | **PASS** |
| **TC-07** | Update non-existing item | "NonExistentWidget" | Method returns `false`; alert displayed gracefully | Handled gracefully | **PASS** |
| **TC-08** | Delete an existing item | Delete "Temporary Item" | Item removed from repository; count decremented | Item removed completely | **PASS** |
| **TC-09** | Duplicate item rejection | Duplicate name with varied case | `false` returned; duplicate warning shown | Duplicate blocked | **PASS** |
| **TC-10** | Stock status thresholds | Quantities: 0, 5, 25 | Evaluated to Out of Stock, Low Stock, In Stock | Exact threshold evaluation | **PASS** |

---

## 6. Git Revision History
The project maintains an atomic, disciplined Git revision history following Conventional Commits format:
1. `feat(core): initialize project structure and implement InventoryItem entity model`
2. `feat(manager): implement InventoryManager business logic, stock status rules, and search capabilities`
3. `test(core): add automated validation test suite for all inventory lifecycle and boundary conditions`
4. `feat(ui): implement modern JavaFX user interface with interactive TableView and real-time dashboard`
5. `docs(javadoc): generate comprehensive Javadoc HTML documentation with complete tag coverage`
6. `chore(tools): add automated build and run scripts for JDK 27 and JavaFX runtime`
