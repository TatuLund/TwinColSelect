package org.vaadin.tatu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.binder.HasItemsAndComponents;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.KeyMapper;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

/**
 * TwinColSelect component, also known as list builder. It is a component for
 * multiselection.
 *
 * This is component consists of two lists. You can move items from the other
 * list to other. The left list is master list and backed by DataProvider. The
 * right list is the selection list and reflects the value of the selection.
 *
 * The component also has drag and drop support.
 *
 * @author Tatu Lund
 *
 * @param <T>
 *            The bean type in TwinColSelect
 */
@Tag("twin-col-select")
@CssImport(value = "./twincolselect.css")
@CssImport(value = "./twincolselect-checkbox.css", themeFor = "vaadin-checkbox")
public class TwinColSelect<T> extends AbstractField<TwinColSelect<T>, Set<T>>
        implements HasItemsAndComponents<T>, HasSize, HasValidation, HasTheme,
        MultiSelect<TwinColSelect<T>, T>, HasDataProvider<T> {

    /**
     * Defines the filter mode
     * 
     * @see setFilterMode(FilterMode)
     */
    public enum FilterMode {
        /**
         * Filter is applied only to the items
         */
        ITEMS,
        /**
         * Value is reset when filter is updated
         */
        RESETVALUE;
    }

    public enum PickMode {
        /**
         * Pick items with single click to selection.
         */
        SINGLE,
        /**
         * Pick items with double click to selection.
         */
        DOUBLE;
    }

    private static final String VALUE = "value";

    private final KeyMapper<T> keyMapper = new KeyMapper<>(this::getItemId);

    private SerializablePredicate<T> itemEnabledProvider = item -> isEnabled();

    VerticalLayout list1 = new VerticalLayout();
    VerticalLayout list2 = new VerticalLayout();
    VerticalLayout buttons = new VerticalLayout();
    private String errorMessage = "";
    private Div errorLabel = new Div();
    private Label label = new Label();
    private Label required = new Label("*");
    private DataProvider<T, ?> dataProvider = DataProvider.ofItems();
    private Random rand = new Random();

    private ItemLabelGenerator<T> itemLabelGenerator = String::valueOf;

    final static String LIST_BORDER = "1px var(--lumo-primary-color) solid";
    final static String LIST_BORDER_RADIUS = "var(--lumo-border-radius)";
    final static String LIST_BORDER_ERROR = "1px var(--lumo-error-color) solid";
    final static String LIST_BORDER_READONLY = "1px dashed var(--lumo-contrast-30pct)";
    final static String LIST_BACKGROUND_ERROR = "var(--lumo-error-color-10pct)";
    final static String LIST_BACKGROUND = "var(--lumo-contrast-10pct)";
    final static String LIST_BACKGROUND_DROP = "var(--lumo-contrast-30pct)";
    final static String LIST_BACKGROUND_READONLY = "transparent";

    private Registration dataProviderListenerRegistration;
    private boolean clearTicksOnSelect = false;

    private Button allButton;
    private Button addButton;
    private Button removeButton;
    private Button clearButton;
    private Button recycleButton;

    private TwinColSelectI18n i18n;

    private boolean requiredIndicatorVisible;

    private PickMode pickMode = PickMode.DOUBLE;

    private class CheckBoxItem<T> extends Checkbox implements ItemComponent<T> {

        private final T item;
        private DragSource<CheckBoxItem<T>> dragSource;
        private DomListenerRegistration reg = null;

        private CheckBoxItem(String id, T item) {
            this.getElement().setAttribute("theme", "checkbox-item");
            this.addClassName("checkbox-item");
            this.item = item;
            getElement().setProperty(VALUE, id);
            dragSource = DragSource.create(this);
            dragSource.setDraggable(true);
            dragSource.setEffectAllowed(EffectAllowed.MOVE);
            dragSource.addDragStartListener(event -> {
                this.setValue(true);
                if (this.getParent().get() == list1) {
                    list2.getStyle().set("background", LIST_BACKGROUND_DROP);
                } else {
                    list1.getStyle().set("background", LIST_BACKGROUND_DROP);
                }
            });
            dragSource.addDragEndListener(event -> {
                String background = LIST_BACKGROUND;
                if (isInvalid()) {
                    background = LIST_BACKGROUND_ERROR;
                }
                list1.getStyle().set("background", LIST_BACKGROUND);
                list2.getStyle().set("background", background);
            });
            addClickListener(click -> {
                // handle doubleclick list swap
                if ((pickMode == PickMode.DOUBLE && click.getClickCount() == 2)
                        || (pickMode == PickMode.SINGLE && !click.isCtrlKey()
                                && !click.isShiftKey())) {
                    setValue(true);
                    doSwapItems();
                } else {
                    // implement range select
                    if (click.isShiftKey()) {
                        // Java is unhappy with passing this around
                        CheckBoxItem<T> anchor = (CheckBoxItem<T>) getAnchor();
                        if (anchor != null) {
                            if (anchor.getParent().get() == list1) {
                                if (this.getParent().get() == list1) {
                                    markRange(list1, anchor, this);
                                }
                            } else {
                                if (this.getParent().get() == list2) {
                                    markRange(list2, anchor, this);
                                }
                            }
                        }
                        setAnchor(null); // always clear the anchor
                    } else {
                        // remember the item clicked
                        setAnchor(this);
                    }
                }
            });
            // There is a subtle bug in Checkbox focus behavior, thus code
            // temporarily
            // disabled. This works in Vaadin 23/24, but not in Vaadin 14.
            reg = getElement().addEventListener("keydown", event -> {
                if (event.getEventData().getNumber("event.keyCode") == 40) {
                    // focusNext();
                } else if (event.getEventData()
                        .getNumber("event.keyCode") == 38) {
                    // focusPrevious();
                } else if (event.getEventData()
                        .getNumber("event.keyCode") == 13) {
                    doSwapItems();
                }
            });
            reg.addEventData("event.keyCode");
            reg.addEventData(
                    "([40, 38, 13].includes(event.keyCode)) ? event.preventDefault() : undefined");
            reg.setFilter("[40, 38, 13].includes(event.keyCode)");
        }

        private void doSwapItems() {
            // Checkbox next = getNextCheckbox();
            // Checkbox previous = getPreviousCheckbox();
            swapItems();
            // if (next != null) {
            // next.focus();
            // } else if (previous != null) {
            // previous.focus();
            // }
        }

        // private void focusNext() {
        // Checkbox c = getNextCheckbox();
        // if (c != null) {
        // c.focus();
        // }
        // }
        //
        // private void focusPrevious() {
        // Checkbox c = getPreviousCheckbox();
        // if (c != null) {
        // c.focus();
        // }
        // }

        // private Checkbox getPreviousCheckbox() {
        // Checkbox c = null;
        // VerticalLayout list = ((VerticalLayout) getParent().get());
        // int index = list.indexOf(this);
        // while (index > 0) {
        // index--;
        // c = (Checkbox) list.getComponentAt(index);
        // if (c.isEnabled())
        // break;
        // }
        // return c;
        // }
        //
        // private Checkbox getNextCheckbox() {
        // Checkbox c = null;
        // VerticalLayout list = ((VerticalLayout) getParent().get());
        // int index = list.indexOf(this);
        // while (index < (list.getComponentCount() - 1)) {
        // index++;
        // c = (Checkbox) list.getComponentAt(index);
        // if (c.isEnabled())
        // break;
        // }
        // return c;
        // }

        private void swapItems() {
            if (this.getParent().get() == list1) {
                moveItems(list1, list2);
            } else {
                moveItems(list2, list1);
            }
            TwinColSelect.this.setModelValue(getSelectedItems(), true);
        }

        @Override
        public T getItem() {
            return item;
        }
    }

    // again, Java doesn't like passing inner class instances in and out for
    // reasons not entirely clear to me
    /**
     * set the value of all children of the list to true which are between a and
     * b, leaving items not between a and b alone
     * 
     * @param list
     * @param anchor
     * @param checkBoxItem
     */
    void markRange(VerticalLayout list, Component anchor,
            Component checkBoxItem) {
        boolean marking = false;
        for (Component i : list.getChildren()
                .filter(c -> ((Checkbox) c).isEnabled())
                .collect(Collectors.toList())) {
            if (i == anchor || i == checkBoxItem) {
                marking = !marking;
                ((CheckBoxItem<T>) i).setValue(true);
            } else {
                if (marking) {
                    ((CheckBoxItem<T>) i).setValue(true);
                }
            }
        }
    }

    /**
     * Default constructor
     */
    public TwinColSelect() {
        this(Collections.emptySet());
    }

    protected TwinColSelect(Set<T> initialValue) {
        super(initialValue);
        if (initialValue != null) {
            setModelValue(initialValue, false);
            setPresentationValue(initialValue);
        }
        getElement().getStyle().set("display", "flex");
        getElement().getStyle().set("flex-direction", "column");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeight("calc(100% - 37px)");
        setErrorLabelStyles();
        errorLabel.setVisible(false);
        errorLabel.setId(randomId("twincolselect-error", 5));
        errorLabel.getElement().setAttribute("role", "alert");
        label.setId(randomId("twincolselect-label", 5));
        label.setVisible(false);
        label.getElement().getStyle().set("--tcs-required-dot-opacity", "0");
        setLabelStyles(label);
        setSizeFull();
        list2.getElement().setAttribute("aria-describedby",
                errorLabel.getId().get());
        list1.addClassName("options");
        list1.getElement().setAttribute("aria-describedby",
                label.getId().get());
        list2.addClassName("value");
        list2.getElement().setAttribute("aria-live", "assertive");
        setupList(list1);
        setupList(list2);
        allButton = new Button(VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
        allButton.addClickListener(event -> {
            list1.getChildren().forEach(comp -> {
                Checkbox checkbox = (Checkbox) comp;
                list1.remove(checkbox);
                list2.add(checkbox);
            });
            updateButtons();
            setModelValue(getSelectedItems(), true);
        });

        allButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        addButton = new Button(VaadinIcon.ANGLE_RIGHT.create());
        addButton.addClickListener(event -> {
            moveItems(list1, list2);
            setModelValue(getSelectedItems(), true);
            if (clearTicksOnSelect)
                clearTicks(ColType.RIGHT);
        });
        addButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        removeButton = new Button(VaadinIcon.ANGLE_LEFT.create());
        removeButton.addClickListener(event -> {
            moveItems(list2, list1);
            setModelValue(getSelectedItems(), true);
            if (clearTicksOnSelect)
                clearTicks(ColType.LEFT);
        });
        removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        clearButton = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
        clearButton.addClickListener(event -> {
            clear();
            updateButtons();
        });
        clearButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        recycleButton = new Button(VaadinIcon.RECYCLE.create());
        recycleButton.addClickListener(event -> {
            // true if any value is selected
            boolean any2 = list2.getChildren()
                    .anyMatch(c -> ((Checkbox) c).getValue());
            // then set all to the negation (any? then all false, none? than all
            // true)
            list2.getChildren().forEach(comp -> {
                Checkbox checkbox = (Checkbox) comp;
                checkbox.setValue(!any2);
            });
            boolean any1 = list1.getChildren()
                    .anyMatch(c -> ((Checkbox) c).getValue());
            list1.getChildren().forEach(comp -> {
                Checkbox checkbox = (Checkbox) comp;
                checkbox.setValue(!any1);
            });
        });
        recycleButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        buttons.setWidth("15%");
        buttons.setHeight("100%");
        buttons.addClassName("buttons");
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);
        buttons.setAlignItems(Alignment.CENTER);
        buttons.add(allButton, addButton, removeButton, clearButton,
                recycleButton);
        layout.setFlexGrow(1, list1, list2);
        layout.add(list1, buttons, list2);
        updateButtons();
        add(label, layout, errorLabel);
    }

    private void detectDirection() {
        getUI().ifPresent(ui -> {
            ui.getPage().executeJs("return document.dir;").then(String.class,
                    value -> {
                        if (value.equals(
                                Direction.RIGHT_TO_LEFT.getClientName())) {
                            removeButton
                                    .setIcon(VaadinIcon.ANGLE_RIGHT.create());
                            clearButton.setIcon(
                                    VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
                            addButton.setIcon(VaadinIcon.ANGLE_LEFT.create());
                            allButton.setIcon(
                                    VaadinIcon.ANGLE_DOUBLE_LEFT.create());
                        } else {
                            removeButton
                                    .setIcon(VaadinIcon.ANGLE_LEFT.create());
                            clearButton.setIcon(
                                    VaadinIcon.ANGLE_DOUBLE_LEFT.create());
                            addButton.setIcon(VaadinIcon.ANGLE_RIGHT.create());
                            allButton.setIcon(
                                    VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
                        }
                    });
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        detectDirection();
        if (getDataProvider() != null
                && dataProviderListenerRegistration == null) {
            setupDataProviderListener(getDataProvider());
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (dataProviderListenerRegistration != null) {
            dataProviderListenerRegistration.remove();
            dataProviderListenerRegistration = null;
        }
        super.onDetach(detachEvent);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        this.requiredIndicatorVisible = requiredIndicatorVisible;
        if (requiredIndicatorVisible) {
            label.getElement().getStyle().set("--tcs-required-dot-opacity",
                    "1");
            list1.getElement().setAttribute("aria-required", "true");
        } else {
            label.getElement().getStyle().set("--tcs-required-dot-opacity",
                    "0");
            list1.getElement().removeAttribute("aria-required");
        }
    }

    /**
     * Determines whether the twincolselect is marked as input required.
     * <p>
     *
     * @return {@code true} if the input is required, {@code false} otherwise
     */
    @Override
    public boolean isRequiredIndicatorVisible() {
        return requiredIndicatorVisible;
    }

    // Internal method that moves items from list1 to list2
    private void moveItems(VerticalLayout list1, VerticalLayout list2) {
        list1.getChildren().forEach(comp -> {
            Checkbox checkbox = (Checkbox) comp;
            if (checkbox.getValue()) {
                list1.remove(checkbox);
                list2.add(checkbox);
                if (!checkbox.isEnabled()) {
                    checkbox.setValue(false);
                }
            }
        });
        DataProvider<T, ?> dp = this.getDataProvider();
        if (dp instanceof InMemoryDataProvider) {
            InMemoryDataProvider<T> dataProvider = (InMemoryDataProvider<T>) dp;
            if (dataProvider.getSortComparator() != null) {
                sortDestinationList(list2, dataProvider);
            }
        }
        updateButtons();
    }

    private void updateButtons() {
        if (addButton != null) {
            if (list1.getComponentCount() == 0) {
                addButton.setEnabled(false);
                allButton.setEnabled(false);
            } else {
                addButton.setEnabled(true);
                allButton.setEnabled(true);
            }
            if (list2.getComponentCount() == 0) {
                removeButton.setEnabled(false);
                clearButton.setEnabled(false);
            } else {
                removeButton.setEnabled(true);
                clearButton.setEnabled(true);
            }
            setTooltipText(addButton,
                    i18n != null ? i18n.getAddToSelected() : null);
            setTooltipText(allButton,
                    i18n != null ? i18n.getAddAllToSelected() : null);
            setTooltipText(removeButton,
                    i18n != null ? i18n.getRemoveFromSelected() : null);
            setTooltipText(clearButton,
                    i18n != null ? i18n.getRemoveAllFromSelected() : null);
            setTooltipText(recycleButton,
                    i18n != null ? i18n.getToggleSelection() : null);
            addButton.addThemeName("icon");
            allButton.addThemeName("icon");
            removeButton.addThemeName("icon");
            clearButton.addThemeName("icon");
            list1.getElement().setAttribute("aria-label",
                    i18n != null ? i18n.getOptions() : "Options");
            list2.getElement().setAttribute("aria-label",
                    i18n != null ? i18n.getSelected() : "Selected");
        }
    }

    private static void setTooltipText(Button button, String text) {
        if (text != null) {
            button.getElement().setAttribute("title", text);
            button.getElement().setAttribute("aria-label", text);
        } else {
            button.getElement().removeAttribute("title");
            button.getElement().removeAttribute("aria-label");
        }
    }

    private CheckBoxItem<T> check = null;

    private FilterMode filterMode = FilterMode.ITEMS;

    private CheckBoxItem<T> anchorItem = null;

    private SerializableFunction<T, String> tooltipGenerator;

    private void setAnchor(Component checkBoxItem) {
        anchorItem = (CheckBoxItem<T>) checkBoxItem;
    }

    private void clearAnchor() {
        anchorItem = null;
    }

    private CheckBoxItem<T> getAnchor() {
        return anchorItem;
    }

    private void sortDestinationList(VerticalLayout list2,
            InMemoryDataProvider<T> dataProvider) {
        SerializablePredicate<T> filter = dataProvider.getFilter();
        dataProvider.clearFilters();
        Query query = new Query(0, Integer.MAX_VALUE, null,
                dataProvider.getSortComparator(), null);
        Stream<T> sorted = dataProvider.fetch(query);
        List<CheckBoxItem<T>> sortedBoxes = new ArrayList<>();
        sorted.forEach(item -> {
            boolean match = list2.getChildren().anyMatch(comp -> {
                check = (CheckBoxItem<T>) comp;
                return (check.getItem().equals(item));
            });
            if (match) {
                sortedBoxes.add(check);
            }
        });
        list2.removeAll();
        sortedBoxes.forEach(check -> list2.add(check));
        dataProvider.setFilter(filter);
    }

    // Setup list layout
    private void setupList(VerticalLayout list) {
        list.getElement().setAttribute("role", "listbox");
        list.getElement().setAttribute("tabindex", "0");
        list.setSpacing(false);
        list.setPadding(false);
        list.getStyle().set("border", LIST_BORDER);
        list.getStyle().set("border-radius", LIST_BORDER_RADIUS);
        list.getStyle().set("background", LIST_BACKGROUND);
        DropTarget<VerticalLayout> dropTarget = DropTarget.create(list);
        dropTarget.setDropEffect(DropEffect.MOVE);
        dropTarget.setActive(true);
        dropTarget.addDropListener(event -> {
            event.getDragSourceComponent().ifPresent(component -> {
                if (component instanceof CheckBoxItem) {
                    Component checkBox = component;
                    VerticalLayout otherList = (VerticalLayout) checkBox
                            .getParent().get();
                    if (otherList != list)
                        moveItems(otherList, list);
                    setModelValue(getSelectedItems(), true);
                }
            });
        });
    }

    /**
     * Set the caption label of the twincolselect
     *
     * @param label
     *            The label as String
     */
    public void setLabel(String label) {
        if (label != null) {
            this.label.setText(label);
            this.label.setVisible(true);
        } else {
            this.label.setVisible(false);
        }
    }

    /**
     * Gets the label of the twincolselect.
     *
     * @return the of the twincolselect
     */
    public String getLabel() {
        return label.getText();
    }

    private void setLabelStyles(HasStyle label) {
        label.addClassName("twincolselect-label-styles");
    }

    private void setErrorLabelStyles() {
        errorLabel.addClassName("twincolselect-errorlabel");
    }

    private void reset(boolean refresh) {
        if (filterMode == FilterMode.RESETVALUE) {
            if (!refresh)
                super.clear();
        }
        keyMapper.removeAll();
        list1.removeAll();
        getDataProvider().fetch(new Query<>()).map(this::createCheckBox)
                .forEach(checkbox -> {
                    if (!this.getSelectedCheckboxItems()
                            .anyMatch(selected -> checkbox.getItem()
                                    .equals(selected.getItem()))) {
                        list1.add(checkbox);
                    } else {

                    }
                });
        updateButtons();
    }

    /**
     * Sets the item label generator that is used to produce the strings shown
     * in the twincolselect for each item. By default,
     * {@link String#valueOf(Object)} is used.
     *
     * @param itemLabelGenerator
     *            the item label provider to use, not null
     */
    public void setItemLabelGenerator(
            ItemLabelGenerator<T> itemLabelGenerator) {
        Objects.requireNonNull(itemLabelGenerator,
                "The item label generator can not be null");
        this.itemLabelGenerator = itemLabelGenerator;
        reset(true);
    }

    /**
     * Sets the tooltip generator that is used to produce the tooltips shown in
     * the twincolselect for each item. By default,
     * {@link String#valueOf(Object)} is used.
     *
     * @param tooltipGenerator
     *            the item label provider to use, set null to disable tooltips.
     */
    public void setTooltipGenerator(
            SerializableFunction<T, String> tooltipGenerator) {
        this.tooltipGenerator = tooltipGenerator;
        reset(true);
    }

    /**
     * Sets the item enabled predicate for this twincolselect. The predicate is
     * applied to each item to determine whether the item should be enabled
     * ({@code true}) or disabled ({@code false}). Disabled items are displayed
     * as grayed out and the user cannot select them. The default predicate
     * always returns true (all the items are enabled).
     *
     * @param itemEnabledProvider
     *            the item enable predicate, not {@code null}
     */
    public void setItemEnabledProvider(
            SerializablePredicate<T> itemEnabledProvider) {
        this.itemEnabledProvider = Objects.requireNonNull(itemEnabledProvider);
        refreshCheckboxes();
    }

    /**
     * Gets the item label generator that is used to produce the strings shown
     * in the twincolselect for each item.
     *
     * @return the item label generator used, not null
     */
    public ItemLabelGenerator<T> getItemLabelGenerator() {
        return itemLabelGenerator;
    }

    /**
     * Gets the tooltip generator that is used to produce the strings shown in
     * the twincolselect for each item.
     *
     * @return the tooltip generator used
     */
    public SerializableFunction<T, String> getTooltipGenerator() {
        return tooltipGenerator;
    }

    /**
     * Returns the item enabled predicate.
     *
     * @return the item enabled predicate
     * @see #setItemEnabledProvider
     */
    public SerializablePredicate<T> getItemEnabledProvider() {
        return itemEnabledProvider;
    }

    private Stream<CheckBoxItem<T>> getCheckboxItems() {
        return list1.getChildren().filter(CheckBoxItem.class::isInstance)
                .map(child -> (CheckBoxItem<T>) child);
    }

    private Stream<CheckBoxItem<T>> getSelectedCheckboxItems() {
        return list2.getChildren().filter(CheckBoxItem.class::isInstance)
                .map(child -> (CheckBoxItem<T>) child);
    }

    private void refreshCheckboxes() {
        getCheckboxItems().forEach(this::updateCheckbox);
    }

    protected boolean isDisabledBoolean() {
        return getElement().getProperty("disabled", false);
    }

    private void updateEnabled(CheckBoxItem<T> checkbox) {
        boolean disabled = isDisabledBoolean()
                || !getItemEnabledProvider().test(checkbox.getItem());
        Serializable rawValue = checkbox.getElement()
                .getPropertyRaw("disabled");
        if (rawValue instanceof Boolean) {
            // convert the boolean value to a String to force update the
            // property value. Otherwise since the provided value is the same as
            // the current one the update don't do anything.
            checkbox.getElement().setProperty("disabled",
                    disabled ? Boolean.TRUE.toString() : null);
        } else {
            checkbox.setEnabled(!disabled);
        }
    }

    private void updateCheckbox(CheckBoxItem<T> checkbox) {
        checkbox.setLabel(getItemLabelGenerator().apply(checkbox.getItem()));
        String title = (getTooltipGenerator() != null
                ? getTooltipGenerator().apply(checkbox.getItem())
                : null);
        if (title != null) {
            checkbox.getElement().setAttribute("title", title);
        } else {
            checkbox.getElement().removeAttribute("title");
        }
        updateEnabled(checkbox);
    }

    private CheckBoxItem<T> createCheckBox(T item) {
        CheckBoxItem<T> checkbox = new CheckBoxItem<>(keyMapper.key(item),
                item);
        checkbox.setWidth("100%");
        checkbox.getElement().setAttribute("role", "listitem");
        // checkbox.setTabIndex(0);
        updateCheckbox(checkbox);
        return checkbox;
    }

    @Override
    public Set<T> getEmptyValue() {
        return new HashSet<T>();
    }

    @Override
    public Element getElement() {
        return super.getElement();
    }

    @Override
    public void setInvalid(boolean invalid) {
        if (invalid) {
            list2.getStyle().set("border", LIST_BORDER_ERROR);
            list2.getStyle().set("background", LIST_BACKGROUND_ERROR);
            list2.getElement().setAttribute("aria-invalid", "true");
            list1.getElement().setAttribute("aria-invalid", "true");
            errorLabel.setVisible(true);
        } else {
            list2.getStyle().set("border", LIST_BORDER);
            list2.getStyle().set("background", LIST_BACKGROUND);
            list2.getElement().removeAttribute("aria-invalid");
            list1.getElement().removeAttribute("aria-invalid");
            errorLabel.setVisible(false);
        }
    }

    @Override
    public void clear() {
        super.clear();
        list2.getChildren().forEach(comp -> {
            Checkbox checkbox = (Checkbox) comp;
            checkbox.setValue(false);
            list2.remove(checkbox);
            list1.add(checkbox);
        });
        setModelValue(getSelectedItems(), true);
    }

    /**
     * The column as LEFT, RIGHT, BOTH
     *
     */
    public enum ColType {
        LEFT, RIGHT, BOTH
    }

    /**
     * Clear the ticks for specified column(s) without affecting the selection.
     *
     * @param column
     *            The column(s) from which to clear the ticks
     */
    public void clearTicks(ColType column) {
        if (column == ColType.LEFT || column == ColType.BOTH) {
            list1.getChildren().forEach(comp -> {
                Checkbox checkbox = (Checkbox) comp;
                checkbox.setValue(false);
            });
        }

        if (column == ColType.RIGHT || column == ColType.BOTH) {
            list2.getChildren().forEach(comp -> {
                Checkbox checkbox = (Checkbox) comp;
                checkbox.setValue(false);
            });
        }
        setAnchor(null);
    }

    /**
     * Clear the ticks after selection action. Default is false.
     *
     * @param clearTicksOnSelect
     *            A boolean value
     */
    public void setClearTicks(boolean clearTicksOnSelect) {
        this.clearTicksOnSelect = clearTicksOnSelect;
    }

    /**
     * Returns if ticks are currently set to be cleared or not.
     *
     * @return A boolean value
     */
    public boolean isClearTicksOnSelect() {
        return clearTicksOnSelect;
    }

    /**
     * Sets the value of this component. If the new value is not equal to the
     * previous value, fires a value change event.
     * <p>
     * The component doesn't accept {@code null} values. The value of a checkbox
     * group without any selected items is an empty set. You can use the
     * {@link #clear()} method to set the empty value.
     *
     * @param value
     *            the new value to set, not {@code null}
     * @throws NullPointerException
     *             if value is {@code null}
     */
    @Override
    public void setValue(Set<T> value) {
        Objects.requireNonNull(value,
                "Cannot set a null value to checkbox group. "
                        + "Use the clear-method to reset the component's value to an empty set.");
        super.setValue(value);
    }

    @Override
    public boolean isInvalid() {
        return errorLabel.isVisible();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        this.dataProvider = dataProvider;
        reset(true);

        setupDataProviderListener(dataProvider);
    }

    private void setupDataProviderListener(DataProvider<T, ?> dataProvider) {
        if (dataProviderListenerRegistration != null) {
            dataProviderListenerRegistration.remove();
        }
        dataProviderListenerRegistration = dataProvider
                .addDataProviderListener(event -> {
                    if (event instanceof DataChangeEvent.DataRefreshEvent) {
                        T otherItem = ((DataChangeEvent.DataRefreshEvent<T>) event)
                                .getItem();
                        this.getCheckboxItems()
                                .filter(item -> Objects.equals(
                                        getItemId(item.item),
                                        getItemId(otherItem)))
                                .findFirst().ifPresent(this::updateCheckbox);
                    } else {
                        reset(false);
                    }
                });
    }

    /**
     * Adds theme variants to the component.
     *
     * @param variants
     *            theme variants to add
     */
    public void addThemeVariants(TwinColSelectVariant... variants) {
        getThemeNames().addAll(
                Stream.of(variants).map(TwinColSelectVariant::getVariantName)
                        .collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants
     *            theme variants to remove
     */
    public void removeThemeVariants(TwinColSelectVariant... variants) {
        getThemeNames().removeAll(
                Stream.of(variants).map(TwinColSelectVariant::getVariantName)
                        .collect(Collectors.toList()));
    }

    @Override
    public Set<T> getSelectedItems() {
        Set<T> set = new HashSet<T>();

        list2.getChildren().forEach(comp -> {
            CheckBoxItem<T> checkbox = (CheckBoxItem) comp;
            set.add(checkbox.getItem());
        });
        return set;
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        Set<T> value = new LinkedHashSet<>(getValue());
        value.removeAll(removedItems);
        value.addAll(addedItems);
        setValue(value);
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        errorLabel.setText(errorMessage);
    }

    /**
     * Gets the current error message from the twincolselect.
     *
     * @return the current error message
     */
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public Registration addSelectionListener(
            MultiSelectionListener<TwinColSelect<T>, T> listener) {
        return addValueChangeListener(event -> listener
                .selectionChange(new MultiSelectionEvent<>(this, this,
                        event.getOldValue(), event.isFromClient())));
    }

    @Override
    protected void setPresentationValue(Set<T> newPresentationValue) {
        if (dataProvider instanceof InMemoryDataProvider) {
            ((InMemoryDataProvider) dataProvider).setFilter(null);
        }
        list2.getChildren().forEach(comp -> {
            Checkbox checkbox = (Checkbox) comp;
            checkbox.setValue(false);
            list2.remove(checkbox);
            list1.add(checkbox);
        });
        newPresentationValue.forEach(item -> {
            list1.getChildren().forEach(comp -> {
                CheckBoxItem checkbox = (CheckBoxItem) comp;
                if (checkbox.getItem().equals(item)) {
                    checkbox.setValue(true);
                }
            });
        });
        moveItems(list1, list2);
    }

    public DataProvider<T, ?> getDataProvider() {
        return dataProvider;
    }

    private Object getItemId(T item) {
        if (getDataProvider() == null) {
            return item;
        }
        return getDataProvider().getId(item);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        updateReadOnlyStyles(readOnly, list1);
        updateReadOnlyStyles(readOnly, list2);
        if (readOnly) {
            list1.getElement().setAttribute("role", "list");
            list2.getElement().setAttribute("role", "list");
            addButton.setEnabled(false);
            allButton.setEnabled(false);
            removeButton.setEnabled(false);
            clearButton.setEnabled(false);
            recycleButton.setEnabled(false);
        } else {
            list1.getElement().setAttribute("role", "listbox");
            list2.getElement().setAttribute("role", "listbox");
            updateButtons();
            recycleButton.setEnabled(true);
        }
    }

    private void updateReadOnlyStyles(boolean readOnly, VerticalLayout list) {
        if (readOnly) {
            list.getStyle().set("background", LIST_BACKGROUND_READONLY);
            list.getStyle().set("border", LIST_BORDER_READONLY);
        } else {
            list.getStyle().set("background", LIST_BACKGROUND);
            list.getStyle().set("border", LIST_BORDER);
        }
        list.getChildren().forEach(comp -> {
            Checkbox checkBox = (Checkbox) comp;
            checkBox.setEnabled(!readOnly);
        });
    }

    /**
     * Define how data providers filter is applied
     * 
     * @param filterMode
     *            Filter mode
     */
    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public void setAllButtonCaption(String text) {
        allButton.setText(text);
    }

    public void setAllButtonIcon(Component icon) {
        allButton.setIcon(icon);
    }

    public void setClearButtonCaption(String text) {
        clearButton.setText(text);
    }

    public void setClearButtonIcon(Component icon) {
        clearButton.setIcon(icon);
    }

    public void setRecycleButtonCaption(String text) {
        recycleButton.setText(text);
    }

    public void setRecycleButtonIcon(Component icon) {
        recycleButton.setIcon(icon);
    }

    public void setRemoveButtonCaption(String text) {
        removeButton.setText(text);
    }

    public void setRemoveButtonIcon(Component icon) {
        removeButton.setIcon(icon);
    }

    public void setAddButtonCaption(String text) {
        addButton.setText(text);
    }

    public void setAddButtonIcon(Component icon) {
        addButton.setIcon(icon);
    }

    /**
     * Set the used PickMode, default is PickMode.DOUBLE.
     * 
     * @param pickMode
     *            The PickMode.
     */
    public void setPickMode(PickMode pickMode) {
        this.pickMode = pickMode;
    }

    private String randomId(String prefix, int chars) {
        int limit = (10 * chars) - 1;
        String key = "" + rand.nextInt(limit);
        key = String.format("%" + chars + "s", key).replace(' ', '0');
        return prefix + "-" + key;
    }

    /**
     * Sets the internationalization properties (texts used for button tooltips)
     * for this component.
     *
     * @param i18n
     *            the internationalized properties, null to disable all
     *            tooltips.
     */
    public void setI18n(TwinColSelectI18n i18n) {
        this.i18n = i18n;
        updateButtons();
    }

    /**
     * Gets the internationalization object previously set for this component.
     *
     * @return the i18n object. It will be <code>null</code>, If the i18n
     *         properties weren't set.
     */
    public TwinColSelectI18n getI18n() {
        return i18n;
    }

    public static class TwinColSelectI18n implements Serializable {
        private String addAllToSelected;
        private String removeAllFromSelected;
        private String toggleSelection;
        private String addToSelected;
        private String removeFromSelected;
        private String selected;
        private String options;

        public String getAddAllToSelected() {
            return addAllToSelected;
        }

        public void setAddAllToSelected(String addAllToSelected) {
            this.addAllToSelected = addAllToSelected;
        }

        public String getRemoveAllFromSelected() {
            return removeAllFromSelected;
        }

        public void setRemoveAllFromSelected(String removeAllFromSelected) {
            this.removeAllFromSelected = removeAllFromSelected;
        }

        public String getToggleSelection() {
            return toggleSelection;
        }

        public void setToggleSelection(String toggleSelection) {
            this.toggleSelection = toggleSelection;
        }

        public String getAddToSelected() {
            return addToSelected;
        }

        public void setAddToSelected(String addToSelected) {
            this.addToSelected = addToSelected;
        }

        public String getRemoveFromSelected() {
            return removeFromSelected;
        }

        public void setRemoveFromSelected(String removeFromSelected) {
            this.removeFromSelected = removeFromSelected;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }

        public static TwinColSelectI18n getDefault() {
            TwinColSelectI18n english = new TwinColSelectI18n();
            english.setToggleSelection("Toggle selection");
            english.setRemoveAllFromSelected("Remove all from selected");
            english.setRemoveFromSelected("Remove from selected");
            english.setAddToSelected("Add to selected");
            english.setAddAllToSelected("Add all to selected");
            english.setSelected("Selected");
            english.setOptions("Options");
            return english;
        }

    }
}
