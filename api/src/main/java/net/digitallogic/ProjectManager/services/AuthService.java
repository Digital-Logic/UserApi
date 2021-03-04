package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;

public interface AuthService {
	boolean activateAccount(ActivateAccountToken activateAccountToken);
	boolean resetPassword(ResetPassword resetPassword);
	boolean accountActivateRequest(ActivateAccountRequest request);
	boolean createResetPasswordToken(ResetPasswordRequest resetPasswordRequest);
}
