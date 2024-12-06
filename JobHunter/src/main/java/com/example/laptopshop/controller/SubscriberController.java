package com.example.laptopshop.controller;


import com.example.laptopshop.domain.Subscriber;
import com.example.laptopshop.service.SubscriberService;
import com.example.laptopshop.util.SecurityUtil;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subcriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws InvalidIdException {
        if(!this.subscriberService.checkEmail(subscriber.getEmail())) {
            throw new InvalidIdException("Email does not exist!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriberService.createSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subcriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws InvalidIdException {
        if(!this.subscriberService.checkExistById(subscriber.getId())) {
            throw new InvalidIdException("Subscriber does not exist!");
        }
        Subscriber updatedSubscriber = this.subscriberService.updateSubscriber(subscriber);
        if(updatedSubscriber == null) {
            throw new InvalidIdException("Subscriber does not exist to update!");
        }
        return ResponseEntity.ok().body(updatedSubscriber);
    }

    @DeleteMapping("/subscribers/{id}")
    @ApiMessage("Delete a subscriber")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable("id") Long id)throws InvalidIdException {
        if(!this.subscriberService.checkExistById(id)) {
            throw new InvalidIdException("Subscriber does not exist!");
        }
        this.subscriberService.deleteSubscriber(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws InvalidIdException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
