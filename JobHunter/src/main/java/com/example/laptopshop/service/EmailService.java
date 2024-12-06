package com.example.laptopshop.service;

import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Skill;
import com.example.laptopshop.domain.Subscriber;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.repository.JobRepository;
import com.example.laptopshop.repository.SubscriberRepository;
import com.example.laptopshop.util.SecurityUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final JobRepository jobRepository;
    private final UserService userService;
    private final SubscriberRepository subscriberRepository;
    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, TemplateEngine templateEngine, JobRepository jobRepository, UserService userService , SubscriberRepository subscriberRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobRepository = jobRepository;
        this.userService = userService;
        this.subscriberRepository = subscriberRepository;
    }
    public void sendSimpleEmail(){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("nguyenloc02082004@gmail.com");
        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World from Spring Boot Email");
        this.mailSender.send(msg);
    }
    @Async
    public void sendEmailFromTemplateSync(String to, String subject, String templateName,String username,Object value)  {
        Context context = new Context();
        String name = "Lộc";
        context.setVariable("name", username);
        context.setVariable("jobs", value);
        String content = templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }


    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }



}
