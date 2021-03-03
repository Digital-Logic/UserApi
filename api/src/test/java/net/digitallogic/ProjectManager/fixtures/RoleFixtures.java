package net.digitallogic.ProjectManager.fixtures;

import net.digitallogic.ProjectManager.persistence.dto.user.RoleDto;
import net.digitallogic.ProjectManager.persistence.entity.auth.RoleEntity;
import net.digitallogic.ProjectManager.security.ROLES;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RoleFixtures {

	private static final Clock clock = Clock.systemUTC();
	private static final Random random = new Random();

	private static final List<RoleDto> roleDtos = Arrays.stream(ROLES.values())
			.map(role -> RoleDto.builder()
					.id(UUID.randomUUID())
					.name(role.name)
					.authorities(AuthorityFixtures.authorityDto(random.nextInt(3)))
					.createdBy(UUID.randomUUID())
					.createdDate(LocalDateTime.now(clock))
					.lastModifiedDate(LocalDateTime.now(clock))
					.lastModifiedBy(UUID.randomUUID())
					.build())
			.collect(Collectors.toList());

	private static final List<RoleEntity> roleEntities = Arrays.stream(ROLES.values())
			.map(role -> RoleEntity.builder()
					.id(UUID.randomUUID())
					.name(role.name)
					.authorities(new HashSet<>(AuthorityFixtures.authorityEntity(random.nextInt(3))))
					.createdBy(UUID.randomUUID())
					.createdDate(LocalDateTime.now(clock))
					.lastModifiedDate(LocalDateTime.now(clock))
					.lastModifiedBy(UUID.randomUUID())
					.build())
			.collect(Collectors.toList());

	public static RoleDto roleDto() {
		return roleDtos.get(random.nextInt(roleDtos.size()));
	}

	public static RoleEntity roleEntity() {
		return new RoleEntity(
				roleEntities.get(random.nextInt(roleEntities.size()))
		);
	}

	public static List<RoleDto> roleDto(int size) {
		return roleDtos.stream()
				.limit(size)
				.map(RoleDto::new)
				.collect(Collectors.toList());
	}

	public static List<RoleEntity> roleEntity(int size) {
		return roleEntities.stream()
				.limit(size)
				.map(RoleEntity::new)
				.collect(Collectors.toList());
	}
}
