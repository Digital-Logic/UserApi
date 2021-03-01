package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;
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

	@Builder.Default
	@Column(name = "count")
	private int count=0;

}
