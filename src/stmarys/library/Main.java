package stmarys.library;

import javax.swing.JOptionPane;
import stmarys.library.dao.BookDAOImpl;
import stmarys.library.dao.BorrowRecordDAOImpl;
import stmarys.library.dao.MemberDAOImpl;
import stmarys.library.service.LibraryService;
import stmarys.library.ui.ConsoleUI;
import stmarys.library.ui.GuiApp;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            BookDAOImpl bookDAO = new BookDAOImpl(databaseManager);
            MemberDAOImpl memberDAO = new MemberDAOImpl(databaseManager);
            BorrowRecordDAOImpl borrowRecordDAO = new BorrowRecordDAOImpl(databaseManager);
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
