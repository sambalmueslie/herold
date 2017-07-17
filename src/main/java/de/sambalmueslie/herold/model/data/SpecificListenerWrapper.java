package de.sambalmueslie.herold.model.data;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.model.parse.Element;
import de.sambalmueslie.herold.model.parse.ElementConverter;

class SpecificListenerWrapper<T extends DataModelElement> implements DataModelChangeListener<T> {

	SpecificListenerWrapper(DataModelChangeListener<T> listener, ElementCache cache, ElementConverter<T> converter) {
		this.listener = listener;
		this.cache = cache;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final SpecificListenerWrapper<?> other = (SpecificListenerWrapper<?>) obj;
		if (listener == null) {
			if (other.listener != null) return false;
		} else if (!listener.equals(other.listener)) return false;
		return true;
	}

	@Override
	public void handleElementAdded(T element) {
		listener.handleElementAdded(element);
	}

	@Override
	public void handleElementChanged(T element) {
		if (element == null) {
			listener.handleElementChanged(element);
			return;
		}

		final long elementId = element.getId();
		final Optional<Element> former = cache.get(elementId);

		if (!former.isPresent()) {
			listener.handleElementChanged(element);
			return;
		}

		// TODO determine the changes, find a suitable method and call em, otherwise default method

	}

	@Override
	public void handleElementRemoved(T element) {
		listener.handleElementRemoved(element);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (listener == null ? 0 : listener.hashCode());
		return result;
	}

	private final ElementCache cache;
	private ElementConverter<T> converter;

	private final DataModelChangeListener<T> listener;

}
