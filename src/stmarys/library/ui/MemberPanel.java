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
import stmarys.library.model.Member;
import stmarys.library.service.LibraryService;
import stmarys.library.util.ValidationException;

public class MemberPanel extends JPanel {
    private final LibraryService service;
    private final JTextField idField = new JTextField(8);
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField typeField = new JTextField(10);
    private final JTextField searchField = new JTextField(22);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Member ID", "Name", "Email", "Type"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public MemberPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(6, 6));
        add(makeForm(), BorderLayout.WEST);
        add(makeTableArea(), BorderLayout.CENTER);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(event -> fillFromTable());
        loadMembers();
    }

    private JPanel makeForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, "Member ID", idField, 0);
        addRow(form, "Name", nameField, 1);
        addRow(form, "Email", emailField, 2);
        addRow(form, "Type", typeField, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(event -> addMember());
        update.addActionListener(event -> updateMember());
        delete.addActionListener(event -> deleteMember());
        clear.addActionListener(event -> clearFields());
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        wrapper.add(new JLabel("Member Details"), BorderLayout.NORTH);
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchButton.addActionListener(event -> loadMembers());
        refreshButton.addActionListener(event -> {
            searchField.setText("");
            loadMembers();
        });
        search.add(new JLabel("Search Name:"));
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

    private void addMember() {
        try {
            service.addMember(idField.getText(), nameField.getText(), emailField.getText(), typeField.getText());
            JOptionPane.showMessageDialog(this, "Member added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadMembers();
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() {
        try {
            service.updateMember(idField.getText(), nameField.getText(), emailField.getText(), typeField.getText());
            JOptionPane.showMessageDialog(this, "Member updated successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadMembers();
        } catch (ValidationException | DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        try {
            int result = JOptionPane.showConfirmDialog(this, "Delete this member?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(idField.getText());
                service.deleteMember(id);
                JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadMembers();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Member ID", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMembers() {
        try {
            List<Member> members;
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                members = service.searchMembers(searchTerm);
            } else {
                members = service.getAllMembers();
            }
            model.setRowCount(0);
            for (Member member : members) {
                model.addRow(new Object[]{member.getMemberId(), member.getMemberName(), member.getEmail(),
                    member.getMembershipType()});
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
        nameField.setText(String.valueOf(model.getValueAt(row, 1)));
        emailField.setText(String.valueOf(model.getValueAt(row, 2)));
        typeField.setText(String.valueOf(model.getValueAt(row, 3)));
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        typeField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
}
