package org.vaadin.tatu;

import java.util.Random;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.DomListenerRegistration;

@Tag(Tag.DIV)
class SelectItem
        extends AbstractSinglePropertyField<SelectItem, Boolean>
        implements HasStyle, Focusable<SelectItem>,
        ClickNotifier<SelectItem>, HasSize {

    private Random rand = new Random();

    SelectItem() {
        super("value", false, String.class, value -> Boolean.valueOf(value),
                value -> "" + value);
        getElement().addEventListener("click",
                event -> toggleValueFromClient());
        DomListenerRegistration reg = getElement().addEventListener("keydown",
                event -> toggleValueFromClient());
        reg.addEventData("event.keyCode");
        reg.addEventData(
                "event.keyCode == 32 ? event.preventDefault() : undefined");
        reg.setFilter("event.keyCode == 32");
        getElement().setAttribute("role", "option");
        getElement().setAttribute("aria-selected","false");
    }

    private void toggleValueFromClient() {
        this.setModelValue(!getValue(), true);
        if (getValue()) {
            getElement().setAttribute("checked", true);
            getElement().setAttribute("aria-selected","true");
        } else {
            getElement().removeAttribute("checked");
            getElement().setAttribute("aria-selected","false");
        }
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value);
        if (value) {
            getElement().setAttribute("checked", true);
            getElement().setAttribute("aria-selected","true");
        } else {
            getElement().removeAttribute("checked");
            getElement().setAttribute("aria-selected","false");
        }
    }

    void setDisabled(boolean disabled) {
        this.getElement().setProperty("disabled", disabled);
    }

    boolean isDisabled() {
        return getElement().getProperty("disabled", false);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        super.setRequiredIndicatorVisible(requiredIndicatorVisible);

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return super.isRequiredIndicatorVisible();
    }

    void setTooltipText(String tooltip) {
        String key = "checkbox-" + getElement().getProperty("keyId");
        setId(key);
        Html html = new Html("<vaadin-tooltip for='" + key + "' text='"
                + tooltip + "'></vaadin-tooltip>");
        getElement().appendChild(html.getElement());
    }

    void setLabel(String labelText) {
        getElement().setText(labelText);
    }

    String getLabel() {
        return getElement().getText();
    }

    // simulate click for tests
    void click() {
        toggleValueFromClient();
        fireEvent(new ClickEvent<>(this, false, 0, 0, 0, 0, 0, 0, false, false,
                false, false));        
    }
}