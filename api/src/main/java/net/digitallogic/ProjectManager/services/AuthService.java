package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.CreateAccountActivationToken;
import net.digitallogic.ProjectManager.persistence.dto.user.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

public interface AuthService {
	boolean activateAccount(ActivateAccountRequest activateAccountRequest);
	boolean resetPassword(String encodedToken, ResetPasswordRequest resetPassword);
	void createAccountActivationToken(CreateAccountActivationToken event);
	String createResetPasswordToken(UserEntity user);
}
