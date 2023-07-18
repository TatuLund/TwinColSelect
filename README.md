[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/twincolselect)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/twincolselect.svg)](https://vaadin.com/directory/component/twincolselect)

# TwinColSelect

Vaadin 23 Java version of TwinColSelect

TwinColSelect component, also known as list builder. It is a component for multiselection.

This is component consists of two lists. You can move items from the other list to other. The left list is
master list and backed by DataProvider. The right list is the selection list and reflects the value of the
selection.
 
The component also has drag and drop support.

This add-on component is also a demo case on how to create a complex custom field as server side 
composition using Java. As the logic is fully implemented in Java, it is also possible to verify 
it using UI Unit Test feature that came in Vaadin 23.2. 

See: https://github.com/TatuLund/TwinColSelect/blob/nextlts/src/test/java/org/vaadin/tatu/ViewTest.java

There is CSS styling example in

https://github.com/TatuLund/TwinColSelect/blob/nextlts/frontend/themes/mytheme/styles.css

## Release notes

### Version 3.0.1
- Use NativeLabel instead of Label
- Small improvement in keyboard navigation

### Version 3.0.0
- The first release to support Vaadin 24

## Development instructions

Starting the test/demo server:
1. Run `mvn jetty:run`.
2. Open http://localhost:8080 in the browser.

## Publishing to Vaadin Directory

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using
```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/twincolselect-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent
