package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.annotations.RepositoryTest;
import net.digitallogic.UserApi.persistence.entity.auth.AuthorityEntity;
import net.digitallogic.UserApi.security.Authorities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class AuthorityRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	AuthorityRepository authorityRepository;

	@Test
	public void findByNameTest() {
		Optional<AuthorityEntity> authority =
				authorityRepository.findByName(Authorities.ADMIN_ROLES.name);

		assertThat(authority).isNotEmpty();
	}

	@Test
	public void findAllTest() {
		List<AuthorityEntity> authorities = StreamSupport.stream(
				authorityRepository.findAll()
						.spliterator(), false)
				.collect(Collectors.toList());

		assertThat(authorities).isNotEmpty();
	}
}
