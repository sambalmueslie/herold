package de.sambalmueslie.herold.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.model.access.AccessController;
import de.sambalmueslie.herold.model.data.Model;
import de.sambalmueslie.herold.model.instance.ModelInstance;

class ModelController<T extends DataModelElement> {
	private static Logger logger = LogManager.getLogger(ModelController.class);

	ModelController(Metadata<T> metadata) {
		this.metadata = metadata;
	}

	Optional<DataModel<T>> createNewInstance(String operatorId) {
		logger.debug("Create new instance for type {}", metadata.getElementType());

		if (model == null) {
			createModel();
		}

		final AccessController<T> accessController = new AccessController<>(operatorId, model);
		final ModelInstance<T> instance = new ModelInstance<>(accessController);
		instances.put(instance.getId(), instance);
		return Optional.of(instance);
	}

	void dispose() {
		logger.debug("Dispose model {}", metadata.getElementType());
		removeAll();
	}

	boolean isUnused() {
		return instances.isEmpty();
	}

	void remove(DataModel<T> modelInstance) {
		if (!(modelInstance instanceof ModelInstance)) return;

		final ModelInstance<T> instance = (ModelInstance<T>) modelInstance;
		final long instanceId = instance.getId();

		if (!instances.containsKey(instanceId)) return;

		logger.debug("Remove instance for type {}", metadata.getElementType());
		instances.remove(instanceId);
		instance.dispose();

		if (isUnused()) {
			model.dispose();
			model = null;
		}
	}

	void removeAll() {
		logger.debug("Remove all model instances for type {}", metadata.getElementType());
		instances.values().forEach(ModelInstance::dispose);
		instances.clear();

		if (model == null) return;

		model.dispose();
		model = null;
	}

	private void createModel() {
		model = new Model<>(metadata);
	}

	private final Map<Long, ModelInstance<T>> instances = new LinkedHashMap<>();
	private final Metadata<T> metadata;
	private LocalModel<T> model;

}
