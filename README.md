# St Marys Digital Library

This repository contains the St Marys Digital Library project skeleton.

Directories:
- .vscode: editor settings
- data: runtime data
- database: DB files
- lib: libraries
- out: build output
- src: source code

Scripts:
- `build.bat` / `build.sh`: build helper
- `run-console.*` / `run-gui.*`: run helpers

Technology Used:
- Java 11+ (tested with OpenJDK/JDK 11 and later)
- Swing for the graphical user interface (GUI)
- SQLite (via `sqlite-jdbc` in the `lib/` folder) for the embedded database
- JDBC for database access

How to run the app
- Using the provided batch files (Windows):
	- Build: `build.bat` or `.\build.bat`
	- Run GUI: `run-gui.bat` or `.\run-gui.bat`
	- Run console: `run-console.bat`
- Running directly without the batch files (example commands):
	- Build from the project root:
		- `javac -d out -cp "lib/sqlite-jdbc.jar" src/stmarys/library/**/*.java`
	- Run GUI:
		- `java -cp out;lib/sqlite-jdbc.jar stmarys.library.Main`
	- Run console mode:
		- `java -cp out;lib/sqlite-jdbc.jar stmarys.library.Main console`

Notes for non-Windows shells (PowerShell or Unix-like):
- On PowerShell use `;` classpath separator (Windows) as above or adjust to `:` on Unix/macOS.
- Example (Unix/macOS): `java -cp out:lib/sqlite-jdbc.jar stmarys.library.Main`

About the app
 - St Mary's Digital Library is a small Java application providing basic library management features:
	 - CRUD for Books and Members
	 - Manage Borrowing Records (borrow, return, list, search)
	 - Simple Swing-based GUI and a console UI for lightweight use or testing
 - The app stores data in an embedded SQLite database located under `data/library.db` by default.
 


