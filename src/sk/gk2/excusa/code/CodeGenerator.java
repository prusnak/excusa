package sk.gk2.excusa.code;

import java.io.*;
import java.util.*;

import javax.wsdl.factory.WSDLFactory;

import sk.gk2.excusa.schema.*;
import sk.gk2.excusa.wsdl.*;

/**
 * Code generator
 * @author stick
 */
public class CodeGenerator {
	private Endpoint endpoint;
	private ArrayList<Structure> structs;
	
	/**
	 * Constructor
	 * 
	 * @param endpoint endpoint
	 */
	public CodeGenerator(Endpoint endpoint) {
		this.endpoint = endpoint;
		structs = new ArrayList<Structure>();
	}
	
	/**
	 * Generates code to default directory - "out"
	 * 
	 * @throws Exception
	 */
	public void generate() throws Exception {
		generate(endpoint.getService().getName());
	}
	
	/**
	 * Generates code to given directory
	 * 
	 * @param dir directory
	 * @throws Exception
	 */
	public void generate(String dir) throws Exception {
		new File(dir).mkdir();

		putCommonHeaderFile( dir + "/" + endpoint.getService().getName() + ".h" );
		putCommonSourceFile( dir + "/common.c" );
		putClientSourceFile( dir + "/client.c" );
		putServerSourceFile( dir + "/server.c" );
		putServerMethodFile(dir + "/server_methods.h");
		putServerSourceFile( dir + "/server.c" );

		putGrammarStructs( dir );

		BufferedWriter out;
		out = new BufferedWriter(new FileWriter(dir + "/convert.h"));
		out.write(getFromResource("convert_header.c"));
		out.close();

		out = new BufferedWriter(new FileWriter(dir + "/convert.c"));
		out.write(getFromResource("convert_source.c"));
		out.close();

		out = new BufferedWriter(new FileWriter(dir + "/Makefile"));
		out.write(getFromResource("makefile", endpoint.getService().getName()));
		out.close();
		
		WSDLFactory wsdlfactory = WSDLFactory.newInstance();
		out = new BufferedWriter(new FileWriter(dir + "/" + endpoint.getService().getName() + ".wsdl"));
		wsdlfactory.newWSDLWriter().writeWSDL(endpoint.getService().getDefinition().getOriginal(), out);
		out.close();
		
		System.out.printf("Web Service generated into directory '%s'\n", dir);
	}
	
	/**
	 * Creates grammar structures files (header and source)
	 * 
	 * @param dir directory to put files into
	 * @throws IOException
	 */
	private void putGrammarStructs(String dir) throws IOException {
		BufferedWriter outh = new BufferedWriter(new FileWriter(dir + "/grammar.h"));
		BufferedWriter outs = new BufferedWriter(new FileWriter(dir + "/grammar.c"));
		String s[];
		ArrayList<String> done = new ArrayList<String>();
		GrammarStructsGenerator gsg = new GrammarStructsGenerator();
		outs.write("#include \"grammar.h\"\n\n");
		outh.write("#define GUNIT int\n");
		outh.write("#define GSHIFT (sizeof(GUNIT)*6)\n");
		outh.write("#define GMASK ((1<<GSHIFT)-1)\n\n");
		for (Operation o: endpoint.getOperations().values()) {
			outh.write("/* " + o.getName() + " input: " + o.getInput().getName() + " */\n");
			outs.write("/* " + o.getName() + " input: " + o.getInput().getName() + " */\n");
			if (!done.contains(o.getInput().getName())) {
				s = gsg.convert(o.getInput().getName(), o.getInput().getGrammar());
				done.add(o.getInput().getName());
				outh.write(s[0]);
				outs.write(s[1]);
			} else {
				outh.write("/* previously defined */\n\n");
				outs.write("/* previously defined */\n\n");
			}
			
			outh.write("/* " + o.getName() + " output: " + o.getOutput().getName() + " */\n");
			outs.write("/* " + o.getName() + " output: " + o.getOutput().getName() + " */\n");
			if (!done.contains(o.getOutput().getName())) {
				s = gsg.convert(o.getOutput().getName(), o.getOutput().getGrammar());
				outh.write(s[0]);
				outs.write(s[1]);
				done.add(o.getOutput().getName());
			} else {
				outh.write("/* previously defined */\n\n");
				outs.write("/* previously defined */\n\n");
			}
			
			for (Message f: o.getFaults()) {
				outh.write("/* " + o.getName() + " output: " + f.getName() + " */\n");
				outs.write("/* " + o.getName() + " output: " + f.getName() + " */\n");
				if (!done.contains(f.getName())) {
					s = gsg.convert(f.getName(), f.getGrammar());
					outh.write(s[0]);
					outs.write(s[1]);
					done.add(f.getName());
				} else {
					outh.write("/* previously defined */\n\n");
					outs.write("/* previously defined */\n\n");
				}
			}
		}
		s = gsg.getTags();
		outh.write(s[0]);
		outs.write(s[1]);
		outh.close();
		outs.close();
	}
	
