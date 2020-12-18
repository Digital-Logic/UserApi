package net.digitallogic.ProjectManager.persistence.biTemporal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity_;
import net.digitallogic.ProjectManager.persistence.dto.audit.AuditMessageDto;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id", "validStart", "systemStart"})
@NoArgsConstructor
@AllArgsConstructor
public class BiTemporalDto<ID extends Serializable> {
	protected static PersistenceUtil pu = Persistence.getPersistenceUtil();

	protected ID id;
	protected LocalDateTime validStart;
	protected LocalDateTime validStop;
	protected LocalDateTime systemStart;
	protected LocalDateTime systemStop;

	protected UUID createdBy;
	protected LocalDateTime createdDate;
	protected AuditMessageDto auditMessage;

	public BiTemporalDto(BiTemporalEntity<ID> entity) {

		this.id = entity.getEmbeddedId();
		this.validStart = entity.getValidStart();
		this.validStop = entity.getValidStop();
		this.systemStart = entity.getSystemStart();
		this.systemStop = entity.getSystemStop();

		this.createdBy = entity.getCreatedBy();
		this.createdDate = entity.getCreatedDate();

		if (pu.isLoaded(entity, BiTemporalEntity_.AUDIT_MESSAGE) &&
				entity.getAuditMessage() != null) {
			auditMessage = new AuditMessageDto(entity.getAuditMessage());
		}
	}

	public BiTemporalDto(BiTemporalDto<ID> dto) {
		this.id = dto.getId();
		this.validStart = dto.getValidStart();
		this.validStop = dto.getValidStop();
		this.systemStart = dto.getSystemStart();
		this.systemStop = dto.getSystemStop();

		this.createdBy = dto.getCreatedBy();
		this.createdDate = dto.getCreatedDate();

		if (dto.getAuditMessage() != null)
			this.auditMessage = new AuditMessageDto(dto.getAuditMessage());
	}
}
