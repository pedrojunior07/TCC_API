package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByMemberId(String memberId);

    boolean existsByMemberKey(String memberKey);

    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.nomeCompleto) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.comunidade) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Member> searchMembers(@Param("search") String search, Pageable pageable);

    List<Member> findByComunidade(String comunidade);

    Page<Member> findByBatizadoTrue(Pageable pageable);

    Page<Member> findByCrismadoTrue(Pageable pageable);

    Page<Member> findByCasadoTrue(Pageable pageable);
}