	/**
	 * Checks whether the structure does exist or not
	 * 
	 * @param name structure name
	 * @return true if structure does exist, false otherwise
	 */
	private boolean structExists(String name) {
		for (Structure s: structs) {
			if (s.getName() == name) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generates StructureField from Element
	 * 
	 * @param e element
	 * @param name name
	 * @return structure field
	 * @throws Exception
	 */
	private StructureField generateElement(Element e, String name) throws Exception {
		String comment = null;
		boolean isList = false, isOptional = false;
		if (e.getMaxOccurs() > 1 || e.getMaxOccurs()==-1) {
			isList = true;
			if (e.getMaxOccurs()==-1) {
				comment = "min: " + e.getMinOccurs() + ", max: unbounded";
			} else {
				comment = "min: " + e.getMinOccurs() + ", max: " + e.getMaxOccurs();
			}
		}
		if (e.getMinOccurs() == 0 && e.getMaxOccurs() == 1) {
			isOptional = true;
			comment = "optional";
		}
		if (e.getSimpleType() != null) {
			Type type = Type.fromSimpleType(endpoint.getService().getDefinition(), e.getSimpleType());
			return new StructureField(type, e.getName(), comment, isList, isOptional);
		}
		if (e.getComplexType() != null) {
			String structname = e.getName();
			if (!structExists(e.getName())) {
				Structure s = new Structure(e.getName());
				s.addField(new StructureField("complex type: " + e.getComplexType().getName() + " start"));
				for (Element ee: e.getComplexType().getElements()) {
					StructureField sf = generateElement(ee, ee.getName());
					s.addField(sf);
				}
				s.addField(new StructureField("complex type " + e.getComplexType().getName() + " end"));
				structs.add(s);
			}
			return new StructureField(structname, name, comment, isList, isOptional);
		}
		return new StructureField("empty element " + e.getName());
	}

	/**
	 * Generates message
	 * 
	 * @param m message
	 * @throws Exception
	 */
	private void generateMessage(Message m) throws Exception {
		if (structExists(m.getName())) {
			return;
		}
		Structure s = new Structure(m.getName(), true);
		for (MessagePart mp: m.getParts().values()) {
			if (mp.getType() != null && endpoint.getService().getDefinition().getSimpleTypes().containsKey(mp.getType())) {
				Type t = Type.fromSimpleType(endpoint.getService().getDefinition(), endpoint.getService().getDefinition().getSimpleType(mp.getType()));
				s.addField(new StructureField(t, mp.getName()));
			}
			if (mp.getElement() != null && endpoint.getService().getDefinition().getElements().containsKey(mp.getElement())) {
				StructureField sf = generateElement(endpoint.getService().getDefinition().getElement(mp.getElement()), mp.getName());
				s.addField(sf);
			}
		}
		structs.add(s);
	}

	/**
	 * Generates structures
	 * 
	 * @param operations operations to generate structures from
	 * @throws Exception
	 */
	private void generateStructures(Collection<Operation> operations) throws Exception {
		for (Operation o: operations) {
			generateMessage(o.getInput());
			generateMessage(o.getOutput());
			for(Message f: o.getFaults()) {
				generateMessage(f);
			}
		}
	}
	
	/**
	 * Creates common header file
	 * 
	 * @param file file to put common header file in
	 * @throws Exception
	 */
	public void putCommonHeaderFile(String file) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		String macro = "_" + endpoint.getService().getName().toUpperCase() + "_DEFINITION_";
		
		out.write("/*\n");
		out.write(" * Service           : " + endpoint.getService().getName() + "\n");
		if (endpoint.getService().getDocumentation() != null) {
			out.write("                " + endpoint.getService().getDocumentation() + "\n");
		}
		out.write(" * Endpoint          : " + endpoint.getName() + "\n");
		if (endpoint.getDocumentation() != null) {
			out.write("                   " + endpoint.getDocumentation() + "\n");
		}
		out.write(" * Endpoint Type     : " + endpoint.getType() + "\n" );
		out.write(" * Endpoint Location : " + endpoint.getLocation() + "\n" );
		out.write(" */\n\n");

		out.write("#ifndef " + macro + "\n");
		out.write("#define " + macro + " 1\n\n");
	
		out.write(getFromResource("header.c", endpoint.getType().name()));
		
		out.write(getFromResource("structures.c"));
		generateStructures(endpoint.getOperations().values());
		for (Structure s: structs) {
			out.write(s.toString());
		}
		out.write("\n/*\n * Alloc functions\n */\n\n");
		for (Structure s: structs) {
			out.write(String.format("#define alloc%1$s() ((struct %1$s *)malloc(sizeof(struct %1$s)))\n", s.getName()));
			out.write(String.format("#define alloc%1$sList(N) ((struct %1$s **)malloc((N)*sizeof(struct %1$s *)))\n", s.getName()));
		}
		out.write("\n/*\n * Free functions\n */\n\n");
		for (Structure s: structs) {
			if (s.isMessage()) {
				out.write(s.getFreeFuncPrototype());
			}
		}
		out.write("\n/*\n * Construct functions\n */\n\n");
		for (Structure s: structs) {
			if (s.isMessage()) {
				out.write(s.getConstructFuncPrototype());
			}
		}
		out.write("\n/*\n * Parse functions\n */\n\n");
		for (Structure s: structs) {
			if (s.isMessage()) {
				out.write(s.getParseFuncPrototype());
			}
		}
		// create method declarations
		out.write("\n/*\n * Methods\n */\n");
		for (Operation o: endpoint.getOperations().values()) {
			if (o.getDocumentation() != null) {
				out.write("\n/* " + o.getDocumentation() + " */");
			}
			out.write("\n" + createDeclarationFromOperation(o) + ";\n");
		}

		out.write("\n#endif\n");		
		out.close();
	}
	
	/**
	 * Creates common source file
	 * 
	 * @param file file to put common source file in
	 * @throws Exception
	 */
	public void putCommonSourceFile(String file) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(getFromResource("common_header.c", endpoint.getService().getName()));
		for (Operation o: endpoint.getOperations().values()) {
			String[] uri;
			if (endpoint.getType() == EndpointType.HTTP_GET || endpoint.getType() == EndpointType.HTTP_POST) {
				uri = (endpoint.getLocation() + o.getLocation()).split("/", 4);
			} else {
				uri = o.getAction().split("/", 4);
				if (uri.length<4) {
					System.err.println("Unknown SOAP action: " + o.getAction());
					return;
				}
			}
			int portnum = 80;
			String hostname = uri[2];
			String action = uri[3];
			int idx = hostname.indexOf(":");
			if (idx >= 0) {
				portnum = Integer.parseInt( hostname.substring(idx+1) );
				hostname = hostname.substring(0, idx);
			}
			out.write(String.format("\t{\"/%1$s\", \"%2$s\", %3$d, (SOAPFunction)%4$s, (SOAPConstructFunction)construct%5$s, (SOAPParseFunction)parse%5$s, (SOAPConstructFunction)construct%6$s, (SOAPParseFunction)parse%6$s},\n",
				action, hostname, portnum,
				o.getName(),
				o.getInput().getName(),
				o.getOutput().getName()));
		}
		out.write("\t{NULL}};\n");
		
		out.write(getFromResource("common_parser.c"));
		out.write("/*\n * Parse functions\n */\n\n");
		for (Structure s: structs) {
			out.write(s.getParseFunc());
		}		
		out.write("/*\n * Free functions\n */\n\n");
		for (Structure s: structs) {
			out.write(s.getFreeFunc());
		}
		out.write("/*\n * Construct functions\n */\n\n");
		for (Structure s: structs) {
			out.write(s.getConstructFunc());
		}
		out.close();
	}
	
