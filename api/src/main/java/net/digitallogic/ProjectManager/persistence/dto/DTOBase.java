package net.digitallogic.ProjectManager.persistence.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class DTOBase<ID extends Serializable> {

	protected ID id;

	@Builder.Default
	protected int version = 0;

	@Builder.Default
	protected boolean archived = false;
}
