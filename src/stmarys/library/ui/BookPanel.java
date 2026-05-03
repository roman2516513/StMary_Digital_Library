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
import stmarys.library.model.Book;
import stmarys.library.service.LibraryService;

public class BookPanel extends JPanel {
    private final LibraryService service;
    private final JTextField idField = UiHelper.textField(8);
    private final JTextField titleField = UiHelper.textField(20);
    private final JTextField authorField = UiHelper.textField(20);
    private final JTextField categoryField = UiHelper.textField(15);
    private final JTextField statusField = UiHelper.textField(10);
    private final JTextField searchField = UiHelper.textField(22);
    private final DefaultTableModel model = new DefaultTableModel(new Object[] {"Book ID", "Title", "Author", "Category", "Status"}, 0) {
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
        table.getSelectionModel().addListSelectionListener(event -> fillFromTable());
        loadBooks();
    }

    private JPanel makeForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        UiHelper.addRow(form, "Book ID", idField, 0);
        UiHelper.addRow(form, "Title", titleField, 1);
        UiHelper.addRow(form, "Author", authorField, 2);
        UiHelper.addRow(form, "Category", categoryField, 3);
        UiHelper.addRow(form, "Status", statusField, 4);
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
        wrapper.add(UiHelper.panelWithTitle("Book Details", form), BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return UiHelper.padding(wrapper);
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchButton.addActionListener(event -> loadBooks());
        refreshButton.addActionListener(event -> { searchField.setText(""); loadBooks(); });
        search.add(new JLabel("Title"));
        search.add(searchField);
        search.add(searchButton);
        search.add(refreshButton);
        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return UiHelper.padding(panel);
    }

    private void addBook() {
        try {
            service.addBook(idField.getText(), titleField.getText(), authorField.getText(), categoryField.getText(), statusField.getText());
            UiHelper.showInfo(this, "Book added successfully.");
            clearFields();
            loadBooks();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void updateBook() {
        try {
            service.updateBook(idField.getText(), titleField.getText(), authorField.getText(), categoryField.getText(), statusField.getText());
            UiHelper.showInfo(this, "Book updated successfully.");
            loadBooks();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void deleteBook() {
        if (!UiHelper.confirm(this, "Delete this book?")) {
            return;
        }
        try {
            boolean deleted = service.deleteBook(idField.getText());
            UiHelper.showInfo(this, deleted ? "Book deleted successfully." : "Book was not found.");
            clearFields();
            loadBooks();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void loadBooks() {
        try {
            List<Book> books;
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                books = service.searchBooks(searchTerm, "All");
            } else {
                books = service.listBooks();
            }
            model.setRowCount(0);
            for (Book book : books) {
                model.addRow(new Object[] {book.getBookId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getAvailabilityStatus()});
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
        table.clearSelection();
    }
}
