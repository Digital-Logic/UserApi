package net.digitallogic.ProjectManager.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.DtoBase;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public abstract class EntityBase<ID extends Serializable>
		implements Persistable<ID> {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	protected ID id;

	@Version
	@Builder.Default
	@Setter(value = AccessLevel.PRIVATE)
	@Column(name = "version", nullable = false)
	protected int version = 0;

	@Builder.Default
	@Column(name = "archived", nullable = false)
	protected boolean archived = false;

	@Transient
	@Builder.Default
	@Setter(AccessLevel.PROTECTED)
	protected boolean isNew = true;

	@CreatedDate
	@Column(name = "created_date", updatable = false)
	protected LocalDateTime createdDate;

	@LastModifiedDate
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

	// Copy constructor
	public EntityBase(EntityBase<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();
		this.archived = entity.isArchived();
		this.isNew = entity.isNew;
		this.createdDate = entity.getCreatedDate();
		this.lastModifiedDate = entity.getLastModifiedDate();
	}
}
