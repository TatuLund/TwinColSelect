[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/twincolselect)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/twincolselect.svg)](https://vaadin.com/directory/component/twincolselect)

# TwinColSelect

Vaadin 14 Java version of TwinColSelect

TwinColSelect component, also known as list builder. It is a component for multiselection.

This is component consists of two lists. You can move items from the other list to other. The left list is
master list and backed by DataProvider. The right list is the selection list and reflects the value of the
selection.
 
 The component also has drag and drop support.

## Release notes

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
