package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.user.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;

public interface AuthService {
	boolean activateAccount(String encodedToken);
	boolean resetPassword(String encodedToken, ResetPasswordRequest resetPassword);
	String createAccountActivationToken(UserEntity user);
	String createResetPasswordToken(UserEntity user);
}
