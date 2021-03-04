package net.digitallogic.ProjectManager.web.controller;

import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;

public interface AuthController {
    boolean activateAccount(ActivateAccountToken token);
    boolean activateAccountRequest(ActivateAccountRequest request);
    boolean resetPassword(ResetPassword resetPassword);
    boolean resetPasswordRequest(ResetPasswordRequest request);
}
