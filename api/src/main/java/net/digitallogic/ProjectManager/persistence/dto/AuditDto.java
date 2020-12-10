package net.digitallogic.ProjectManager.persistence.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
}
