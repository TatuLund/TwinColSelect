package org.vaadin.tatu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.vaadin.tatu.TwinColSelect.TwinColSelectI18n;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ThemeList;

public class TwinColSelectTest {

    @Test
    public void preserveOrderTest() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");

        Set<String> value = null;
        List<String> list1 = null;

        select.select("Eight");
        select.select("Two");
        select.select("Four");
        value = select.getValue();
        list1 = value.stream().toList();
        assertEquals("Eight", list1.get(0));
        assertEquals("Two", list1.get(1));
        assertEquals("Four", list1.get(2));
        select.clear();

        Set<String> list2 = new LinkedHashSet<>(
                Arrays.asList("Eight", "Two", "Four").stream().toList());
        select.setValue(list2);
        value = select.getValue();
        list1 = value.stream().toList();
        assertEquals("Eight", list1.get(0));
        assertEquals("Two", list1.get(1));
        assertEquals("Four", list1.get(2));
        select.clear();

        select.select("Eight", "Two", "Four");
        value = select.getValue();
        list1 = value.stream().toList();
        assertEquals("Eight", list1.get(0));
        assertEquals("Two", list1.get(1));
        assertEquals("Four", list1.get(2));

    }

    @Test
    public void dataView() {
        TwinColSelect<String> select = new TwinColSelect<>();
        TwinColSelectListDataView<String> dataView = select.setItems("One",
                "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                "Ten");

        assertEquals(10, dataView.getItemCount());
        assertEquals("One", dataView.getItem(0));
        assertEquals("Two", dataView.getItem(1));
        assertEquals("Three", dataView.getItem(2));

        assertEquals("One", select.getGenericDataView().getItem(0));
        assertEquals("Two", select.getGenericDataView().getItem(1));
        assertEquals("Three", select.getGenericDataView().getItem(2));

        dataView.setFilter(item -> item.startsWith("T"));
        assertEquals(3, dataView.getItemCount());
        assertEquals("Two", dataView.getItem(0));
        assertEquals("Three", dataView.getItem(1));
        assertEquals("Ten", dataView.getItem(2));

        assertEquals(3, select.getGenericDataView().getItems().count());
        assertEquals("Two", select.getGenericDataView().getItem(0));
        assertEquals("Three", select.getGenericDataView().getItem(1));
        assertEquals("Ten", select.getGenericDataView().getItem(2));

        dataView.setFilter(null);
        assertEquals(10, dataView.getItemCount());
        assertEquals("One", dataView.getItem(0));
        assertEquals("Two", dataView.getItem(1));
        assertEquals("Three", dataView.getItem(2));
    }

    @Test
    public void markRange() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");

        for (int i = 0; i < 10; i++) {
            Component checkbox = select.list1.getComponentAt(i);
            assertFalse(checkbox.getElement().getProperty("value", false));
        }

        Component from = select.list1.getChildren().skip(2).findFirst().get();
        Component to = select.list1.getChildren().skip(5).findFirst().get();
        select.markRange(select.list1, from, to);

        for (int i = 2; i < 5; i++) {
            Component checkbox = select.list1.getComponentAt(i);
            assertTrue(checkbox.getElement().getProperty("value", false));
        }
    }

    @Test
    public void ariaAttributes() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three");

        // Assert roles, and right initial values of the options list
        Element options = select.getElement().getChild(1).getChild(0);
        assertEquals(options.getAttribute("aria-describedby"),
                select.getElement().getChild(0).getAttribute("id"));
        assertEquals("listbox", options.getAttribute("role"));
        assertEquals("Options", options.getAttribute("aria-label"));
        assertEquals("0", options.getAttribute("tabindex"));
        assertEquals("option", options.getChild(0).getAttribute("role"));
        assertEquals("false",
                options.getChild(0).getAttribute("aria-selected"));
        assertEquals("option", options.getChild(1).getAttribute("role"));
        assertEquals("false",
                options.getChild(0).getAttribute("aria-selected"));
        assertEquals("option", options.getChild(2).getAttribute("role"));
        assertEquals("false",
                options.getChild(0).getAttribute("aria-selected"));
        assertEquals(null, options.getAttribute("aria-invalid"));

        // Assert roles, and right initial values of the selection list
        Element selection = select.getElement().getChild(1).getChild(2);
        assertEquals("listbox", selection.getAttribute("role"));
        assertEquals("Selected", selection.getAttribute("aria-label"));
        assertEquals("0", selection.getAttribute("tabindex"));
        assertEquals(null, selection.getAttribute("aria-invalid"));
        assertEquals("assertive", selection.getAttribute("aria-live"));

        // Assert error label initial state
        assertEquals("alert",
                select.getElement().getChild(2).getAttribute("role"));
        assertFalse(select.getElement().getChild(2).isVisible());

        // Set required
        select.setRequiredIndicatorVisible(true);
        assertEquals("1", select.getElement().getChild(0).getStyle()
                .get("--tcs-required-dot-opacity"));
        assertEquals("true", options.getAttribute("aria-required"));
        select.setRequiredIndicatorVisible(false);
        assertEquals("0", select.getElement().getChild(0).getStyle()
                .get("--tcs-required-dot-opacity"));
        assertEquals(null, options.getAttribute("aria-required"));

        // Set component invalid assert attributes
        select.setInvalid(true);
        assertEquals("true", options.getAttribute("aria-invalid"));
        assertEquals("true", selection.getAttribute("aria-invalid"));

        // Set error message, should be visible
        select.setErrorMessage("error message");
        assertEquals(selection.getAttribute("aria-describedby"),
                select.getElement().getChild(2).getAttribute("id"));
        assertEquals("error message",
                select.getElement().getChild(2).getText());
        assertTrue(select.getElement().getChild(2).isVisible());

        // Turn component back valid, assert attribute updates
        select.setInvalid(false);
        assertEquals(null, options.getAttribute("aria-invalid"));
        assertEquals(null, selection.getAttribute("aria-invalid"));
        assertFalse(select.getElement().getChild(2).isVisible());

        // Test that
        select.setReadOnly(true);
        assertEquals("list", options.getAttribute("role"));
        assertEquals("list", selection.getAttribute("role"));
    }

    @Test
    public void setItemsAndValueDomBehavior() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three");

        // Set label
        select.setLabel("A label");
        assertEquals("A label", select.getLabel());
        assertEquals("A label", select.getElement().getChild(0).getText());
        assertTrue(select.getElement().getChild(0).isVisible());

        // Remove label
        select.setLabel(null);
        assertFalse(select.getElement().getChild(0).isVisible());

        Element options = select.getElement().getChild(1).getChild(0);
        assertEquals(3, options.getChildCount());
        assertEquals("One", options.getChild(0).getText());
        assertEquals("Two", options.getChild(1).getText());
        assertEquals("Three", options.getChild(2).getText());

        Element selection = select.getElement().getChild(1).getChild(2);
        assertEquals(0, selection.getChildCount());

        Set<String> value = Set.of("One");
        select.setValue(value);
        assertEquals(value, select.getValue());

        assertEquals(1, selection.getChildCount());
        assertEquals(2, options.getChildCount());
        assertEquals("One", selection.getChild(0).getText());

        select.select("Two");
        assertEquals(Set.of("One", "Two"), select.getValue());

        assertEquals(2, selection.getChildCount());
        assertEquals(1, options.getChildCount());
        assertEquals("Two", selection.getChild(0).getText());
    }

    @Test
    public void selectionEvent() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItemLabelGenerator(String::toUpperCase);
        select.setItems("one", "two", "three");

        AtomicInteger count = new AtomicInteger(0);
        Set<String> value = new HashSet<>();
        select.addSelectionListener(e -> {
            count.addAndGet(1);
            value.addAll(e.getValue());
        });

        select.select("two");
        assertEquals(1, count.get());
        assertEquals(1, value.size());
        assertTrue(value.contains("two"));
    }

    @Test
    public void itemLabelGenerator() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItemLabelGenerator(String::toUpperCase);
        select.setItems("one", "two", "three");

        Element options = select.getElement().getChild(1).getChild(0);
        assertEquals(3, options.getChildCount());
        assertEquals("ONE", options.getChild(0).getText());
        assertEquals("TWO", options.getChild(1).getText());
        assertEquals("THREE", options.getChild(2).getText());

        select.select("two");

        Element value = select.getElement().getChild(1).getChild(2);
        assertEquals(1, value.getChildCount());
        assertEquals("TWO", value.getChild(0).getText());
    }

    @Test
    public void twinColSelectSerializable() throws IOException {
        TwinColSelect<String> select = new TwinColSelect<>();
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(select);
    }

    @Test
    public void twinColSelectI18nSerializable() throws IOException {
        TwinColSelectI18n selectI18n = TwinColSelectI18n.getDefault();
        new ObjectOutputStream(new ByteArrayOutputStream())
                .writeObject(selectI18n);
    }

    @Test
    public void addThemeVariant_themeNamesContainsThemeVariant() {
        TwinColSelect select = new TwinColSelect();
        select.addThemeVariants(TwinColSelectVariant.VERTICAL);

        ThemeList themeNames = select.getThemeNames();
        assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
    }

    @Test
    public void addThemeVariant_removeThemeVariant_themeNamesDoesNotContainThemeVariant() {
        TwinColSelect select = new TwinColSelect();
        select.addThemeVariants(TwinColSelectVariant.VERTICAL);
        select.addThemeVariants(TwinColSelectVariant.NO_BUTTONS);
        ThemeList themeNames = select.getThemeNames();
        assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
        themeNames = select.getThemeNames();
        assertTrue(themeNames
                .contains(TwinColSelectVariant.NO_BUTTONS.getVariantName()));
        select.removeThemeVariants(TwinColSelectVariant.NO_BUTTONS);

        themeNames = select.getThemeNames();
        assertFalse(themeNames
                .contains(TwinColSelectVariant.NO_BUTTONS.getVariantName()));
        assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
    }

    @Test
    public void refreshItem() {
        TwinColSelect<TestItem> select = new TwinColSelect<>();
        Stream<TestItem> items = Arrays.asList("One", "Two", "Three").stream()
                .map(TestItem::new);
        select.setItems(items);
        select.setItemLabelGenerator(TestItem::getData);
        select.getGenericDataView().setIdentifierProvider(TestItem::getId);

        Element options = select.getElement().getChild(1).getChild(0);
        assertEquals(3, options.getChildCount());
        assertEquals("One", options.getChild(0).getText());
        assertEquals("Two", options.getChild(1).getText());
        assertEquals("Three", options.getChild(2).getText());

        TestItem item = select.getGenericDataView().getItem(0);
        item.setData("Zero");
        select.getGenericDataView().refreshItem(item);
        assertEquals("Zero", options.getChild(0).getText());
    }

    public class TestItem {
        private UUID id = UUID.randomUUID();
        private String data;

        public TestItem(String data) {
            this.setData(data);
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }
}
