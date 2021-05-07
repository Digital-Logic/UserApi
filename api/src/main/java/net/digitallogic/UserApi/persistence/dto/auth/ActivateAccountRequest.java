package net.digitallogic.UserApi.persistence.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivateAccountRequest {

    @NotEmpty
    private String email;
}
