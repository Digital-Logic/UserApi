package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.events.CreateAccountActivationToken;
import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserUpdateDto;
import net.digitallogic.ProjectManager.persistence.entity.auth.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.RoleRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.ROLES;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import net.digitallogic.ProjectManager.web.error.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static net.digitallogic.ProjectManager.services.Utils.processSortBy;
import static net.digitallogic.ProjectManager.web.filter.SpecSupport.toSpecification;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final GraphBuilder<UserEntity> userGraphBuilder;
	private final UserStatusRepository userStatusRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final Clock systemClock;


	@Autowired
	public UserServiceImpl(
			UserRepository userRepository,
			GraphBuilder<UserEntity> userGraphBuilder,
			UserStatusRepository userStatusRepository,
			ApplicationEventPublisher eventPublisher, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			Clock systemClock
	) {

		this.userRepository = userRepository;
		this.userGraphBuilder = userGraphBuilder;
		this.userStatusRepository = userStatusRepository;
		this.eventPublisher = eventPublisher;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.systemClock = systemClock;

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Slice<UserDto> getAllUsers(int page, int limit, String sort,
	                                  @Nullable String filter,
	                                  @Nullable String expand) {

		return userRepository.findAll(
				toSpecification(filter),
				PageRequest.of(page, limit, Sort.by(processSortBy(sort))),
				userGraphBuilder.createResolver(expand)
		)
				.map(UserDto::new);
	}

	@Override
	@Transactional
	public UserDto updateUser(UUID id, UserUpdateDto updateUser) {

		UserEntity userEntity = userRepository.findById(id)
				.orElseThrow(() ->
					new NotFoundException(
							ErrorCode.NON_EXISTENT_ENTITY,
							MessageTranslator.NonExistentEntity("User", id))
				);

		// TODO Get users current state before updating user info

		if (updateUser.getFirstName() != null && !updateUser.getFirstName().isBlank() &&
				!userEntity.getFirstName().equals(updateUser.getFirstName()))
			userEntity.setFirstName(updateUser.getFirstName());

		if (updateUser.getLastName() != null && !updateUser.getLastName().isBlank() &&
				!userEntity.getLastName().equals(updateUser.getLastName()))
			userEntity.setLastName(updateUser.getLastName());

//		if (updateUser.isArchived() && userEntity.isArchived() != updateUser.isArchived())
//			userEntity.setArchived(true);

		return new UserDto(userRepository.save(userEntity));
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserDto getUser(final UUID id, @Nullable String expand) {
		return userRepository.findById(id, userGraphBuilder.createResolver(expand))
						.map(UserDto::new)
				.orElseThrow(() ->
					new NotFoundException(
							ErrorCode.NON_EXISTENT_ENTITY,
							MessageTranslator.NonExistentEntity("User", id)));
	}

	@Override
	@Transactional
	public UserDto createUser(CreateUserRequest dto) {
		LocalDateTime now = LocalDateTime.now(systemClock);

		if (userRepository.existsByEmailIgnoreCase(dto.getEmail()))
			throw new BadRequestException(
					ErrorCode.DUPLICATE_ENTITY,
					MessageTranslator.DuplicateEntityExist("User", dto.getEmail())
			);

		RoleEntity role = roleRepository.findByName(ROLES.USER.name)
				.orElseThrow(); // Throw something that can be cached and logged for review

		UserEntity user = UserEntity.builder()
				.id(UUID.randomUUID())
				.email(dto.getEmail())
				.password(passwordEncoder.encode(dto.getPassword()))
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.build();

		user.addRole(role);
		userRepository.save(user);

		UserEntity sysUser = userRepository.findByEmail("system_account@localhost")
				.orElseThrow();

		// TODO optimized this into one save, by adding UserStatusEntity to UserEntity
		//  with propagation
		UserStatusEntity status = UserStatusEntity.builder()
				.user(user)
				.validStart(now)
				.systemStart(now)
				.createdBy(sysUser.getId())
				.build();

		userStatusRepository.save(status);

		eventPublisher.publishEvent(new CreateAccountActivationToken(user));

		return new UserDto(user);
	}




	// Not being used right now...
//	private String getMessage(String code, Object... args) {
//		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
//	}
}
