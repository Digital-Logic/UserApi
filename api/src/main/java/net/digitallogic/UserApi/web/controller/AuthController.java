package net.digitallogic.UserApi.web.controller;

import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.UserApi.persistence.dto.security.ResetPassword;
import net.digitallogic.UserApi.persistence.dto.security.ResetPasswordRequest;

public interface AuthController {
    void activateAccount(ActivateAccountToken token);
    void activateAccountRequest(ActivateAccountRequest request);
    void resetPassword(ResetPassword resetPassword);
    void resetPasswordRequest(ResetPasswordRequest request);
}
