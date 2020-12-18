package net.digitallogic.ProjectManager.persistence.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditDto<ID extends Serializable> {
	protected static PersistenceUtil pu = Persistence.getPersistenceUtil();

	protected ID id;

	@Builder.Default
	protected int version = 0;

	@Builder.Default
	protected boolean archived = false;

	/* ** Audit Fields ** */
	protected LocalDateTime createdDate;
	protected LocalDateTime lastModifiedDate;
	protected UUID lastModifiedBy;
	protected UUID createdBy;


	public AuditDto(AuditEntity<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();

		this.archived = entity.isArchived();

		this.createdBy = entity.getCreatedBy();
		this.createdDate = entity.getCreatedDate();

		this.lastModifiedBy = entity.getLastModifiedBy();
		this.lastModifiedDate = entity.getLastModifiedDate();
	}

	public AuditDto(AuditDto<ID> dto) {
		this.id = dto.getId();
		this.version = dto.getVersion();
		this.archived = dto.isArchived();
		this.createdBy = dto.getCreatedBy();
		this.createdDate = dto.getCreatedDate();
		this.lastModifiedDate = dto.getLastModifiedDate();
		this.lastModifiedBy = dto.getLastModifiedBy();
	}
}
