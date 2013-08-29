package ch.thierry.datanucleus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Person {

    @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
    @PrimaryKey
    private Long id;

    @Persistent
    @Column(allowsNull = "false")
    @Index(unique = "true")
    private String name;

    @Persistent(table = "PARENT_CHILD")
    @Element(types = Person.class, column = "CHILD_ID", dependent = "false")
    @Join(column = "PARENT_ID")
    private List<Person> children = new ArrayList<Person>();

    @Persistent(table = "PARENT_CHILD", mappedBy = "children")
    @Element(types = Person.class, column = "PARENT_ID", dependent = "false")
    @Join(column = "CHILD_ID")
    private Set<Person> parents = new HashSet<Person>();

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public Set<Person> getParents() {
        return parents;
    }

    public void setParents(Set<Person> parents) {
        this.parents = parents;
    }
}
