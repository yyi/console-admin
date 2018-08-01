package com.founder.domain.sysadmin;

import com.founder.domain.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SYS_RESOURCE")
@Data
@EqualsAndHashCode(exclude = {"parent", "subResources", "roles"})

@SqlResultSetMapping(
        name = "ResourceValueMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ResourceValue.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "parentId", type = Long.class),
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "type"),
                                @ColumnResult(name = "url"),
                                @ColumnResult(name = "permission"),
                                @ColumnResult(name = "status"),
                                @ColumnResult(name = "lv", type = Long.class)
                        }
                )}
)
@NamedNativeQueries(
        {
                @NamedNativeQuery(name = "Resource.findRoleResource", query = "select  a.id as id,a.parent_id as parentId,a.name as name,a.resource_type as type,a.url as url,a.permission as permission ,a.status as status ,to_number(level-1) as lv from SYS_RESOURCE a where id !=1 and exists (select 1 from SYS_ROLE_RESOURCE b where a.id=b.resource_id and b.ROLE_ID in (?1)) start with id =1 connect by prior id = parent_id order by level ,a.orders", resultSetMapping = "ResourceValueMapping"),
                @NamedNativeQuery(name = "Resource.findRolePermission", query = "select  permission  from sys_role a, sys_role_resource b,sys_resource c where a.id=b.role_id and b.resource_id=c.id and a.id in (?1) and permission is not null")
        }
)
public class Resource implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private String url;

    private String permission;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Resource parent;

    private Long orders;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = {CascadeType.REMOVE})
    @Fetch(FetchMode.JOIN)
    private List<Resource> subResources = Collections.emptyList();

    @ManyToMany(mappedBy = "resources", fetch = FetchType.EAGER)
    public Set<Role> roles = Collections.emptySet();


    public boolean hasAssociatedRole() {
        return roles.size() > 0;
    }

    public boolean hasSubResource() {
        return subResources.size() > 0;
    }

    public enum ResourceType {
        MENU, ENTRY, BUTTON
    }
}
