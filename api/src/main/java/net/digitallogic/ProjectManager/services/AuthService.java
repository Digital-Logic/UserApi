package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.CreateAccountActivateToken;
import net.digitallogic.ProjectManager.persistence.dto.user.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

public interface AuthService {
	boolean activateAccount(String encodedToken);
	boolean resetPassword(String encodedToken, ResetPasswordRequest resetPassword);
	void createAccountActivationToken(CreateAccountActivateToken event);
	String createResetPasswordToken(UserEntity user);
}
