package net.digitallogic.ProjectManager.web.controller;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.persistence.dto.user.ActivateAccountRequest;
import net.digitallogic.ProjectManager.services.AuthService;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = Routes.AUTH_ROUTE)
@Slf4j
public class AuthControllerImpl {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = Routes.ACTIVATE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean activateAccount(@RequestBody @Valid ActivateAccountRequest activateAccount) {
        log.info("Activate Account Request.");
        return authService.activateAccount(activateAccount);
    }
}
