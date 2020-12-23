package net.digitallogic.ProjectManager.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.DtoBase;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class EntityBase<ID extends Serializable>
		implements Persistable<ID> {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	protected ID id;

	@Version
	@Builder.Default
	@Column(name = "version", nullable = false)
	protected int version = 0;

	@Builder.Default
	@Column(name = "archived", nullable = false)
	protected boolean archived = false;

	@Transient
	@Builder.Default
	@Setter(AccessLevel.PROTECTED)
	protected boolean isNew = true;

	@Column(name = "created_date", updatable = false, insertable = false)
	protected LocalDateTime createdDate;

	@Column(name = "last_modified_date")
	protected LocalDateTime lastModifiedDate;

	/* ** Toggle isNew boolean ** */
	@PostPersist
	@PostLoad
	private void toggleIsNew() { isNew = false; }

	public EntityBase(DtoBase<ID> dto) {
		this.id = dto.getId();
		this.version = dto.getVersion();
		this.archived = dto.isArchived();
	}

	public EntityBase(EntityBase<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();
		this.archived = entity.isArchived();
		this.isNew = entity.isNew;
		this.createdDate = entity.getCreatedDate();
		this.lastModifiedDate = entity.getLastModifiedDate();

	}
}
