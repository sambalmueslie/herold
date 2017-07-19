package de.sambalmueslie.herold.model.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.Value;
import de.sambalmueslie.herold.model.Metadata;

/**
 * @param <T>
 */
class ListenerMgr<T extends DataModelElement> {

	private class Instance {

		public void notifyElementAdded(T element) {
			genericListeners.stream().forEach(l -> l.handleElementAdded(element));
			specificListeners.stream().forEach(l -> l.handleElementAdded(element));
		}

		public void notifyElementChanged(T element, Map<String, Object> formerValues) {
			genericListeners.stream().forEach(l -> l.handleElementChanged(element));

			int specificUpdates = 0;
			for (final Entry<String, Object> e : formerValues.entrySet()) {
				final Method m = changeMethods.get(e.getKey());
				if (m == null) {
					continue;
				}
				specificUpdates++;
				specificListeners.stream().forEach(l -> {
					try {
						m.invoke(l, element, e.getValue());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					}
				});
			}

			if (specificUpdates <= 0 || specificUpdates != formerValues.size()) {
				specificListeners.stream().forEach(l -> l.handleElementChanged(element));
			}

		}

		public void notifyElementRemoved(T element) {
			genericListeners.stream().forEach(l -> l.handleElementRemoved(element));
			specificListeners.stream().forEach(l -> l.handleElementRemoved(element));
		}

		void dispose() {
			genericListeners.clear();
			specificListeners.clear();
		}

		void register(DataModelChangeListener<T> listener) {
			if (listener == null) return;
			if (isSpecificListener(listener)) {
				specificListeners.add(listener);
			} else {
				genericListeners.add(listener);
			}
		}

		void unregister(DataModelChangeListener<T> listener) {
			if (listener == null) return;
			genericListeners.remove(listener);
		}

		private boolean isSpecificListener(DataModelChangeListener<T> listener) {
			return specificListenerType.isAssignableFrom(listener.getClass());
		}

		private final Set<DataModelChangeListener<T>> genericListeners = new LinkedHashSet<>();
		private final Set<DataModelChangeListener<T>> specificListeners = new LinkedHashSet<>();

	}

	private static Logger logger = LogManager.getLogger(ListenerMgr.class);

	ListenerMgr(Metadata<T> metadata) {
		this.specificListenerType = metadata.getSpecificListenerType();
		elementType = metadata.getElementType();
		elementImplType = metadata.getElementImplType();
		setup();
	}

	void dispose() {
		instances.clear();
	}

	void notifyElementAdded(long instanceId, T element) {
		instances.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(e -> e.notifyElementAdded(element));
	}

	void notifyElementRemoved(long instanceId, T element) {
		instances.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(e -> e.notifyElementRemoved(element));
	}

	void notifyElementUpdated(long instanceId, T element, Map<String, Object> formerValues) {
		instances.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(e -> e.notifyElementChanged(element, formerValues));
	}

	void register(long instanceId, DataModelChangeListener<T> listener) {
		Instance instance = instances.get(instanceId);
		if (instance == null) {
			instance = new Instance();
			instances.put(instanceId, instance);
		}

		instance.register(listener);
	}

	void unregister(long instanceId, DataModelChangeListener<T> listener) {
		final Instance instance = instances.get(instanceId);
		if (instance == null) return;

		instance.unregister(listener);

		if (instance.genericListeners.isEmpty()) {
			instances.remove(instanceId);
		}
	}

	void unregisterAll(long instanceId) {
		final Instance instance = instances.remove(instanceId);
		if (instance == null) return;

		instance.dispose();
	}

	private void setup() {
		for (final Field f : elementImplType.getDeclaredFields()) {
			if (!f.isAnnotationPresent(Value.class)) {
				continue;
			}

			final String fieldName = f.getName();
			final String methodName = "handle" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "Changed";

			try {
				final Method m = specificListenerType.getMethod(methodName, elementType, f.getType());
				changeMethods.put(fieldName, m);

			} catch (NoSuchMethodException | SecurityException e) {
				logger.warn("Cannot find change method " + methodName + " for " + fieldName, e);
			}
		}
	}

	private final Map<String, Method> changeMethods = new HashMap<>();

	/** the element implementation type. */
	private final Class<? extends T> elementImplType;

	private final Class<T> elementType;

	/** the instances by id. */
	private final Map<Long, Instance> instances = new HashMap<>();

	private final Class<?> specificListenerType;

}
