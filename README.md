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

### Version 2.3.0
- Add API's to set tooltips for items and buttons, requires Vaadin 23.3.0 or newer.

### Version 2.2.3
- Correct button states after setting readonly false

### Version 2.2.2
- Preseve order of selection when comparator is not set

### Version 2.2.1
- Fix issues with buttons

### Version 2.2.0
- Fix required indicator to be similar to other Vaadin components

### Version 2.1.0
- Fixed issue with buttons enabled when no data
- Fixed issue with Filter and SortComparator not working via DataView
- Fixed issue with sorting not done when moving items
- Compiled with Java 11 for Vaadin 23

### Version 2.0.0
- Added DataView API support for Vaadin 17+

### Version 1.8.1
- Fixed css that breaks with Vaadin 14.6.0

### Version 1.8.0
- Improved range select & Double click to select, kudos to Michael Thorne for contributions
- Small fixes  

### Version 1.7.0
- Made the items look less like conventional checkboxes
- Added API to customize button icons and captions

### Version 1.6.0

- Add option to configure whether value is reset when filter is changed or not
- Fixed corner case bugs related to filtering and sorting

### Version 1.5.1

- Fixed bug with undefined direction

### Version 1.5.0

- Added right to left support, requires Vaadin 14.3 or newer

### Version 1.4.1

- Fixed the default constructor to use empty set as default value (see #4)

### Version 1.4.0

- Added range selection option

### Version 1.3.1

- Fixed broken readOnly functionality

### Version 1.3.0

- Added clearTicks(..) and setClearTicksOnSelect(..)

### Version 1.2.4

- Fixes bug with deselect / deselectAll
- Changed clear() to reset checkboxes too

### Version 1.2.3

- Fixes broken clear()

### Version 1.2.2

- Fixes broken setValue(..)

### Version 1.2.1

- Changed default value of the error message

### Version 1.2.0

- Keep lists sorted if in memory DataProvider has Comparator set

### Version 1.1.0

- Small fixes, added some JavaDocs added Drag and Drop support
- Vaadin 14.1 or newer required

### Version 1.0.0

- The first release 

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
