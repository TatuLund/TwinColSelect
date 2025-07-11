package org.vaadin.tatu;

import java.util.List;

import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("twin-col-select")
public class TwinColSelectElement extends TestBenchElement {

    public VerticalLayoutElement getOptionList() {
        return this.$(VerticalLayoutElement.class).withClassName("options")
                .first();
    }

    public List<DivElement> getOptions() {
        return getOptionList().$(DivElement.class).all().stream()
                .filter(el -> el.hasClassName("twincolselect-item")).toList();
    }

    public VerticalLayoutElement getValueList() {
        return this.$(VerticalLayoutElement.class).withClassName("value")
                .first();
    }

    public List<DivElement> getValues() {
        return getValueList().$(DivElement.class).all().stream()
                .filter(el -> el.hasClassName("twincolselect-item")).toList();
    }

}