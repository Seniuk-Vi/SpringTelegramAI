package org.brain.springtelegramai.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "roles")
public class RoleEntity {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_sequence")
    @SequenceGenerator(name = "role_id_sequence", sequenceName = "role_id_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;
    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    Set<UserEntity> users;
}
