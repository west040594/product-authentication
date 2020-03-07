package com.tstu.authenticationsystem.model;

import com.tstu.commons.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;


@Table(name = "tb_role")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Role  extends BaseEntity implements GrantedAuthority {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    @Override
    public String getAuthority() {
        return name;
    }
}
