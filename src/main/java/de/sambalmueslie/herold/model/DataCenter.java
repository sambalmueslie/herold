package de.sambalmueslie.herold.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.HeroldDataCenter;

/**
 * The implementation of the {@link HeroldDataCenter}.
 */
class DataCenter implements HeroldDataCenter {
	private static Logger logger = LogManager.getLogger(DataCenter.class);

	@Override
	public <T extends DataModelElement> DataModel<T> createModel(Class<T> elementType) {
		logger.debug("Create new model of type {}", elementType);

		final ModelController<T> controller = getController(elementType);
		return controller.createNewInstance();
	}

	@Override
	public void removeAllModel() {
		logger.debug("Remove all models");

		models.values().forEach(ModelController::dispose);
		models.clear();
	}

	@Override
	public <T extends DataModelElement> void removeAllModel(Class<T> elementType) {
		if (!models.containsKey(elementType)) return;

		logger.debug("Create remove all model of type {}", elementType);

		final ModelController<T> controller = getController(elementType);
		controller.removeAll();

		controller.dispose();
		models.remove(elementType);
	}

	@Override
	public <T extends DataModelElement> void removeModel(Class<T> elementType, DataModel<T> model) {
		if (!models.containsKey(elementType)) return;

		logger.debug("Create remove model of type {}", elementType);

		final ModelController<T> controller = getController(elementType);
		controller.remove(model);

		if (controller.isUnused()) {
			controller.dispose();
			models.remove(elementType);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends DataModelElement> ModelController<T> getController(Class<T> elementType) {
		ModelController<T> controller = (ModelController<T>) models.get(elementType);
		if (controller == null) {
			controller = new ModelController<>(elementType);
			models.put(elementType, controller);
		}
		return controller;
	}

	/** the models by type. */
	private final Map<Class<?>, ModelController<?>> models = new LinkedHashMap<>();

}
