package de.sambalmueslie.herold.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.HeroldDataCenter;
import de.sambalmueslie.herold.annotations.ImplementationType;

/**
 * The implementation of the {@link HeroldDataCenter}.
 */
class DataCenter implements HeroldDataCenter {

	private static Logger logger = LogManager.getLogger(DataCenter.class);

	/**
	 * Constructor.
	 *
	 * @param operatorId
	 *            {@link #globalOperatorId}
	 */
	public DataCenter(String operatorId) {
		this.globalOperatorId = operatorId;
	}

	@Override
	public <T extends DataModelElement> Optional<DataModel<T>> createModel(Class<T> elementType) {
		return createModel(elementType, globalOperatorId);
	}

	@Override
	public <T extends DataModelElement> Optional<DataModel<T>> createModel(Class<T> elementType, String operatorId) {
		logger.debug("Create new model of type {} for operator {}", elementType, operatorId);

		final Metadata<T> metadata = getMetadata(elementType);

		if (!isValid(metadata)) {
			logger.error("Cannot create model of invalid type {}", elementType);
			return Optional.empty();
		}

		final ModelController<T> controller = getController(metadata);
		return controller.createNewInstance(operatorId);
	}

	@Override
	public void removeAllModel() {
		logger.debug("Remove all models");

		models.values().forEach(ModelController::dispose);
		models.clear();
		metadataCache.clear();
	}

	@Override
	public <T extends DataModelElement> void removeAllModel(Class<T> elementType) {
		if (!models.containsKey(elementType)) return;

		logger.debug("Create remove all model of type {}", elementType);
		final Metadata<T> metadata = getMetadata(elementType);
		final ModelController<T> controller = getController(metadata);
		controller.removeAll();

		controller.dispose();

		models.remove(metadata.getElementImplType());
		metadataCache.remove(elementType);
	}

	@Override
	public <T extends DataModelElement> void removeModel(Class<T> elementType, DataModel<T> model) {
		if (!models.containsKey(elementType)) return;

		logger.debug("Create remove model of type {}", elementType);

		final Metadata<T> metadata = getMetadata(elementType);
		final ModelController<T> controller = getController(metadata);
		controller.remove(model);

		if (controller.isUnused()) {
			controller.dispose();
			models.remove(metadata.getElementImplType());
			metadataCache.remove(elementType);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends DataModelElement> ModelController<T> getController(Metadata<T> metadata) {
		final Class<?> implType = metadata.getElementImplType();
		ModelController<T> controller = (ModelController<T>) models.get(implType);
		if (controller == null) {
			controller = new ModelController<>(metadata);
			models.put(implType, controller);
		}
		return controller;
	}

	@SuppressWarnings("unchecked")
	private <T extends DataModelElement> Metadata<T> getMetadata(Class<T> elementType) {
		if (metadataCache.containsKey(elementType)) return (Metadata<T>) metadataCache.get(elementType);

		final Metadata<T> metadata = new Metadata<>(elementType);
		metadataCache.put(elementType, metadata);
		return metadata;
	}

	private <T extends DataModelElement> boolean isValid(Metadata<T> metadata) {
		final Class<T> elementType = metadata.getElementType();
		final Class<? extends T> implType = metadata.getElementImplType();

		if (implType.isInterface()) {
			logger.error("Element type {} must define annotation {} or be an instanceable type.", elementType, ImplementationType.class);
			return false;
		}

		try {
			if (implType.getConstructor() == null) {
				logger.error("Element type {} must define default constructor.", elementType);
				return false;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error("Element type {} must define accesible default constructor.", elementType);
			return false;
		}

		if (!elementType.isAssignableFrom(implType)) {
			logger.error("Implementation type {} must implement {}.", implType, elementType);
			return false;
		}

		if (!DataModelElement.class.isAssignableFrom(implType)) {
			logger.error("Element type {} must implement {}.", elementType, DataModelElement.class);
			return false;
		}

		return true;
	}

	/** the operator id. */
	private final String globalOperatorId;

	private final Map<Class<?>, Metadata<?>> metadataCache = new HashMap<>();

	/** the models by type. */
	private final Map<Class<?>, ModelController<?>> models = new LinkedHashMap<>();

}
