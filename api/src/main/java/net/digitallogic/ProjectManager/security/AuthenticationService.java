package net.digitallogic.ProjectManager.security;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.security.UserAuthentication.UserAuthenticationBuilder;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record8;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.digitallogic.ProjectManager.Tables.ROLE_AUTHORITY_LOOKUP;
import static net.digitallogic.ProjectManager.Tables.USER_ROLE_LOOKUP;
import static net.digitallogic.ProjectManager.tables.AuthorityEntity.AUTHORITY_ENTITY;
import static net.digitallogic.ProjectManager.tables.RoleEntity.ROLE_ENTITY;
import static net.digitallogic.ProjectManager.tables.UserEntity.USER_ENTITY;
import static net.digitallogic.ProjectManager.tables.UserStatus.USER_STATUS;
import static org.jooq.impl.DSL.upper;

@Component
@Slf4j
@Primary
public class AuthenticationService implements UserDetailsService {

	private final DSLContext dsl;
	private final Clock clock;

	@Autowired
	public AuthenticationService(DSLContext dsl, Clock clock) {
		this.dsl = dsl;
		this.clock = clock;
	}

	@Override
	public UserAuthentication loadUserByUsername(String email) throws UsernameNotFoundException {
		// log.info(generateSql(email).getSQL());
		// Fetch records from database
		log.info("Load user by email: {}", email);
		var results = generateSql(email).fetch();

		Map<UUID, UserAuthenticationBuilder> authMap = new HashMap<>();

		results.forEach(r -> {
			UUID userId = r.getValue(USER_ENTITY.ID);
			authMap.computeIfAbsent(userId, id ->
					UserAuthentication.builder()
							.id(id)
							.email(r.getValue(USER_ENTITY.EMAIL))
							.password(r.getValue(USER_ENTITY.PASSWORD))
							.accountEnabled(r.getValue(USER_STATUS.ACCOUNT_ENABLED))
							.accountExpired(r.getValue(USER_STATUS.ACCOUNT_EXPIRED))
							.accountLocked(r.getValue(USER_STATUS.ACCOUNT_LOCKED))
							.credentialsExpired(r.getValue(USER_STATUS.CREDENTIALS_EXPIRED))
			);

			if (r.getValue(AUTHORITY_ENTITY.NAME) != null &&
					!r.getValue(AUTHORITY_ENTITY.NAME).isBlank())
				authMap.get(userId)
					.authority(new Authority(r.getValue(AUTHORITY_ENTITY.NAME)));

		});

		return authMap.values().stream()
				.findFirst()
				.map(UserAuthenticationBuilder::build)
				.orElseThrow(() -> new UsernameNotFoundException("User not found."));
	}

	private SelectConditionStep<Record8<UUID, String, String, Boolean, Boolean, Boolean, Boolean, String>> generateSql(String email) {

		return dsl.select(
				USER_ENTITY.ID,
				USER_ENTITY.EMAIL,
				USER_ENTITY.PASSWORD,
				USER_STATUS.ACCOUNT_ENABLED,
				USER_STATUS.ACCOUNT_EXPIRED,
				USER_STATUS.ACCOUNT_LOCKED,
				USER_STATUS.CREDENTIALS_EXPIRED,
				AUTHORITY_ENTITY.NAME
		)
				.from(USER_ENTITY
						.join(USER_STATUS)
						.on(USER_STATUS.ID.eq(USER_ENTITY.ID))

						.join(USER_ROLE_LOOKUP)
						.on(USER_ENTITY.ID.eq(USER_ROLE_LOOKUP.USER_ID))

						.join(ROLE_ENTITY)
						.on(ROLE_ENTITY.ID.eq(USER_ROLE_LOOKUP.ROLE_ID))

						.join(ROLE_AUTHORITY_LOOKUP, JoinType.LEFT_OUTER_JOIN)
						.on(ROLE_ENTITY.ID.eq(ROLE_AUTHORITY_LOOKUP.ROLE_ID))

						.join(AUTHORITY_ENTITY, JoinType.LEFT_OUTER_JOIN)
						.on(ROLE_AUTHORITY_LOOKUP.AUTHORITY_ID.eq(AUTHORITY_ENTITY.ID))
				)

				.where(upper(USER_ENTITY.EMAIL).eq(email.toUpperCase()))
				.and(USER_STATUS.VALID_START.lessOrEqual(LocalDateTime.now(clock)))
				.and(USER_STATUS.VALID_STOP.greaterThan(LocalDateTime.now(clock)))
				.and(USER_STATUS.SYSTEM_START.lessOrEqual(LocalDateTime.now(clock)))
				.and(USER_STATUS.SYSTEM_STOP.greaterThan(LocalDateTime.now(clock)));
	}
}
