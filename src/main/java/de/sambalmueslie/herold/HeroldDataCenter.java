package de.sambalmueslie.herold;

public interface HeroldDataCenter {
	/**
	 * Create a new model for a specified type.
	 *
	 * @param elementType
	 *            the type
	 */
	<T extends DataModelElement> DataModel<T> createModel(Class<T> elementType);

	/**
	 * Create a new model for a specified type for a defined operator.
	 *
	 * @param elementType
	 *            the type
	 * @param operatorId
	 *            the operator id
	 */
	<T extends DataModelElement> DataModel<T> createModel(Class<T> elementType, String operatorId);

	/**
	 * Remove all model in the data center.
	 */
	void removeAllModel();

	/**
	 * Remove all model of a specified type.
	 *
	 * @param elementType
	 *            the element type
	 */
	<T extends DataModelElement> void removeAllModel(Class<T> elementType);

	/**
	 * Remove a specified model for an element type.
	 *
	 * @param elementType
	 *            the type
	 * @param model
	 *            the model to remove
	 */
	<T extends DataModelElement> void removeModel(Class<T> elementType, DataModel<T> model);
}
