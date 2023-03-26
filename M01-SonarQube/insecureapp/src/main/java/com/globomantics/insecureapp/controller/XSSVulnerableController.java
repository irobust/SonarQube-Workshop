package com.globomantics.insecureapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping(path = "api/v1")
public class XSSVulnerableController {

    @GetMapping("/xss1")
    ResponseEntity<String> hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new ResponseEntity<>("Hello World!" + name, HttpStatus.OK);
    }

    // https://rules.sonarsource.com/java/type/Vulnerability?search=XSS
    // Endpoints should not be vulnerable to reflected cross-site scripting (XSS) attacks
    @GetMapping("/xss2")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        PrintWriter out = resp.getWriter();
        out.write("Hello " + name); // Noncompliant
    }
}
