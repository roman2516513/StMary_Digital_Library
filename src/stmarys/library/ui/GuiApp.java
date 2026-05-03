package stmarys.library.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import stmarys.library.service.LibraryService;

public class GuiApp {
    private final LibraryService service;

    public GuiApp(LibraryService service) {
        this.service = service;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Books", new BookPanel(service));
            tabs.addTab("Members", new MemberPanel(service));
            tabs.addTab("Borrowing", new BorrowPanel(service));
            frame.add(tabs, BorderLayout.CENTER);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
