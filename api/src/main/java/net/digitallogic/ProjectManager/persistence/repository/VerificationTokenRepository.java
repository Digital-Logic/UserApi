package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String>,
		EntityGraphRepository<VerificationToken, String> {

	Optional<VerificationToken> findByUser(UserEntity user);
}
