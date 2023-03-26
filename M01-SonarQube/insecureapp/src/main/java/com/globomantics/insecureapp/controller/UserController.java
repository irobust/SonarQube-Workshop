package com.globomantics.insecureapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;

@RestController
@RequestMapping(path = "api/v1/profile")
public class UserController {

    @GetMapping
    public String getUserProfile(HttpServletRequest req){
        Connection connection = null;
        Statement statement = null;
        String query = "";
        String username = req.getParameter("username");
        String rsStr = "";
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
            query = "'SELECT * FROM users WHERE username = '" + req.getParameter("username") + "'";

            // ... OR ...
            // Insecurely format the query string using user defined data
            // String query2 = String.format("SELECT secret FROM Users WHERE (username = '%s' AND NOT role = 'admin')", username);

            // Execute query and return the results
            ResultSet rs = statement.executeQuery(query);

            statement.close();
            rsStr = rs.toString();
            rs.close();
            return rsStr;
        }
        catch (SQLException | NullPointerException e){
            System.out.println("Problem during SQL execution in: " + this.getClass().getName());
            return rsStr;
        }
    }

    // https://rules.sonarsource.com/java/type/Vulnerability/RSPEC-5131
    // Endpoints should not be vulnerable to reflected cross-site scripting (XSS) attacks
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        PrintWriter out = resp.getWriter();
        out.write("Hello " + name); // Noncompliant
    }
}
