package com.example.laptopshop.controller;

import com.example.laptopshop.service.EmailService;
import com.example.laptopshop.service.SubscriberService;
import com.example.laptopshop.util.annotation.ApiMessage;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;
    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    //@Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public String sendSimpleEmail() {
        //this.emailService.sendSimpleEmail();
        //this.emailService.sendEmailSync("nguyenloc02082004@gmail.com","Testing from Spring Boot","<h1><b>Hello World from Spring Boot Email</b></h1>",false,true);
        //this.emailService.sendEmailFromTemplateSync("nguyenloc02082004@gmail.com","Testing from Spring Boot","job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
}

