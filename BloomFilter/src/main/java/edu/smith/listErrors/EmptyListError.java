package edu.smith.listErrors;

/**
 * This class defines our own special error for when an operation is called on
 * an empty list that doesn't make sense.
 * 
 * @author jfoley
 *
 */
@SuppressWarnings("serial")
public class EmptyListError extends RuntimeException {
	public EmptyListError() {
		super("EmptyListError");
	}
}
