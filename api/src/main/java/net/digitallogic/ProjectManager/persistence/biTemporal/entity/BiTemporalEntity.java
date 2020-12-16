package net.digitallogic.ProjectManager.persistence.biTemporal.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.audit.AuditMessageEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static net.digitallogic.ProjectManager.persistence.biTemporal.Constants.MAX_DATE;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@MappedSuperclass
public abstract class BiTemporalEntity<ID extends Serializable> {

	@Builder.Default
	@EmbeddedId
	protected BiTemporalEntityId<ID> id = new BiTemporalEntityId<>();

	public LocalDateTime getValidStart() { return id.getValidStart(); }
	public LocalDateTime getSystemStart() { return id.getSystemStart(); }
	public ID getEmbeddedId() { return id.getId(); }

	@Builder.Default
	@Column(name = "valid_stop")
	protected LocalDateTime validStop = MAX_DATE;

	@Builder.Default
	@Column(name = "system_stop")
	protected LocalDateTime systemStop = MAX_DATE;

	/* ** Audit fields ** */
	@Column(name = "created_by", nullable = false)
	protected UUID createdBy;

	@Column(name = "created_date", nullable = false)
	protected LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "audit_message")
	protected AuditMessageEntity auditMessage;
}
