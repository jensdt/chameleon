package chameleon.core.reference;

import chameleon.core.validation.BasicProblem;

public class UnresolvableCrossReference extends BasicProblem {

	public UnresolvableCrossReference(CrossReference element) {
		super(element, "Cross reference could not be resolved.");
	}

}
