package chameleon.tool;

import chameleon.core.language.Language;

/**
 * @author Marko van Dooren
 */
public abstract class ConnectorImpl implements Connector {

    private Language _language;

    public Language language() {
        return _language;
    }

    /**
     * T MUST BE A SUPERTYPE OF THIS OBJECT!!!
     */
    public <T extends Connector> void setLanguage(Language lang, Class<T> connectorInterface) {
        if (lang!=_language) {
        	  // 1) remove old backpointer
            if (_language!=null) {
                _language.removeConnector(connectorInterface);
            }
            // 2) set _language
            _language = lang;
            // 3) set new backpointer
            if (_language!=null) {
                _language.setConnector(connectorInterface, (T)this);
            }
        }
    }

    public abstract Connector clone();
}
