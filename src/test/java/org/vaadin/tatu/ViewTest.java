package org.vaadin.tatu;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.testbench.unit.UIUnit4Test;

public class ViewTest extends UIUnit4Test {

    @Test
    public void sorting() {
        navigate(View.class);
        test($(Button.class).withCaption("Set").first()).click();
        test($(Checkbox.class).withCaption("Sorting").first()).click();

        List<Checkbox> options = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first()).all();

        // List is now alphabetically ordered
        Assert.assertEquals("Eight", options.get(0).getLabel());
        Assert.assertEquals("Five", options.get(1).getLabel());
        Assert.assertEquals("Four", options.get(2).getLabel());
        Assert.assertEquals("Nine", options.get(3).getLabel());
        Assert.assertEquals("One", options.get(4).getLabel());
        Assert.assertEquals("Seven", options.get(5).getLabel());
        Assert.assertEquals("Six", options.get(6).getLabel());
        Assert.assertEquals("Ten", options.get(7).getLabel());
        Assert.assertEquals("Three", options.get(8).getLabel());
        Assert.assertEquals("Two", options.get(9).getLabel());

        // Pick items
        test($(Checkbox.class).withCaption("Two").first()).click();
        test($(Button.class).atIndex(2)).click();
        test($(Checkbox.class).withCaption("Four").first()).click();
        test($(Button.class).atIndex(2)).click();

        // Selection follows the order
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Four,Two selected!", value);

