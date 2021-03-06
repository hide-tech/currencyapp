package com.yazykov.currencyservice.security.controller;

import com.yazykov.currencyservice.security.dto.RegistrationResponse;
import com.yazykov.currencyservice.security.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createNewUser(@RequestBody RegistrationResponse response){
        return registrationService.register(response);
    }

    @GetMapping
    @RequestMapping("/confirm/{username}")
    @PreAuthorize("#username == authentication.name")
    public String confirmEmail(@PathVariable("username") String username){
        return registrationService.confirm(username);
    }
}
