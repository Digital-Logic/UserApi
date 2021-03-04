package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;

public interface AuthService {
	void activateAccount(ActivateAccountToken activateAccountToken);
	void resetPassword(ResetPassword resetPassword);
	void accountActivateRequest(ActivateAccountRequest request);
	void createResetPasswordToken(ResetPasswordRequest resetPasswordRequest);
}
