package net.digitallogic.ProjectManager.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.dto.AuditDto;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity<ID extends Serializable>
		implements Persistable<ID> {

	@Id
	@Column(name = "id", nullable = false, unique = true)
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

	/* ** Toggle isNew boolean ** */
	@PostPersist
	@PostLoad
	private void toggleIsNew() { isNew = false; }

	/* ** Audit Fields ** */
	@CreatedBy
	@Column(name = "created_by")
	protected UUID createdBy;

	@CreatedDate
	@Column(name = "created_date", updatable = false) // Database will insert value
	protected LocalDateTime createdDate;

	@LastModifiedDate
	@Column(name = "last_modified_by")
	protected UUID lastModifiedBy;

	@LastModifiedBy
	@Column(name = "last_modified_date")
	protected LocalDateTime lastModifiedDate;

	public AuditEntity(AuditDto<ID> dto) {
		this.id = dto.getId();
		this.archived = dto.isArchived();
		this.version = dto.getVersion();
	}

	public AuditEntity(AuditEntity<ID> entity) {
		this.id = entity.getId();
		this.version = entity.getVersion();
		this.archived = entity.isArchived();
		this.isNew = entity.isNew;
		this.createdDate = entity.getCreatedDate();
		this.lastModifiedDate = entity.getLastModifiedDate();
		this.createdBy = entity.getCreatedBy();
		this.lastModifiedBy = entity.getLastModifiedBy();
	}
}
