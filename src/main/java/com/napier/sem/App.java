package com.napier.sem;

import java.sql.*;

public class App
{


    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();
        // Get Employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }
        /**
         * Connection to MySQL database.
         */
        private Connection con = null;

        /**
         * Connect to the MySQL database.
         */
        public void connect ()
        {
            try {
                // Load Database driver
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load SQL driver");
                System.exit(-1);
            }

            int retries = 10;
            for (int i = 0; i < retries; ++i) {
                System.out.println("Connecting to database...");
                try {
                    // Wait a bit for db to start
                    Thread.sleep(30000);
                    // Connect to database
                    con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                    System.out.println("Successfully connected");
                    break;
                } catch (SQLException sqle) {
                    System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                    System.out.println(sqle.getMessage());
                } catch (InterruptedException ie) {
                    System.out.println("Thread interrupted? Should not happen.");
                }
            }
        }

        /**
         * Disconnect from the MySQL database.
         */
        public void disconnect ()
        {
            if (con != null) {
                try {
                    // Close connection
                    con.close();
                } catch (Exception e) {
                    System.out.println("Error closing connection to database");
                }
            }
        }

    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create a SQL query string to retrieve the employee's details along with their current title,
            // salary, department name, and manager's name, based on the provided employee ID
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, "
                            + "d.dept_name, CONCAT(m.first_name, ' ', m.last_name) AS manager "
                            + "FROM employees e "
                            + "JOIN titles t ON e.emp_no = t.emp_no AND t.to_date = '9999-01-01' "  // Join titles table, filtering for current title
                            + "JOIN salaries s ON e.emp_no = s.emp_no AND s.to_date = '9999-01-01' " // Join salaries table, filtering for current salary
                            + "JOIN dept_emp de ON e.emp_no = de.emp_no AND de.to_date = '9999-01-01' " // Join dept_emp table, filtering for current department
                            + "JOIN departments d ON de.dept_no = d.dept_no " // Join departments table to get department name
                            + "LEFT JOIN dept_manager dm ON d.dept_no = dm.dept_no AND dm.to_date = '9999-01-01' " // Left join dept_manager table for current manager, if any
                            + "LEFT JOIN employees m ON dm.emp_no = m.emp_no " // Left join employees table to get manager's name
                            + "WHERE e.emp_no = " + ID; // Filter by employee ID

            // Execute the SQL query and get the result set
            ResultSet rset = stmt.executeQuery(strSelect);

            // Check if any data was returned (i.e., employee exists)
            if (rset.next())
            {
                // Create a new Employee object to store the retrieved data
                Employee emp = new Employee();

                // Set the employee number
                emp.emp_no = rset.getInt("emp_no");

                // Set the employee's first name
                emp.first_name = rset.getString("first_name");

                // Set the employee's last name
                emp.last_name = rset.getString("last_name");

                // Set the employee's current job title
                emp.title = rset.getString("title");

                // Set the employee's current salary
                emp.salary = rset.getInt("salary");

                // Set the name of the department the employee currently belongs to
                emp.dept_name = rset.getString("dept_name");

                // Set the name of the employee's current manager (if available)
                emp.manager = rset.getString("manager");

                // Return the populated Employee object
                return emp;
            }
            else
                // Return null if no employee was found with the given ID
                return null;
        }
        catch (Exception e)
        {
            // Print any error message if an exception occurs
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");

            // Return null to indicate that an error occurred
            return null;
        }
    }



    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }


}