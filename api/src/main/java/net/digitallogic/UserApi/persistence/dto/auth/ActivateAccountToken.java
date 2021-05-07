package net.digitallogic.UserApi.persistence.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountToken {

    @NotEmpty
    private String token;
}
