import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


class Transaction {
    private String type;
    private String category;
    private double amount;
    private String date;

    public Transaction(String type, String category, double amount, String date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return type + "," + category + "," + amount + "," + date;
    }
}

public class ExpenseTracker {

    private List<Transaction> transactions;
    private JFrame frame;

    public ExpenseTracker() {
        transactions = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

       
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton addButton = createLargeButton("Add Transaction");
        JButton loadButton = createLargeButton("Load From File");
        JButton saveButton = createLargeButton("Save To File");
        JButton summaryButton = createLargeButton("View Summary");
        JButton exitButton = createLargeButton("Exit");

        toolbar.add(addButton);
        toolbar.add(loadButton);
        toolbar.add(saveButton);
        toolbar.add(summaryButton);
        toolbar.add(exitButton);
        frame.add(toolbar, BorderLayout.NORTH);

      
        addButton.addActionListener(e -> showAddTransactionDialog());
        loadButton.addActionListener(e -> loadTransactionsFromFile());
        saveButton.addActionListener(e -> saveTransactionsToFile());
        summaryButton.addActionListener(e -> showMonthlySummaryDialog());
        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private JButton createLargeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

   private void showMessage(String message, boolean isSuccess) {
    JTextPane textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setFont(new Font("Arial", Font.BOLD, 18));
    textPane.setForeground(isSuccess ? new Color(0, 100, 0) : Color.RED); // Dark green for success
    textPane.setText(message.replaceAll(",", "\n")); // Replace commas with new lines for separation
    
  
    StyledDocument doc = textPane.getStyledDocument();
    SimpleAttributeSet center = new SimpleAttributeSet();
    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    doc.setParagraphAttributes(0, doc.getLength(), center, false);

    JOptionPane.showMessageDialog(frame, textPane, "Message", JOptionPane.PLAIN_MESSAGE);
}


 private void showAddTransactionDialog() {
    String[] incomeCategories = {"Salary", "Business"};
    String[] expenseCategories = {"Food", "Rent", "Travel"};

    JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
    JComboBox<String> categoryComboBox = new JComboBox<>(incomeCategories);
    JTextField amountField = new JTextField();

   
    LocalDate currentDate = LocalDate.now();
    int currentYear = currentDate.getYear();
    int currentMonth = currentDate.getMonthValue();

    JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2000, 2100, 1));
    JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));

    typeComboBox.addActionListener(e -> {
        if (typeComboBox.getSelectedItem().equals("Income")) {
            categoryComboBox.setModel(new DefaultComboBoxModel<>(incomeCategories));
        } else {
            categoryComboBox.setModel(new DefaultComboBoxModel<>(expenseCategories));
        }
    });

    JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
    panel.add(new JLabel("Type:"));
    panel.add(typeComboBox);
    panel.add(new JLabel("Category:"));
    panel.add(categoryComboBox);
    panel.add(new JLabel("Amount:"));
    panel.add(amountField);
    panel.add(new JLabel("Year:"));
    panel.add(yearSpinner);
    panel.add(new JLabel("Month:"));
    panel.add(monthSpinner);

    int result = JOptionPane.showConfirmDialog(frame, panel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            String type = (String) typeComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            int year = (Integer) yearSpinner.getValue();
            int month = (Integer) monthSpinner.getValue();
            String date = String.format("%04d-%02d", year, month);
            transactions.add(new Transaction(type, category, amount, date));
            showMessage("Transaction added successfully!", true);
        } catch (NumberFormatException ex) {
            showMessage("Invalid amount format!", false);
        }
    }
}
    private void loadTransactionsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String type = parts[0];
                        String category = parts[1];
                        double amount = Double.parseDouble(parts[2]);
                        String date = parts[3];
                        transactions.add(new Transaction(type, category, amount, date));
                    }
                }
                showMessage("Transactions loaded successfully from " + file.getName(), true);
            } catch (IOException e) {
                showMessage("Error reading file!", false);
            }
        }
    }

    private void saveTransactionsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Transaction t : transactions) {
                    bw.write(t.toString());
                    bw.newLine();
                }
                showMessage("Transactions saved successfully to " + file.getName(), true);
            } catch (IOException e) {
                showMessage("Error writing to file!", false);
            }
        }
    }

 private void showMonthlySummaryDialog() {
    LocalDate currentDate = LocalDate.now();
    int currentYear = currentDate.getYear();
    int currentMonth = currentDate.getMonthValue();

    JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2000, 2100, 1));
    JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));

    JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
    panel.add(new JLabel("Year:"));
    panel.add(yearSpinner);
    panel.add(new JLabel("Month:"));
    panel.add(monthSpinner);

    int result = JOptionPane.showConfirmDialog(frame, panel, "View Summary", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        int year = (Integer) yearSpinner.getValue();
        int month = (Integer) monthSpinner.getValue();
        String selectedMonth = String.format("%04d-%02d", year, month);

        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.getDate().equals(selectedMonth)) {
                if (t.getType().equalsIgnoreCase("Income")) {
                    totalIncome += t.getAmount();
                } else if (t.getType().equalsIgnoreCase("Expense")) {
                    totalExpense += t.getAmount();
                }
            }
        }

        String summary = "Monthly Summary for " + selectedMonth + ":\n" +
                "Total Income: " + totalIncome + "\n" +
                "Total Expense: " + totalExpense + "\n" +
                "Net Savings: " + (totalIncome - totalExpense) + "\n";

        showMessage(summary, true);
    }
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTracker::new);
    }
}
