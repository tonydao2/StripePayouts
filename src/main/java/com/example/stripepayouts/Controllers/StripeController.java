package com.example.stripepayouts.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
class StripeController {

    @RequestMapping
    public String index() {
        return "Greetings from StripeController!";
    }
}
