# Patient Management System (ABM)

This is a simple command-line application for managing patient records (Alta, Baja, Modificación - Create, Read, Update, Delete).

## Prerequisites

*   Java Development Kit (JDK) version 8 or higher.
*   The H2 database driver JAR.
*   JUnit 5 JARs (for running tests).

## Setup

1.  **Clone the repository (or download the source code).**
2.  **Ensure necessary JARs are in the `lib` directory:**
    *   **H2 Database Driver:** Download the H2 JAR (e.g., `h2-2.x.x.jar`) from [Maven Central](https://search.maven.org/artifact/com.h2database/h2) and place it in the `lib` folder. This is required to run the application.
    *   **JUnit 5 (for testing):** If you plan to run tests, download `junit-jupiter-api-5.x.x.jar` and `junit-platform-console-standalone-1.x.x.jar` (or `junit-jupiter-engine-5.x.x.jar` if running through an IDE) and place them in the `lib` folder. The console standalone JAR is recommended for command-line test execution.

## Compilation

1.  Open a terminal or command prompt in the root directory of the project.
2.  Create an `out` directory if it doesn't exist (for compiled classes):
    ```bash
    mkdir out
    ```
3.  Compile the source code:
    *   On Linux/macOS:
        ```bash
        javac -d out -cp "src:lib/*" $(find src -name "*.java")
        ```
    *   On Windows (Command Prompt - explicit list for reliability):
        ```bash
        javac -d out -cp "src;lib\*" src\com\example\model\Patient.java src\com\example\dao\PatientDAO.java src\com\example\dao\PatientDAOImpl.java src\com\example\service\PatientService.java src\com\example\ui\Main.java src\com\example\exception\DataAccessException.java src\com\example\exception\PatientManagementException.java src\com\example\exception\PatientNotFoundException.java src\com\example\exception\ServiceException.java
        ```

## Running the Application

1.  After successful compilation, run the application using the following command from the project root:
    *   On Linux/macOS:
        ```bash
        java -cp "out:lib/*" com.example.ui.Main
        ```
    *   On Windows:
        ```bash
        java -cp "out;lib\*" com.example.ui.Main
        ```

## Running Tests (using JUnit 5 Console Launcher)

1.  Compile the test files along with the source files (ensure `out` directory exists):
    *   On Linux/macOS:
        ```bash
        javac -d out -cp "src:test:lib/*" $(find src -name "*.java") $(find test -name "*.java")
        ```
    *   On Windows (Command Prompt - explicit list for reliability):
        ```bash
        javac -d out -cp "src;test;lib\*" src\com\example\model\Patient.java src\com\example\dao\PatientDAO.java src\com\example\dao\PatientDAOImpl.java src\com\example\exception\DataAccessException.java src\com\example\exception\PatientManagementException.java src\com\example\exception\PatientNotFoundException.java src\com\example\exception\ServiceException.java test\com\example\dao\PatientDAOImplTest.java
        ```
2.  Run the tests using the JUnit Console Launcher (ensure `junit-platform-console-standalone-1.x.x.jar` is in `lib`):
    *   On Linux/macOS:
        ```bash
        java -jar lib/junit-platform-console-standalone-1.x.x.jar -cp "out:lib/*" --scan-classpath
        ```
    *   On Windows:
        ```bash
        java -jar lib\junit-platform-console-standalone-1.x.x.jar -cp "out;lib\*" --scan-classpath
        ```

This README provides instructions for setup, compilation, running the application, and executing tests from the command line.
