package de.sambalmueslie.herold.model.change;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.ChangeListener;
import de.sambalmueslie.herold.model.LocalModel;
import de.sambalmueslie.herold.model.Metadata;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class ChangeDetector<T extends DataModelElement> implements LocalModel<T> {

	public ChangeDetector(LocalModel<T> model) {
		this.model = model;

		setup();
	}

	@Override
	public boolean contains(long elementId) {
		return model.contains(elementId);
	}

	@Override
	public void dispose() {
		specificListener.clear();
		model.dispose();
	}

	@Override
	public Optional<T> get(long elementId) {
		return model.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		return model.getAll();
	}

	@Override
	public Metadata<T> getMetadata() {
		return model.getMetadata();
	}

	@Override
	public void add(long instanceId, T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(long instanceId, long elementId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(long instanceId, T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(long instanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(long instanceId, T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEmpty() {
		return model.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		if (isSpecificListener(listener)) {

		}

		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return model.size();
	}

	@Override
	public Stream<T> stream() {
		return model.stream();
	}

	@Override
	public void unregister(long instanceId) {
		model.unregister(instanceId);
	}

	private boolean isSpecificListener(DataModelChangeListener<T> listener) {
		return specificListenerType.isAssignableFrom(listener.getClass());
	}

	private void setup() {
		final Optional<ChangeListener> result = AnnotationSpy.findAnnotation(getMetadata().getElementType(), ChangeListener.class);
		if (result.isPresent()) {
			specificListenerType = result.get().value();
		}
	}

	private final LocalModel<T> model;
	private final Map<Long, DataModelChangeListener<T>> specificListener = new LinkedHashMap<>();
	private Class<?> specificListenerType;
}
