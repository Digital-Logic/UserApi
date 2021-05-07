package net.digitallogic.UserApi.persistence.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.dto.DtoBase;
import net.digitallogic.UserApi.persistence.dto.auth.RoleDto;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto extends DtoBase<UUID> {

	private String email;
	private String firstName;
	private String lastName;

	@Builder.Default
	private List<RoleDto> roles = new ArrayList<>();

	@Builder.Default
	private List<UserStatusDto> userStatus = new ArrayList<>();

	/* ** Map UserEntity to UserDto ** */
	public UserDto(UserEntity entity) {
		super(entity);
		this.email = entity.getEmail();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();

		if (pu.isLoaded(entity, UserEntity_.ROLES)) {
			roles = entity.getRoles().stream()
					.map(RoleDto::new)
					.collect(Collectors.toList());
		}
	}

	public UserDto(UserDto dto) {
		super(dto);

		this.email = dto.getEmail();
		this.firstName = dto.getFirstName();
		this.lastName = dto.getLastName();

		this.roles = dto.getRoles().stream()
				.map(RoleDto::new)
				.collect(Collectors.toList());

		this.userStatus = dto.getUserStatus().stream()
				.map(UserStatusDto::new)
				.collect(Collectors.toList());
	}
}
