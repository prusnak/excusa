NAME=excusa
JAVAFILES=$(shell cd src ; find -name '*.java')
SOURCES=$(addprefix src/, $(JAVAFILES))
OBJECTS=$(addprefix bin/, $(JAVAFILES:.java=.class))

all: $(OBJECTS)

$(OBJECTS): $(SOURCES)
	mkdir -p bin/res
	javac -classpath bin:lib/qname.jar:lib/wsdl4j.jar:lib/xmlschema.jar -d bin src/sk/gk2/excusa{,/code,/grammar,/schema,/wsdl}/*.java
	cp -a src/manifest.txt bin/manifest.txt
	cp -a src/res/* bin/res

jar: lib/$(NAME).jar

lib/$(NAME).jar: $(OBJECTS)
	jar cfm lib/$(NAME).jar bin/manifest.txt -C bin res -C bin sk

tarball: lib/$(NAME).jar
	tar cf $(NAME).tar $(NAME) lib/*.jar
	gzip --best $(NAME).tar

doc:
	rm -rf javadoc
	mkdir javadoc
	javadoc -private -sourcepath src -d javadoc sk.gk2.excusa{,.code,.grammar,.schema,.wsdl}

clean:
	rm -rf bin javadoc lib/$(NAME).jar
