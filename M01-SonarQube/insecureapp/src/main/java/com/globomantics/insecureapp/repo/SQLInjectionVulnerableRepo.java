package com.globomantics.insecureapp.repo;


import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.Properties;

public class SQLInjectionVulnerableRepo {

    public ResultSet getProfileByUid(HttpServletRequest req/*, String uid*/) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        String query = "";
        String username = req.getParameter("uid");
        try {
            Properties connProperties = new Properties();
            connProperties.put("uid", "sonar");
            connProperties.put("pwd", "sonar");
            connection = DriverManager.getConnection("jdbc:mysql:localhost:3306", connProperties);

            // Get username from parameters
//        String username = request.getParameter("username");

            // Create a statement from database connection
            statement = connection.createStatement();

            // Create unsafe query by concatenating user defined data with query string
            query = "SELECT secret FROM Users WHERE (username = '" + username + "' AND NOT role = 'admin')";

            // ... OR ...
            // Insecurely format the query string using user defined data
            String query2 = String.format("SELECT secret FROM Users WHERE (username = '%s' AND NOT role = 'admin')", username);

            // Execute query and return the results
            return statement.executeQuery(query);
        }
        catch (NullPointerException npe){
            System.out.println("Problem during SQL execution in: " + this.getClass().getName());
            return statement.executeQuery(query);
        }
        finally {
            if(statement != null) {
                statement.close();
            }
        }
    }
}
