package com.yazykov.currencyservice.security.service;

import com.yazykov.currencyservice.security.appuser.AppUser;
import com.yazykov.currencyservice.security.appuser.AppUserRole;
import com.yazykov.currencyservice.security.dto.RegistrationResponse;
import com.yazykov.currencyservice.security.email.EmailSender;
import com.yazykov.currencyservice.security.repository.AppUserRepository;
import com.yazykov.currencyservice.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private AppUserRepository repository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private EmailSender sender;
    @Mock
    private CurrencyService currencyService;

    private RegistrationService service;

    @BeforeEach
    void setUp() {
        service = new RegistrationService(repository, encoder, sender, currencyService);
    }

    @Test
    void register_thenOk() {
        //given
        RegistrationResponse response = new RegistrationResponse("user"
                , "123", "user@user.com");
        when(repository.save(any(AppUser.class))).thenReturn(new AppUser(1L,"user",
                "123", "user@user.com", AppUserRole.ROLE_USER, false,
                true,"USD", BigDecimal.ZERO, null));
        //when
        service.register(response);
        //then
        Mockito.verify(repository, Mockito.times(1)).save(any(AppUser.class));

        Mockito.verify(sender, Mockito.times(1)).send(
                anyString(),
                anyString()
        );
    }

    @Test
    void register_thenFault() {
        //init
        Mockito.doReturn(Optional.of(new AppUser()))
                .when(repository)
                .findByUsername("user");
        //given
        RegistrationResponse response = new RegistrationResponse("user"
                , "123", "user@user.com");
        //then
        assertThrows(IllegalArgumentException.class, () -> service.register(response));
    }

    @Test
    void confirm_thenOk() {
        //init
        AppUser user = new AppUser(1L,"user",
                "123", "user@user.com", AppUserRole.ROLE_USER, false,
                true,"USD", BigDecimal.ZERO, null);
        Mockito.doReturn(Optional.of(user))
                .when(repository)
                .findByUsername("user");
        //when
        service.confirm("user");
        //then
        assertEquals(new BigDecimal("100"), user.getAmount());
    }

    @Test
    void confirm_thenFault() {
        //then
        assertThrows(IllegalStateException.class, () -> service.confirm("user"));
    }
}