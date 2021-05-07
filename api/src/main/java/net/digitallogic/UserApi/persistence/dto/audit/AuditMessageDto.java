package net.digitallogic.UserApi.persistence.dto.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.dto.AuditDto;
import net.digitallogic.UserApi.persistence.entity.audit.AuditMessageEntity;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AuditMessageDto extends AuditDto<UUID> {
	private String message;

	public AuditMessageDto(AuditMessageEntity entity) {
		super(entity);
		this.message = entity.getMessage();
	}

	public AuditMessageDto(AuditMessageDto dto) {
		super(dto);
		this.message = dto.getMessage();
	}
}
