package org.vaadin.tatu;

import java.util.stream.Collectors;

import org.vaadin.tatu.TwinColSelect.ColType;
import org.vaadin.tatu.TwinColSelect.TwinColSelectI18n;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("twoselects")
public class ViewWithTwoSelects extends VerticalLayout {

    public ViewWithTwoSelects() {
        HorizontalLayout layout = new HorizontalLayout();
        TwinColSelect<String> select1 = new TwinColSelect<>();
        select1.setId("first");
        select1.setItems("Pekka", "Matti", "Jussi");
        select1.setWidth("500px");
        select1.setHeight("400px");
        select1.setI18n(TwinColSelectI18n.getDefault());
        select1.addValueChangeListener(e -> {
            Span span = new Span("" + select1.getValue().stream()
                    .collect(Collectors.joining(",")));
            span.setId("value-change1");
            add(span);
        });

        TwinColSelect<String> select2 = new TwinColSelect<>();
        select2.setId("second");
        select2.setItems("One", "Two", "Three");
        select2.setWidth("500px");
        select2.setHeight("400px");
        select2.select("One", "Two");
        select2.clearTicks(ColType.BOTH);

        select2.addValueChangeListener(e -> {
            Span span = new Span("" + select2.getValue().stream()
                    .collect(Collectors.joining(",")));
            span.setId("value-change2");
            add(span);
        });

        layout.add(select1, select2);
        add(layout);
    }
}
