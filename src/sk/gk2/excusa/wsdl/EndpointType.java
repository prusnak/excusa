package sk.gk2.excusa.wsdl;

/**
 * Enumerates endpoint types
 * @author stick
 */
public enum EndpointType {
	HTTP_GET,
	HTTP_POST,
	SOAP,
	SOAP12;
	
	/**
	 * Returns string representation of this type
	 */
	@Override
	public String toString() {
		switch (this) {
			case HTTP_GET:
				return "HTTP GET";
			case HTTP_POST:
				return "HTTP POST";
			case SOAP:
				return "SOAP 1.1";
			case SOAP12:
				return "SOAP 1.2";
			default:
				return "?";
		}
	}
}
