package net.digitallogic.ProjectManager.persistence.entity.user;

import lombok.*;
import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntity;
import net.digitallogic.ProjectManager.persistence.entity.SoftDelete;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserStatusEntity")
@Table(name = "user_status")
public class UserStatusEntity extends BiTemporalEntity<UUID> implements SoftDelete {

	@Column(name = "account_enabled", updatable = false)
	private boolean accountEnabled = false;

	@Column(name = "account_expired", updatable = false)
	private boolean accountExpired = false;

	@Column(name = "account_locked", updatable = false)
	private boolean accountLocked = false;

	@Column(name = "credentials_expired", updatable = false)
	private boolean credentialsExpired = false;

	@Column(name = "deleted", updatable = false)
	private boolean deleted = false;

	@MapsId("id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "id")
	private UserEntity user;

	/* ** BiDirectional linking to UserEntity is not maintained
		on coping, This is only intended to be used during testing ** */
	public UserStatusEntity(UserStatusEntity entity) {
		super(entity);
		this.accountEnabled = entity.isAccountEnabled();
		this.accountExpired = entity.isAccountExpired();
		this.accountLocked = entity.isAccountLocked();
		this.credentialsExpired = entity.isCredentialsExpired();
		this.user = entity.getUser();
	}

	public UserStatusEntity(UserStatusEntityBuilder<UserStatusEntity, UserStatusEntityBuilderImpl> builder) {
		super(builder);
		this.accountEnabled = builder.accountEnabled;
		this.accountExpired = builder.accountExpired;
		this.accountLocked = builder.accountLocked;
		this.credentialsExpired = builder.credentialsExpired;
		this.deleted = builder.deleted;

		if (builder.user != null) {
			this.user = builder.user;
		}
	}

	public static UserStatusEntityBuilder<?, ?> builder() {return new UserStatusEntityBuilderImpl();}

	public static abstract class UserStatusEntityBuilder<C extends UserStatusEntity, B extends UserStatusEntityBuilder<C, B>> extends BiTemporalEntityBuilder<UUID, C, B> {
		private boolean accountEnabled;
		private boolean accountExpired;
		private boolean accountLocked;
		private boolean credentialsExpired;
		private boolean deleted;
		private UserEntity user;

		public B accountEnabled(boolean accountEnabled) {
			this.accountEnabled = accountEnabled;
			return self();
		}

		public B accountExpired(boolean accountExpired) {
			this.accountExpired = accountExpired;
			return self();
		}

		public B accountLocked(boolean accountLocked) {
			this.accountLocked = accountLocked;
			return self();
		}

		public B credentialsExpired(boolean credentialsExpired) {
			this.credentialsExpired = credentialsExpired;
			return self();
		}

		public B deleted(boolean deleted) {
			this.deleted = deleted;
			return self();
		}

		public B user(UserEntity user) {
			this.user = user;
			return self();
		}

		protected abstract B self();

		public abstract C build();

		public String toString() {
			return "UserStatusEntity.UserStatusEntityBuilder(super=" + super.toString() +
					", accountEnabled$value=" + this.accountEnabled +
					", accountExpired$value=" + this.accountExpired +
					", accountLocked$value=" + this.accountLocked +
					", credentialsExpired$value=" + this.credentialsExpired +
					", deleted$value=" + this.deleted +
					", user=" + this.user + ")";}
	}

	private static final class UserStatusEntityBuilderImpl extends UserStatusEntityBuilder<UserStatusEntity, UserStatusEntityBuilderImpl> {
		private UserStatusEntityBuilderImpl() {}

		protected UserStatusEntityBuilderImpl self() {return this;}

		public UserStatusEntity build() {return new UserStatusEntity(this);}
	}
}
