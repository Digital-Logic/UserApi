package net.digitallogic.ProjectManager.web.controller;

import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;

public interface AuthController {
    void activateAccount(ActivateAccountToken token);
    void activateAccountRequest(ActivateAccountRequest request);
    void resetPassword(ResetPassword resetPassword);
    void resetPasswordRequest(ResetPasswordRequest request);
}
