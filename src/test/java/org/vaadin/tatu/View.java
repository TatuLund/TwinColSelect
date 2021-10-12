package org.vaadin.tatu;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.tatu.TwinColSelect.ColType;
import org.vaadin.tatu.TwinColSelect.FilterMode;

import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends VerticalLayout {
    int newi = 1000;
    VerticalLayout log = new VerticalLayout();

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
        TwinColSelect<String> select = new TwinColSelect<>();
        select.setLabel("Do selection");
        TwinColSelectListDataView<String> dataView = select.setItems("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten");
        // Set<String> set = new HashSet<>();
        // for (Integer i=1;i<101;i++) {
        // set.add("Item "+i);
        // }
        // select.setItems(set);
//        ListDataProvider<String> dp = (ListDataProvider<String>) select
//                .getDataProvider();
//        Set<String> set = dp.getItems().stream().collect(Collectors.toSet());
//        dp.setSortComparator((a, b) -> a.compareTo(b));
        dataView.setSortComparator((a, b) -> a.compareTo(b));
        select.setHeight("350px");
        select.setWidth("500px");
        Set<String> selection = dataView.getItems().filter(item -> item.contains("o")).collect(Collectors.toSet());

        Bean bean =  new Bean();
        bean.setSelection(selection);

        binder.forField(select)
            .asRequired("Empty selection not allowed")
            .withValidator(sel -> sel.size() == 2 && sel.contains("Two") && sel.contains("Four"),"Selection needs to contain two and four")
            .bind(Bean::getSelection,Bean::setSelection);        
        binder.setBean(bean);
        dataView.addItemCountChangeListener(event -> {
            Notification.show("Item count changed");
        });

        select.addValueChangeListener(event -> {
            log.removeAll();
            log.addComponentAsFirst(new Span(("Value changed")));
            event.getValue().forEach(item -> log
                    .addComponentAsFirst(new Span(item + " selected!")));
        });
        Button refresh = new Button("Add/Refresh");
        refresh.addClickListener(event -> {
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
        log.getStyle().set("overflow-y", "auto");
        log.setHeight("100px");
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(refresh, clear, clearTicks, readOnly, filterMode);
        add(filterField, select, buttons, log);
        setFlexGrow(1, log);
    }
}
