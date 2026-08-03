package org.vaadin.tatu;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.BrowserTest;

public class TwinColSelectIT extends AbstractViewTest {

    public TwinColSelectIT() {
        super("twoselects");
    }

    @BeforeEach
    public void init() throws Exception {
        super.setup();

        // Hide dev mode gizmo, it would interfere screenshot tests
        try {
            $("vaadin-dev-tools").first().setProperty("hidden", true);
            $("copilot-main").first().setProperty("hidden", true);
        } catch (NotFoundException e) {

        }
    }

    @BrowserTest
    public void dragFromOptionsToValue() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select = $(TwinColSelectElement.class).id("first");
        VerticalLayoutElement valueList = select.getValueList();
        List<DivElement> options = select.getOptions();
        int optionCount = options.size();

        // Drag the first option to values list, assert the name of
        // option is correct, value change happened etc. and lists
        // are updated accordingly
        DivElement option = options.get(0);
        String optionText = option.getText();
        Assertions.assertEquals("Pekka", optionText);
        action.moveToElement(option).clickAndHold().moveToElement(valueList)
                .release().build().perform();
        SpanElement message = $(SpanElement.class).id("value-change1");
        Assertions.assertEquals(optionText, message.getText());
        Assertions.assertEquals(optionCount - 1, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assertions.assertEquals(optionText, values.get(0).getText());
    }

    @BrowserTest
    public void dragTwoFromOptionsToValue() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select = $(TwinColSelectElement.class).id("first");
        VerticalLayoutElement valueList = select.getValueList();
        List<DivElement> options = select.getOptions();
        int optionCount = options.size();

        // Click first option selected and drag by second option
        // should drag the both to values
        options.get(0).click();
        DivElement option = options.get(1);
        action.moveToElement(option).clickAndHold().moveToElement(valueList)
                .release().build().perform();

        SpanElement message = $(SpanElement.class).id("value-change1");
        Assertions.assertEquals("Pekka,Matti", message.getText());
        Assertions.assertEquals(optionCount - 2, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assertions.assertEquals(2, values.size());
        Assertions.assertEquals("Pekka", values.get(0).getText());
        Assertions.assertEquals("Matti", values.get(1).getText());
    }

    @BrowserTest
    public void tabbing() {
        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("options",
                focusedElement().getAttribute("class"));
        action.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("Pekka", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("Matti", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("Jussi", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        WebElement button = focusedElement();
        Assertions.assertEquals("vaadin-button", button.getTagName());
        WebElement tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assertions.assertEquals("Add all to selected",
                tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        button = focusedElement();
        Assertions.assertEquals("vaadin-button", button.getTagName());
        tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assertions.assertEquals("Add to selected",
                tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        button = focusedElement();
        Assertions.assertEquals("vaadin-button", button.getTagName());
        tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assertions.assertEquals("Toggle selection",
                tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("value",
                focusedElement().getAttribute("class"));
    }

    @BrowserTest
    public void moveTwoByKeyboardFromOptionsToValue() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select = $(TwinColSelectElement.class).id("first");
        List<DivElement> options = select.getOptions();
        int optionCount = options.size();

        action.click(options.get(0)).perform();
        Assertions.assertEquals("Pekka", focusedElement().getText());
        Assertions.assertEquals("true",
                focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ARROW_DOWN).perform();
        wait(Duration.ofMillis(30));
        Assertions.assertEquals("Matti", focusedElement().getText());
        Assertions.assertEquals(null, focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ARROW_DOWN).perform();
        wait(Duration.ofMillis(20));
        Assertions.assertEquals("Jussi", focusedElement().getText());
        Assertions.assertEquals(null, focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.SPACE).perform();
        wait(Duration.ofMillis(20));
        Assertions.assertEquals("true",
                focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ENTER).perform();
        wait(Duration.ofMillis(20));

        SpanElement message = $(SpanElement.class).id("value-change1");
        Assertions.assertEquals("Pekka,Jussi", message.getText());
        Assertions.assertEquals(optionCount - 2, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assertions.assertEquals(2, values.size());
        Assertions.assertEquals("Pekka", values.get(0).getText());
        Assertions.assertEquals("Jussi", values.get(1).getText());
    }

    @BrowserTest
    public void dragFromValueToOptions() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select = $(TwinColSelectElement.class)
                .id("second");
        VerticalLayoutElement optionList = select.getOptionList();
        List<DivElement> values = select.getValues();
        int valueCount = values.size();

        // Drag the first value item back to the options list, assert
        // that value item texts are correct, value change happens and
        // lists are updated accordingly
        DivElement value = values.get(0);
        String valueText = value.getText();
        Assertions.assertEquals("One", valueText);
        action.moveToElement(value).clickAndHold().moveToElement(optionList)
                .release().build().perform();
        SpanElement message = $(SpanElement.class).id("value-change2");
        Assertions.assertEquals("Two", message.getText());
        Assertions.assertEquals(valueCount - 1, select.getValues().size());

        DivElement option = select.getOptions().get(1);
        Assertions.assertEquals(valueText, option.getText());
    }

    @BrowserTest
    public void dragFromTwinColSelectToOtherTwinColSelectPrevented() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select1 = $(TwinColSelectElement.class)
                .id("first");
        TwinColSelectElement select2 = $(TwinColSelectElement.class)
                .id("second");

        // Part 2: Attempt to move value from select2 to select1, assert
        // that no change in number of children is observed
        VerticalLayoutElement value1List = select1.getValueList();
        VerticalLayoutElement value2List = select2.getValueList();
        Assertions.assertEquals(2, select2.getValues().size());
        Assertions.assertEquals(0, select1.getValues().size());

        DivElement value2 = select2.getValues().get(0);
        action.moveToElement(value2).clickAndHold().moveToElement(value1List)
                .release().build().perform();

        Assertions.assertEquals(2, value2List.$(DivElement.class).all().size());
        Assertions.assertEquals(0, value1List.$(DivElement.class).all().size());

        // Part 2: Attempt to move option from select2 to select1, assert
        // that no change in number of children is observed
        VerticalLayoutElement option1List = select1.getOptionList();
        Assertions.assertEquals(1, select2.getOptions().size());
        Assertions.assertEquals(3, select1.getOptions().size());

        DivElement option2 = select2.getOptions().get(0);
        action.moveToElement(option2).clickAndHold().moveToElement(option1List)
                .release().build().perform();

        Assertions.assertEquals(1, select2.getOptions().size());
        Assertions.assertEquals(3, select1.getOptions().size());
    }

    public void wait(Duration duration) {
        long timeoutMillis = duration.toMillis();
        if (timeoutMillis > 0) {
            try {
                Thread.sleep(timeoutMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private WebElement focusedElement() {
        return getDriver().switchTo().activeElement();
    }
}