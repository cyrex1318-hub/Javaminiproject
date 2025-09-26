<<<<<<< HEAD
# Javaminiproject
=======
Online Shopping Mini Project (Java AWT + JDBC + MySQL)

Requirements
- JDK 8+ (AWT)
- MySQL Server 8+
- MySQL Connector/J (mysql-connector-j-8.x.x.jar)
- NetBeans or Eclipse (or any IDE)

Database Setup
1. Start MySQL.
2. Run the SQL script at `resources/sql/schema.sql` to create DB and sample data.

Project Setup (NetBeans)
1. File -> New Project -> Java with Ant -> Java Application.
2. Set project name (e.g., `Javaminproject`) and finish.
3. Create package directories `src/com/minishop/db` and `src/com/minishop/ui`.
4. Copy source files from this repo into `src`.
5. Add MySQL Connector/J to project libraries:
   - Right-click project -> Properties -> Libraries -> Add JAR/Folder -> select `mysql-connector-j-8.x.x.jar`.
6. Update DB credentials in `src/com/minishop/db/DBConnection.java` (USER/PASSWORD).
7. Run `com.minishop.Main`.

Project Setup (Eclipse)
1. File -> New -> Java Project.
2. Copy `src` folder into the project `src`.
3. Right-click project -> Build Path -> Configure Build Path -> Libraries -> Add External JARs -> select MySQL Connector/J.
4. Update DB credentials in `DBConnection.java`.
5. Run `com.minishop.Main` as Java Application.

Usage
- Register users via the Register window (or use sample users in DB).
- Login as User to place orders and view order history.
- Login as Admin (email: `admin@example.com`, password: `admin123`) to manage products and view orders.

Notes
- Passwords are stored in plain text for simplicity. For production, hash passwords (e.g., BCrypt).
- The UI uses AWT components and basic layouts to keep the example simple.


>>>>>>> 37f6563 (Javaminiproject)
