package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.dto.user.CreateUserDto;
import net.digitallogic.ProjectManager.persistence.dto.user.UserDto;
import net.digitallogic.ProjectManager.persistence.entity.user.RoleEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.RoleRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.ROLES;
import net.digitallogic.ProjectManager.web.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static net.digitallogic.ProjectManager.services.Utils.processSortBy;
import static net.digitallogic.ProjectManager.web.filter.SpecSupport.toSpecification;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final GraphBuilder<UserEntity> userGraphBuilder;
	private final UserStatusRepository userStatusRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final MessageSource messageSource;

	@Autowired
	public UserServiceImpl(
			UserRepository userRepository,
			GraphBuilder<UserEntity> userGraphBuilder,
			UserStatusRepository userStatusRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			MessageSource messageSource) {

		this.userRepository = userRepository;
		this.userGraphBuilder = userGraphBuilder;
		this.userStatusRepository = userStatusRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Slice<UserDto> getAllUsers(int page, int limit, String sort, @Nullable String filter, @Nullable String expand) {

		return userRepository.findAll(
				toSpecification(filter), // TODO filter processors do not have access to messageSource -- FixMe
				PageRequest.of(page, limit, Sort.by(processSortBy(sort))),
				userGraphBuilder.createResolver(expand)
		)
				.map(UserDto::new);
	}

	@Transactional
	public UserDto createUser(CreateUserDto dto) {
		if (userRepository.existsByEmailIgnoreCase(dto.getEmail()))
			throw new BadRequestException(
					getMessage("exception.duplicateEntity", "User", dto.getEmail())
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

		UserStatusEntity status = UserStatusEntity.builder()
				.user(user)
				.createdBy(sysUser.getId())
				.build();

		userStatusRepository.save(status);

		return new UserDto(user);
	}

	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}
