package net.digitallogic.UserApi.services;

import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.UserApi.persistence.dto.security.ResetPassword;
import net.digitallogic.UserApi.persistence.dto.security.ResetPasswordRequest;

public interface AuthService {
	void activateAccount(ActivateAccountToken activateAccountToken);
	void resetPassword(ResetPassword resetPassword);
	void accountActivateRequest(ActivateAccountRequest request);
	void createResetPasswordToken(ResetPasswordRequest resetPasswordRequest);
}
