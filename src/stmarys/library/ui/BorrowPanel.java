package stmarys.library.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import stmarys.library.model.BorrowRecord;
import stmarys.library.service.LibraryService;

public class BorrowPanel extends JPanel {
    private final LibraryService service;
    private final JTextField recordIdField = UiHelper.textField(8);
    private final JTextField bookIdField = UiHelper.textField(8);
    private final JTextField memberIdField = UiHelper.textField(8);
    private final JTextField borrowDateField = UiHelper.textField(10);
    private final JTextField dueDateField = UiHelper.textField(10);
    private final JTextField statusField = UiHelper.textField(10);
    private final JTextField searchField = UiHelper.textField(22);
    private final DefaultTableModel model = new DefaultTableModel(new Object[] {"Record ID", "Book ID", "Book Title", "Member ID", "Member Name", "Borrow Date", "Due Date", "Status"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public BorrowPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(6, 6));
        add(makeForm(), BorderLayout.WEST);
        add(makeTableArea(), BorderLayout.CENTER);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(event -> fillFromTable());
        loadRecords();
    }

    private JPanel makeForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        UiHelper.addRow(form, "Record ID", recordIdField, 0);
        UiHelper.addRow(form, "Book ID", bookIdField, 1);
        UiHelper.addRow(form, "Member ID", memberIdField, 2);
        UiHelper.addRow(form, "Borrow Date", borrowDateField, 3);
        UiHelper.addRow(form, "Due Date", dueDateField, 4);
        UiHelper.addRow(form, "Status", statusField, 5);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(event -> addRecord());
        update.addActionListener(event -> updateRecord());
        delete.addActionListener(event -> deleteRecord());
        clear.addActionListener(event -> clearFields());
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);
        wrapper.add(UiHelper.panelWithTitle("Borrowing Details", form), BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return UiHelper.padding(wrapper);
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchButton.addActionListener(event -> loadRecords());
        refreshButton.addActionListener(event -> { searchField.setText(""); loadRecords(); });
        search.add(new JLabel("Record ID"));
        search.add(searchField);
        search.add(searchButton);
        search.add(refreshButton);
        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return UiHelper.padding(panel);
    }

    private void addRecord() {
        try {
            service.addBorrowRecord(recordIdField.getText(), bookIdField.getText(), memberIdField.getText(), borrowDateField.getText(), dueDateField.getText(), statusField.getText());
            UiHelper.showInfo(this, "Borrowing record added successfully.");
            clearFields();
            loadRecords();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void updateRecord() {
        try {
            service.updateBorrowRecord(recordIdField.getText(), bookIdField.getText(), memberIdField.getText(), borrowDateField.getText(), dueDateField.getText(), statusField.getText());
            UiHelper.showInfo(this, "Borrowing record updated successfully.");
            loadRecords();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void deleteRecord() {
        if (!UiHelper.confirm(this, "Delete this borrowing record?")) {
            return;
        }
        try {
            boolean deleted = service.deleteBorrowRecord(recordIdField.getText());
            UiHelper.showInfo(this, deleted ? "Borrowing record deleted successfully." : "Borrowing record was not found.");
            clearFields();
            loadRecords();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void loadRecords() {
        try {
            List<BorrowRecord> records;
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                records = service.searchBorrowRecords(searchTerm, "All");
            } else {
                records = service.listBorrowRecords();
            }
            model.setRowCount(0);
            for (BorrowRecord record : records) {
                model.addRow(new Object[] {record.getRecordId(), record.getBookId(), record.getBookTitle(), record.getMemberId(), record.getMemberName(), record.getBorrowDate(), record.getDueDate(), record.getReturnStatus()});
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void fillFromTable() {
        if (table.getSelectedRow() < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        recordIdField.setText(String.valueOf(model.getValueAt(row, 0)));
        bookIdField.setText(String.valueOf(model.getValueAt(row, 1)));
        memberIdField.setText(String.valueOf(model.getValueAt(row, 3)));
        borrowDateField.setText(String.valueOf(model.getValueAt(row, 5)));
        dueDateField.setText(String.valueOf(model.getValueAt(row, 6)));
        statusField.setText(String.valueOf(model.getValueAt(row, 7)));
    }

    private void clearFields() {
        recordIdField.setText("");
        bookIdField.setText("");
        memberIdField.setText("");
        borrowDateField.setText("");
        dueDateField.setText("");
        statusField.setText("");
        table.clearSelection();
    }
}
