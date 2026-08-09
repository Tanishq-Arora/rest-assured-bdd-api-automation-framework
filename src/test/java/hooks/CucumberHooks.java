package hooks;

import clients.BaseClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CucumberHooks {

    @Before
    public void setUp() {
        BaseClient.setup();
    }

    @After
    public void tearDown() {
        BaseClient.cleanup();
    }
}