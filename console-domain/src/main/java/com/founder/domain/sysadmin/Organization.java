package com.founder.domain.sysadmin;

import com.founder.domain.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SYS_ORGANIZATION")
@Data
@EqualsAndHashCode(exclude = {"parent", "subOrganization", "users"})
@SqlResultSetMapping(
        name = "OrganizationValueMapping",
        classes = {
                @ConstructorResult(
                        targetClass = OrganizationValue.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "parentId", type = Long.class),
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "status"),
                                @ColumnResult(name = "organizationType"),
                                @ColumnResult(name = "organizationOrder", type = Long.class),
                                @ColumnResult(name = "organizationNo"),
                                @ColumnResult(name = "address"),
                                @ColumnResult(name = "zipCode"),
                                @ColumnResult(name = "lv", type = Long.class),
                                @ColumnResult(name = "organizationLevel")
                        }
                )}
)

@NamedNativeQueries({
        @NamedNativeQuery(name = "Organization.findAllOrganizations", query = "select id as id,parent_id as parentId,name as name,status as status,organization_type as organizationType,organization_order as organizationOrder,organization_no as organizationNo,address as address ,zip_code as zipCode,to_number(level) as lv,ORGANIZATION_LEVEL as organizationLevel  from sys_organization start with parent_id is null connect by prior id = parent_id order by level,organization_order", resultSetMapping = "OrganizationValueMapping"),
        @NamedNativeQuery(name = "Organization.findByUserId", query = "select g.id as id,g.parent_id as parentId,g.name as name,g.status as status,g.organization_type as organizationType,g.organization_order as organizationOrder,g.organization_no as organizationNo,g.address as address ,g.zip_code as zipCode,to_number(g.level) as lv,g.ORGANIZATION_LEVEL as organizationLevel  from sys_organization g, SYS_USER u, SYS_USER_ORGANIZATION ou where u.id = ou.USER_ID and ou.ORGANIZATION_ID = g.id start with parent_id is null connect by prior id = parent_id order by level,organization_order", resultSetMapping = "OrganizationValueMapping")
}
)

public class Organization implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Organization> affiliatedOrganization = Collections.emptyList();

    @ManyToMany(mappedBy = "organizations", fetch = FetchType.LAZY)
    private Set<User> users = Collections.emptySet();

    private String organizationNo;

    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;

    @Enumerated(EnumType.STRING)
    private OrganizationLevel organizationLevel;

    private Long organizationOrder;

    private String createUser;

    private Date createTime;

    private String address;

    private String zipCode;

    public boolean hasUser() {
        return users.size() > 0;
    }

    public boolean hasSubOrganizations() {
        return affiliatedOrganization.size() > 0;
    }

    public enum  OrganizationType{
        A,B,C
    }

    public enum OrganizationLevel{
        FirstClass,SecondClass,BranchCompany
    }

}
