package sk.gk2.excusa.wsdl;

import java.util.*;
import javax.xml.namespace.QName;
import javax.wsdl.extensions.http.*;
import javax.wsdl.extensions.soap.*;
import javax.wsdl.extensions.soap12.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.ws.commons.schema.*;

import sk.gk2.excusa.schema.*;

/**
 * Web service definition
 * @author stick
 */
public class Definition {

	private HashMap<String, Service> services;
	private HashMap<String, Element> elements;
	private HashMap<String, SimpleType> simpleTypes;
	private HashMap<String, ComplexType> complexTypes;
	private javax.wsdl.Definition origDefinition;

	/**
	 * Constructor
	 * @param wsdl WSDL file or URI to parse
	 * @throws Exception
	 */
	public Definition(String wsdl) throws Exception {
		services = new HashMap<String, Service>();
		elements = new HashMap<String, Element>();
		simpleTypes = new HashMap<String, SimpleType>();
		complexTypes = new HashMap<String, ComplexType>();
		WSDLFactory wsdlfactory = WSDLFactory.newInstance();
		WSDLReader wsdlreader = wsdlfactory.newWSDLReader();
		javax.wsdl.Definition definition = wsdlreader.readWSDL(wsdl);
		translateTypesAndElements(definition);
		translateServices(definition);
		origDefinition = definition;
	}
	
	/**
	 * Gets original Axis definition
	 * @return original Axis definition
	 */
	public javax.wsdl.Definition getOriginal() {
		return origDefinition;
	}
	
	/**
	 * Returns list of services
	 * @return list of services
	 */
	public Map<String, Service> getServices() {
		return services;
	}

	/**
	 * Returns service by its name
	 * @param name name of the service
	 * @return service
	 */
	public Service getService(String name) {
		return services.get(name);
	}

	/**
	 * Returns list of elements
	 * @return list of elements
	 */
	public Map<String, Element> getElements() {
		return elements;
	}
	
	/**
	 * Returns element by its name
	 * @param name name of the element
	 * @return element
	 */
	public Element getElement(String name) {
		return elements.get(name);
	}

	/**
	 * Returns list of simple types
	 * @return list of simple types
	 */
	public Map<String, SimpleType> getSimpleTypes() {
		return simpleTypes;
	}
	
	/**
	 * Returns simple type by its name
	 * @param name name of the simple type
	 * @return simple type
	 */
	public SimpleType getSimpleType(String name) {
		return simpleTypes.get(name);
	}

	/**
	 * Returns list of complex types
	 * @return list of complex types
	 */
	public Map<String, ComplexType> getComplexTypes() {
		return complexTypes;
	}
	
	/**
	 * Return complex type by its name
	 * @param name name of the complex type
	 * @return complex type
	 */
	public ComplexType getComplexType(String name) {
		return complexTypes.get(name);
	}

