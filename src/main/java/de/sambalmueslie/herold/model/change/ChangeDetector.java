package de.sambalmueslie.herold.model.change;

import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.ChangeListener;
import de.sambalmueslie.herold.model.LocalModel;
import de.sambalmueslie.herold.model.LocalModelFilter;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class ChangeDetector<T extends DataModelElement> extends LocalModelFilter<T> {

	public ChangeDetector(LocalModel<T> model) {
		super(model);

		this.model = model;
		cache = new Cache<>();

		setup();
	}

	@Override
	public void add(long instanceId, T element) {
		if (element == null) return;

		model.add(instanceId, element);
		cache.update(element);
	}

	@Override
	public void dispose() {
		model.dispose();
		cache.clear();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		if (isSpecificListener(listener)) {
			final SpecificListenerWrapper<T> wrapper = new SpecificListenerWrapper<>(specificListenerType, listener, cache);
			model.register(instanceId, wrapper);
		} else {
			model.register(instanceId, listener);
		}
	}

	@Override
	public void remove(long instanceId, long elementId) {
		cache.remove(elementId);
		model.remove(instanceId, elementId);
	}

	@Override
	public void remove(long instanceId, T element) {
		if (element == null) return;
		remove(instanceId, element.getId());
	}

	@Override
	public void removeAll(long instanceId) {
		cache.clear();
		model.removeAll(instanceId);
	}

	@Override
	public Stream<T> stream() {
		return model.stream();
	}

	@Override
	public void update(long instanceId, T element) {
		if (element == null) return;

		// TODO merge with existing element
		// TODO ignore updates without a change

		model.update(instanceId, element);
		cache.update(element);
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

	private final Cache<T> cache;
	private final LocalModel<T> model;
	private Class<?> specificListenerType;
}
