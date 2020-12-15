package net.digitallogic.ProjectManager.persistence.biTemporal.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

import static net.digitallogic.ProjectManager.persistence.biTemporal.Constants.MAX_DATE;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
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
}
