package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.annotations.RepositoryTest;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken.TokenType;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class VerificationTokenRepositoryTest {

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public GraphBuilder<VerificationToken> verificationTokenGraphBuilder() {
            return GraphBuilder.builder(VerificationToken.class)
                    .addProperty(VerificationToken_.user)
                    .build();
        }
    }

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    GraphBuilder<VerificationToken> graphBuilder;

    private final UUID userID = UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c");
    private final String tokenId = "fJvhX9z1L+c4QN3MD8ZFWdNamUnK7JIQQyicDzMIevk+CTQG";

    @Test
    @Sql(value = "classpath:db/verificationTokenRepositoryTest.sql")
    void findByIdAndTokenTypeTest() {
        UserEntity user = userRepository.findById(userID)
                .orElseThrow();

        Optional<VerificationToken> token = verificationTokenRepository.
                findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT);

        assertThat(token).isNotEmpty();
    }

    @Test
    @Sql(value = "classpath:db/verificationTokenRepositoryTest.sql")
    void findByIdAndTokenTypeWithTimeTest() {
        UserEntity user = userRepository.findById(UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c"))
                .orElseThrow();

        Optional<VerificationToken> token = verificationTokenRepository.
                findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, LocalDateTime.now(Clock.systemUTC()));

        assertThat(token).isNotEmpty();
    }

    @Test
    @Sql(value = "classpath:db/verificationTokenRepositoryTest.sql")
    void findByIdAndTokenTypeWithTimeAndGraphResolverTest() {
        UserEntity user = userRepository.findById(UUID.fromString("4876a5ba-319e-4ca1-849d-1f6cb5e3524c"))
                .orElseThrow();

        Optional<VerificationToken> token = verificationTokenRepository.
                findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, LocalDateTime.now(Clock.systemUTC()),
                        graphBuilder.createResolver(VerificationToken_.user));

        assertThat(token).isNotEmpty();
    }
}
