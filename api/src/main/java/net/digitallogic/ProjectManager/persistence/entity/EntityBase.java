package net.digitallogic.ProjectManager.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class EntityBase<ID extends Serializable> implements Persistable<ID> {

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
	protected boolean isNew = true;

	/* ** Toggle isNew boolean ** */
	@PostPersist
	@PostLoad
	private void toggleIsNew() { isNew = false; }
}
