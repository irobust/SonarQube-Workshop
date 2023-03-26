package com.globomantics.insecureapp.controller;

import com.globomantics.insecureapp.service.RegistrationService;
import org.apache.catalina.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "api/v1/register")
public class RegisterController {

    private static final String COOKIENAME = "login_cookie";

    private final RegistrationService registrationService;

    public RegisterController(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    @GetMapping
    public String uidPwdRegister(@RequestParam ("uid") String uid,
                                 @RequestParam("pwd") String pwd,
                                 HttpServletRequest req){

        // Sensitive info inserted into cookie
        // MITRE ATT&CK technique T1189 - Drive-by Compromise
        // https://attack.mitre.org/techniques/T1189
        // OWASP Top 10 2017 Category A7 - Cross-Site Scripting (XSS)
        // https://www.owasp.org/index.php/Top_10-2017_A7-Cross-Site_Scripting_(XSS)
        Cookie c = new Cookie(COOKIENAME, req.getSession().getId());
        c.setHttpOnly(false);  // Sensitive: this sensitive cookie is created with the httponly flag set to false
                                // and so it can be stolen easily in case of XSS vulnerability


        String view;
        if(registrationService.registerUser(uid, pwd)){
            view = "confirmed_registration_page";
        }
        else{
            view = "registration_failed_page";
        }

        return view;
    }
}
