package chameleon.core.declaration;

import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.element.Element;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class CompositeQualifiedName<E extends CompositeQualifiedName<E>> extends QualifiedName<E> {

	public List<Signature> signatures() {
		return _signatures.getOtherEnds();
	}
	
	public void append(Signature signature) {
		setAsParent(_signatures, signature);
	}
	
	public void appendAll(List<Signature> signatures) {
		for(Signature signature: signatures) {
			append(signature);
		}
	}
	
	public void prefix(Signature signature) {
		if(signature != null) {
			_signatures.addInFront(signature.parentLink());
		}
	}
	
	public void remove(Signature signature) {
		if(signature != null) {
			_signatures.remove(signature.parentLink());
		}
	}
	
	private OrderedMultiAssociation<QualifiedName, Signature> _signatures = new OrderedMultiAssociation<QualifiedName, Signature>(this);

	public Signature lastSignature() {
		return _signatures.lastElement();
	}
	
	@Override
	public E clone() {
		CompositeQualifiedName result = new CompositeQualifiedName();
		for(Signature signature: signatures()) {
			result.append(signature.clone());
		}
		return (E)result;
	}
	
	public int length() {
		return _signatures.size();
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();//new BasicProblem(this, "TODO: implement verifySelf of FullyQualifiedName");
	}

	public List<? extends Element> children() {
		return signatures();
	}

	public QualifiedName<?> poppedName() {
		CompositeQualifiedName<?> result = clone();
		result.remove(result.lastSignature());
		return result;
	}

	@Override
	public Signature elementAt(int index) {
		return _signatures.elementAt(index);
	} 

	/**
	 * For debugging purposes because Eclipse detail formatters simply don't work.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		List<Signature> signatures = signatures();
		int size = signatures.size();
		for(int i=0; i< size; i++) {
			result.append(signatures.get(i));
			if(i < size - 1) {
				result.append('.');
			}
		}
		return result.toString();
	}

	
}
