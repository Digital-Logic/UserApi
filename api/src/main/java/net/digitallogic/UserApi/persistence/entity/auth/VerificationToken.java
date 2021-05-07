package net.digitallogic.UserApi.persistence.entity.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.digitallogic.UserApi.persistence.entity.EntityBase;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;

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
	private TokenType tokenType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Column(name = "expires")
	private LocalDateTime expires;

	@Builder.Default
	@Column(name = "used_count")
	private int usedCount=0;

	public void incrementCounter() {
		this.usedCount += 1;
	}

	public enum TokenType {
		ENABLE_ACCOUNT(1),
		RESET_PASSWORD(2)
		;

		public final int value;
		TokenType(int value) { this.value = value; }
	}
}
