package stmarys.library;

import javax.swing.JOptionPane;
import stmarys.library.dao.BookDAO;
import stmarys.library.dao.BorrowRecordDAO;
import stmarys.library.dao.MemberDAO;
import stmarys.library.service.LibraryService;
import stmarys.library.ui.ConsoleUI;
import stmarys.library.ui.GuiApp;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            BookDAO bookDAO = new BookDAO(databaseManager);
            MemberDAO memberDAO = new MemberDAO(databaseManager);
            BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO(databaseManager);
            LibraryService service = new LibraryService(bookDAO, memberDAO, borrowRecordDAO);
            if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
                new ConsoleUI(service).start();
            } else {
                new GuiApp(service).show();
            }
        } catch (Exception ex) {
            System.out.println("Could not start the application: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
