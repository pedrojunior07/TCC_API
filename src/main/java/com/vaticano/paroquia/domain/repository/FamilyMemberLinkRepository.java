package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.FamilyMemberLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberLinkRepository extends JpaRepository<FamilyMemberLink, String> {

    List<FamilyMemberLink> findByFamilyId(String familyId);

    List<FamilyMemberLink> findByMemberKey(String memberKey);

    Optional<FamilyMemberLink> findByFamilyIdAndMemberKey(String familyId, String memberKey);

    void deleteByFamilyIdAndMemberKey(String familyId, String memberKey);

    boolean existsByFamilyIdAndMemberKey(String familyId, String memberKey);
}
