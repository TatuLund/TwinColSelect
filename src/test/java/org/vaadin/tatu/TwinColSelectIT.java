package org.vaadin.tatu;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;

public class TwinColSelectIT extends AbstractViewTest {

    public TwinColSelectIT() {
        super("twoselects");
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        // Hide dev mode gizmo, it would interfere screenshot tests
        $("vaadin-dev-tools").first().setProperty("hidden", true);
    }

    @Test
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
        Assert.assertEquals("Pekka", optionText);
        action.moveToElement(option).clickAndHold().moveToElement(valueList)
                .release().build().perform();
        SpanElement message = $(SpanElement.class).id("value-change1");
        Assert.assertEquals(optionText, message.getText());
        Assert.assertEquals(optionCount - 1, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assert.assertEquals(optionText, values.get(0).getText());
    }

    @Test
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
        Assert.assertEquals("Pekka,Matti", message.getText());
        Assert.assertEquals(optionCount - 2, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assert.assertEquals("Pekka", values.get(0).getText());
        Assert.assertEquals("Matti", values.get(1).getText());
    }

    @Test
    public void tabbing() {
        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.TAB).perform();
        Assert.assertEquals("options", focusedElement().getAttribute("class"));
        action.sendKeys(Keys.TAB).perform();
        Assert.assertEquals("Pekka", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        Assert.assertEquals("Matti", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        Assert.assertEquals("Jussi", focusedElement().getText());
        action.sendKeys(Keys.TAB).perform();
        WebElement button = focusedElement();
        Assert.assertEquals("vaadin-button", button.getTagName());
        WebElement tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assert.assertEquals("Add all to selected",
                tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        button = focusedElement();
        Assert.assertEquals("vaadin-button", button.getTagName());
        tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assert.assertEquals("Add to selected", tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        button = focusedElement();
        Assert.assertEquals("vaadin-button", button.getTagName());
        tooltip = button.findElement(By.tagName("vaadin-tooltip"));
        Assert.assertEquals("Toggle selection", tooltip.getDomProperty("text"));
        action.sendKeys(Keys.TAB).perform();
        Assert.assertEquals("value", focusedElement().getAttribute("class"));
    }

    @Test
    public void moveTwoByKeyboardFromOptionsToValue() {
        Actions action = new Actions(getDriver());
        TwinColSelectElement select = $(TwinColSelectElement.class).id("first");
        List<DivElement> options = select.getOptions();
        int optionCount = options.size();

        action.click(options.get(0)).perform();
        Assert.assertEquals("Pekka", focusedElement().getText());
        Assert.assertEquals("true", focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ARROW_DOWN).perform();
        wait(20);
        Assert.assertEquals("Matti", focusedElement().getText());
        Assert.assertEquals(null, focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ARROW_DOWN).perform();
        wait(20);
        Assert.assertEquals("Jussi", focusedElement().getText());
        Assert.assertEquals(null, focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.SPACE).perform();
        wait(20);
        Assert.assertEquals("true", focusedElement().getAttribute("checked"));
        action.sendKeys(Keys.ENTER).perform();
        wait(20);

        SpanElement message = $(SpanElement.class).id("value-change1");
        Assert.assertEquals("Pekka,Jussi", message.getText());
        Assert.assertEquals(optionCount - 2, select.getOptions().size());

        List<DivElement> values = select.getValues();
        Assert.assertEquals("Pekka", values.get(0).getText());
        Assert.assertEquals("Jussi", values.get(1).getText());
    }

    @Test
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
        Assert.assertEquals("One", valueText);
        action.moveToElement(value).clickAndHold().moveToElement(optionList)
                .release().build().perform();
        SpanElement message = $(SpanElement.class).id("value-change2");
        Assert.assertEquals("Two", message.getText());
        Assert.assertEquals(valueCount - 1, select.getValues().size());

        DivElement option = select.getOptions().get(1);
        Assert.assertEquals(valueText, option.getText());
    }

    @Test
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
        Assert.assertEquals(2, select2.getValues().size());
        Assert.assertEquals(0, select1.getValues().size());

        DivElement value2 = select2.getValues().get(0);
        action.moveToElement(value2).clickAndHold().moveToElement(value1List)
                .release().build().perform();

        Assert.assertEquals(2, value2List.$(DivElement.class).all().size());
        Assert.assertEquals(0, value1List.$(DivElement.class).all().size());

        // Part 2: Attempt to move option from select2 to select1, assert
        // that no change in number of children is observed
        VerticalLayoutElement option1List = select1.getOptionList();
        Assert.assertEquals(1, select2.getOptions().size());
        Assert.assertEquals(3, select1.getOptions().size());

        DivElement option2 = select2.getOptions().get(0);
        action.moveToElement(option2).clickAndHold().moveToElement(option1List)
                .release().build().perform();

        Assert.assertEquals(1, select2.getOptions().size());
        Assert.assertEquals(3, select1.getOptions().size());
    }

    public void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    private WebElement focusedElement() {
        return getDriver().switchTo().activeElement();
    }
}