        List<Checkbox> selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first())
                        .withValue(true).all();
        Assert.assertEquals("Four", selected.get(0).getLabel());
        Assert.assertEquals("Two", selected.get(1).getLabel());
    }

    @Test
    public void paintSelected() {
        navigate(View.class);
        test($(Button.class).withCaption("Set").first()).click();

        List<Checkbox> options = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first())
                        .withValue(true).all();
        Assert.assertEquals(0, options.size());

        // Pick items
        test($(Checkbox.class).withCaption("Two").first()).click();
        test($(Checkbox.class).withCaption("Four").first()).click();
        test($(Button.class).atIndex(2)).click();

        List<Checkbox> selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first())
                        .withValue(true).all();
        Assert.assertEquals(2, selected.size());

        test($(Button.class).atIndex(5)).click();
        options = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first())
                        .withValue(true).all();
        Assert.assertEquals(8, options.size());

        selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first())
                        .withValue(true).all();
        Assert.assertEquals(0, selected.size());
    }

    @Test
    public void initialState() {
        navigate(View.class);

        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        // Assert that label has correct text
        Assert.assertEquals("Select Two and Four",
                test($(Label.class).withClassName("twincolselect-label-styles")
                        .first()).getText());

        // Assert that button states are correct
        Assert.assertFalse(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());

        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        Assert.assertEquals("Item count: 10",
                test($(Notification.class).last()).getText());

        // Assert that options have 10 items
        Assert.assertEquals(10, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        // Assert button states
        Assert.assertTrue(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void readOnlyState() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Put the TwinColSelect in read only state
        test($(Button.class).withCaption("Read only").first()).click();

        // Assert that options are inert
        List<Checkbox> options = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first()).all();
        options.forEach(box -> Assert.assertFalse(test(box).isUsable()));

        // Assert that buttons are inert
        Assert.assertFalse(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(5)).isUsable());

        // Put the TwinColSelect back in normal state
        test($(Button.class).withCaption("Read only").first()).click();

        // Assert that options are selectable
        options.forEach(box -> Assert.assertTrue(test(box).isUsable()));

        // Assert that button states are correct
        Assert.assertTrue(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void selectAll_deselectAll() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        Assert.assertTrue(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());

        test($(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);

        // Options is empty and value has 10 items
        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(10, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
        List<Checkbox> selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first())
                        .withValue(true).all();
        Assert.assertEquals(0, selected.size());

        // Assert button states are correct
        Assert.assertFalse(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());

        // De-select
        test($(Button.class).atIndex(4)).click();

        // Assert value is empty
        Assert.assertEquals(10, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        // Assert button states are correct
        Assert.assertTrue(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertFalse(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());

        // Assert that error text is correct as the field was requited
        Div errorLabel = $(Div.class)
                .withAttribute("class", "twincolselect-errorlabel").first();
        Assert.assertEquals("Empty selection not allowed",
                test(errorLabel).getText());
    }

    @Test
    public void selectAll_deselectOne() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        test($(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);

        // Options is empty and value has 10 items
        Assert.assertEquals(0, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(10, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        test($(Checkbox.class).withCaption("One").first()).click();
        test($(Button.class).atIndex(3)).click();

        // Assert value has nine items
        Assert.assertEquals(1, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(9, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(
                "Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);
        List<Checkbox> tickedOptions = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first())
                        .withValue(true).all();
        Assert.assertEquals(1, tickedOptions.size());
    }

    @Test
    public void filteringItems() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Set the filter to be "T"
        test($(TextField.class).withCaption("Filter").first()).setValue("T");
        Assert.assertEquals("Item count: 3",
                test($(Notification.class).last()).getText());

        List<Checkbox> filtered = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first()).all();

        // Assert that we have right values, i.e. ones startign with "T"
        Assert.assertEquals("Two", filtered.get(0).getLabel());
        Assert.assertEquals("Three", filtered.get(1).getLabel());
        Assert.assertEquals("Ten", filtered.get(2).getLabel());

        // Select all three
        test($(Button.class).atIndex(1)).click();
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Two,Three,Ten selected!", value);

        // Clear filter
        test($(TextField.class).withCaption("Filter").first()).setValue("");
        Assert.assertEquals("Item count: 10",
                test($(Notification.class).last()).getText());
        // Options has 7 and value has 3 items
        Assert.assertEquals(7, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

    }

    @Test
    public void errorLabelIsShown() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Pick items
        test($(Checkbox.class).withCaption("One").first()).click();
        test($(Checkbox.class).withCaption("Three").first()).click();
        test($(Button.class).atIndex(2)).click();

        // Assert the error label as the selection does not match validator
        Div errorLabel = $(Div.class)
                .withAttribute("class", "twincolselect-errorlabel").first();
        Assert.assertEquals("Selection needs to contain two and four",
                test(errorLabel).getText());

        // Pick items
        test($(Checkbox.class).withCaption("Two").first()).click();
        test($(Checkbox.class).withCaption("Four").first()).click();
        test($(Button.class).atIndex(2)).click();

        // Assert that error label is not visible as validator passes
        Assert.assertFalse(errorLabel.isVisible());
    }

    @Test
    public void selectionOrderIsPreserved() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Pick three items one at the time
        test($(Checkbox.class).withCaption("Five").first()).click();
        test($(Button.class).atIndex(2)).click();
        test($(Checkbox.class).withCaption("Two").first()).click();
        test($(Button.class).atIndex(2)).click();
        test($(Checkbox.class).withCaption("Four").first()).click();
        test($(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out in correct order
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Five,Two,Four selected!", value);

        // Find the checkboxes from the target list and assert their labels are
        // in assumed order
        List<Checkbox> selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first())
                        .withValue(true).all();
        Assert.assertEquals("Five", selected.get(0).getLabel());
        Assert.assertEquals("Two", selected.get(1).getLabel());
        Assert.assertEquals("Four", selected.get(2).getLabel());
    }

    @Test
    public void selectionOrderIsPreserved_clear_singleClick() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        test($(Select.class).first()).selectItem("SINGLE");

        // Pick three items one at the time
        test($(Checkbox.class).withCaption("Five").first()).click();
        String value = test($(Span.class).id("value")).getText();
        // Assert that span is containing the value
        Assert.assertEquals("Five selected!", value);
        test($(Checkbox.class).withCaption("Two").first()).click();
        // Assert that span containing the value prints out in correct order
        value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Five,Two selected!", value);
        test($(Checkbox.class).withCaption("Four").first()).click();
        // Assert that span containing the value prints out in correct order
        value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Five,Two,Four selected!", value);

        // Find the checkboxes from the target list and assert their labels are
        // in assumed order
        List<Checkbox> selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first()).all();
        Assert.assertEquals("Five", selected.get(0).getLabel());
        Assert.assertEquals("Two", selected.get(1).getLabel());
        Assert.assertEquals("Four", selected.get(2).getLabel());

        // Move Two back to options, and re-assert
        test($(Checkbox.class).withCaption("Two").first()).click();
        value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Five,Four selected!", value);
        selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first()).all();
        Assert.assertEquals("Five", selected.get(0).getLabel());
        Assert.assertEquals("Four", selected.get(1).getLabel());

        // Clear and assert
        test($(Button.class).withCaption("Clear").first()).click();
        selected = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("value").first()).all();
        value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(" selected!", value);
        Assert.assertEquals(0, selected.size());
    }

    @Test
    public void clearTicks() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Tick items
        test($(Checkbox.class).withCaption("Two").first()).click();
        test($(Checkbox.class).withCaption("Four").first()).click();

        List<Checkbox> ticked = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first())
                        .withValue(true).all();

        Assert.assertEquals(2, ticked.size());

        test($(Button.class).withCaption("Clear Ticks (BOTH)").first()).click();

        ticked = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first())
                        .withValue(true).all();

        Assert.assertEquals(0, ticked.size());
    }

    @Test
    public void selectOne() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        test($(Checkbox.class).withCaption("One").first()).click();
        test($(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out in correct order
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("One selected!", value);

        // Assert button states are correct
        Assert.assertTrue(test($(Button.class).atIndex(1)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(2)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(3)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(4)).isUsable());
        Assert.assertTrue(test($(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void addRefresh() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        test($(Select.class).first()).selectItem("SINGLE");
        Assert.assertEquals("Item count: 10",
                test($(Notification.class).last()).getText());

        test($(Button.class).withCaption("Add/Refresh").first()).click();
        Assert.assertEquals("Item count: 11",
                test($(Notification.class).last()).getText());
        test($(Checkbox.class).withCaption("New 1").first()).click();

        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("New 1 selected!", value);
    }

    @Test
    public void selectNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        test($(Button.class).withCaption("Disable nine").first()).click();

        // Check "Nine" is disabled
        Assert.assertFalse(
                test($(Checkbox.class).withCaption("Nine").first()).isUsable());

        test($(Button.class).withCaption("Select").first()).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Eight,Nine,Ten selected!", value);

        Assert.assertEquals(7, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
    }

    @Test
    public void selectAllNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        test($(Button.class).withCaption("Disable nine").first()).click();

        test($(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Ten selected!", value);

        // Options has one and value has 9 items
        Assert.assertEquals(1, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(9, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
    }

    @Test
    public void paintAllNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();
        test($(Button.class).withCaption("Disable nine").first()).click();

        test($(Button.class).atIndex(5)).click();
        test($(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Ten selected!", value);

        // Options has one and value has 9 items
        Assert.assertEquals(1, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(9, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
    }

    @Test
    public void programmaticSelectionResetsFilter() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        // Set the filter to be "T"
        test($(TextField.class).withCaption("Filter").first()).setValue("T");

        // Do programmatic select
        test($(Button.class).withCaption("Select").first()).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Eight,Nine,Ten selected!", value);

        Assert.assertEquals(7, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
    }

    @Test
    public void selectItemsAndFilter() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test($(Button.class).withCaption("Set").first()).click();

        test($(Checkbox.class).withCaption("Eight").first()).click();
        test($(Checkbox.class).withCaption("Nine").first()).click();
        test($(Checkbox.class).withCaption("Ten").first()).click();

        test($(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out right value
        String value = test($(Span.class).id("value")).getText();
        Assert.assertEquals("Eight,Nine,Ten selected!", value);

        Assert.assertEquals(7, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        // Set the filter to be "T"
        test($(TextField.class).withCaption("Filter").first()).setValue("T");

        List<Checkbox> filtered = $(Checkbox.class,
                $(VerticalLayout.class).withClassName("options").first()).all();

        // Assert that we have right values, i.e. ones starting with "T", except
        // "Ten" which is selected
        Assert.assertEquals("Two", filtered.get(0).getLabel());
        Assert.assertEquals("Three", filtered.get(1).getLabel());

        Assert.assertEquals(2, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());

        // Set the filter to be "T"
        test($(TextField.class).withCaption("Filter").first()).setValue("");

        Assert.assertEquals(7, $(VerticalLayout.class).withClassName("options")
                .first().getComponentCount());
        Assert.assertEquals(3, $(VerticalLayout.class).withClassName("value")
                .first().getComponentCount());
    }

    @Override
    public boolean printTree() {
        return true;
    }
}
