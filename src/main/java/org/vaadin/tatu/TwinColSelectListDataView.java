package org.vaadin.tatu;

import com.vaadin.flow.data.provider.AbstractListDataView;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.IdentifierProvider;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.SerializableSupplier;

public class TwinColSelectListDataView<T> extends AbstractListDataView<T> {

    private SerializableConsumer<IdentifierProvider<T>> identifierChangedCallback;

    /**
     * Creates a new in-memory data view for TwinColSelect and verifies the
     * passed data provider is compatible with this data view implementation.
     *
     * @param dataProviderSupplier
     *            data provider supplier
     * @param twinColSelect
     *            TwinColSelect instance for this DataView
     * @param filterOrSortingChangedCallback
     *            callback, which is being invoked when the TwinColSelect's
     *            filtering or sorting changes, not <code>null</code>
     */
    public TwinColSelectListDataView(
            SerializableSupplier<DataProvider<T, ?>> dataProviderSupplier,
            TwinColSelect<T> twinColSelect,
            SerializableBiConsumer<SerializablePredicate<T>, SerializableComparator<T>> filterOrSortingChangedCallback) {
        super(dataProviderSupplier, twinColSelect,
                filterOrSortingChangedCallback);
    }

    /**
     * Creates a new in-memory data view for TwinColSelect and verifies the
     * passed data provider is compatible with this data view implementation.
     *
     * @param dataProviderSupplier
     *            data provider supplier
     * @param twinColSelect
     *            TwinColSelect instance for this DataView
     * @param identifierChangedCallback
     *            callback method which should be called when identifierProvider
     *            is changed
     * @param filterOrSortingChangedCallback
     *            callback, which is being invoked when the TwinColSelect's
     *            filtering or sorting changes, not <code>null</code>
     */
    public TwinColSelectListDataView(
            SerializableSupplier<DataProvider<T, ?>> dataProviderSupplier,
            TwinColSelect<T> twinColSelect,
            SerializableConsumer<IdentifierProvider<T>> identifierChangedCallback,
            SerializableBiConsumer<SerializablePredicate<T>, SerializableComparator<T>> filterOrSortingChangedCallback) {
        super(dataProviderSupplier, twinColSelect,
                filterOrSortingChangedCallback);
        this.identifierChangedCallback = identifierChangedCallback;
    }

    @Override
    public void setIdentifierProvider(
            IdentifierProvider<T> identifierProvider) {
        super.setIdentifierProvider(identifierProvider);

        if (identifierChangedCallback != null) {
            identifierChangedCallback.accept(identifierProvider);
        }
    }

}
