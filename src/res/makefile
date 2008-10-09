CC=gcc
CFLAGS=-g -ggdb3
# CFLAGS=-g -Wall
OUTPUT=libclient.a server
OBJECTS=common.o client.o server.o wsdl.o grammar.o convert.o

all: $(OBJECTS) $(OUTPUT)

libclient.a: client.o common.o grammar.o convert.o
	ar rcs libclient.a client.o common.o grammar.o convert.o

server: server.o wsdl.o common.o grammar.o convert.o 
	$(CC) $(CFLAGS) server.o wsdl.o common.o grammar.o convert.o -o server

common.o: %1$s.h common.c grammar.h
	$(CC) $(CFLAGS) -c common.c -o common.o

grammar.o: grammar.h grammar.c
	$(CC) $(CFLAGS) -c grammar.c -o grammar.o

client.o: %1$s.h client.c
	$(CC) $(CFLAGS) -c client.c -o client.o

server.o: %1$s.h server.c server_methods.h
	$(CC) $(CFLAGS) -c server.c -o server.o

convert.o: convert.h convert.c
	$(CC) $(CFLAGS) -c convert.c -o convert.o
	
wsdl.o: wsdl.c
	$(CC) $(CFLAGS) -c wsdl.c -o wsdl.o

wsdl.c: %1$s.wsdl
	echo -n 'char *WSDLdefinition = "' > wsdl.c
	sed -e 's:[ \t]\+: :g' -e 's:^[ \t]::' -e 's:[ \t]$$::' -e 's:":\\":g' %1$s.wsdl | tr -d '\r\n' >> wsdl.c
	echo '";' >> wsdl.c

strip: $(OUTPUT)
	strip $(OUTPUT)
clean:
	rm -rf $(OBJECTS) $(OUTPUT)
