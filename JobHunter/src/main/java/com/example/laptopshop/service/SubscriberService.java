package com.example.laptopshop.service;

import com.example.laptopshop.domain.Job;
import com.example.laptopshop.domain.Skill;
import com.example.laptopshop.domain.Subscriber;
import com.example.laptopshop.domain.email.ResEmailJob;
import com.example.laptopshop.repository.JobRepository;
import com.example.laptopshop.repository.SkillRepository;
import com.example.laptopshop.repository.SubscriberRepository;
import com.example.laptopshop.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, UserRepository userRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean checkEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
    public boolean checkExistById(Long id) {
        return this.subscriberRepository.existsById(id);
    }
    public Subscriber createSubscriber(Subscriber subscriber) {
        if(subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findAllById(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber updateSubscriber(Subscriber subscriber) {
        Optional<Subscriber> dbSubscriber = this.subscriberRepository.findById(subscriber.getId());
        if (dbSubscriber.isPresent()) {
            Subscriber updatedSubscriber = dbSubscriber.get();
            if(subscriber.getSkills() != null){
                List<Long> reqSkills = subscriber.getSkills().stream()
                        .map(x -> x.getId()).collect(Collectors.toList());
                List<Skill> dbSkills = this.skillRepository.findAllById(reqSkills);
                updatedSubscriber.setSkills(dbSkills);
            }
            return this.subscriberRepository.save(updatedSubscriber);
        }
       return null;
    }

    public Subscriber getSubscriberById(Long id) {
        Optional<Subscriber> dbSubscriber = this.subscriberRepository.findById(id);
        if (dbSubscriber.isPresent()) {
            return dbSubscriber.get();
        }
        return null;
    }
    public void deleteSubscriber(Long id) {
        this.subscriberRepository.deleteById(id);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        String normalized = Normalizer.normalize(job.getName(), Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Chuyển thành chữ thường
        String lowerCase = noDiacritics.toLowerCase();

        // Thay khoảng trắng bằng dấu '-'
        res.setSlug(lowerCase.replaceAll("\\s+", "-"));
        res.setId(job.getId());
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream()
                .map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                         List<ResEmailJob> arr = listJobs.stream().map(
                         job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm đang chờ đón bạn, hãy khám phá ngay",
                                "job",
                                sub.getName(),
                                arr
                        );
                    }
                }
            }
        }
    }
    public Subscriber findByEmail(String email){
        return this.subscriberRepository.findByEmail(email);
    }

//    @Scheduled(cron = "*/10 * * * * *")
//    public void testCron(){
//        System.out.println("test" + Instant.now());
//    }
}
