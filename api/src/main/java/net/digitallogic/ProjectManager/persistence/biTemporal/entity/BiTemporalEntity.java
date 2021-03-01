package net.digitallogic.ProjectManager.persistence.biTemporal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.digitallogic.ProjectManager.persistence.entity.audit.AuditMessageEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static net.digitallogic.ProjectManager.persistence.biTemporal.Constants.MAX_DATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@MappedSuperclass
public abstract class BiTemporalEntity<ID extends Serializable> {

	@EmbeddedId
	protected BiTemporalEntityId<ID> id = new BiTemporalEntityId<>();

	public LocalDateTime getValidStart() { return id.getValidStart(); }
	public LocalDateTime getSystemStart() { return id.getSystemStart(); }

	public ID getEmbeddedId() { return id.getId(); }

	@Column(name = "valid_stop")
	protected LocalDateTime validStop = MAX_DATE;

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

	public BiTemporalEntity(BiTemporalEntityBuilder<ID, ?, ?> builder) {
		this.id = new BiTemporalEntityId<>(builder.id, builder.validStart, builder.systemStart);

		if (builder.validStop != null)
			this.validStop = builder.validStop;

		if (builder.systemStop != null)
			this.systemStop = builder.systemStop;

		this.createdBy = builder.createdBy;
		this.auditMessage = builder.auditMessage;
	}

	public static abstract class BiTemporalEntityBuilder<ID extends Serializable, C extends BiTemporalEntity<ID>, B extends BiTemporalEntityBuilder<ID, C, B>> {
		private ID id;

		private LocalDateTime validStart;
		private LocalDateTime validStop;
		private LocalDateTime systemStart;
		private LocalDateTime systemStop;

		private UUID createdBy;
		private AuditMessageEntity auditMessage;

		public B id(ID id) {
			this.id = id;
			return self();
		}
		public  B validStart(LocalDateTime validStart) {
			this.validStart = validStart;
			return self();
		}

		public B validStop(LocalDateTime validStop) {
			this.validStop = validStop;
			return self();
		}

		public B systemStart(LocalDateTime systemStart) {
			this.systemStart = systemStart;
			return  self();
		}

		public B systemStop(LocalDateTime systemStop) {
			this.systemStop = systemStop;
			return self();
		}

		public B createdBy(UUID createdBy) {
			this.createdBy = createdBy;
			return self();
		}

		public B auditMessage(AuditMessageEntity auditMessage) {
			this.auditMessage = auditMessage;
			return self();
		}

		protected abstract B self();

		public abstract C build();

		public String toString() {return "BiTemporalEntity.BiTemporalEntityBuilder(id=" + this.id +
				"validStart=" + this.validStart +
				", validStop=" + this.validStop +
				", systemStart=" + this.systemStart +
				", systemStop=" + this.systemStop +
				", createdBy=" + this.createdBy +
				", auditMessage=" + this.auditMessage +
				")";}
	}
}
