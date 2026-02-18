package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.User;
import com.vaticano.paroquia.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("""
            SELECT u
            FROM User u
            WHERE u.deletedAt IS NULL
              AND (
                    u.usernameNorm LIKE CONCAT('%', :usernameNorm, '%')
                 OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
              )
            """)
    Page<User> findByUsernameNormContainingOrNameContainingIgnoreCase(
            @Param("usernameNorm") String usernameNorm,
            @Param("name") String name,
            Pageable pageable
    );

    Optional<User> findByUsernameNorm(String usernameNorm);

    boolean existsByUsernameNorm(String usernameNorm);

    List<User> findByRole(Role role);

    List<User> findByActiveTrue();

    long countByRoleAndActiveTrue(Role role);
}
