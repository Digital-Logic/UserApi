package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken.TokenType;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken_;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.repositoryFactory.EntityGraphRepository;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<VerificationToken, String>,
        EntityGraphRepository<VerificationToken, String>, JpaSpecificationExecutor<VerificationToken> {


    default Optional<VerificationToken> findValidTokenByUserAndTokenType(UserEntity user, TokenType type, LocalDateTime expires) {
    	return findOne(findValidTokenByUserAndTypeSpec(user, type, expires));
	}

    default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type) {
        return findOne(findByIdAndTokenTypeSpec(id, type));
    }

    default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type, LocalDateTime expires) {
        return findOne(findByIdAndTokenTypeSpec(id, type, expires));
    }

    // Graph Resolver methods
    default Optional<VerificationToken> findByIdAndTokenType(String id, TokenType type, GraphResolver<VerificationToken> graphResolver) {
        return findOne(findByIdAndTokenTypeSpec(id, type), graphResolver);
    }

    default Optional<VerificationToken> findValidByIdAndTokenType(String id, TokenType type, LocalDateTime expires, GraphResolver<VerificationToken> graphResolver) {
        return findOne(findByIdAndTokenTypeSpec(id, type, expires), graphResolver);
    }


    /* **************** Specifications ****************** */
    default Specification<VerificationToken> findValidTokenByUserAndTypeSpec(UserEntity user, TokenType type, LocalDateTime expires) {
        return ((root, query, builder) ->
                builder.and(
                        builder.equal(root.get(VerificationToken_.user), user),
                        builder.equal(root.get(VerificationToken_.tokenType), type),
                        builder.equal(root.get(VerificationToken_.usedCount), 0),
                        builder.greaterThanOrEqualTo(root.get(VerificationToken_.expires), expires)
                )
        );
    }

    default Specification<VerificationToken> findByIdAndTokenTypeSpec(String id, TokenType type) {
        return (root, query, builder) ->
                builder.and(
                        builder.equal(root.get(VerificationToken_.id), id),
                        builder.equal(root.get(VerificationToken_.tokenType), type)
                );
    }

    // Return token that has not expired by specified time.
    default Specification<VerificationToken> findByIdAndTokenTypeSpec(String id, TokenType type, LocalDateTime expires) {
        return (root, query, builder) ->
                builder.and(
                        builder.equal(root.get(VerificationToken_.id), id),
                        builder.equal(root.get(VerificationToken_.tokenType), type),
                        builder.greaterThanOrEqualTo(root.get(VerificationToken_.expires), expires)
                );
    }
}
