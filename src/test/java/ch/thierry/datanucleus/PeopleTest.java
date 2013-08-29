package ch.thierry.datanucleus;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.hsqldb.jdbcDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PeopleTest {

    private PersistenceManagerFactory pmf;
    private PersistenceManager pm;

    /**
     * @throws SQLException
     */
    @Before
    public void setUp() throws SQLException {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("datanucleus.autoCreateSchema", true);
        props.put("datanucleus.rdbms.statementBatchLimit", 0);

        pmf = new JDOPersistenceManagerFactory(props);
        pmf.setConnectionDriverName(jdbcDriver.class.getName());
        pmf.setConnectionURL("jdbc:hsqldb:mem:testdb;hsqldb.sqllog=3");
        pmf.setConnectionUserName("SA");
        pmf.setConnectionPassword("");

        pm = pmf.getPersistenceManager();

        Person dad = pm.makePersistent(new Person("Dad"));
        Person mom = pm.makePersistent(new Person("Mom"));
        Person jack = pm.makePersistent(new Person("Jack"));
        Person john = pm.makePersistent(new Person("John"));
        Person jane = pm.makePersistent(new Person("Jane"));

        dad.getChildren().add(jack);
        dad.getChildren().add(john);
        dad.getChildren().add(jane);

        mom.getChildren().add(jack);
        mom.getChildren().add(john);
        mom.getChildren().add(jane);

        jack.getParents().add(dad);
        jack.getParents().add(mom);

        john.getParents().add(dad);
        john.getParents().add(mom);

        jane.getParents().add(dad);
        jane.getParents().add(mom);

        flush();
    }

    @After
    public void tearDown() {
        pm.close();
        pmf.close();
    }

    @Test
    public void testRelations() {
        Person mom = find("Mom");
        Person dad = find("Dad");
        Person jack = find("Jack");
        Person john = find("John");
        Person jane = find("Jane");

        assertThat(jack.getChildren().isEmpty(), is(true));
        assertThat(jack.getParents(), containsInAnyOrder(mom, dad));

        assertThat(john.getChildren().isEmpty(), is(true));
        assertThat(john.getParents(), containsInAnyOrder(mom, dad));

        assertThat(jane.getChildren().isEmpty(), is(true));
        assertThat(jane.getParents(), containsInAnyOrder(mom, dad));

        assertThat(mom.getParents().isEmpty(), is(true));
        assertThat(mom.getChildren(), containsInAnyOrder(jack, john, jane));

        assertThat(dad.getParents().isEmpty(), is(true));
        assertThat(dad.getChildren(), containsInAnyOrder(jack, john, jane));
    }

    private Person find(String name) {
        Query query = pm.newQuery(Person.class);
        query.setUnique(true);
        query.setFilter("name == pName");
        query.declareParameters("java.lang.String pName");
        return (Person) query.execute(name);
    }

    private void flush() {

    }
}
