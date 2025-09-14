package com.hackaton.vk_mini_backend.repository;

import com.hackaton.vk_mini_backend.model.ClsActivationStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClsActivationStatusRepo
        extends JpaRepository<ClsActivationStatus, Long>, JpaSpecificationExecutor<ClsActivationStatus> {

    List<ClsActivationStatus> findAllByIsDeletedOrderByIdAsc(boolean isDeleted);

    @Query(
            value =
                    """
            SELECT p.*
            FROM cls_activation_status p
            WHERE (:name IS NULL OR p.name LIKE '%:name%')
            AND (:code IS NULL OR p.code = :code)
            """,
            nativeQuery = true)
    Page<ClsActivationStatus> findActivationStatusPage(
            @Nullable @Param("name") String name, @Nullable @Param("code") String code, Pageable pageable);
}
