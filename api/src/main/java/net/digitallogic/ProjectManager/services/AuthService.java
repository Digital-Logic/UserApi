package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.CreateAccountActivationToken;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

public interface AuthService {
	boolean activateAccount(ActivateAccountRequest activateAccountRequest);
	boolean resetPassword(String encodedToken, ResetPasswordRequest resetPassword);
	void createAccountActivationToken(CreateAccountActivationToken event);
	String createResetPasswordToken(UserEntity user);
}
