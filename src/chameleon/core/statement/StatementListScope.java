package chameleon.core.statement;

import java.util.Iterator;

import chameleon.core.MetamodelException;
import chameleon.core.element.Element;
import chameleon.core.scope.LexicalScope;
import chameleon.core.scope.Scope;

/**
 * @author Marko van Dooren
 */
public class StatementListScope extends Scope {

  
  /**
   * @param _block
   * @param _statement
   */
  public StatementListScope(StatementListContainer container, Statement statement) {
    _container = container;
    _statement = statement;
  }
  
  public boolean geRecursive(Scope other) throws MetamodelException {
  	return (
		    (other instanceof StatementListScope) && 
        ((StatementListScope)other).getStatement().ancestors().contains(getContainer()) &&
        (getStatement().before(((StatementListScope)other).getStatement()))
       ) || 
       ((other instanceof LexicalScope) && contains(((LexicalScope)other).element()));
  }
  
  public Statement getStatement() {
    return _statement;
  }
  
  public StatementListContainer getContainer() {
    return _container;
  }

	private StatementListContainer _container;

	private Statement _statement;

	@Override
	public boolean contains(Element element) throws MetamodelException {
		Statement statement = getStatement();
		boolean result = element.ancestors().contains(statement);
		Iterator<Statement> iter = getContainer().statementsAfter(getStatement()).iterator();
		while(! result && iter.hasNext()) {
			result = iter.next().ancestors().contains(statement);
		}
		return result;
	}

}