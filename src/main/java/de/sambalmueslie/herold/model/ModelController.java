package de.sambalmueslie.herold.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelElement;

class ModelController<T extends DataModelElement> {
	private static Logger logger = LogManager.getLogger(ModelController.class);

	ModelController(Class<T> elementType) {
		this.elementType = elementType;
	}

	DataModel<T> createNewInstance(String operatorId) {
		logger.debug("Create new instance for type {}", elementType);

		if (model == null) {
			createModel();
		}

		final ModelAccessController<T> accessController = new ModelAccessController<>(operatorId, model, elementType);
		final ModelInstance<T> instance = new ModelInstance<>(accessController);
		instances.put(instance.getId(), instance);
		return instance;
	}

	void dispose() {
		logger.debug("Dispose model {}", elementType);
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

		logger.debug("Remove instance for type {}", elementType);
		instances.remove(instanceId);
		instance.dispose();

		if (isUnused()) {
			model.dispose();
			model = null;
		}
	}

	void removeAll() {
		logger.debug("Remove all model instances for type {}", elementType);
		instances.values().forEach(ModelInstance::dispose);
		instances.clear();

		if (model == null) return;

		model.dispose();
		model = null;
	}

	private void createModel() {
		model = new Model<>();
	}

	private final Class<T> elementType;
	private final Map<Long, ModelInstance<T>> instances = new LinkedHashMap<>();
	private Model<T> model;

}
