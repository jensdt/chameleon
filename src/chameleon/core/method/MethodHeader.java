package chameleon.core.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.exception.ModelException;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.generics.TypeParameterBlock;
import chameleon.util.Util;
/**
 * A class of objects representing method headers. A method header contains for example the name and parameters of a method.
 * 
 * @author Marko van Dooren
 *
 * @param <E>
 * @param <P>
 * @param <S>
 */
public abstract class MethodHeader<E extends MethodHeader, P extends NamespaceElement, S extends MethodSignature> extends DeclarationWithParametersHeader <E,P,S> implements VariableContainer<E, P> { //extends Signature<E, P> 
  
  /**
   * Return the signature of the method of this method header. The signature is generated based on
   * the information in the header.
   * @return
   */
  public abstract S signature();
  
  public abstract void setName(String name);
  
  public abstract E createFromSignature(Signature signature);
  
  protected abstract E cloneThis();
  
  public E clone() {
	  return super.clone();
  }
}
