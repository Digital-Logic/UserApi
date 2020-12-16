package net.digitallogic.ProjectManager.persistence.dto.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.AuditDto;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AuditMessageDto extends AuditDto<UUID> {
	private String message;
}
