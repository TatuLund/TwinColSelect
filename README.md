[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/twincolselect)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/twincolselect.svg)](https://vaadin.com/directory/component/twincolselect)

# TwinColSelect

Vaadin 25 Java version of TwinColSelect

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

### Version 4.1.2
- Migrate frontend resources to `META-INF/frontend` to avoid deprecated resource location warnings in Vaadin 25.2 and newer

### Version 4.1.1
- Depend on `vaadin-core` instead of `vaadin` to avoid transitive dependencies on commercial components

### Version 4.1.0
- Add support for the Aura theme

### Version 4.0.0
- The first release to support Vaadin 25.0.0
- Only the Lumo theme is supported in this version

## Development instructions

Bundled frontend sources used with `@JsModule` or `@CssImport` belong in
`src/main/resources/META-INF/frontend`, not the deprecated
`src/main/resources/META-INF/resources/frontend` location.
Runtime resources used with `@StyleSheet` or `@JavaScript`, including
`twincolselect.css`, remain in `src/main/resources/META-INF/resources`.

Starting the test/demo server:
1. Run `mvn jetty:run`.
2. Open http://localhost:8080 in the browser.

Run unit tests with `mvn test`. To also run the headless Chrome E2E tests,
use `mvn -Pintegration-tests verify`; this starts and stops the demo server.
The E2E suite requires Chrome and a valid Vaadin TestBench license
(`VAADIN_PRO_KEY` on CI). The dev container installs Chromium and its matching
ChromeDriver. The test browser runs with `--no-sandbox`, so run it only against
trusted content.

## Publishing to Vaadin Directory

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using
```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/twincolselect-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent
