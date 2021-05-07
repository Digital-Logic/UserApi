package net.digitallogic.UserApi.web.controller;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.UserApi.persistence.dto.security.ResetPassword;
import net.digitallogic.UserApi.persistence.dto.security.ResetPasswordRequest;
import net.digitallogic.UserApi.services.AuthService;
import net.digitallogic.UserApi.web.Routes;
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
    public void activateAccount(@RequestBody @Valid ActivateAccountToken activateAccountToken) {
        log.info("Attempting to activate user account.");
        authService.activateAccount(activateAccountToken);
    }

    // Send a new Activate Account email
    @Override
    @PostMapping(path = Routes.ACTIVATE_ACCOUNT_REQUEST)
    public void activateAccountRequest(@RequestBody @Valid ActivateAccountRequest activateAccountRequest) {
        log.info("Send user account activation email request.");
        authService.accountActivateRequest(activateAccountRequest);
    }

    @Override
    @PostMapping(path = Routes.RESET_PASSWORD)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resetPassword(@RequestBody @Valid ResetPassword resetPassword) {
        log.info("Reset user account password");
        authService.resetPassword(resetPassword);
    }

    @Override
    @PostMapping(path = Routes.RESET_PASSWORD_REQUEST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resetPasswordRequest(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("User request password password reset.");
        authService.createResetPasswordToken(resetPasswordRequest);
    }
}
