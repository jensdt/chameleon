package chameleon.oo.type.generics;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;

public class SuperWildcard<E extends SuperWildcard> extends ActualTypeArgumentWithTypeReference<E> {

	public SuperWildcard(TypeReference ref) {
		super(ref);
	}

	@Override
	public E clone() {
		return (E) new SuperWildcard(typeReference().clone());
	}

//	@Override
//	public boolean contains(ActualTypeArgument other) throws LookupException {
//		return (
//		     (other instanceof BasicTypeArgument) && 
//		     (baseType().subTypeOf(((BasicTypeArgument)other).type()))
//		   ) ||
//		   (
//		  	 (other instanceof SuperWildCard) &&
//		  	 (baseType().subTypeOf(((ExtendsWildCard)other).baseType()))
//		   );
//	}

	@Override
	public Type type() throws LookupException {
		SuperWildcardType superWildCardType = new SuperWildcardType(baseType());
		superWildCardType.setUniParent(this);
		return superWildCardType;
	}

	@Override
	public Type lowerBound() throws LookupException {
		return baseType();
	}

	@Override
	public Type upperBound() throws LookupException {
		return baseType().language(ObjectOrientedLanguage.class).getDefaultSuperClass();
	}

	public Type baseType() throws LookupException {
		TypeReference tref = typeReference();
		if(tref != null) {
			Type type = tref.getType();
			if(type != null) {
			  return type;
			} else {
				throw new LookupException("Lookup of type of generic argument return null."); 
			}
		} else {
			throw new LookupException("Generic argument has no type reference.");
		}
	}

	@Override
	public TypeParameter capture(FormalTypeParameter formal) {
		CapturedTypeParameter newParameter = new CapturedTypeParameter(formal.signature().clone());
		for(TypeConstraint constraint: formal.constraints()) {
			newParameter.addConstraint(constraint.clone());
		}
		newParameter.addConstraint(new SuperConstraint(typeReference().clone()));
    return newParameter;
	}

}