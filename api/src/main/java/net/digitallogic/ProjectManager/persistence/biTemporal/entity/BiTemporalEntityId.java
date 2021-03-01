package net.digitallogic.ProjectManager.persistence.biTemporal.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "validStart", "systemStart"})
@Builder
@Embeddable
@ToString(of = {"id"})
public class BiTemporalEntityId<ID extends Serializable> implements Serializable {
	public static final long serialVersionUID = -7542524972168201884L;

	protected ID id;
	protected LocalDateTime validStart;
	protected LocalDateTime systemStart;


	public BiTemporalEntityId(ID id) {
		this.id = id;
	}

	public BiTemporalEntityId(ID id, LocalDateTime effective) {
		this.id = id;
		this.validStart = effective;
	}

	public BiTemporalEntityId(
			ID id,
			LocalDateTime validStart,
			LocalDateTime systemStart) {

		this.id = id;
		this.validStart = validStart;
		this.systemStart = systemStart;
	}

	public BiTemporalEntityId(BiTemporalEntityId<ID> entity) {
		this.id = entity.getId();
		this.validStart = entity.getValidStart();
		this.systemStart = entity.getSystemStart();
	}
}
