package org.vaadin.tatu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.tatu.TwinColSelect.ColType;
import org.vaadin.tatu.TwinColSelect.FilterMode;
import org.vaadin.tatu.TwinColSelect.PickMode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

@Theme(themeFolder = "mytheme")
@Route("")
public class View extends VerticalLayout {
    int newi = 1000;
    VerticalLayout log = new VerticalLayout();
    ListDataProvider<String> dp;

    public class Bean {
        private Set<String> selection;

        public Set<String> getSelection() {
            return selection;
        }

        public void setSelection(Set<String> selection) {
            this.selection = selection;
        }
    };

    public View() {
        this.setSizeFull();
        // Un-comment to test right to left mode
        // UI.getCurrent().setDirection(Direction.RIGHT_TO_LEFT);
        Binder<Bean> binder = new Binder<>();
        Checkbox sorting = new Checkbox("Sorting");
        sorting.setEnabled(false);
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setLabel("Do selection");
        select.setHeight("350px");
        select.setWidth("500px");
        Button setItems = new Button("Set");
        Bean bean = new Bean();
        setItems.addClickListener(event -> {
            List<String> items = new ArrayList<>(
                    Arrays.asList("One", "Two", "Three", "Four", "Five", "Six",
                            "Seven", "Eight", "Nine", "Ten"));
            select.setItems(items);
            dp = (ListDataProvider<String>) select.getDataProvider();
            Set<String> set = dp.getItems().stream()
                    .collect(Collectors.toSet());
            dp.setSortComparator((a, b) -> a.compareTo(b));
            Set<String> selection = new HashSet<>();
            set.forEach(item -> {
                if (item.contains("o")) {
                    selection.add(item);
                }
            });
            bean.setSelection(selection);
            sorting.setEnabled(true);
        });

        binder.forField(select).asRequired("Empty selection not allowed")
                .withValidator(
                        sel -> sel.size() == 2 && sel.contains("Two")
                                && sel.contains("Four"),
                        "Selection needs to contain two and four")
                .bind(Bean::getSelection, Bean::setSelection);
        binder.setBean(bean);

        select.addValueChangeListener(event -> {
            log.removeAll();
            log.addComponentAsFirst(new Span(("Value changed")));
            event.getValue().forEach(item -> log
                    .addComponentAsFirst(new Span(item + " selected!")));
        });
        Button refresh = new Button("Add/Refresh");
        refresh.addClickListener(event -> {
            if (dp != null) {
                dp.getItems().add("An item " + newi);
                newi++;
                dp.refreshAll();
            }
        });
        Button clear = new Button("Clear");
        clear.addClickListener(event -> {
            select.deselectAll();
        });
        Button clearTicks = new Button("Clear Ticks (BOTH)");
        clearTicks.addClickListener(event -> {
            select.clearTicks(ColType.BOTH);
        });
        Button readOnly = new Button("Read only");
        readOnly.addClickListener(event -> {
            select.setReadOnly(!select.isReadOnly());
        });
        TextField filterField = new TextField("Filter");
        filterField.addValueChangeListener(event -> {
            if (dp != null) {
                dp.setFilter(item -> item.toUpperCase()
                        .startsWith(event.getValue().toUpperCase()));

            }
        });
        Checkbox filterMode = new Checkbox("Reset filter mode");
        filterMode.addValueChangeListener(event -> {
            if (event.getValue()) {
                select.setFilterMode(FilterMode.RESETVALUE);
            } else {
                select.setFilterMode(FilterMode.ITEMS);
            }
        });

        sorting.addValueChangeListener(event -> {
            if (event.getValue()) {
                dp.setSortComparator((a, b) -> a.compareTo(b));
            } else {
                dp.setSortComparator(null);
            }
        });

        Checkbox styled = new Checkbox("Styled");
        styled.addValueChangeListener(event -> {
            if (event.getValue()) {
                select.getElement().getThemeList().add("styled");
            } else {
                select.getElement().getThemeList().remove("styled");
            }
        });

        Button nineDisabled = new Button("Disable nine");
        nineDisabled.addClickListener(event -> {
            select.setItemEnabledProvider(item -> !item.equals("Nine"));
        });

        Button selectSome = new Button("Select");
        selectSome.addClickListener(event -> {
            select.select("Eight", "Nine", "Ten");
        });

        Select<PickMode> pickMode = new Select<>();
        pickMode.setItems(PickMode.values());
        pickMode.setValue(PickMode.DOUBLE);
        pickMode.addValueChangeListener(e -> {
            select.setPickMode(pickMode.getValue());
        });

        CheckboxGroup<TwinColSelectVariant> variants = new CheckboxGroup<>();
        variants.setLabel("Variants");
        variants.setItems(TwinColSelectVariant.values());
        variants.addValueChangeListener(e -> {
            select.removeThemeVariants(TwinColSelectVariant.values());
            variants.getValue()
                    .forEach(variant -> select.addThemeVariants(variant));
        });

        log.getStyle().set("overflow-y", "auto");
        log.setHeight("100px");
        HorizontalLayout buttons1 = new HorizontalLayout();
        buttons1.add(setItems, refresh, clear, clearTicks, readOnly,
                nineDisabled, selectSome);
        HorizontalLayout buttons2 = new HorizontalLayout();
        buttons2.add(pickMode, filterMode, sorting, styled);
        add(filterField, select, buttons1, buttons2, variants, log);
        setFlexGrow(1, log);
    }
}