	/**
	 * Translates types and elements from Axis representation to EXCUSA representation
	 * @param definition web service definition
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Iterator to Iterator<E>
	private void translateTypesAndElements(javax.wsdl.Definition definition) throws Exception {
		// find schema (if any)
		XmlSchema schema = null;
		for (Object o: definition.getTypes().getExtensibilityElements()) {
			if (o instanceof javax.wsdl.extensions.schema.Schema) {
				XmlSchemaCollection xsc = new XmlSchemaCollection();
				schema = xsc.read(((javax.wsdl.extensions.schema.Schema)o).getElement());
				break;
			}				
		}
		if (schema == null) {
			return;
		}
		Iterator<XmlSchemaObject> i = schema.getItems().getIterator();
		while (i.hasNext()) {
			XmlSchemaObject o = i.next();
			if (o instanceof XmlSchemaElement) {
				Element e = translateElement((XmlSchemaElement)o);
				elements.put(e.getName(), e);
			}
			if (o instanceof XmlSchemaSimpleType) {
				SimpleType st = translateSimpleType((XmlSchemaSimpleType)o);
				simpleTypes.put(st.getName(), st);				
			}
			if (o instanceof XmlSchemaComplexType) {
				ComplexType ct = translateComplexType((XmlSchemaComplexType)o);
				complexTypes.put(ct.getName(), ct);
			}
			o.toString();
		}
	}
	
	/**
	 * Translates element from Axis representation to EXCUSA representation
	 * @param xse XML schema element
	 * @return EXCUSA representation of the element
	 * @throws Exception
	 */
	private Element translateElement(XmlSchemaElement xse) throws Exception{
		String name;
		// Schema can use anonymous elements - generate name for them
		if (xse.getName() != null) {
			name = xse.getName(); 
		} else {
			name = "Element_" + Integer.toHexString(xse.hashCode());			
		}
		int min = (int)xse.getMinOccurs();
		int max = xse.getMaxOccurs()!=Long.MAX_VALUE ? (int)xse.getMaxOccurs() : -1;
		Element e = new Element(name, min, max);		
		if (xse.getSchemaType() instanceof XmlSchemaSimpleType) {
			e.setSimpleType( translateSimpleType((XmlSchemaSimpleType)xse.getSchemaType()) );
		} else
		if (xse.getSchemaType() instanceof XmlSchemaComplexType) {
			e.setComplexType( translateComplexType((XmlSchemaComplexType)xse.getSchemaType()) );
		} else {
			throw new Exception("Unknown SchemaType " + xse.getSchemaType());
		}
		return e;
	}
	
	/**
	 * Translates simple type from Axis representation to EXCUSA representation
	 * @param xsst XML schema simple type
	 * @return EXCUSA representation of the simple type
	 * @throws Exception
	 */
	private SimpleType translateSimpleType(XmlSchemaSimpleType xsst) throws Exception {
		// Basic Type
		if (xsst.getContent() == null) {
			SimpleTypeBasic stb = new SimpleTypeBasic(xsst.getName());
			simpleTypes.put(stb.getName(), stb);
			return stb;
		}		
		String name;
		// Schema can use anonymous simple types - generate name for them
		if (xsst.getName() != null) {
			name = xsst.getName(); 
		} else {
			name = "SimpleType_" + Integer.toHexString(xsst.hashCode());			
		}
		// Restriction
		if (xsst.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
			return translateSimpleTypeRestriction(name, (XmlSchemaSimpleTypeRestriction)xsst.getContent());
		} else
		// List
		if (xsst.getContent() instanceof XmlSchemaSimpleTypeList) {
			return translateSimpleTypeList(name, (XmlSchemaSimpleTypeList)xsst.getContent());
		} else
		// Union
		if (xsst.getContent() instanceof XmlSchemaSimpleTypeUnion) {
			return translateSimpleTypeUnion(name, (XmlSchemaSimpleTypeUnion)xsst.getContent());
		}
		throw new Exception("Unknown SimpleType " + xsst);
	}

