package net.digitallogic.UserApi.persistence.entity.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.entity.AuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "AuditMessage")
@Table(name = "audit_message")
public class AuditMessageEntity extends AuditEntity<UUID> {

	@Column(name = "message", nullable = false)
	private String message;

	public AuditMessageEntity(AuditMessageEntity entity) {
		super(entity);
		this.message = entity.getMessage();
	}
}
