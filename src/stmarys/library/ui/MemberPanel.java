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
import stmarys.library.model.Member;
import stmarys.library.service.LibraryService;

public class MemberPanel extends JPanel {
    private final LibraryService service;
    private final JTextField idField = UiHelper.textField(8);
    private final JTextField nameField = UiHelper.textField(20);
    private final JTextField emailField = UiHelper.textField(20);
    private final JTextField typeField = UiHelper.textField(10);
    private final JTextField searchField = UiHelper.textField(22);
    private final DefaultTableModel model = new DefaultTableModel(new Object[] {"Member ID", "Name", "Email", "Type"}, 0) {
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
        UiHelper.addRow(form, "Member ID", idField, 0);
        UiHelper.addRow(form, "Name", nameField, 1);
        UiHelper.addRow(form, "Email", emailField, 2);
        UiHelper.addRow(form, "Type", typeField, 3);
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
        wrapper.add(UiHelper.panelWithTitle("Member Details", form), BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return UiHelper.padding(wrapper);
    }

    private JPanel makeTableArea() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchButton.addActionListener(event -> loadMembers());
        refreshButton.addActionListener(event -> { searchField.setText(""); loadMembers(); });
        search.add(new JLabel("Name"));
        search.add(searchField);
        search.add(searchButton);
        search.add(refreshButton);
        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return UiHelper.padding(panel);
    }

    private void addMember() {
        try {
            service.addMember(idField.getText(), nameField.getText(), emailField.getText(), typeField.getText());
            UiHelper.showInfo(this, "Member added successfully.");
            clearFields();
            loadMembers();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void updateMember() {
        try {
            service.updateMember(idField.getText(), nameField.getText(), emailField.getText(), typeField.getText());
            UiHelper.showInfo(this, "Member updated successfully.");
            loadMembers();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void deleteMember() {
        if (!UiHelper.confirm(this, "Delete this member?")) {
            return;
        }
        try {
            boolean deleted = service.deleteMember(idField.getText());
            UiHelper.showInfo(this, deleted ? "Member deleted successfully." : "Member was not found.");
            clearFields();
            loadMembers();
        } catch (Exception ex) {
            UiHelper.showError(this, ex);
        }
    }

    private void loadMembers() {
        try {
            List<Member> members;
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                members = service.searchMembers(searchTerm, "All");
            } else {
                members = service.listMembers();
            }
            model.setRowCount(0);
            for (Member member : members) {
                model.addRow(new Object[] {member.getMemberId(), member.getMemberName(), member.getEmail(), member.getMembershipType()});
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
        nameField.setText(String.valueOf(model.getValueAt(row, 1)));
        emailField.setText(String.valueOf(model.getValueAt(row, 2)));
        typeField.setText(String.valueOf(model.getValueAt(row, 3)));
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        typeField.setText("");
        table.clearSelection();
    }
}
