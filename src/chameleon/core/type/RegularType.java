package chameleon.core.type;

import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.member.Member;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.generics.TypeParameterBlock;
import chameleon.core.type.inheritance.InheritanceRelation;

public class RegularType extends Type {

	public RegularType(SimpleNameSignature sig) {
		super(sig);
		_body.connectTo(new ClassBody().parentLink());
		_parameters.connectTo(new TypeParameterBlock().parentLink());
	}

  public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
  	if(element == parameterBlock() || element.isDerived()) {
  		return parent().lexicalLookupStrategy(this);
  	} else {
  		return super.lexicalLookupStrategy(element);
  	}
  }
	@Override
	public RegularType clone() {
		RegularType result = cloneThis();
		result.copyContents(this);
		return result;
	}

	protected RegularType cloneThis() {
		return new RegularType(signature().clone());
	}

	private SingleAssociation<Type, ClassBody> _body = new SingleAssociation<Type, ClassBody>(this);

	public ClassBody body() {
		return _body.getOtherEnd();
	}
	
	public void setBody(ClassBody body) {
		if(body == null) {
			throw new ChameleonProgrammerException("Body passed to setBody is null.");
		} else {
			_body.connectTo(body.parentLink());
		}
	}
	
	public void add(TypeElement element) {
	  body().add(element);
	}
	
	@Override
	public void remove(TypeElement element) throws ChameleonProgrammerException {
		body().remove(element);
	}

  /**
   * Return the members directly declared by this type.
   * @return
   */
 /*@
   @ public behavior
   @
   @ post \result == body.members();
   @*/
  public List<Member> directlyDeclaredMembers() {
     return body().members();
  }

	private OrderedMultiAssociation<Type, InheritanceRelation> _inheritanceRelations = new OrderedMultiAssociation<Type, InheritanceRelation>(this);

	public void removeInheritanceRelation(InheritanceRelation relation) {
		_inheritanceRelations.remove(relation.parentLink());
	}

	@Override
	public List<InheritanceRelation> inheritanceRelations() {
		return _inheritanceRelations.getOtherEnds();
	}
	
	@Override
	public void addInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException {
		_inheritanceRelations.add(relation.parentLink());
	}

	@Override
	public <D extends Member> List<D> directlyDeclaredMembers(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(directlyDeclaredMembers());
	}
	
  public void replace(TypeElement oldElement, TypeElement newElement) {
		body().replace(oldElement, newElement);
  }

	@Override
	public Type baseType() {
		return this;
	}

	private SingleAssociation<Type, TypeParameterBlock> _parameters = new SingleAssociation<Type, TypeParameterBlock>(this);
	
	public TypeParameterBlock parameterBlock() {
		return _parameters.getOtherEnd();
	}
	
	public List<TypeParameter> parameters() {
		return parameterBlock().parameters();
	}
	
	public void addParameter(TypeParameter parameter) {
		parameterBlock().add(parameter);
	}

	public void removeParameter(TypeParameter parameter) {
		parameterBlock().add(parameter);
	}
	
	public void replaceParameter(TypeParameter oldParameter, TypeParameter newParameter) {
		parameterBlock().replace(oldParameter, newParameter);
	}

	@Override
	public List<? extends TypeElement> directlyDeclaredElements() {
		return body().elements();
	}



}
