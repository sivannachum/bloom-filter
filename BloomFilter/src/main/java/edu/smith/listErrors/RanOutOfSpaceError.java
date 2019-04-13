package edu.smith.listErrors;


/**
 * This is an error for {@link edu.smith.cs.csc212.p6.FixedSizeList}.
 * @author jfoley
 *
 */
@SuppressWarnings("serial")
public class RanOutOfSpaceError extends RuntimeException {
	public RanOutOfSpaceError() {
		super("RanOutOfSpace::FixedSizeList");
	}
}

