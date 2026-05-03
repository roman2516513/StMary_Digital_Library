package stmarys.library.ui;

import java.util.List;
import java.util.Scanner;
import stmarys.library.model.Book;
import stmarys.library.model.BorrowRecord;
import stmarys.library.model.Member;
import stmarys.library.service.LibraryService;

public class ConsoleUI {
    private final LibraryService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(LibraryService service) {
        this.service = service;
    }

    public void start() {
        boolean running = true;
        try {
            while (running) {
                System.out.println();
                System.out.println("St Mary's Digital Library System");
                System.out.println("1. Manage Books");
                System.out.println("2. Manage Members");
                System.out.println("3. Manage Borrowing Records");
                System.out.println("4. Search Records");
                System.out.println("0. Exit");
                String choice = ask("Choose option: ");
                switch (choice) {
                    case "1" -> bookMenu();
                    case "2" -> memberMenu();
                    case "3" -> borrowMenu();
                    case "4" -> searchMenu();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void bookMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Book Menu");
            System.out.println("1. List Books");
            System.out.println("2. Add Book");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("5. Search Books");
            System.out.println("0. Back");
            String choice = ask("Choose option: ");
            switch (choice) {
                case "1" -> listBooks();
                case "2" -> addBook();
                case "3" -> updateBook();
                case "4" -> deleteBook();
                case "5" -> searchBooks();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void memberMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Member Menu");
            System.out.println("1. List Members");
            System.out.println("2. Add Member");
            System.out.println("3. Update Member");
            System.out.println("4. Delete Member");
            System.out.println("5. Search Members");
            System.out.println("0. Back");
            String choice = ask("Choose option: ");
            switch (choice) {
                case "1" -> listMembers();
                case "2" -> addMember();
                case "3" -> updateMember();
                case "4" -> deleteMember();
                case "5" -> searchMembers();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void borrowMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Borrowing Menu");
            System.out.println("1. List Borrowing Records");
            System.out.println("2. Add Borrowing Record");
            System.out.println("3. Update Borrowing Record");
            System.out.println("4. Delete Borrowing Record");
            System.out.println("5. Search Borrowing Records");
            System.out.println("0. Back");
            String choice = ask("Choose option: ");
            switch (choice) {
                case "1" -> listBorrowRecords();
                case "2" -> addBorrowRecord();
                case "3" -> updateBorrowRecord();
                case "4" -> deleteBorrowRecord();
                case "5" -> searchBorrowRecords();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void searchMenu() {
        System.out.println();
        System.out.println("1. Search Books");
        System.out.println("2. Search Members");
        System.out.println("3. Search Borrowing Records");
        String choice = ask("Choose option: ");
        switch (choice) {
            case "1" -> searchBooks();
            case "2" -> searchMembers();
            case "3" -> searchBorrowRecords();
            default -> System.out.println("Invalid option.");
        }
    }

    private void listBooks() {
        try {
            printBooks(service.getAllBooks());
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void searchBooks() {
        try {
            String text = ask("Search title: ");
            printBooks(service.searchBooks(text));
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void addBook() {
        try {
            service.addBook(ask("Book ID: "), ask("Title: "), ask("Author: "), ask("Category: "), askStatus("Availability", "Available", "Borrowed"));
            System.out.println("Book added successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void updateBook() {
        try {
            service.updateBook(ask("Book ID: "), ask("Title: "), ask("Author: "), ask("Category: "), askStatus("Availability", "Available", "Borrowed"));
            System.out.println("Book updated successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void deleteBook() {
        try {
            if (confirm()) {
                int id = Integer.parseInt(ask("Book ID: "));
                service.deleteBook(id);
                System.out.println("Book deleted successfully.");
            }
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void listMembers() {
        try {
            printMembers(service.getAllMembers());
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void searchMembers() {
        try {
            String text = ask("Search name: ");
            printMembers(service.searchMembers(text));
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void addMember() {
        try {
            service.addMember(ask("Member ID: "), ask("Name: "), ask("Email: "), askStatus("Type", "Student", "Staff"));
            System.out.println("Member added successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void updateMember() {
        try {
            service.updateMember(ask("Member ID: "), ask("Name: "), ask("Email: "), askStatus("Type", "Student", "Staff"));
            System.out.println("Member updated successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void deleteMember() {
        try {
            if (confirm()) {
                int id = Integer.parseInt(ask("Member ID: "));
                service.deleteMember(id);
                System.out.println("Member deleted successfully.");
            }
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void listBorrowRecords() {
        try {
            printBorrowRecords(service.getAllBorrowRecords());
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void searchBorrowRecords() {
        try {
            System.out.println("Showing all borrowing records...");
            printBorrowRecords(service.getAllBorrowRecords());
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void addBorrowRecord() {
        try {
            service.addBorrowRecord(ask("Book ID: "), ask("Member ID: "), ask("Borrow date yyyy-mm-dd: "), ask("Due date yyyy-mm-dd: "));
            System.out.println("Borrowing record added successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void updateBorrowRecord() {
        try {
            service.updateBorrowRecord(ask("Record ID: "), askStatus("Status", "Borrowed", "Returned", "Overdue"));
            System.out.println("Borrowing record updated successfully.");
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void deleteBorrowRecord() {
        try {
            if (confirm()) {
                int id = Integer.parseInt(ask("Record ID: "));
                service.deleteBorrowRecord(id);
                System.out.println("Borrowing record deleted successfully.");
            }
        } catch (Exception ex) {
            printError(ex);
        }
    }

    private void printBooks(List<Book> books) {
        System.out.printf("%-8s %-35s %-25s %-20s %-12s%n", "ID", "Title", "Author", "Category", "Status");
        for (Book book : books) {
            System.out.printf("%-8d %-35s %-25s %-20s %-12s%n", book.getBookId(), shortText(book.getTitle(), 34), shortText(book.getAuthor(), 24), shortText(book.getCategory(), 19), book.getAvailabilityStatus());
        }
    }

    private void printMembers(List<Member> members) {
        System.out.printf("%-8s %-25s %-35s %-12s%n", "ID", "Name", "Email", "Type");
        for (Member member : members) {
            System.out.printf("%-8d %-25s %-35s %-12s%n", member.getMemberId(), shortText(member.getMemberName(), 24), shortText(member.getEmail(), 34), member.getMembershipType());
        }
    }

    private void printBorrowRecords(List<BorrowRecord> records) {
        System.out.printf("%-8s %-8s %-25s %-9s %-20s %-12s %-12s %-10s%n", "ID", "Book", "Title", "Member", "Name", "Borrow", "Due", "Status");
        for (BorrowRecord record : records) {
            System.out.printf("%-8d %-8d %-25s %-9d %-20s %-12s %-12s %-10s%n", record.getRecordId(), record.getBookId(), shortText(record.getBookTitle(), 24), record.getMemberId(), shortText(record.getMemberName(), 19), record.getBorrowDate(), record.getDueDate(), record.getReturnStatus());
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String askWithAll(String label, String first, String second) {
        String value = ask(label + " All/" + first + "/" + second + ": ");
        return value.isBlank() ? "All" : value;
    }

    private String askWithAll(String label, String first, String second, String third) {
        String value = ask(label + " All/" + first + "/" + second + "/" + third + ": ");
        return value.isBlank() ? "All" : value;
    }

    private String askStatus(String label, String first, String second) {
        return ask(label + " " + first + "/" + second + ": ");
    }

    private String askStatus(String label, String first, String second, String third) {
        return ask(label + " " + first + "/" + second + "/" + third + ": ");
    }

    private boolean confirm() {
        return ask("Are you sure? y/n: ").equalsIgnoreCase("y");
    }

    private String shortText(String text, int size) {
        if (text == null) {
            return "";
        }
        if (text.length() <= size) {
            return text;
        }
        return text.substring(0, size - 3) + "...";
    }

    private void printError(Exception ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        System.out.println("Error: " + cause.getMessage());
    }
}
