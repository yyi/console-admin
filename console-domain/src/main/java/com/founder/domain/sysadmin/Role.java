package com.founder.domain.sysadmin;

import com.founder.domain.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "SYS_ROLE")
@Data
@EqualsAndHashCode(exclude = {"resources", "users"})
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_ROLE_RESOURCE",
            joinColumns = {@JoinColumn(name = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID")})
    private Set<Resource> resources = Collections.emptySet();

    public void clearResources() {
        resources.clear();
    }

    public void appendResource(Resource resource) {
        resources.add(resource);
    }

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = Collections.emptySet();

    public boolean hasAssociatedUser() {
        return users.size() > 0;
    }

}
