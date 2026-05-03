package stmarys.library.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import stmarys.library.dao.DaoException;
import stmarys.library.model.BorrowRecord;
import stmarys.library.service.LibraryService;
import stmarys.library.util.ValidationException;

@SuppressWarnings("this-escape")
public class BorrowPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final transient LibraryService service;
    private final JTextField recordIdField = new JTextField(8);
    private final JTextField bookIdField = new JTextField(8);
    private final JTextField memberIdField = new JTextField(8);
    private final JTextField borrowDateField = new JTextField(10);
    private final JTextField dueDateField = new JTextField(10);
    private final JTextField statusField = new JTextField(10);
    private final JTextField searchField = new JTextField(22);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Record ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status"}, 0) {
        @Override
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
        javax.swing.SwingUtilities.invokeLater(() -> {
            table.getSelectionModel().addListSelectionListener(event -> fillFromTable());
            loadRecords();
        });
        service.addChangeListener(new stmarys.library.service.LibraryChangeListener() {
            @Override
            public void booksChanged() {
                javax.swing.SwingUtilities.invokeLater(() -> loadRecords());
            }

            @Override
            public void borrowsChanged() {
                javax.swing.SwingUtilities.invokeLater(() -> loadRecords());
            }
        });
    }

    private JPanel makeForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, "Record ID", recordIdField, 0);
        addRow(form, "Book ID", bookIdField, 1);
        addRow(form, "Member ID", memberIdField, 2);
        addRow(form, "Borrow Date", borrowDateField, 3);
        addRow(form, "Due Date", dueDateField, 4);
        addRow(form, "Status", statusField, 5);

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

        wrapper.add(new JLabel("Borrowing Details"), BorderLayout.NORTH);
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> loadRecords());
        search.add(new JLabel("Borrow Records"));
        search.add(refreshButton);

        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void addRow(JPanel panel, String label, JTextField field, int row) {
        java.awt.GridBagConstraints left = new java.awt.GridBagConstraints();
        left.gridx = 0;
        left.gridy = row;
        left.anchor = java.awt.GridBagConstraints.WEST;
        left.insets = new java.awt.Insets(3, 3, 3, 3);
        panel.add(new JLabel(label), left);

        java.awt.GridBagConstraints right = new java.awt.GridBagConstraints();
        right.gridx = 1;
        right.gridy = row;
        right.fill = java.awt.GridBagConstraints.HORIZONTAL;
        right.weightx = 1;
        right.insets = new java.awt.Insets(3, 3, 3, 3);
        panel.add(field, right);
    }

    private void addRecord() {
        try {
            service.addBorrowRecord(bookIdField.getText(), memberIdField.getText(),
                    borrowDateField.getText(), dueDateField.getText());
            JOptionPane.showMessageDialog(this, "Borrowing record added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadRecords();
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        try {
            service.updateBorrowRecord(recordIdField.getText(), statusField.getText());
            JOptionPane.showMessageDialog(this, "Borrowing record updated successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadRecords();
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        try {
            int result = JOptionPane.showConfirmDialog(this, "Delete this borrowing record?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                int recordId = Integer.parseInt(recordIdField.getText());
                service.deleteBorrowRecord(recordId);
                JOptionPane.showMessageDialog(this, "Borrowing record deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadRecords();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Record ID", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecords() {
        try {
            List<BorrowRecord> records = service.getAllBorrowRecords();
            model.setRowCount(0);
            for (BorrowRecord record : records) {
                model.addRow(new Object[]{record.getRecordId(), record.getBookId(), record.getMemberId(),
                    record.getBorrowDate(), record.getDueDate(), record.getReturnStatus()});
            }
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFromTable() {
        if (table.getSelectedRow() < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        recordIdField.setText(String.valueOf(model.getValueAt(row, 0)));
        bookIdField.setText(String.valueOf(model.getValueAt(row, 1)));
        memberIdField.setText(String.valueOf(model.getValueAt(row, 2)));
        borrowDateField.setText(String.valueOf(model.getValueAt(row, 3)));
        dueDateField.setText(String.valueOf(model.getValueAt(row, 4)));
        statusField.setText(String.valueOf(model.getValueAt(row, 5)));
    }

    private void clearFields() {
        recordIdField.setText("");
        bookIdField.setText("");
        memberIdField.setText("");
        borrowDateField.setText("");
        dueDateField.setText("");
        statusField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
}
