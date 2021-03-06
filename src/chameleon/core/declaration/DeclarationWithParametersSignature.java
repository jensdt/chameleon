package chameleon.core.declaration;

import java.util.Iterator;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.method.MethodHeader;
import chameleon.core.method.MethodSignature;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ExtendsConstraint;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeParameter;

public abstract class DeclarationWithParametersSignature<E extends DeclarationWithParametersSignature,P extends Element> extends Signature<E, P> {
	public abstract E clone();
	
	public abstract String name();
	
	public abstract int nbFormalParameters();
	
	public abstract List<Type> parameterTypes() throws LookupException;
	
	public boolean sameParameterBoundsAs(DeclarationWithParametersSignature other) throws LookupException {
  	// substitute paramaters.
  	Method method = (Method)other.nearestAncestor(Method.class); // TODO: change this to invocation after refactoring those
  	MethodHeader otherHeader = method.header();
  	int nbOtherFormalParameters = otherHeader.nbFormalParameters();
  	int nbMyFormalParameters = nbFormalParameters();
  	boolean result = nbOtherFormalParameters == nbMyFormalParameters;
  	if(result) {
  		MethodHeader clonedHeader = otherHeader.clone();
  		clonedHeader.setUniParent(method);
  		List<TypeParameter> cloneTypeParameters = clonedHeader.typeParameters();
  		List<TypeParameter> myTypeParameters = nearestAncestor(Method.class).typeParameters();
  		int size = myTypeParameters.size();
  		result = (size == cloneTypeParameters.size());
  		if(result) {
  			List<FormalParameter> clonedFormalParameters = (List<FormalParameter>)clonedHeader.formalParameters();
  			for(int i=0; i < size; i++) {
  				TypeParameter myTypeParameter = myTypeParameters.get(i);
  				TypeParameter clonedTypeParameter = cloneTypeParameters.get(i);
  				TypeReference replacement = language(ObjectOrientedLanguage.class).createTypeReference(myTypeParameter.signature().name());
  				replacement.setUniParent(myTypeParameter.parent());
  				// substitute in formal parameters
  				for(FormalParameter formal: clonedFormalParameters) {
  					language(ObjectOrientedLanguage.class).replace(replacement, clonedTypeParameter, formal.getTypeReference());
  				}

  				// substitute in type bounds of the type parameters of the cloned header.
  				for(TypeParameter typeParameter: (List<TypeParameter>)clonedHeader.typeParameters()) {
  					if(typeParameter instanceof FormalTypeParameter) {
  						FormalTypeParameter formal = (FormalTypeParameter) typeParameter;
  						language(ObjectOrientedLanguage.class).replace(replacement, clonedTypeParameter, ((ExtendsConstraint)formal.constraints().get(0)).typeReference());
  					}
  				}
  			}
  			List<Type> myFormalParameterTypes = parameterTypes();
  			for(int i=0; result && i < nbMyFormalParameters; i++) {
  				result = clonedFormalParameters.get(i).getType().sameAs(myFormalParameterTypes.get(i));
  			}
  			for(int i=0; result && i < size; i++) {
  				result = cloneTypeParameters.get(i).upperBound().sameAs(myTypeParameters.get(i).upperBound());
  			}
  		}
  	}
  	return result;
	}
	
  public boolean sameParameterTypesAs(DeclarationWithParametersSignature other) throws LookupException {
  	boolean result = false;
  	if (other != null) {
			List<Type> mine = parameterTypes();
			List<Type> others = other.parameterTypes();
			result = mine.size() == others.size();
			Iterator<Type> iter1 = mine.iterator();
			Iterator<Type> iter2 = others.iterator();
			while (result && iter1.hasNext()) {
        result = result && iter1.next().sameAs(iter2.next());
			}
		}
  	return result;
  }
}
