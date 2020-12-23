package net.digitallogic.ProjectManager.persistence.repository;

import net.digitallogic.ProjectManager.persistence.biTemporal.repository.BiTemporalRepository;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;

import java.util.UUID;

public interface UserStatusRepository extends BiTemporalRepository<UserStatusEntity, UUID> {
}
