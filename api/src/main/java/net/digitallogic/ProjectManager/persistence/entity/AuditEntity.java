package net.digitallogic.ProjectManager.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

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
public abstract class AuditEntity<ID extends Serializable> implements Persistable<ID> {

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
	protected boolean isNew = true;

	/* ** Toggle isNew boolean ** */
	@PostPersist
	@PostLoad
	private void toggleIsNew() { isNew = false; }

	/* ** Audit Fields ** */
	@Column(name = "created_date", updatable = false, insertable = false) // Database will insert value
	protected LocalDateTime createdDate;

	@Column(name = "last_modified_date")
	protected LocalDateTime lastModifiedDate;

	@Column(name = "last_modified_by")
	protected UUID lastModifiedBy;

	@Column(name = "created_by")
	protected UUID createdBy;
}