	/**
	 * Creates client source file
	 * @param file file to put client source file in
	 * @throws IOException
	 */
	private void putClientSourceFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(getFromResource("client_header.c", endpoint.getService().getName()));

		out.write(getFromResource("client_posix.c"));

		out.write("/* Methods */\n\n");
		int operationID = 0;
		for (Operation o: endpoint.getOperations().values()) {
			out.write(getFromResource("client_method.c", createDeclarationFromOperation(o), operationID++));
		}
		out.close();
	}

	/**
	 * Creates server source file
	 * 
	 * @param file file to put server source file in
	 * @throws IOException
	 */
	private void putServerSourceFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		out.write(String.format("#include \"%s.h\"\n\n", endpoint.getService().getName()));
		
		out.write(String.format("#include \"server_methods.h\"\n\n"));

		int portnum = 80;
		String[] uri = endpoint.getLocation().split("/");
		int idx = uri[2].indexOf(":");
		if (idx >= 0) {
			portnum = Integer.parseInt( uri[2].substring(idx+1) );
		}		
		out.write(getFromResource("server_posix.c", portnum));

		out.close();
	}

	/**
	 * Creates server methods file
	 * @param file file to put server methods file in
	 * @throws IOException
	 */
	private void putServerMethodFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		out.write("/* Methods */\n\n");
		for (Operation o: endpoint.getOperations().values()) {
			out.write(getFromResource("server_method.c", createDeclarationFromOperation(o)));
		}

		out.close();
	}

	/**
	 * Creates declaration from operation
	 * 
	 * @param o operation
	 * @return string representation of declaration
	 * @throws IOException
	 */
	private String createDeclarationFromOperation(Operation o) throws IOException {
		return "int " + o.getName() + "(struct " + o.getInput().getName() + " *in, struct " + o.getOutput().getName() + " **out)";
	}

	/**
	 * Gets string from resource
	 * 
	 * @param res resource name
	 * @param args arguments (like printf)
	 * @return string with expanded arguments
	 * @throws IOException
	 */
	private String getFromResource(String res, Object ... args) throws IOException {
		String line;
		InputStream is = getClass().getResourceAsStream("/res/" + res);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer(1024);
		for (;;) {
			line = br.readLine();
			if (line == null) {
				break;
			}
			sb.append(line + "\n");
		}
		br.close();
		is.close();
		return args.length > 0 ? String.format(sb.toString(), args) : sb.toString();
	}
}
