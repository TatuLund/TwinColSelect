package org.vaadin.tatu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

public class ViewTest extends BrowserlessTest {

    @Test
    public void sorting() {
        navigate(View.class);
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Checkbox.class).withCaption("Sorting").single()).click();

        List<SelectItem> options = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .all();

        // List is now alphabetically ordered
        assertEquals("Eight", options.get(0).getLabel());
        assertEquals("Five", options.get(1).getLabel());
        assertEquals("Four", options.get(2).getLabel());
        assertEquals("Nine", options.get(3).getLabel());
        assertEquals("One", options.get(4).getLabel());
        assertEquals("Seven", options.get(5).getLabel());
        assertEquals("Six", options.get(6).getLabel());
        assertEquals("Ten", options.get(7).getLabel());
        assertEquals("Three", options.get(8).getLabel());
        assertEquals("Two", options.get(9).getLabel());

        // Pick items
        find(SelectItem.class).withText("Two").single().click();
        test(find(Button.class).atIndex(2)).click();
        find(SelectItem.class).withText("Four").single().click();
        test(find(Button.class).atIndex(2)).click();

        // Selection follows the order
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Four,Two selected!", value);

        List<SelectItem> selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .withValue(true).all();
        assertEquals("Four", selected.get(0).getLabel());
        assertEquals("Two", selected.get(1).getLabel());
    }

    @Test
    public void paintSelected() {
        navigate(View.class);
        test(find(Button.class).withCaption("Set").single()).click();

        List<SelectItem> options = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .withValue(true).all();
        assertEquals(0, options.size());

        // Pick items
        find(SelectItem.class).withText("Two").single().click();
        find(SelectItem.class).withText("Four").single().click();
        test(find(Button.class).atIndex(2)).click();

        List<SelectItem> selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .withValue(true).all();
        assertEquals(2, selected.size());

        test(find(Button.class).atIndex(5)).click();
        options = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .withValue(true).all();
        assertEquals(8, options.size());

        selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .withValue(true).all();
        assertEquals(0, selected.size());
    }

    @Test
    public void initialState() {
        navigate(View.class);

        assertEquals(0, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(0, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        // Assert that label has correct text
        NativeLabel label = find(NativeLabel.class)
                .withClassName("twincolselect-label-styles").single();
        assertEquals("Select Two and Four", test(label).getText());

        // Assert that button states are correct
        assertFalse(test(find(Button.class).atIndex(1)).isUsable());
        assertFalse(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());

        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        assertEquals("Item count: 10",
                test(find(Notification.class).last()).getText());

        // Assert that options have 10 items
        assertEquals(10, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(0, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        // Assert button states
        assertTrue(test(find(Button.class).atIndex(1)).isUsable());
        assertTrue(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void readOnlyState() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Put the TwinColSelect in read only state
        test(find(Button.class).withCaption("Read only").single()).click();

        // Assert that options are inert
        List<Checkbox> options = find(Checkbox.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .all();
        options.forEach(box -> assertFalse(test(box).isUsable()));

        // Assert that buttons are inert
        assertFalse(test(find(Button.class).atIndex(1)).isUsable());
        assertFalse(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertFalse(test(find(Button.class).atIndex(5)).isUsable());

        // Put the TwinColSelect back in normal state
        test(find(Button.class).withCaption("Read only").single()).click();

        // Assert that options are selectable
        options.forEach(box -> assertTrue(test(box).isUsable()));

        // Assert that button states are correct
        assertTrue(test(find(Button.class).atIndex(1)).isUsable());
        assertTrue(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void selectAll_deselectAll() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        assertTrue(test(find(Button.class).atIndex(1)).isUsable());
        assertTrue(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());

        test(find(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);

        // Options is empty and value has 10 items
        assertEquals(0, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(10, find(VerticalLayout.class)
                .withClassName("value").single().getComponentCount());
        List<Checkbox> selected = find(Checkbox.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .withValue(true).all();
        assertEquals(0, selected.size());

        // Assert button states are correct
        assertFalse(test(find(Button.class).atIndex(1)).isUsable());
        assertFalse(test(find(Button.class).atIndex(2)).isUsable());
        assertTrue(test(find(Button.class).atIndex(3)).isUsable());
        assertTrue(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());

        // De-select
        test(find(Button.class).atIndex(4)).click();

        // Assert value is empty
        assertEquals(10, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(0, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        // Assert button states are correct
        assertTrue(test(find(Button.class).atIndex(1)).isUsable());
        assertTrue(test(find(Button.class).atIndex(2)).isUsable());
        assertFalse(test(find(Button.class).atIndex(3)).isUsable());
        assertFalse(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());

        // Assert that error text is correct as the field was requited
        Div errorLabel = find(Div.class)
                .withAttribute("class", "twincolselect-errorlabel").single();
        assertEquals("Empty selection not allowed",
                test(errorLabel).getText());
    }

    @Test
    public void selectAll_deselectOne() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        test(find(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);

        // Options is empty and value has 10 items
        assertEquals(0, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(10, find(VerticalLayout.class)
                .withClassName("value").single().getComponentCount());

        find(SelectItem.class).withText("One").single().click();
        test(find(Button.class).atIndex(3)).click();

        // Assert value has nine items
        assertEquals(1, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(9, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        value = test(find(Span.class).id("value")).getText();
        assertEquals(
                "Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten selected!",
                value);
        List<SelectItem> tickedOptions = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .withValue(true).all();
        assertEquals(1, tickedOptions.size());
    }

    @Test
    public void filteringItems() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Set the filter to be "T"
        test(find(TextField.class).withCaption("Filter").single())
                .setValue("T");
        assertEquals("Item count: 3",
                test(find(Notification.class).last()).getText());

        List<SelectItem> filtered = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .all();

        // Assert that we have right values, i.e. ones startign with "T"
        assertEquals("Two", filtered.get(0).getLabel());
        assertEquals("Three", filtered.get(1).getLabel());
        assertEquals("Ten", filtered.get(2).getLabel());

        // Select all three
        test(find(Button.class).atIndex(1)).click();
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Two,Three,Ten selected!", value);

        // Clear filter
        test(find(TextField.class).withCaption("Filter").single()).setValue("");
        assertEquals("Item count: 10",
                test(find(Notification.class).last()).getText());
        // Options has 7 and value has 3 items
        assertEquals(7, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

    }

    @Test
    public void errorLabelIsShown() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Pick items
        find(SelectItem.class).withText("One").single().click();
        find(SelectItem.class).withText("Three").single().click();
        test(find(Button.class).atIndex(2)).click();

        // Assert the error label as the selection does not match validator
        Div errorLabel = find(Div.class)
                .withAttribute("class", "twincolselect-errorlabel").single();
        assertEquals("Selection needs to contain two and four",
                test(errorLabel).getText());

        // Pick items
        find(SelectItem.class).withText("Two").single().click();
        find(SelectItem.class).withText("Four").single().click();
        test(find(Button.class).atIndex(2)).click();

        // Assert that error label is not visible as validator passes
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void selectionOrderIsPreserved() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Pick three items one at the time
        find(SelectItem.class).withText("Five").single().click();
        test(find(Button.class).atIndex(2)).click();
        find(SelectItem.class).withText("Two").single().click();
        test(find(Button.class).atIndex(2)).click();
        find(SelectItem.class).withText("Four").single().click();
        test(find(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out in correct order
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Five,Two,Four selected!", value);

        // Find the checkboxes from the target list and assert their labels are
        // in assumed order
        List<SelectItem> selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .withValue(true).all();
        assertEquals("Five", selected.get(0).getLabel());
        assertEquals("Two", selected.get(1).getLabel());
        assertEquals("Four", selected.get(2).getLabel());
    }

    @Test
    public void selectionOrderIsPreserved_clear_singleClick() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Select.class).single()).selectItem("SINGLE");

        // Pick three items one at the time
        find(SelectItem.class).withText("Five").single().click();
        String value = test(find(Span.class).id("value")).getText();
        // Assert that span is containing the value
        assertEquals("Five selected!", value);
        find(SelectItem.class).withText("Two").single().click();
        // Assert that span containing the value prints out in correct order
        value = test(find(Span.class).id("value")).getText();
        assertEquals("Five,Two selected!", value);
        find(SelectItem.class).withText("Four").single().click();
        // Assert that span containing the value prints out in correct order
        value = test(find(Span.class).id("value")).getText();
        assertEquals("Five,Two,Four selected!", value);

        // Find the checkboxes from the target list and assert their labels are
        // in assumed order
        List<SelectItem> selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .all();
        assertEquals("Five", selected.get(0).getLabel());
        assertEquals("Two", selected.get(1).getLabel());
        assertEquals("Four", selected.get(2).getLabel());

        test(find(Button.class).withCaption("Clear Ticks (BOTH)").single())
                .click();

        // Move Two back to options, and re-assert
        find(SelectItem.class).withText("Two").single().click();
        value = test(find(Span.class).id("value")).getText();
        assertEquals("Five,Four selected!", value);
        selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .all();
        assertEquals("Five", selected.get(0).getLabel());
        assertEquals("Four", selected.get(1).getLabel());

        // Clear and assert
        test(find(Button.class).withCaption("Clear").single()).click();
        selected = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("value").single())
                        .all();
        value = test(find(Span.class).id("value")).getText();
        assertEquals(" selected!", value);
        assertEquals(0, selected.size());
    }

    @Test
    public void clearTicks() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Tick items
        find(SelectItem.class).withText("Two").single().click();
        find(SelectItem.class).withText("Four").single().click();

        List<SelectItem> ticked = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .withValue(true).all();

        assertEquals(2, ticked.size());

        test(find(Button.class).withCaption("Clear Ticks (BOTH)").single())
                .click();

        ticked = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .withValue(true).all();

        assertEquals(0, ticked.size());
    }

    @Test
    public void selectOne() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        find(SelectItem.class).withText("One").single().click();
        test(find(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out in correct order
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("One selected!", value);

        // Assert button states are correct
        assertTrue(test(find(Button.class).atIndex(1)).isUsable());
        assertTrue(test(find(Button.class).atIndex(2)).isUsable());
        assertTrue(test(find(Button.class).atIndex(3)).isUsable());
        assertTrue(test(find(Button.class).atIndex(4)).isUsable());
        assertTrue(test(find(Button.class).atIndex(5)).isUsable());
    }

    @Test
    public void addRefresh() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Select.class).single()).selectItem("SINGLE");
        assertEquals("Item count: 10",
                test(find(Notification.class).last()).getText());

        test(find(Button.class).withCaption("Add/Refresh").single()).click();
        assertEquals("Item count: 11",
                test(find(Notification.class).last()).getText());
        find(SelectItem.class).withText("New 1").single().click();

        String value = test(find(Span.class).id("value")).getText();
        assertEquals("New 1 selected!", value);
    }

    @Test
    public void selectNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Button.class).withCaption("Disable nine").single()).click();

        // Check "Nine" is disabled
        assertFalse(
                find(SelectItem.class).withText("Nine").single().isEnabled());

        test(find(Button.class).withCaption("Select").single()).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Eight,Nine,Ten selected!", value);

        assertEquals(7, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());
    }

    @Test
    public void selectAllNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Button.class).withCaption("Disable nine").single()).click();

        test(find(Button.class).atIndex(1)).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Ten selected!", value);

        // Options has one and value has 9 items
        assertEquals(1, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(9, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());
    }

    @Test
    public void paintAllNineDisabled() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();
        test(find(Button.class).withCaption("Disable nine").single()).click();

        test(find(Button.class).atIndex(5)).click();
        test(find(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals(
                "One,Two,Three,Four,Five,Six,Seven,Eight,Ten selected!", value);

        // Options has one and value has 9 items
        assertEquals(1, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(9, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());
    }

    @Test
    public void programmaticSelectionResetsFilter() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        // Set the filter to be "T"
        test(find(TextField.class).withCaption("Filter").single())
                .setValue("T");

        // Do programmatic select
        test(find(Button.class).withCaption("Select").single()).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Eight,Nine,Ten selected!", value);

        assertEquals(7, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());
    }

    @Test
    public void selectItemsAndFilter() {
        navigate(View.class);
        // Populate the TwinColSelect by clicking set button
        test(find(Button.class).withCaption("Set").single()).click();

        find(SelectItem.class).withText("Eight").single().click();
        find(SelectItem.class).withText("Nine").single().click();
        find(SelectItem.class).withText("Ten").single().click();

        test(find(Button.class).atIndex(2)).click();

        // Assert that span containing the value prints out right value
        String value = test(find(Span.class).id("value")).getText();
        assertEquals("Eight,Nine,Ten selected!", value);

        assertEquals(7, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        // Set the filter to be "T"
        test(find(TextField.class).withCaption("Filter").single())
                .setValue("T");

        List<SelectItem> filtered = find(SelectItem.class,
                find(VerticalLayout.class).withClassName("options").single())
                        .all();

        // Assert that we have right values, i.e. ones starting with "T", except
        // "Ten" which is selected
        assertEquals("Two", filtered.get(0).getLabel());
        assertEquals("Three", filtered.get(1).getLabel());

        assertEquals(2, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());

        // Set the filter to be "T"
        test(find(TextField.class).withCaption("Filter").single()).setValue("");

        assertEquals(7, find(VerticalLayout.class)
                .withClassName("options").single().getComponentCount());
        assertEquals(3, find(VerticalLayout.class).withClassName("value")
                .single().getComponentCount());
    }
}
