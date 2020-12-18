package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AuthorityEntity")
@Table(name = "authority_entity")
public class AuthorityEntity {

	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	public AuthorityEntity(AuthorityEntity entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}
}
