package net.digitallogic.ProjectManager.persistence.biTemporal.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.audit.AuditMessageEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static net.digitallogic.ProjectManager.persistence.biTemporal.Constants.MAX_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@MappedSuperclass
public abstract class BiTemporalEntity<ID extends Serializable> {

	@Builder.Default
	@EmbeddedId
	protected BiTemporalEntityId<ID> id = new BiTemporalEntityId<>();

	public LocalDateTime getValidStart() { return id.getValidStart(); }
	public LocalDateTime getSystemStart() { return id.getSystemStart(); }
	public void setSystemStart(LocalDateTime systemStart) { id.setSystemStart(systemStart); }

	public ID getEmbeddedId() { return id.getId(); }

	@Builder.Default
	@Column(name = "valid_stop")
	protected LocalDateTime validStop = MAX_DATE;

	@Builder.Default
	@Column(name = "system_stop")
	protected LocalDateTime systemStop = MAX_DATE;

	/* ** Audit fields ** */
	@Column(name = "created_by", nullable = false, updatable = false)
	protected UUID createdBy;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "audit_message")
	protected AuditMessageEntity auditMessage;


	public BiTemporalEntity(BiTemporalEntity<ID> entity) {
		this.id = new BiTemporalEntityId<>(entity.getId());
		this.validStop = entity.getValidStop();
		this.systemStop = entity.getSystemStop();

		this.createdBy = entity.getCreatedBy();
		this.auditMessage = entity.getAuditMessage();
	}
}
