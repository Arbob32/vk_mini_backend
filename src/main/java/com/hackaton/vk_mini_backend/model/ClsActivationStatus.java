package com.hackaton.vk_mini_backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "cls_activation_status", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
public class ClsActivationStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String APPROVAL = "APPROVAL";

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "name", nullable = false)
    protected String name;

    @Basic
    @Column(name = "code", nullable = false, unique = true)
    protected String code;

    @Basic
    @Column(name = "is_deleted", insertable = false)
    protected Boolean isDeleted = false;
}
