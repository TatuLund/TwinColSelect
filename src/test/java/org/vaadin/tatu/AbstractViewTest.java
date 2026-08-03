package org.vaadin.tatu;

import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.ScreenshotOnFailureExtension;
import com.vaadin.testbench.TestBench;

/**
 * Base class for ITs
 * <p>
 * The tests use Chrome driver (see pom.xml for integration-tests profile) to
 * run integration tests on a headless Chrome. If a property {@code test.use
 * .hub} is set to true, {@code AbstractViewTest} will assume that the TestBench
 * test is running in a CI environment. In order to keep the this class light,
 * it makes certain assumptions about the CI environment (such as available
 * environment variables). It is not advisable to use this class as a base class
 * for you own TestBench tests.
 * <p>
 * To learn more about TestBench, visit <a href=
 * "https://vaadin.com/docs/v10/testbench/testbench-overview.html">Vaadin
 * TestBench</a>.
 */
public abstract class AbstractViewTest extends BrowserTestBase {
    private static final int SERVER_PORT = 8080;

    private final String route;

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    public AbstractViewTest() {
        this("");
    }

    protected AbstractViewTest(String route) {
        this.route = route;
    }

    @BeforeEach
    public void setup() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        TestBench.createDriver(new ChromeDriver(options));
        getDriver().get(getURL(route));

        // We do screenshot testing, adjust settings to ensure less flakiness
        Parameters.setScreenshotComparisonTolerance(0.05);
        Parameters.setScreenshotComparisonCursorDetection(true);
        testBench().resizeViewPortTo(800, 600);
        Parameters.setMaxScreenshotRetries(3);
        Parameters.setScreenshotRetryDelay(1000);

        // Wait for frontend compilation complete before testing
        waitForDevServer();
    }

    @AfterEach
    public void afterEach() {
        getDriver().close();
    }

    /**
     * Returns deployment host name concatenated with route.
     *
     * @return URL to route
     */
    private static String getURL(String route) {
        return String.format("http://%s:%d/%s", getDeploymentHostname(),
                SERVER_PORT, route);
    }

    /**
     * Property set to true when running on a test hub.
     */
    private static final String USE_HUB_PROPERTY = "test.use.hub";

    /**
     * Returns whether we are using a test hub. This means that the starter is
     * running tests in Vaadin's CI environment, and uses TestBench to connect
     * to the testing hub.
     *
     * @return whether we are using a test hub
     */
    private static boolean isUsingHub() {
        return Boolean.TRUE.toString()
                .equals(System.getProperty(USE_HUB_PROPERTY));
    }

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    private static String getDeploymentHostname() {
        return isUsingHub() ? System.getenv("HOSTNAME") : "localhost";
    }

    protected void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void scrollToElement(WebElement element) {
        Objects.requireNonNull(element,
                "The element to scroll to should not be null");
        getCommandExecutor().executeScript("arguments[0].scrollIntoView(true);",
                element);
    }
}