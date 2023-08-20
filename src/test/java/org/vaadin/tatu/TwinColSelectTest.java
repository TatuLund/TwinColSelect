package org.vaadin.tatu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.tatu.TwinColSelect.TwinColSelectI18n;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ThemeList;

public class TwinColSelectTest {

    private Set<String> value;
    private int count;

    @Test
    public void sorting() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");
        select.addValueChangeListener(e -> {
            value = e.getValue();
            count++;
        });
        ListDataProvider<String> dp = (ListDataProvider<String>) select
                .getDataProvider();
        dp.setSortComparator((a, b) -> a.compareTo(b));

        List<Checkbox> options = select.list1.getChildren()
                .map(c -> (Checkbox) c).collect(Collectors.toList());

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

        select.select("Two");
        select.select("Four");

        Assert.assertEquals(2, count);
        Assert.assertEquals(2, value.size());
        Assert.assertTrue(value.contains("Two"));
        Assert.assertTrue(value.contains("Four"));

        List<Checkbox> selected = select.list2.getChildren()
                .map(c -> (Checkbox) c).collect(Collectors.toList());

        Assert.assertEquals(8, select.list1.getComponentCount());
        Assert.assertEquals(2, select.list2.getComponentCount());

        Assert.assertEquals("Four", selected.get(0).getLabel());
        Assert.assertEquals("Two", selected.get(1).getLabel());
    }

    @Test
    public void filtering() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");
        ListDataProvider<String> dp = (ListDataProvider<String>) select
                .getDataProvider();
        dp.setFilter(item -> item.startsWith("T"));
        select.addValueChangeListener(e -> {
            value = e.getValue();
            count++;
        });

        List<Checkbox> filtered = select.list1.getChildren()
                .map(c -> (Checkbox) c).collect(Collectors.toList());

        // Assert that we have right values, i.e. ones startign with "T"
        Assert.assertEquals("Two", filtered.get(0).getLabel());
        Assert.assertEquals("Three", filtered.get(1).getLabel());
        Assert.assertEquals("Ten", filtered.get(2).getLabel());

        Button button = (Button) select.buttons.getComponentAt(0);
        button.click();
        
        Assert.assertEquals(1, count);
        Assert.assertEquals(3, value.size());
        Assert.assertTrue(value.contains("Two"));
        Assert.assertTrue(value.contains("Three"));
        Assert.assertTrue(value.contains("Ten"));

        Assert.assertEquals(0, select.list1.getComponentCount());
        Assert.assertEquals(3, select.list2.getComponentCount());
 
    }

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
        list1 = value.stream().collect(Collectors.toList());
        Assert.assertEquals("Eight", list1.get(0));
        Assert.assertEquals("Two", list1.get(1));
        Assert.assertEquals("Four", list1.get(2));
        select.clear();

        Set<String> list2 = new LinkedHashSet<>(
                Arrays.asList("Eight", "Two", "Four").stream()
                        .collect(Collectors.toList()));
        select.setValue(list2);
        value = select.getValue();
        list1 = value.stream().collect(Collectors.toList());
        Assert.assertEquals("Eight", list1.get(0));
        Assert.assertEquals("Two", list1.get(1));
        Assert.assertEquals("Four", list1.get(2));
        select.clear();

        select.select("Eight", "Two", "Four");
        value = select.getValue();
        list1 = value.stream().collect(Collectors.toList());
        Assert.assertEquals("Eight", list1.get(0));
        Assert.assertEquals("Two", list1.get(1));
        Assert.assertEquals("Four", list1.get(2));

    }

    @Test
    public void markRange() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");

        for (int i = 0; i < 10; i++) {
            Checkbox checkbox = (Checkbox) select.list1.getComponentAt(i);
            Assert.assertFalse(checkbox.getValue());
        }

        Component from = select.list1.getChildren().skip(2).findFirst().get();
        Component to = select.list1.getChildren().skip(5).findFirst().get();
        select.markRange(select.list1, from, to);

        for (int i = 2; i < 5; i++) {
            Checkbox checkbox = (Checkbox) select.list1.getComponentAt(i);
            Assert.assertTrue(checkbox.getValue());
        }
    }

    @Test
    public void ariaAttributes() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three");

        // Assert roles, and right initial values of the options list
        Element options = select.getElement().getChild(1).getChild(0);
        Assert.assertEquals(options.getAttribute("aria-describedby"),
                select.getElement().getChild(0).getAttribute("id"));
        Assert.assertEquals("listbox", options.getAttribute("role"));
        Assert.assertEquals("Options", options.getAttribute("aria-label"));
        Assert.assertEquals("0", options.getAttribute("tabindex"));
        Assert.assertEquals("listitem",
                options.getChild(0).getAttribute("role"));
        Assert.assertEquals("listitem",
                options.getChild(1).getAttribute("role"));
        Assert.assertEquals("listitem",
                options.getChild(2).getAttribute("role"));
        Assert.assertEquals(null, options.getAttribute("aria-invalid"));

        // Assert roles, and right initial values of the selection list
        Element selection = select.getElement().getChild(1).getChild(2);
        Assert.assertEquals("listbox", selection.getAttribute("role"));
        Assert.assertEquals("Selected", selection.getAttribute("aria-label"));
        Assert.assertEquals("0", selection.getAttribute("tabindex"));
        Assert.assertEquals(null, selection.getAttribute("aria-invalid"));
        Assert.assertEquals("assertive", selection.getAttribute("aria-live"));

        // Assert error label initial state
        Assert.assertEquals("alert",
                select.getElement().getChild(2).getAttribute("role"));
        Assert.assertFalse(select.getElement().getChild(2).isVisible());

        // Set required
        select.setRequiredIndicatorVisible(true);
        Assert.assertEquals("1", select.getElement().getChild(0).getStyle()
                .get("--tcs-required-dot-opacity"));
        Assert.assertEquals("true", options.getAttribute("aria-required"));
        select.setRequiredIndicatorVisible(false);
        Assert.assertEquals("0", select.getElement().getChild(0).getStyle()
                .get("--tcs-required-dot-opacity"));
        Assert.assertEquals(null, options.getAttribute("aria-required"));

        // Set component invalid assert attributes
        select.setInvalid(true);
        Assert.assertEquals("true", options.getAttribute("aria-invalid"));
        Assert.assertEquals("true", selection.getAttribute("aria-invalid"));

        // Set error message, should be visible
        select.setErrorMessage("error message");
        Assert.assertEquals(selection.getAttribute("aria-describedby"),
                select.getElement().getChild(2).getAttribute("id"));
        Assert.assertEquals("error message",
                select.getElement().getChild(2).getText());
        Assert.assertTrue(select.getElement().getChild(2).isVisible());

        // Turn component back valid, assert attribute updates
        select.setInvalid(false);
        Assert.assertEquals(null, options.getAttribute("aria-invalid"));
        Assert.assertEquals(null, selection.getAttribute("aria-invalid"));
        Assert.assertFalse(select.getElement().getChild(2).isVisible());

        // Test that
        select.setReadOnly(true);
        Assert.assertEquals("list", options.getAttribute("role"));
        Assert.assertEquals("list", selection.getAttribute("role"));
    }

    @Test
    public void setItemsAndValueDomBehavior() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three");

        // Set label
        select.setLabel("A label");
        Assert.assertEquals("A label", select.getLabel());
        Assert.assertEquals("A label",
                select.getElement().getChild(0).getText());
        Assert.assertTrue(select.getElement().getChild(0).isVisible());

        // Remove label
        select.setLabel(null);
        Assert.assertFalse(select.getElement().getChild(0).isVisible());

        Element options = select.getElement().getChild(1).getChild(0);
        Assert.assertEquals(3, options.getChildCount());
        Assert.assertEquals("One", options.getChild(0).getText());
        Assert.assertEquals("Two", options.getChild(1).getText());
        Assert.assertEquals("Three", options.getChild(2).getText());

        Element selection = select.getElement().getChild(1).getChild(2);
        Assert.assertEquals(0, selection.getChildCount());

        Set<String> value = Set.of("One");
        select.setValue(value);
        Assert.assertEquals(value, select.getValue());

        Assert.assertEquals(1, selection.getChildCount());
        Assert.assertEquals(2, options.getChildCount());
        Assert.assertEquals("One", selection.getChild(0).getText());

        select.select("Two");
        Assert.assertEquals(Set.of("One", "Two"), select.getValue());

        Assert.assertEquals(2, selection.getChildCount());
        Assert.assertEquals(1, options.getChildCount());
        Assert.assertEquals("Two", selection.getChild(0).getText());
    }

    @Test
    public void selectionEvent() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItemLabelGenerator(item -> item.toUpperCase());
        select.setItems("one", "two", "three");

        AtomicInteger count = new AtomicInteger(0);
        Set<String> value = new HashSet<>();
        select.addSelectionListener(e -> {
            count.addAndGet(1);
            value.addAll(e.getValue());
        });

        select.select("two");
        Assert.assertEquals(1, count.get());
        Assert.assertEquals(1, value.size());
        Assert.assertTrue(value.contains("two"));
    }

    @Test
    public void itemLabelGenerator() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItemLabelGenerator(item -> item.toUpperCase());
        select.setItems("one", "two", "three");

        Element options = select.getElement().getChild(1).getChild(0);
        Assert.assertEquals(3, options.getChildCount());
        Assert.assertEquals("ONE", options.getChild(0).getText());
        Assert.assertEquals("TWO", options.getChild(1).getText());
        Assert.assertEquals("THREE", options.getChild(2).getText());
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
        Assert.assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
    }

    @Test
    public void addThemeVariant_removeThemeVariant_themeNamesDoesNotContainThemeVariant() {
        TwinColSelect select = new TwinColSelect();
        select.addThemeVariants(TwinColSelectVariant.VERTICAL);
        select.addThemeVariants(TwinColSelectVariant.NO_BUTTONS);
        ThemeList themeNames = select.getThemeNames();
        Assert.assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
        themeNames = select.getThemeNames();
        Assert.assertTrue(themeNames
                .contains(TwinColSelectVariant.NO_BUTTONS.getVariantName()));
        select.removeThemeVariants(TwinColSelectVariant.NO_BUTTONS);

        themeNames = select.getThemeNames();
        Assert.assertFalse(themeNames
                .contains(TwinColSelectVariant.NO_BUTTONS.getVariantName()));
        Assert.assertTrue(themeNames
                .contains(TwinColSelectVariant.VERTICAL.getVariantName()));
    }

    @Test
    public void refreshItem() {
        TwinColSelect<TestItem> select = new TwinColSelect<>();
        List<TestItem> items = Arrays.asList("One", "Two", "Three").stream()
                .map(data -> new TestItem(data)).collect(Collectors.toList());
        TestDataProvider dataProvider = new TestDataProvider(items);

        select.setItems(items);
        select.setItemLabelGenerator(TestItem::getData);

        Element options = select.getElement().getChild(1).getChild(0);
        Assert.assertEquals(3, options.getChildCount());
        Assert.assertEquals("One", options.getChild(0).getText());
        Assert.assertEquals("Two", options.getChild(1).getText());
        Assert.assertEquals("Three", options.getChild(2).getText());

        TestItem item = items.get(0);
        item.setData("Zero");
        select.getDataProvider().refreshItem(item);
        Assert.assertEquals("Zero", options.getChild(0).getText());
    }

    public class TestDataProvider extends ListDataProvider<TestItem> {
        public TestDataProvider(Collection<TestItem> items) {
            super(items);
        }

        @Override
        public Object getId(TestItem item) {
            return item.getId();
        }
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