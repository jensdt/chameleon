package chameleon.core.expression;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;
import org.rejuse.java.collections.Visitor;

import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.DeclaratorSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.UnresolvableCrossReference;
import chameleon.core.statement.CheckedExceptionList;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 * 
 * @param <D> The type of declaration invoked by this invocation.
 */

public abstract class MethodInvocation<E extends MethodInvocation<E,D>,D extends Method> extends TargetedExpression<E> implements CrossReference<E,Element,D> {

  public MethodInvocation(InvocationTarget target) {
	  setTarget(target);
  }
  
  
  public final DeclarationSelector<D> selector() throws LookupException {
  	if(_selector == null) {
  	  _selector = createSelector();
  	}
    return _selector;
  }
  
  protected abstract DeclarationSelector<D> createSelector() throws LookupException;
  
  protected DeclarationSelector<D> _selector;
  
	/**
	 * TARGET
	 */
	private SingleAssociation<MethodInvocation,InvocationTarget> _target = new SingleAssociation<MethodInvocation,InvocationTarget>(this);


  public InvocationTarget getTarget() {
    return _target.getOtherEnd();
  }

  public void setTarget(InvocationTarget target) {
    if (target != null) {
      _target.connectTo(target.parentLink());
    }
    else {
      _target.connectTo(null);
    }
  }

 
  
	/*********************
	 * ACTUAL PARAMETERS *
	 *********************/
  private OrderedMultiAssociation<MethodInvocation,Expression> _parameters = new OrderedMultiAssociation<MethodInvocation,Expression>(this);
 
  public void addArgument(Expression parameter) {
  	setAsParent(_parameters, parameter);
  }
  
  public void addAllArguments(List<Expression> parameters) {
  	for(Expression parameter: parameters) {
  		addArgument(parameter);
  	}
  }

  public void removeParameter(Expression parameter) {
  	_parameters.remove(parameter.parentLink());
  }

  public List<Expression> getActualParameters() {
    return _parameters.getOtherEnds();
  }

 /*@
   @ public behavior
   @
   @ post \result == getActualParameters().size;
   @*/
  public int nbActualParameters() {
  	return _parameters.size();
  }
  
  public List<Type> getActualParameterTypes() throws LookupException {
	    List<Expression> params = getActualParameters();
	    final List<Type> result = new ArrayList<Type>();
	    for(Expression param:params) {
        Type type = param.getType();
        if (type != null) {
      	  result.add(type);
        }
        else {
          //Type ttt = ((ActualParameter)param).getType(); //DEBUG
          throw new LookupException("Cannot determine type of expression");
        }
	    }
	    return result;
	}

 /*@
   @ also public behavior 
   @
   @ post \result.containsAll(getMethod().getExceptionClause().getExceptionTypes(this));
   @ post (getLanguage().getUncheckedException(getPackage().getDefaultPackage()) != null) ==>
   @      result.contains(getLanguage().getUncheckedException(getPackage().getDefaultPackage());
   @*/  
  public Set getMethodExceptions() throws LookupException {
    Set result = getMethod().getExceptionClause().getExceptionTypes(this);
    Type rte = language(ObjectOrientedLanguage.class).getUncheckedException();
    if (rte != null) {
      result.add(rte);
    }
    return result;
  }
  
 /*@
   @ also public behavior 
   @
   @ post \result.containsAll(getMethodExceptions());
   @ post (getLanguage().getNullInvocationException(getPackage().getDefaultPackage()) != null) ==>
   @      result.contains(getLanguage().getNullInvocationException(getPackage().getDefaultPackage());
   @*/  
  public Set getDirectExceptions() throws LookupException {
    Set result = getMethodExceptions();
    if(getTarget() != null) {
      Util.addNonNull(language(ObjectOrientedLanguage.class).getNullInvocationException(), result);
    }
    return result;
  }
  
 /*@
   @ also public behavior
   @
   @ post \result.contains(actualArgumentList());
   @ post getTarget() != null ==> \result.contains(getTarget());
   @*/  
  public List<Element> children() {
    List<Element> result = new ArrayList<Element>();
    result.addAll(getActualParameters());
    result.addAll(typeArguments());
    Util.addNonNull(getTarget(), result);
    return result;
  }

