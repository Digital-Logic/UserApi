package net.digitallogic.UserApi.fixtures;

import net.digitallogic.UserApi.persistence.dto.auth.AuthorityDto;
import net.digitallogic.UserApi.persistence.entity.auth.AuthorityEntity;
import net.digitallogic.UserApi.security.Authorities;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthorityFixtures {

	private static final Random random = new Random();

	private static final List<AuthorityDto> authDtos = Arrays.stream(Authorities.values())
			.map(auth -> AuthorityDto.builder()
					.id(UUID.randomUUID())
					.name(auth.name)
					.build()
			)
			.collect(Collectors.toList());

	private static final List<AuthorityEntity> authEntities = Arrays.stream(Authorities.values())
			.map(auth -> AuthorityEntity.builder()
					.id(UUID.randomUUID())
					.name(auth.name)
					.build())
			.collect(Collectors.toList());


	public static AuthorityEntity authorityEntity() {
		return new AuthorityEntity(authEntities.get(random.nextInt(authEntities.size())));
	}

	public static AuthorityDto authorityDto() {
		return new AuthorityDto(authDtos.get(random.nextInt(authDtos.size())));
	}

	public static List<AuthorityEntity> authorityEntity(int size) {
		return authEntities.stream()
				.limit(size)
				.map(AuthorityEntity::new)
				.collect(Collectors.toList());
	}

	public static List<AuthorityDto> authorityDto(int size) {
		return authDtos.stream()
				.limit(size)
				.map(AuthorityDto::new)
				.collect(Collectors.toList());
	}
}
