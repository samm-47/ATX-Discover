package com.austinlocal.repository;

import com.austinlocal.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    List<UserPreference> findByUserId(String userId);
    Optional<UserPreference> findByUserIdAndCategory(String userId, String category);
}
