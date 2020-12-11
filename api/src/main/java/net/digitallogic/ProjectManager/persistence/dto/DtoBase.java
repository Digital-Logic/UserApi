package net.digitallogic.ProjectManager.persistence.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.EntityBase;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class DtoBase<ID extends Serializable> {

	protected ID id;

	@Builder.Default
	protected int version = 0;

	@Builder.Default
	protected boolean archived = false;

	public DtoBase(EntityBase<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();
		this.archived = entity.isArchived();
	}
}
