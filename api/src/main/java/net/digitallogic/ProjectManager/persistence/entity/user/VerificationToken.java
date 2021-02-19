package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.digitallogic.ProjectManager.persistence.entity.EntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "VerificationToken")
@Table(name = "verification_token")
public class VerificationToken extends EntityBase<String> {

	@Column(name = "token_type")
	private String tokenType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Column(name = "expires")
	private LocalDateTime expires;

}