	//  public Set getDirectExceptions() throws NotResolvedException {
	//    Set result = getMethodExceptions();
	//    Type npe = getLanguage().getNullInvocationException(getPackage().getDefaultPackage());
	//    if(npe != null) {
	//      result.add(npe);
	//    }
	//    result.addAll(getTarget().getDirectExceptions());
	//    Iterator iter = getActualParameters().iterator();
	//    while(iter.hasNext()) {
	//      result.addAll(((Expression)iter.next()).getDirectExceptions());
	//    }
	//    return result;
	//  }

  
  public D getElement() throws LookupException {
  	return getElement(selector());
  }
  
	public Declaration getDeclarator() throws LookupException {
		return getElement(new DeclaratorSelector(selector()));
	}
	
  private SoftReference<D> _cache;
  
  @Override
  public void flushLocalCache() {
  	super.flushLocalCache();
  	_cache = null;
  }
  
  protected D getCache() {
  	D result = null;
  	if(Config.cacheElementReferences() == true) {
  	  result = (_cache == null ? null : _cache.get());
  	}
  	return result;
  }
  
  protected void setCache(D value) {
//  	if(! value.isDerived()) {
    	if(Config.cacheElementReferences() == true) {
    		_cache = new SoftReference<D>(value);
    	}
//  	} else {
//  		_cache = null;
//  	}
  }


	/**
	 * Return the method invoked by this invocation.
	 */
 /*@
	 @ public behavior
	 @
	 @ post \result != null;
	 @
	 @ signals (NotResolvedException) (* The method could not be found *);
	 @*/
//	public abstract D getMethod() throws MetamodelException;
  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
  	X result = null;
  	
  	//OPTIMISATION
  	boolean cache = selector.equals(selector());
  	if(cache) {
  		result = (X) getCache();
  	}
	  if(result != null) {
	   	return result;
	  }
	   
  	InvocationTarget target = getTarget();
  	if(target == null) {
      result = lexicalLookupStrategy().lookUp(selector);
  	} else {
  		result = target.targetContext().lookUp(selector);
  	}
		if (result != null) {
	  	//OPTIMISATION
	  	if(cache) {
	  		setCache((D) result);
	  	}
	    return result;
		}
		else {
			//repeat lookup for debugging purposes.
			//Config.setCaching(false);
	  	if(target == null) {
	      result = lexicalLookupStrategy().lookUp(selector);
	  	} else {
	  		result = target.targetContext().lookUp(selector);
	  	}
			throw new LookupException("Method returned by invocation is null", this);
		}
  }

  public D getMethod() throws LookupException {
  	return getElement();
  }
  
  /**
   * Return a clone of this invocation without target or parameters.
   */
 /*@
   @ protected behavior
   @
   @ post \result != null;
   @*/
  protected abstract E cloneInvocation(InvocationTarget target);
  
  public E clone() {
    InvocationTarget target = null;
    if(getTarget() != null) {
      target = getTarget().clone();
    }
    final E result = cloneInvocation(target);
    for(Expression element: getActualParameters()) {
      result.addArgument(element.clone());
    }
    for(ActualTypeArgument arg: typeArguments()) {
    	result.addArgument(arg.clone());
    }
    return result;
  }

//  public void substituteParameter(String name, Expression expr) throws MetamodelException {
//    if(getTarget()!= null) {
//      getTarget().substituteParameter(name, expr);
//    }
//  }
  
  public CheckedExceptionList getDirectCEL() throws LookupException {
    throw new Error();
  }
  
  public CheckedExceptionList getDirectAbsCEL() throws LookupException {
    throw new Error();
  }
  
  public List<ActualTypeArgument> typeArguments() {
  	return _genericParameters.getOtherEnds();
  }
  
  public void addArgument(ActualTypeArgument arg) {
  	if(arg != null) {
  		_genericParameters.add(arg.parentLink());
  	}
  }
  
  public void addAllTypeArguments(List<ActualTypeArgument> args) {
  	for(ActualTypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(ActualTypeArgument arg) {
  	if(arg != null) {
  		_genericParameters.remove(arg.parentLink());
  	}
  }
  
  private OrderedMultiAssociation<MethodInvocation,ActualTypeArgument> _genericParameters = new OrderedMultiAssociation<MethodInvocation, ActualTypeArgument>(this);

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		try {
			if(getElement() == null) {
				result = result.and(new UnresolvableCrossReference(this));
			}
		} catch (LookupException e) {
			result = result.and(new UnresolvableCrossReference(this));
		}
		return result;
	}

  
}
