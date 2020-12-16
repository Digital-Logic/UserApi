package net.digitallogic.ProjectManager.persistence.entity.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.AuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "AuditMessage")
@Table(name = "audit_message")
public class AuditMessageEntity extends AuditEntity<UUID> {

	@Column(name = "message", nullable = false)
	private String message;
}
