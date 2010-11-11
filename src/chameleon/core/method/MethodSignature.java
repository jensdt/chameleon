package chameleon.core.method;

import java.util.Iterator;
import java.util.List;

import chameleon.core.declaration.DeclarationWithParametersSignature;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ExtendsConstraint;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeParameter;

public abstract class MethodSignature<E extends MethodSignature,P extends NamespaceElement> extends DeclarationWithParametersSignature<E, P> {

//	public String name() {
//		return _name;
//	}
//	
//	private String _name;
//	
//  /*********************
//   * FORMAL PARAMETERS *
//   *********************/
//
//  public List<TypeReference> typeReferences() {
//    return _parameters.getOtherEnds();
//  }
//
//
//  public void add(TypeReference arg) {
//    _parameters.add(arg.parentLink());
//  }
//
//  public int getNbTypeReferences() {
//    return _parameters.size();
//  }
//
//  private OrderedReferenceSet<E,TypeReference> _parameters = new OrderedReferenceSet<E,TypeReference>((E) this);

	public abstract E clone();
	
	public abstract String name();
	
	public abstract int nbFormalParameters();
	
	public abstract List<Type> parameterTypes() throws LookupException;  
}
