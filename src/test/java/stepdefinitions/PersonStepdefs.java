package stepdefinitions;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PersonStepdefs {
    private WebDriver webDriver;
    private static final String pathToChromeDriver = "/home/oscar/Oscar/Docencia/Curso2016-2017/Taller/chromedriver";

    @Before // Cuidado con esta anotación, está en el paquete cucumber, no junit
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);
        webDriver = new ChromeDriver();
        // Wait before getting WebElements
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }

    @After // Cuidado con esta anotación, está en el paquete cucumber, no junit
    public void tearDown() {
        webDriver.quit();
    }

    @Given("^I am on the main page$")
    public void i_am_on_the_main_page() throws Throwable {
        webDriver.navigate().to("http://localhost:8080");
    }

    @Given("^No user with nif \"([^\"]*)\" exists$")
    public void no_user_with_nif_exists(String nif) throws Throwable {
        try {
            webDriver.findElement(By.id(nif));
            fail("There already is a Person with nif: " + nif);
        } catch (NoSuchElementException e) {
        }
    }

    @When("^I provide \"([^\"]*)\" for the name$")
    public void i_provide_for_the_name(String name) throws Throwable {
        webDriver.findElement(By.xpath("//input[@ng-model='name']")).sendKeys(name);
    }

    @And("^I provide \"([^\"]*)\" for the surname$")
    public void i_provide_for_the_surname(String surname) throws Throwable {
        webDriver.findElement(By.xpath("//input[@ng-model='surname']")).sendKeys(surname);
    }

    @And("^I provide \"([^\"]*)\" for the nif$")
    public void i_provide_for_the_nif(Integer nif) throws Throwable {
        webDriver.findElement(By.xpath("//input[@ng-model='nif']")).sendKeys(""+nif);
    }

    @And("^I click the New button$")
    public void i_click_the_New_button() throws Throwable {
        webDriver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Then("^The person with nif \"([^\"]*)\" is created in the Agenda$")
    public void the_person_with_nif_is_created_in_the_Agenda(String nif) throws Throwable {
        assertNotNull(webDriver.findElement(By.id(nif)));
    }
}
