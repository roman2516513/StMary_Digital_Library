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
import stmarys.library.model.Book;
import stmarys.library.service.LibraryService;
import stmarys.library.util.ValidationException;

@SuppressWarnings("this-escape")
public class BookPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final transient LibraryService service;
    private final JTextField idField = new JTextField(8);
    private final JTextField titleField = new JTextField(20);
    private final JTextField authorField = new JTextField(20);
    private final JTextField categoryField = new JTextField(15);
    private final JTextField statusField = new JTextField(10);
    private final JTextField searchField = new JTextField(22);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Book ID", "Title", "Author", "Category", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public BookPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(6, 6));
        add(makeForm(), BorderLayout.WEST);
        add(makeTableArea(), BorderLayout.CENTER);
        table.setAutoCreateRowSorter(true);
        javax.swing.SwingUtilities.invokeLater(() -> {
            table.getSelectionModel().addListSelectionListener(event -> fillFromTable());
            loadBooks();
        });
        // Refresh when library changes elsewhere
        service.addChangeListener(new stmarys.library.service.LibraryChangeListener() {
            @Override
            public void booksChanged() {
                javax.swing.SwingUtilities.invokeLater(() -> loadBooks());
            }

            @Override
            public void borrowsChanged() {
                javax.swing.SwingUtilities.invokeLater(() -> loadBooks());
            }
        });
    }

    private JPanel makeForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, "Book ID", idField, 0);
        addRow(form, "Title", titleField, 1);
        addRow(form, "Author", authorField, 2);
        addRow(form, "Category", categoryField, 3);
        addRow(form, "Status", statusField, 4);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(event -> addBook());
        update.addActionListener(event -> updateBook());
        delete.addActionListener(event -> deleteBook());
        clear.addActionListener(event -> clearFields());
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        wrapper.add(new JLabel("Book Details"), BorderLayout.NORTH);
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchButton.addActionListener(event -> loadBooks());
        refreshButton.addActionListener(event -> {
            searchField.setText("");
            loadBooks();
        });
        search.add(new JLabel("Search Title:"));
        search.add(searchField);
        search.add(searchButton);
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

    private void addBook() {
        try {
            service.addBook(idField.getText(), titleField.getText(), authorField.getText(),
                    categoryField.getText(), statusField.getText());
            JOptionPane.showMessageDialog(this, "Book added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadBooks();
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        try {
            service.updateBook(idField.getText(), titleField.getText(), authorField.getText(),
                    categoryField.getText(), statusField.getText());
            JOptionPane.showMessageDialog(this, "Book updated successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadBooks();

            // If the status was set to Borrowed from the book panel, offer to create a borrow record
            if ("Borrowed".equalsIgnoreCase(statusField.getText().trim())) {
                int opt = JOptionPane.showConfirmDialog(this, "Create a borrowing record for this book now?", "Create Borrow Record", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    try {
                        String memberId = JOptionPane.showInputDialog(this, "Member ID:");
                        if (memberId == null) return;
                        String borrowDate = JOptionPane.showInputDialog(this, "Borrow date (yyyy-mm-dd):", java.time.LocalDate.now().toString());
                        if (borrowDate == null) return;
                        String dueDate = JOptionPane.showInputDialog(this, "Due date (yyyy-mm-dd):", java.time.LocalDate.now().plusWeeks(2).toString());
                        if (dueDate == null) return;
                        service.addBorrowRecord(idField.getText(), memberId, borrowDate, dueDate);
                        JOptionPane.showMessageDialog(this, "Borrow record created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadBooks();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        try {
            int result = JOptionPane.showConfirmDialog(this, "Delete this book?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(idField.getText());
                service.deleteBook(id);
                JOptionPane.showMessageDialog(this, "Book deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadBooks();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Book ID", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBooks() {
        try {
            List<Book> books;
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                books = service.searchBooks(searchTerm);
            } else {
                books = service.getAllBooks();
            }
            model.setRowCount(0);
            for (Book book : books) {
                model.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(),
                    book.getCategory(), book.getAvailabilityStatus()});
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
        idField.setText(String.valueOf(model.getValueAt(row, 0)));
        titleField.setText(String.valueOf(model.getValueAt(row, 1)));
        authorField.setText(String.valueOf(model.getValueAt(row, 2)));
        categoryField.setText(String.valueOf(model.getValueAt(row, 3)));
        statusField.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        categoryField.setText("");
        statusField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
}

