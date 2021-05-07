package net.digitallogic.UserApi.persistence.repository;

import net.digitallogic.UserApi.persistence.biTemporal.repository.BiTemporalRepository;
import net.digitallogic.UserApi.persistence.entity.user.UserStatusEntity;

import java.util.UUID;

public interface UserStatusRepository extends BiTemporalRepository<UserStatusEntity, UUID> {
}
