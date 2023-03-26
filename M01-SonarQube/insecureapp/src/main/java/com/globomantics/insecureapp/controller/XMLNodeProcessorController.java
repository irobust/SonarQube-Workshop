package com.globomantics.insecureapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "api/v1/process-xml-node")
public class XMLNodeProcessorController {

    // https://rules.sonarsource.com/java/type/Vulnerability/RSPEC-6399
    // XML operations should not be vulnerable to injection attacks - can lead to XSS problems
    @GetMapping
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String xml = "<node id=\""+ req.getParameter("id") + "\"></node>";
        FileOutputStream fos = new FileOutputStream("output.xml");
        fos.write(xml.getBytes(StandardCharsets.UTF_8));  // Noncompliant\
        fos.close();
    }
}
