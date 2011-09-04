package sk.gk2.excusa;

import java.util.*;
import sk.gk2.excusa.code.*;
import sk.gk2.excusa.wsdl.*;

/**
 * Application main class
 * @author stick
 */
public class Main {
	/**
	 * Application entry point 
	 * @param args parameters passed to the application on the command line
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
				"\n EXCUSA - Effective XML Communication Using SOAP and Automata\n\n" +
				" Usage: excusa file|URI [outputdir]\n\n" +
				"     file|URI        location of Web Services Description Language file\n" +
				"     outputdir       output directory (default = webservice name)\n\n" +
				" http://stick.gk2.sk/projects/excusa/\n"
			);
			return;
		}

		try {
			Definition definition = new Definition(args[0]);
			//dumpWSDL(definition);
			Endpoint endpoint = selectEndpointAuto(definition);
			if (definition == null || endpoint == null) return;
			CodeGenerator cg = new CodeGenerator(endpoint);
			if (args.length<2) {
				cg.generate();
			} else {
				cg.generate(args[1]);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	/**
	 * Selects SOAP endpoint from definition
	 * @param definition web service definition to select SOAP endpoint from
	 * @return SOAP endpoint
	 */
	public static Endpoint selectEndpointAuto(Definition definition) {
		Service service = null;
		Endpoint endpoint = null;
		if (definition.getServices().size() < 1) {
			return null;
		}
		service = definition.getServices().values().iterator().next();
		if (service.getEndpoints().size() < 1) {
			return null;
		}
		Endpoint[] p = service.getEndpoints().values().toArray(new Endpoint[service.getEndpoints().size()]);
		for (int i = 0; i < p.length; i++) {
			if (p[i].getType() == EndpointType.SOAP)
				return p[i];
		}
		for (int i = 0; i < p.length; i++) {
			if (p[i].getType() == EndpointType.SOAP12)
				return p[i];
		}
		// HTTP_GET and HTTP_POST are not supported
		return null;
	}

	/**
	 * Selects endpoint interactively
	 * 
	 * @param definition web service definition to select endpoint from
	 * @return Endpoint endpoint chosen by user
	 */
	public static Endpoint selectEndpointInteractive(Definition definition) {
		Service service = null;
		Endpoint endpoint = null;
		// select service
		if (definition.getServices().isEmpty()) {
			System.out.println("WSDL definition must contain at least one service.");
			return null;
		}
		if (definition.getServices().size() > 1) {
			Service[] s = definition.getServices().values().toArray(new Service[definition.getServices().size()]);
			System.out.println("Choose service:");
			for (int i=1; i<=s.length; i++) {
				System.out.println(i + ". " + s[i-1].getName());
			}
			service = s[ readInt(1,s.length)-1 ];
		} else {
			Iterator<Service> i = definition.getServices().values().iterator();
			service = i.next();
		}
		System.out.println("* Selected service " + service.getName());
		// select endpoint
		if (service.getEndpoints().isEmpty()) {
			System.out.println("Selected service must contain at least one endpoint.");
			return null;
		}
		if (service.getEndpoints().size() > 1) {
			Endpoint[] p = service.getEndpoints().values().toArray(new Endpoint[service.getEndpoints().size()]);
			System.out.println("Choose endpoint:");
			for (int i=1; i<=p.length; i++) {
				System.out.println(i + ". " + p[i-1].getName());
			}
			endpoint = p[ readInt(1,p.length)-1 ];
		} else {
			Iterator<Endpoint> i = service.getEndpoints().values().iterator();
			endpoint = i.next();
		}
		System.out.println("* Selected endpoint " + endpoint.getName());
		return endpoint;
	}
	
	/**
	 * Reads integer from stdin
	 * 
	 * @param min minimal accepted value
	 * @param max maximal accepted value
	 * @return integer read from stdin
	 */
	static private int readInt(int min, int max) {
		int r;
		Scanner s = new Scanner(System.in);
		for (;;) {
			System.out.print("> ");
			r = s.nextInt();
			if (r >= min && r <= max) {
				return r;
			}
		}
	}
	
	/**
	 * Dumps WSDL definition to stdout
	 * 
	 * @param definition web service definition
	 */
	static private void dumpWSDL(Definition definition) {
		for (Service s: definition.getServices().values()) {
			System.out.println("service: " + s);
			for (Endpoint p: s.getEndpoints().values()) {
			System.out.println("  endpoint: " + p);
				for (Operation o: p.getOperations().values()) {
					System.out.println("    operation: " + o);
					System.out.println("      input: " + o.getInput());
					if (o.getInput() != null) {
						System.out.println("        grammar:\n" + o.getInput().getGrammar());
					}
					System.out.println("      output: " + o.getOutput());
					if (o.getOutput() != null) {
						System.out.println("        grammar:\n" + o.getOutput().getGrammar());
					}
					for (Message f: o.getFaults()) {
						System.out.println("      fault: " + f);
						System.out.println("        grammar:\n" + f.getGrammar());
					}
				}
			}
		}
	}
}
