package com.yazykov.currencyservice.security.controller;

import com.yazykov.currencyservice.security.dto.*;
import com.yazykov.currencyservice.security.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<AppUserAdminResponse> getAllUsers(){
        return appUserService.loadAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public AppUserResponse getUserById(@PathVariable("id") Long id){
        return appUserService.loadUserById(id);
    }

    @PostMapping("/{id}/change")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String changeBase(@RequestBody ChangeBaseRequest change){
        return appUserService.changeBaseCurrency(change);
    }

    @PostMapping("/ban")
    @Secured("ROLE_ADMIN")
    public String banUserById(@RequestBody BanRequest request){
        return appUserService.banUser(request.getId());
    }

    @PostMapping("/unban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unbanUserById(@RequestBody BanRequest request){
        return appUserService.unbanUser(request.getId());
    }

    @PostMapping("/amount")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changeAmount(@RequestBody AddValueRequest request){
        appUserService.changeAmountCurrency(request);
    }
}
