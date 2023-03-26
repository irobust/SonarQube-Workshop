package com.globomantics.insecureapp.controller;

import com.globomantics.insecureapp.service.LoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;


@RestController
@RequestMapping(path = "api/v1/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    @GetMapping
    public String uidPwdLogin(@RequestParam ("uid") String uid,
                               @RequestParam("pwd") String pwd){
        String view = "";
       try {
           if (loginService.login(uid, pwd)) {
               view = "landing_page";
           } else {
               view = "login_page";
           }
       }
       catch (SQLException se){
           view = "login";
           se.printStackTrace();
       }

        return view;
    }
}
