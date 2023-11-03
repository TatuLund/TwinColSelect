package org.vaadin.tatu;

import java.util.List;

import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("twin-col-select")
public class TwinColSelectElement extends TestBenchElement {

    public VerticalLayoutElement getOptionList() {
        return this.$(VerticalLayoutElement.class).attribute("class", "options")
                .first();
    }

    public List<DivElement> getOptions() {
        return getOptionList().$(DivElement.class).all();
    }

    public VerticalLayoutElement getValueList() {
        return this.$(VerticalLayoutElement.class).attribute("class", "value")
                .first();

    }

    public List<DivElement> getValues() {
        return getValueList().$(DivElement.class).all();
    }

}