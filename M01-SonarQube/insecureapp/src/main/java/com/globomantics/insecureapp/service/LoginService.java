package com.globomantics.insecureapp.service;

import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class LoginService {
    public boolean login(String uid, String pwd) throws SQLException{
        String connectionString = "jdbc:mysql:3306/myschema";
        Connection conn = DriverManager.getConnection(connectionString);
        return authenticate(uid, pwd, conn);
    }

    // Database queries should not be vulnerable to injection attacks - SQL Injection
    // https://rules.sonarsource.com/java/type/Vulnerability/RSPEC-3649
    // MITRE ATT&ACK T1190 - Exploit Public-Facing Application
    // https://attack.mitre.org/techniques/T1190
    // OWASP Top 10 2017 Category A1 - Injection
    // MITRE, CWE-89 - Improper Neutralization of Special Elements used in an SQL Command
    // https://cwe.mitre.org/data/definitions/89
    private boolean authenticate(String uid, String pwd, java.sql.Connection connection) throws SQLException {

        String query = "SELECT * FROM users WHERE uid = '" + uid + "' AND pwd = '" + pwd + "'"; // Unsafe

        // A hacker can bypass authentication by passing "foo' OR 1=1 --" for the value of either "uid or "pwd"
        // resulting in this query:
        // SELECT * FROM users WHERE uid = 'foo' OR 1=1 --' AND pwd = '...'
        // '--'  indicates a comment follows until then end of the line, which ignores the password completely:
        // SELECT * FROM users WHERE uid = 'foo' OR 1=1
        // the expression after OR is always true, and therefore
        // SELECT * FROM users WHERE 1=1
        // or more succinctly to:
        // SELECT * FROM users

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query); // Noncompliant
        return resultSet.next();
    }
}