	/**
	 * Translates simple type - restriction from Axis representation to EXCUSA representation
	 * @param xsstr XML schema simple type - restriction
	 * @return EXCUSA representation of the simple type - restriction
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Iterator to Iterator<E>
	private SimpleType translateSimpleTypeRestriction(String name, XmlSchemaSimpleTypeRestriction xsstr) throws Exception {
		String baseType;
		// anonymous simple type
		if (xsstr.getBaseType() != null) {
			SimpleType b = translateSimpleType(xsstr.getBaseType());
			simpleTypes.put(b.getName(), b);
			baseType = b.getName();
		} else {
			baseType = xsstr.getBaseTypeName().getLocalPart();	
		}		
		SimpleTypeRestriction str = new SimpleTypeRestriction(name, baseType);
		// add facets restrictions
		Iterator<XmlSchemaFacet> i = xsstr.getFacets().getIterator();
		while (i.hasNext()) {
			XmlSchemaFacet f = i.next();
			// minExclusive
			if (f instanceof XmlSchemaMinExclusiveFacet) {
				str.setMinExclusive(Integer.decode((String)f.getValue()));				
			} else
			// minInclusive
			if (f instanceof XmlSchemaMinInclusiveFacet) {
				str.setMinInclusive(Integer.decode((String)f.getValue()));				
			} else
			// maxExclusive
			if (f instanceof XmlSchemaMaxExclusiveFacet) {
				str.setMaxExclusive(Integer.decode((String)f.getValue()));				
			} else
			// maxInclusive
			if (f instanceof XmlSchemaMaxInclusiveFacet) {
				str.setMaxInclusive(Integer.decode((String)f.getValue()));				
			} else
			// totalDigits
			if (f instanceof XmlSchemaTotalDigitsFacet) {
				str.setTotalDigits(Integer.decode((String)f.getValue()));	
			} else
			// fractionDigits
			if (f instanceof XmlSchemaFractionDigitsFacet) {
				str.setFractionDigits(Integer.decode((String)f.getValue()));				
			} else
			// length
			if (f instanceof XmlSchemaLengthFacet) {
				str.setLength(Integer.decode((String)f.getValue()));				
			} else
			// minLength
			if (f instanceof XmlSchemaMinLengthFacet) {
				str.setMinLength(Integer.decode((String)f.getValue()));				
			} else
			// maxLength
			if (f instanceof XmlSchemaMaxLengthFacet) {
				str.setMaxLength(Integer.decode((String)f.getValue()));				
			} else
			// enumeration
			if (f instanceof XmlSchemaEnumerationFacet) {
				str.addEnumeration((String)f.getValue());				
			} else
			// pattern
			if (f instanceof XmlSchemaPatternFacet) {
				str.addPattern((String)f.getValue());
			}
		}
		return str;
	}
	
	/**
	 * Translates simple type - list from Axis representation to EXCUSA representation
	 * @param xsstl XML schema simple type - list
	 * @return EXCUSA representation of the simple type - list
	 * @throws Exception
	 */	
	private SimpleType translateSimpleTypeList(String name, XmlSchemaSimpleTypeList xsstl) throws Exception {
		String itemType;
		// anonymous simple type
		if (xsstl.getItemType() != null) {
			SimpleType b = translateSimpleType(xsstl.getItemType());
			simpleTypes.put(b.getName(), b);
			itemType = b.getName();
		} else {
			itemType = xsstl.getItemTypeName().getLocalPart();	
		}		
		return new SimpleTypeList(name, itemType);
	}
	
