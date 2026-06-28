package com.talbiya.CivicPulseAi.repository;


import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRoleAndCityAndArea(Role role, String city, String area);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User> findById(Long id);
}
