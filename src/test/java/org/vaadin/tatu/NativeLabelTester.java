/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package org.vaadin.tatu;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.testbench.HtmlContainerTester;
import com.vaadin.testbench.unit.Tests;

@Tests(NativeLabel.class)
public class NativeLabelTester extends HtmlContainerTester<NativeLabel> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NativeLabelTester(NativeLabel component) {
        super(component);
    }
}