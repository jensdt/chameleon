package chameleon.oo.type.generics;



import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;

public abstract class ActualTypeArgument<E extends ActualTypeArgument> extends NamespaceElementImpl<E, Element> implements TypeReference<E> {

	public ActualTypeArgument() {
	}
	
	public abstract E clone();
	
//	@Override
//	public boolean uniSameAs(Element element) throws LookupException {
//		boolean result = false;
//		if(element instanceof ActualTypeArgument) {
//			return upperBound().sameAs(((ActualTypeArgument) element).upperBound())
//			      && lowerBound().sameAs(((ActualTypeArgument) element).lowerBound());
//		}
//		return result;
//	}
	
	public abstract Type type() throws LookupException;
	
	public final boolean contains(ActualTypeArgument other) throws LookupException {
		return other.upperBound().subTypeOf(upperBound()) && lowerBound().subTypeOf(other.lowerBound());
	}
	
	public abstract Type upperBound() throws LookupException;
	
	public abstract Type lowerBound() throws LookupException;

	public abstract TypeParameter capture(FormalTypeParameter formal);
	
	/**
	 * Return the type reference that must be used for substitution of a formal parameter.
	 * 
	 * @param parameter
	 * @return
	 */
	public TypeReference substitutionReference() {
		throw new ChameleonProgrammerException();
	}

//	public boolean alwaysSameAs(ActualTypeArgument argument) throws LookupException {
//		return false;
//	}

	@Override
	public boolean uniSameAs(Element other) throws LookupException {
		return (other.getClass().equals(getClass())) && (type().sameAs(((ActualTypeArgument)other).type()));
	}

	@Override
	public int hashCode() {
		try {
			int hashCode = type().hashCode();
			return hashCode;
		} catch (LookupException e) {
			throw new ChameleonProgrammerException();
		}
	}
	
	public Type getElement() throws LookupException {
		return type();
	}

	public Type getType() throws LookupException {
		return type();
	}

	public TypeReference intersection(TypeReference other) {
		return other.intersectionDoubleDispatch(this);
	}

	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(), other.clone());
	}

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference<?> other) {
		IntersectionTypeReference<?> result = other.clone();
		result.add(clone());
		return result;
	}

	public Declaration getDeclarator() throws LookupException {
		return getElement();
	}

}