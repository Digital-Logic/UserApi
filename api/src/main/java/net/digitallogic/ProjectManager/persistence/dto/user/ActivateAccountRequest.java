package net.digitallogic.ProjectManager.persistence.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountRequest {

    @NotEmpty
    private String code;
}
