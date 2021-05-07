package net.digitallogic.UserApi.services;

import net.digitallogic.UserApi.events.SendMailEvent;
import net.digitallogic.UserApi.fixtures.UserFixtures;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.UserApi.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.UserApi.persistence.dto.security.ResetPassword;
import net.digitallogic.UserApi.persistence.dto.security.ResetPasswordRequest;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;
import net.digitallogic.UserApi.persistence.repository.TokenRepository;
import net.digitallogic.UserApi.persistence.repository.UserRepository;
import net.digitallogic.UserApi.persistence.repository.UserStatusRepository;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.UserApi.persistence.repositoryFactory.GraphBuilder.GraphResolver;
import net.digitallogic.UserApi.web.error.ErrorCode;
import net.digitallogic.UserApi.web.error.exceptions.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static net.digitallogic.UserApi.persistence.entity.auth.VerificationToken.TokenType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AuthServiceTest {

    @Mock
    TokenRepository tokenRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserStatusRepository userStatusRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    PasswordEncoder encoder;

    @Mock
    GraphBuilder<VerificationToken> tokenGraphBuilder;

    @Mock
    GraphResolver<VerificationToken> graphResolver;

    Clock systemClock = Clock.fixed(Clock.systemDefaultZone()
            .instant(), ZoneId.of("UTC"));

    AuthService authService;

    AutoCloseable closeable;

    @BeforeAll
    static void beforeAll() {

    }

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        when(tokenGraphBuilder.createResolver(
                ArgumentMatchers.<SingularAttribute<VerificationToken, UserEntity>>any()))
                .thenReturn(graphResolver);

        authService = new AuthServiceImpl(
                tokenRepository,
                userRepository,
                userStatusRepository,
                eventPublisher,
                encoder,
                tokenGraphBuilder,
                systemClock,
                6,
                4
        );
    }

    @AfterEach
    void teardown() throws Exception {
        closeable.close();
    }


    @Test
    void successfullyActivateAccountTest() {
        String token = "jflaskjlio3weu2";

        UserEntity user = UserFixtures.userEntity();
        UserStatusEntity status = Mockito.spy(UserStatusEntity.builder()
                .id(user.getId())
                .user(user)
                .validStart(LocalDateTime.now(systemClock).minusMinutes(30))
                .systemStart(LocalDateTime.now(systemClock).minusMinutes(30))
                .build());



        // Get the user from the token
        when(tokenRepository.findByIdAndTokenType(
                anyString(),
                any(TokenType.class),
                any(GraphResolver.class))
        )
                .thenReturn(Optional.of(VerificationToken.builder()
                        .tokenType(TokenType.ENABLE_ACCOUNT)
                        .user(user)
                        .expires(LocalDateTime.now(systemClock).plusMinutes(5))
                        .build()));

        // Get the users current status
        when(userStatusRepository.findByEntityId(any(UUID.class), any(Clock.class)))
                .thenReturn(Optional.of(status));

        doAnswer(invocation -> {
            List<UserStatusEntity> statusList = invocation.getArgument(0);
            statusList.sort(Comparator.comparing(UserStatusEntity::getValidStart));

            assertThat(statusList).hasSize(2);

            assertThat(statusList.get(0).getValidStart()).isBefore(statusList.get(1).getValidStart());

            assertThat(statusList.get(0).getValidStart()).isEqualTo(status.getValidStart());
            assertThat(status.getSystemStop()).isEqualTo(statusList.get(0).getSystemStart());
            assertThat(status.getSystemStop()).isEqualTo(statusList.get(1).getSystemStart());

            return statusList;
        }).when(userStatusRepository).saveAll(anyCollection());

        authService.activateAccount(
                new ActivateAccountToken(token)
        );

        verify(tokenGraphBuilder, times(1)).createResolver(
                ArgumentMatchers.<SingularAttribute<VerificationToken, UserEntity>>any()
        );

        // Verify account status is enabled
        verify(status, atLeastOnce()).isAccountEnabled();
        verify(status, atLeastOnce()).setSystemStop(any(LocalDateTime.class));
    }

    @Test
    void enableAccountThatIsAlreadyEnabledTest() {
        String token = "jflaskjlio3weu2";

        UserEntity user = UserFixtures.userEntity();
        UserStatusEntity status = Mockito.spy(UserStatusEntity.builder()
                .id(user.getId())
                .user(user)
                .accountEnabled(true)
                .validStart(LocalDateTime.now(systemClock).minusMinutes(30))
                .systemStart(LocalDateTime.now(systemClock).minusMinutes(30))
                .build());

        // Get the user from the token
        when(tokenRepository.findByIdAndTokenType(
                anyString(),
                any(TokenType.class),
                any(GraphResolver.class))
        )
                .thenReturn(Optional.of(VerificationToken.builder()
                        .tokenType(TokenType.ENABLE_ACCOUNT)
                        .user(user)
                        .expires(LocalDateTime.now(systemClock).plusMinutes(5))
                        .build()));

        // Get the users current status
        when(userStatusRepository.findByEntityId(any(UUID.class), any(Clock.class)))
                .thenReturn(Optional.of(status));

        when(userStatusRepository.saveAll(anyCollection()))
                .thenReturn(Collections.emptyList());

        authService.activateAccount(new ActivateAccountToken(token));

        verify(userStatusRepository, never()).saveAll(anyCollection());
    }

    @Test
    void tokenExpiredTest() {
        String tokenId = "fjalskj3lkrslkdfnm";

        when(tokenRepository.findByIdAndTokenType(
                anyString(),
                any(TokenType.class),
                any(GraphResolver.class)
        )).thenReturn(Optional.of(VerificationToken.builder()
                .id(tokenId)
                .tokenType(TokenType.ENABLE_ACCOUNT)
                .createdDate(LocalDateTime.now(systemClock).minusHours(4))
                // token has expired
                .expires(LocalDateTime.now(systemClock).minusSeconds(1))
                .build()));

        assertThatThrownBy(() ->
                authService.activateAccount(new ActivateAccountToken(tokenId)))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    void invalidActivationTokenTest() {

        assertThatThrownBy(() ->
                authService.activateAccount(new ActivateAccountToken("jlkjalsdkfjasdf")))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_INVALID);
    }

    @Test
    void requestAccountActivationTokenTest() {
        UserEntity user = UserFixtures.userEntity();
        UserStatusEntity status = UserStatusEntity.builder()
                .id(user.getId())
                .user(user)
                .validStart(LocalDateTime.now(systemClock).minusMinutes(1))
                .systemStart(LocalDateTime.now(systemClock).minusMinutes(1))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        when(userStatusRepository.findByEntityId(user.getId(), systemClock))
                .thenReturn(Optional.of(status));

        // Setup Context holder
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(tokenRepository.save(any(VerificationToken.class))).then(answer -> answer.getArgument(0));

        // Send email to user
        doNothing().when(eventPublisher).publishEvent(any(SendMailEvent.class));

        authService.accountActivateRequest(new ActivateAccountRequest(user.getEmail()));

        verify(eventPublisher, atLeastOnce()).publishEvent(any(SendMailEvent.class));
        verify(tokenRepository, atLeastOnce()).save(any(VerificationToken.class));
    }

    @Test
    void requestAccountActivationFailureTest() {
        UserEntity user = UserFixtures.userEntity();
        UserStatusEntity status = UserStatusEntity.builder()
                .id(user.getId())
                .user(user)
                .accountEnabled(true)
                .validStart(LocalDateTime.now(systemClock).minusMinutes(1))
                .systemStart(LocalDateTime.now(systemClock).minusMinutes(1))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userStatusRepository.findByEntityId(user.getId(), systemClock)).thenReturn(Optional.of(status));
        when(tokenRepository.save(any(VerificationToken.class))).then(answer -> answer.getArgument(0));


        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThatThrownBy(() -> authService.accountActivateRequest(new ActivateAccountRequest(user.getEmail())))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_APPLICABLE);

        verify(tokenRepository, never()).save(any(VerificationToken.class));
    }

    @Test
    void createResetPasswordTokenTest() {
        UserEntity user = UserFixtures.userEntity();
        UserStatusEntity status = UserStatusEntity.builder()
                .accountEnabled(true)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userStatusRepository.findByEntityId(any(UUID.class), any(Clock.class))).thenReturn(Optional.of(status));
        when(tokenRepository.save(any(VerificationToken.class))).then(a -> a.getArgument(0));

        authService.createResetPasswordToken(new ResetPasswordRequest(user.getEmail()));

        verify(eventPublisher, only()).publishEvent(any(SendMailEvent.class));
    }


    @ParameterizedTest(name = "createResetPwdFailed: {0}")
    @MethodSource
    void createResetPasswordFailureTest(UserStatusEntity status) {
        UserEntity user = UserFixtures.userEntity();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userStatusRepository.findByEntityId(any(UUID.class), any(Clock.class))).thenReturn(Optional.of(status));
        when(tokenRepository.save(any(VerificationToken.class))).then(a -> a.getArgument(0));

        assertThatThrownBy(() ->
                authService.createResetPasswordToken(new ResetPasswordRequest(user.getEmail()))
        ).isInstanceOf(BadRequestException.class);


        verify(eventPublisher, never()).publishEvent(any(SendMailEvent.class));
    }

    static Stream<Arguments> createResetPasswordFailureTest() {
        return Stream.of(Arguments.of(
                UserStatusEntity.builder()
                        .accountEnabled(false)
                        .build()),
                Arguments.of(
                        UserStatusEntity.builder()
                                .accountEnabled(true)
                                .accountLocked(true)
                                .build()
                ),
                Arguments.of(
                        UserStatusEntity.builder()
                                .accountEnabled(true)
                                .accountExpired(true)
                                .build()
                ),
                Arguments.of(
                        UserStatusEntity.builder()
                                .accountEnabled(true)
                                .credentialsExpired(true)
                                .build()
                )
        );
    }

    @Test
    void resetPasswordTest() {
        UserEntity user = Mockito.spy(UserFixtures.userEntity());
        String newPassword = "MyNewPassword123";
        VerificationToken token = VerificationToken.builder()
                .id("jalskjfowiur234asdjfl")
                .user(user)
                .expires(LocalDateTime.now(systemClock).plusMinutes(1))
                .tokenType(TokenType.RESET_PASSWORD)
                .build();

        UserStatusEntity status = UserStatusEntity.builder()
                .id(user.getId())
                .user(user)
                .accountEnabled(true)
                .build();

        when(tokenRepository.findByIdAndTokenType(anyString(), any(TokenType.class), any(GraphResolver.class)))
                .thenReturn(Optional.of(token));

        when(userStatusRepository.findByEntityId(any(UUID.class), any(Clock.class)))
                .thenReturn(Optional.of(status));

        when(encoder.encode(anyString())).thenReturn("EncodedNewPasssword");

        authService.resetPassword(new ResetPassword(token.getId(), user.getEmail(), newPassword));
        verify(user, times(1)).setPassword(anyString());
    }

    @Test
    void resetPasswordFailureTest() {
        assertThatThrownBy(() ->
                authService.resetPassword(new ResetPassword("asdfjalskjdf", "guy@gmail.com", "jalskdjfls"))
        )
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_INVALID);
    }

    @Test
    void resetPasswordExpiredTokenTest() {

        when(tokenRepository.findByIdAndTokenType(anyString(), any(TokenType.class), any(GraphResolver.class)))
                .thenReturn(Optional.of(VerificationToken.builder()
                        .id("ajsdlfkjasdf")
                        .expires(LocalDateTime.now(systemClock).minusMinutes(1))
                .build()));

        assertThatThrownBy(() ->
                authService.resetPassword(new ResetPassword("asdfasdf", "guy@gmail.com", "jalskdjflasd"))
        ).isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    void resetPasswordTokenUsedTest() {

        when(tokenRepository.findByIdAndTokenType(anyString(), any(TokenType.class), any(GraphResolver.class)))
                .thenReturn(Optional.of(VerificationToken.builder()
                        .id("ajsdlfkjasdf")
                        .usedCount(1)
                        .expires(LocalDateTime.now(systemClock).plusHours(5))
                        .build()));

        assertThatThrownBy(() ->
                authService.resetPassword(new ResetPassword("asdfasdf", "guy@gmail.com", "jalskdjflasd"))
        ).isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_USED);
    }

    @Test
    void resetPasswordTokenUserMismatchTest() {
        UserEntity user = UserFixtures.userEntity();

        when(tokenRepository.findByIdAndTokenType(anyString(), any(TokenType.class), any(GraphResolver.class)))
                .thenReturn(Optional.of(VerificationToken.builder()
                        .id("ajsdlfkjasdf")
                        .tokenType(TokenType.RESET_PASSWORD)
                        .user(user)
                        .usedCount(0)
                        .expires(LocalDateTime.now(systemClock).plusHours(5))
                        .build()));

        assertThatThrownBy(() ->
                authService.resetPassword(new ResetPassword("asdfasdf", "guy@gmail.com", "jalskdjflasd"))
        ).isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_INVALID);
    }
}