	/**
	 * Translates simple type - union from Axis representation to EXCUSA representation
	 * @param xsstu XML schema simple type - union
	 * @return EXCUSA representation of the simple type - union
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Iterator to Iterator<E>
	private SimpleType translateSimpleTypeUnion(String name, XmlSchemaSimpleTypeUnion  xsstu) throws Exception {
		ArrayList<String> baseTypes = new ArrayList<String>();
		// anonymous types
		if (xsstu.getBaseTypes() != null) {
			Iterator<XmlSchemaSimpleType> i = xsstu.getBaseTypes().getIterator();
			while (i.hasNext()) {
				XmlSchemaSimpleType xsst = i.next();
				SimpleType st = translateSimpleType(xsst);
				simpleTypes.put(st.getName(), st);
				baseTypes.add(st.getName());
			}
		}
		// non-anonymous types
		if (xsstu.getMemberTypesQNames() != null) {
			for (QName n: xsstu.getMemberTypesQNames()) {
				baseTypes.add(n.getLocalPart());
			}
		}
		return new SimpleTypeUnion(name, baseTypes);
	}	

	/**
	 * Translates complex type from Axis representation to EXCUSA representation
	 * @param xsct XML schema complex type
	 * @return EXCUSA representation of the complex type
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Iterator to Iterator<E>
	private ComplexType translateComplexType(XmlSchemaComplexType xsct) throws Exception {
		String name;
		// Schema can use anonymous complex types - generate name for them
		if (xsct.getName() != null) {
			name = xsct.getName(); 
		} else {
			name = "ComplexType_" + Integer.toHexString(xsct.hashCode());			
		}
		if (xsct.getParticle() instanceof XmlSchemaGroupBase) {
			XmlSchemaGroupBase xsgb = (XmlSchemaGroupBase)xsct.getParticle();
			int min = (int)xsgb.getMinOccurs();
			int max = xsgb.getMaxOccurs()!=Long.MAX_VALUE ? (int)xsgb.getMaxOccurs() : -1;
			ComplexTypeGroupType type = null;
			if (xsgb instanceof XmlSchemaAll) {
				type = ComplexTypeGroupType.All;
			} else
			if (xsgb instanceof XmlSchemaChoice) {
				type = ComplexTypeGroupType.Choice;
			} else
			if (xsgb instanceof XmlSchemaSequence) {
				type = ComplexTypeGroupType.Sequence;
			}
			ComplexType ct = new ComplexType(name, min, max, type);
			Iterator<XmlSchemaElement> i = xsgb.getItems().getIterator();
			while (i.hasNext()) {
				ct.addElement( translateElement(i.next()) );
			}
			return ct;
		}
		// Unsupported ComplexType
		System.err.println("Unsupported ComplexType: " + name);
		return new ComplexType(name, 1, 1, ComplexTypeGroupType.Sequence);
	}

	/**
	 * Translates services from Axis representation to EXCUSA representation
	 * @param definition web service definition
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Map to Map<K,V>
	private void translateServices(javax.wsdl.Definition definition) throws Exception {
		Map<QName, javax.wsdl.Service> m = definition.getServices();
		for (javax.wsdl.Service s: m.values()) {
			Service service = translateService(s);
			service.setDefinition(this);
			services.put(service.getName(), service);
		}
	}

	/**
	 * Translates service from Axis representation to EXCUSA representation
	 * @param service Axis web service
	 * @return EXCUSA representation of the web service
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Map to Map<K,V>
	private Service translateService(javax.wsdl.Service service) throws Exception {
		org.w3c.dom.Element doc = service.getDocumentationElement();
		String documentation = (doc!=null) ? doc.getTextContent(): null;
		Service ret = new Service(service.getQName().getLocalPart(), documentation);
		Map<String, javax.wsdl.Port> m = service.getPorts();
		for (javax.wsdl.Port p: m.values()  ) {
			ret.addEndpoint(translateEndpoint(p));
		}
		return ret;
	}

	/**
	 * Translates endpoint from Axis representation to EXCUSA representation
	 * @param port Axis port
	 * @return EXCUSA representation of the endpoint
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from List to List<K,V>
	private Endpoint translateEndpoint(javax.wsdl.Port port) throws Exception {
		EndpointType type = null;
		String location = null;

		// detect type
		for (Object o: port.getBinding().getExtensibilityElements()) {
			if (o instanceof HTTPBinding) {
				if (((HTTPBinding)o).getVerb().equalsIgnoreCase("GET")) {
					type = EndpointType.HTTP_GET;
				} else
				if (((HTTPBinding)o).getVerb().equalsIgnoreCase("POST")) {
					type = EndpointType.HTTP_POST;
				}
				break;
			} else
			if (o instanceof SOAPBinding) {
				type = EndpointType.SOAP;
				break;
			} else
			if (o instanceof SOAP12Binding) {
				type = EndpointType.SOAP12;
				break;
			}	
		}
		
		// detect address
		for (Object o: port.getExtensibilityElements()) {
			if ((type==EndpointType.HTTP_GET || type==EndpointType.HTTP_POST) && o instanceof HTTPAddress) {
				location = ((HTTPAddress)o).getLocationURI();
				break;
			} else
			if (type==EndpointType.SOAP && o instanceof SOAPAddress) {
				location = ((SOAPAddress)o).getLocationURI();
				break;
			} else
			if (type==EndpointType.SOAP12 && o instanceof SOAP12Address) {
				location = ((SOAP12Address)o).getLocationURI();
				break;
			}	
		}

		if (type == null || location == null) {
			throw new Exception("Unknown Endpoint Type");
		}
		
		org.w3c.dom.Element doc = port.getDocumentationElement();
		String documentation = (doc!=null) ? doc.getTextContent(): null;
		
		Endpoint ret = new Endpoint(port.getName(), type, location, documentation);
		List<javax.wsdl.BindingOperation> l = port.getBinding().getBindingOperations();
		for (javax.wsdl.BindingOperation bo: l ) {
			ret.addOperation(translateOperation(bo, type) );
		}

		return ret;
	}

	/**
	 * Translates operation from Axis representation to EXCUSA representation
	 * @param bindingOperation Axis binding operation
	 * @param type endpoint type
	 * @return EXCUSA representation of the operation
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Map to Map<K,V>
	private Operation translateOperation(javax.wsdl.BindingOperation bindingOperation, EndpointType type) throws Exception {
		javax.wsdl.Operation operation = bindingOperation.getOperation();
		String action = null;
		String location = null;
		for (Object o: bindingOperation.getExtensibilityElements()) {
			if (o instanceof HTTPOperation && (type==EndpointType.HTTP_GET || type==EndpointType.HTTP_POST)) {
				location = ((HTTPOperation)o).getLocationURI();
			} else
			if (o instanceof SOAPOperation && type==EndpointType.SOAP) {
				action = ((SOAPOperation)o).getSoapActionURI();
			} else
			if (o instanceof SOAP12Operation && type==EndpointType.SOAP12) {
				action = ((SOAP12Operation)o).getSoapActionURI();
			}
		}
		org.w3c.dom.Element doc = operation.getDocumentationElement();
		String documentation = (doc!=null) ? doc.getTextContent() : null;
		Operation ret = new Operation(operation.getName(), location, action, documentation);
		if (operation.getInput() != null && operation.getInput().getMessage() != null) {
			ret.setInput(translateMessage(operation.getInput().getMessage(), MessageType.Input));	
		} else {
			throw new Exception("Input message not defined");
		}
		if (operation.getOutput() != null && operation.getOutput().getMessage() != null) {
			ret.setOutput(translateMessage(operation.getOutput().getMessage(), MessageType.Output));	
		} else {
			throw new Exception("Output message not defined");
		}
		Map<String,javax.wsdl.Fault> m = operation.getFaults();
		for (javax.wsdl.Fault f: m.values()) {
			if (f.getMessage() != null) {
				ret.addFault(translateMessage(f.getMessage(), MessageType.Fault));
			} else {
				throw new Exception("Fault message not defined");
			}
		}
		return ret;
	}
	
	/**
	 * Translates message from Axis representation to EXCUSA representation
	 * @param msg Axis message
	 * @param msgtype message type
	 * @return EXCUSA representation of the message
	 * @throws Exception
	 */
	@SuppressWarnings(value={"unchecked"}) // unchecked conversion from Map to Map<K,V>
	private Message translateMessage(javax.wsdl.Message msg, MessageType msgtype) throws Exception {
		org.w3c.dom.Element doc = msg.getDocumentationElement();
		String documentation = (doc!=null) ? doc.getTextContent(): null;
		Message ret = new Message(msg.getQName().getLocalPart(), documentation, msgtype);
		Map<String,javax.wsdl.Part> m = msg.getParts();
		for (javax.wsdl.Part p: m.values()) {
			String element = null;
			String type = null;
			if (p.getElementName() != null) {
				element = p.getElementName().getLocalPart();
			}
			if (p.getTypeName() != null) {
				type = p.getTypeName().getLocalPart();
				if (!simpleTypes.containsKey(type)) {
					// check if it is basic type (and add it to types if needed)
					SimpleTypeBasic stb = new SimpleTypeBasic(type);
					if (stb.getType() != null) {
						simpleTypes.put(type, stb);
					}
				}
			}
			ret.addPart( new MessagePart(p.getName(), element, type) );
		}
		ret.createGrammar(this);
		return ret;
	}
}
