CREATE TABLE IF NOT EXISTS books (
    book_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT NOT NULL,
    availability_status TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS members (
    member_id INTEGER PRIMARY KEY,
    member_name TEXT NOT NULL,
    email TEXT NOT NULL,
    membership_type TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS borrow_records (
    record_id INTEGER PRIMARY KEY,
    book_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_status TEXT NOT NULL
);

INSERT INTO books (book_id, title, author, category, availability_status) VALUES
(1, 'Introduction to Java', 'John Smith', 'Programming', 'Available'),
(2, 'Database Systems', 'Maria Garcia', 'Computer Science', 'Borrowed'),
(3, 'Software Engineering Principles', 'Alan Brown', 'Engineering', 'Available');

INSERT INTO members (member_id, member_name, email, membership_type) VALUES
(1, 'Alice Johnson', 'alice.johnson@stmarys.ac.uk', 'Student'),
(2, 'Michael Lee', 'michael.lee@stmarys.ac.uk', 'Staff'),
(3, 'Sara Ahmed', 'sara.ahmed@stmarys.ac.uk', 'Student');

INSERT INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES
(1, 2, 1, '2025-03-01', '2025-03-15', 'Borrowed'),
(2, 1, 2, '2025-03-02', '2025-03-16', 'Returned'),
(3, 3, 3, '2025-03-05', '2025-03-19', 'Borrowed');

