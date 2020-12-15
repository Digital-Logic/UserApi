package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@Entity(name = "AuthorityEntity")
@Table(name = "authority_entity")
public class AuthorityEntity {

	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;
}
