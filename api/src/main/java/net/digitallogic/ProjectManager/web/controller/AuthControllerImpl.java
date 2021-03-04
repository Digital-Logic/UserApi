package net.digitallogic.ProjectManager.web.controller;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;
import net.digitallogic.ProjectManager.services.AuthService;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = Routes.AUTH_ROUTE)
@Slf4j
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping(path = "activate-account")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean activateAccount(@RequestBody @Valid ActivateAccountToken activateAccountToken) {
        log.info("Attempting to activate user account.");
        return authService.activateAccount(activateAccountToken);
    }

    // Send a new Activate Account email
    @Override
    @PostMapping(path = Routes.ACTIVATE_ACCOUNT_REQUEST)
    public boolean activateAccountRequest(@RequestBody @Valid ActivateAccountRequest activateAccountRequest) {
        log.info("Send user account activation email request.");
        return authService.accountActivateRequest(activateAccountRequest);
    }

    @Override
    @PostMapping(path = Routes.RESET_PASSWORD)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean resetPassword(@RequestBody @Valid ResetPassword resetPassword) {
        log.info("Reset user account password");
        return authService.resetPassword(resetPassword);
    }

    @Override
    @PostMapping(path = Routes.RESET_PASSWORD_REQUEST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean resetPasswordRequest(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("User request password password reset.");
        return authService.createResetPasswordToken(resetPasswordRequest);
    }
}
