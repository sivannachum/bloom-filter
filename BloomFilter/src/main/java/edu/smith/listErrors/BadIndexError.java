package edu.smith.listErrors;

/**
 * This class defines our own special error for when an operation is called on
 * an list that doesn't have that index.
 * 
 * @author jfoley
 *
 */
@SuppressWarnings("serial")
public class BadIndexError extends RuntimeException {
	public BadIndexError(int index) {
		super("BadIndexError at "+index);
	}
}
