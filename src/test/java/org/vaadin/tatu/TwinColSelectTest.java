package org.vaadin.tatu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.compress.harmony.unpack200.SegmentOptions;
import org.junit.Assert;
import org.junit.Test;
import org.vaadin.tatu.TwinColSelect.TwinColSelectI18n;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
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

        Element selection = select.getElement().getChild(1).getChild(2);
        Assert.assertEquals("listbox", selection.getAttribute("role"));
        Assert.assertEquals("Selected", selection.getAttribute("aria-label"));
        Assert.assertEquals("0", selection.getAttribute("tabindex"));
        Assert.assertEquals(null, options.getAttribute("aria-invalid"));
        Assert.assertEquals(null, selection.getAttribute("aria-invalid"));
        Assert.assertEquals("assertive", selection.getAttribute("aria-live"));

        select.setReadOnly(true);
        Assert.assertEquals("list", options.getAttribute("role"));
        Assert.assertEquals("list", selection.getAttribute("role"));

        select.setInvalid(true);
        Assert.assertEquals("true", options.getAttribute("aria-invalid"));
        Assert.assertEquals("true", selection.getAttribute("aria-invalid"));

        select.setInvalid(false);
        Assert.assertEquals(null, options.getAttribute("aria-invalid"));
        Assert.assertEquals(null, selection.getAttribute("aria-invalid"));

        select.setErrorMessage("error message");
        Assert.assertEquals(selection.getAttribute("aria-describedby"),
                select.getElement().getChild(2).getAttribute("id"));
        Assert.assertEquals("error message",
                select.getElement().getChild(2).getText());
    }

    @Test
    public void setItemsAndValueDomBehavior() {
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setItems("One", "Two", "Three");
        select.setLabel("A label");

        Assert.assertEquals("A label",
                select.getElement().getChild(0).getText());

        Element options = select.getElement().getChild(1).getChild(0);
        Assert.assertEquals(3, options.getChildCount());
        Assert.assertEquals("One", options.getChild(0).getProperty("label"));
        Assert.assertEquals("Two", options.getChild(1).getProperty("label"));
        Assert.assertEquals("Three", options.getChild(2).getProperty("label"));

        Element selection = select.getElement().getChild(1).getChild(2);
        Assert.assertEquals(0, selection.getChildCount());

        Set<String> value = Set.of("One");
        select.setValue(value);
        Assert.assertEquals(value, select.getValue());

        Assert.assertEquals(1, selection.getChildCount());
        Assert.assertEquals(2, options.getChildCount());
        Assert.assertEquals("One", selection.getChild(0).getProperty("label"));

        select.select("Two");
        Assert.assertEquals(Set.of("One", "Two"), select.getValue());

        Assert.assertEquals(2, selection.getChildCount());
        Assert.assertEquals(1, options.getChildCount());
        Assert.assertEquals("Two", selection.getChild(0).getProperty("label"));
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
        Assert.assertEquals("ONE", options.getChild(0).getProperty("label"));
        Assert.assertEquals("TWO", options.getChild(1).getProperty("label"));
        Assert.assertEquals("THREE", options.getChild(2).getProperty("label"));
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

}
