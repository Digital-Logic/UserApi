package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken.TokenType;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.EntityGraphRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<VerificationToken, String>,
		EntityGraphRepository<VerificationToken, String>, JpaSpecificationExecutor<VerificationToken> {

	// Do I need this method????
	Optional<VerificationToken> findByUser(UserEntity user);

	default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type) {
		return findOne(findByIdAndTokenTypeSpec(id, type, LocalDateTime.now(Clock.systemUTC())));
	}

	default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type, LocalDateTime time) {
		return findOne(findByIdAndTokenTypeSpec(id, type, time));
	}

	// Graph Resolver methods
	default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type, GraphResolver graphResolver) {
		return findOne(findByIdAndTokenTypeSpec(id, type, LocalDateTime.now(Clock.systemUTC())), graphResolver);
	}

	default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type, LocalDateTime time, GraphResolver graphResolver) {
		return findOne(findByIdAndTokenTypeSpec(id, type, time), graphResolver);
	}

	default Specification<VerificationToken> findByIdAndTokenTypeSpec(String id, TokenType type, LocalDateTime time) {
		return (root, query, builder) ->
				builder.and(
						builder.equal(root.get(VerificationToken_.id), id),
						builder.equal(root.get(VerificationToken_.tokenType), type),
						builder.greaterThanOrEqualTo(root.get(VerificationToken_.expires), time)
				);
	}
}
