package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.annotations.RepositoryTest;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken_;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static net.digitallogic.UserApi.persistence.entity.auth.VerificationToken.TokenType;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class TokenRepositoryTest {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    GraphBuilder<VerificationToken> graphBuilder;

    private final UUID userID = UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c");
    private final String tokenId = "fJvhX9z1L+c4QN3MD8ZFWdNamUnK7JIQQyicDzMIevk+CTQG";

    @Test
    @Sql(value = "classpath:db/accountActivationToken.sql")
    void findByIdAndTokenTypeTest() {
        UserEntity user = userRepository.findById(userID)
                .orElseThrow();

        Optional<VerificationToken> token = tokenRepository.
                findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT);

        assertThat(token).isNotEmpty();
    }

    @Test
    @Sql(value = "classpath:db/accountActivationToken.sql")
    void findByIdAndTokenTypeWithGraphResolverTest() {
        UserEntity user = userRepository.findById(UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c"))
                .orElseThrow();

        Optional<VerificationToken> token = tokenRepository.
                findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, graphBuilder.createResolver(VerificationToken_.user));

        assertThat(token).isNotEmpty();
    }

    @Test
    @Sql(value = "classpath:db/accountActivationToken.sql")
    void findByIdAndTokenTypeWithTimeAndGraphResolverTest() {
        UserEntity user = userRepository.findById(UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c"))
                .orElseThrow();

        Optional<VerificationToken> token = tokenRepository.
                findValidByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, LocalDateTime.now(Clock.systemUTC()),
                        graphBuilder.createResolver(VerificationToken_.user));

        assertThat(token).isNotEmpty();
    }
}
