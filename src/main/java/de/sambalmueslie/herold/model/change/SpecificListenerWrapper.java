package de.sambalmueslie.herold.model.change;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

class SpecificListenerWrapper<T extends DataModelElement> implements DataModelChangeListener<T> {

	SpecificListenerWrapper(Class<?> specificListenerType, DataModelChangeListener<T> specificListener, Cache<T> cache) {
		this.specificListenerType = specificListenerType;
		this.specificListener = specificListener;
		this.cache = cache;
	}

	@Override
	public void handleElementAdded(T element) {
		specificListener.handleElementAdded(element);
	}

	@Override
	public void handleElementChanged(T element) {
		if (element == null) {
			specificListener.handleElementChanged(element);
			return;
		}

		final long elementId = element.getId();
		final Optional<T> former = cache.get(elementId);

		if (!former.isPresent()) {
			specificListener.handleElementChanged(element);
			return;
		}

		// TODO determine the changes, find a suitable method and call em, otherwise default method

	}

	@Override
	public void handleElementRemoved(T element) {
		specificListener.handleElementRemoved(element);
	}

	private final Cache<T> cache;
	private final DataModelChangeListener<T> specificListener;
	private final Class<?> specificListenerType;

}
