# 🐄 Farm Store Manager Project
**Java CSC-251 – Advanced Java Programming**

A Java application that helps manage the daily operations of a small farm store — including tracking inventory, recording sales, and generating reports.  
This project demonstrates the use of **object-oriented programming**, **loops**, **arrays/collections**, and **file I/O** concepts.

---

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Setup & Installation](#setup--installation)
- [How to Run](#how-to-run)
- [Usage Example](#usage-example)
- [Design Structure](#design-structure)
- [Contributors](#contributors)
- [Deliverables](#deliverables)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## 🐑 Overview
The **Farm Store Manager** application was created as part of the **Java CSC-251 (Advanced Java Programming)** course at *Fayetteville Technical Community College (FTCC)*.  
It allows users to:
- Manage farm inventory (produce, supplies, and animal products)
- Track weekly sales and returns
- Generate summary reports
- Demonstrate Java programming skills for academic purposes

---

## 🌾 Features
- Add, edit, and remove farm items  
- Process customer sales and returns  
- View weekly or total revenue  
- Display inventory lists in a clean, tabular format  
- Option to repeat the program using loops  
- Basic input validation and error handling  

---

## 🧰 Technologies
- **Language:** Java 17 (or your version)
- **IDE:** Visual Studio Code / IntelliJ / Eclipse  
- **Version Control:** Git + GitHub  
- **Paradigm:** Object-Oriented Programming (OOP)
- **Optional:** Java Swing GUI components (if implemented)

---

## ⚙️ Setup & Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/JAVA-CSC-251/Farm-Store-Manager-Project.git
   ```
2. Open the folder in your preferred IDE (VS Code, IntelliJ, or Eclipse).

3. Make sure your Java SDK is properly configured.

4. Navigate to the src folder if necessary.

## ▶️ How to Run

Run the program using your IDE’s Run button or from the terminal:
```bash
cd Farm-Store-Manager-Project/src
javac FarmStoreManager.java
java FarmStoreManager
```

You’ll see a text-based menu to add inventory, process sales, and generate reports.

## 💡 Usage Example

**Main Menu Example:**
```
=== FARM STORE MANAGER ===
1. Add Item to Inventory
2. Process Sale
3. Process Return
4. View Report
5. Exit
Enter your choice:
```

**Example Output:**
```
Item added successfully!
Current inventory total: 25 items
Weekly revenue: $1,245.50
```
## 🧩 Design Structure

FarmStoreManager.java – Main driver class containing the program loop

InventoryItem.java – Class defining product details

InventoryManager.java – Handles adding/removing items and quantity updates

SalesManager.java – Processes purchases, returns, and calculations

ReportGenerator.java – Displays totals and summaries

DataHandler.java – (Optional) Manages file reading/writing

All classes demonstrate encapsulation, modularization, and reusability.

## 🧾 Deliverables

Java source files (.java)

Pseudocode (Word document)

Screenshots of program output

Final README.md file (this one)

GitHub commit history for collaboration tracking

## 🚀 Future Improvements

Add a GUI using Java Swing or JavaFX

Connect to a MySQL or SQLite database

Generate formatted reports (CSV, PDF)

Implement user authentication and roles

Add JUnit test cases for key components


## 📄 License

This project is created for educational purposes at FTCC.
You may reference or modify this code for learning and non-commercial use.

## 🌟 Thank you for viewing our Farm Store Manager Project!

If you’d like to suggest an improvement, please open an issue or pull request on GitHub.




