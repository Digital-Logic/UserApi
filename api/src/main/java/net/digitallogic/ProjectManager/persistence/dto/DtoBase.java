package net.digitallogic.ProjectManager.persistence.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.EntityBase;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class DtoBase<ID extends Serializable> {
	protected static PersistenceUtil pu = Persistence.getPersistenceUtil();

	protected ID id;

	@Builder.Default
	protected int version = 0;

//	@Builder.Default
//	protected boolean archived = false;

	protected LocalDateTime createdDate;
	protected LocalDateTime lastModifiedDate;

	public DtoBase(EntityBase<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();
//		this.archived = entity.isArchived();
		this.createdDate = entity.getCreatedDate();
		this.lastModifiedDate = entity.getLastModifiedDate();
	}

	public DtoBase(DtoBase<ID> dto) {
		this.id = dto.getId();
		this.version = dto.getVersion();
//		this.archived = dto.isArchived();
		this.createdDate = dto.getCreatedDate();
		this.lastModifiedDate = dto.getLastModifiedDate();
	}
}
