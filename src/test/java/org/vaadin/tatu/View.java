package org.vaadin.tatu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.tatu.TwinColSelect.ColType;
import org.vaadin.tatu.TwinColSelect.FilterMode;
import org.vaadin.tatu.TwinColSelect.PickMode;
import org.vaadin.tatu.TwinColSelect.TwinColSelectI18n;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

@Route("")
@Theme("mytheme")
public class View extends VerticalLayout implements AppShellConfigurator {
    int newi = 1000;
    VerticalLayout log = new VerticalLayout();
    TwinColSelectListDataView<String> dataView = null;

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
        Button setItems = new Button("Set");
        Bean bean = new Bean();
        setItems.addClickListener(event -> {
            dataView = select.setItems("One", "Two", "Three", "Four", "Five",
                    "Six", "Seven", "Eight", "Nine", "Ten");
            Set<String> selection = dataView.getItems()
                    .filter(item -> item.contains("o"))
                    .collect(Collectors.toSet());
            bean.setSelection(selection);
            dataView.addItemCountChangeListener(e -> {
                Notification.show("Item count changed");
            });
            sorting.setEnabled(true);
        });
        // Set<String> set = new HashSet<>();
        // for (Integer i=1;i<101;i++) {
        // set.add("Item "+i);
        // }
        // select.setItems(set);
        // ListDataProvider<String> dp = (ListDataProvider<String>) select
        // .getDataProvider();
        // Set<String> set = dp.getItems().stream().collect(Collectors.toSet());
        // dp.setSortComparator((a, b) -> a.compareTo(b));

        select.setHeight("350px");
        select.setWidth("500px");
        select.setTooltipGenerator(item -> "This is item " + item);

        select.setI18n(TwinColSelectI18n.getDefault());

        binder.forField(select).asRequired("Empty selection not allowed")
                .withValidator(
                        sel -> sel.contains("Two") && sel.contains("Four"),
                        "Selection needs to contain two and four")
                .bind(Bean::getSelection, Bean::setSelection);
        binder.setBean(bean);

        select.addValueChangeListener(event -> {
            log.removeAll();
            log.addComponentAsFirst(new Span(("Value changed")));
            String value = event.getValue().stream()
                    .collect(Collectors.joining(","));
            Span valueSpan = new Span(value + " selected!");
            valueSpan.setId("value");
            log.addComponentAsFirst(valueSpan);
        });
        Button refresh = new Button("Add/Refresh");
        refresh.addClickListener(event -> {
            if (dataView == null) {
                List<String> items = new ArrayList<>();
                dataView = select.setItems(items);
            }
            dataView.addItem("An item " + newi);
            newi++;
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
            // dp.setFilter(item -> item.toUpperCase()
            // .startsWith(event.getValue().toUpperCase()));
            dataView.setFilter(item -> item.toUpperCase()
                    .startsWith(event.getValue().toUpperCase()));
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
                dataView.setSortComparator((a, b) -> a.compareTo(b));
            } else {
                dataView.removeSorting();
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
        pickMode.addValueChangeListener(e -> {
            select.setPickMode(pickMode.getValue());
        });
        
        log.getStyle().set("overflow-y", "auto");
        log.setHeight("100px");
        HorizontalLayout buttons1 = new HorizontalLayout();
        buttons1.add(setItems, refresh, clear, clearTicks, readOnly,
                nineDisabled, selectSome);
        HorizontalLayout buttons2 = new HorizontalLayout();
        buttons2.add(pickMode, filterMode, sorting, styled);
        add(filterField, select, buttons1, buttons2, log);
        setFlexGrow(1, log);
    }

}
