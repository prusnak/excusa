/*
 * Defines
 */

#include "convert.h"

#define ENDPOINT_TYPE_%s 1
#define BUF_REQ_SIZE 65536
#define BUF_RES_SIZE 65536
#define STACK_SIZE 10240

extern void *fault;
extern int fault_type;

typedef int (*SOAPFunction)(void *, void *);
typedef int (*SOAPConstructFunction)(char *, int, void *);
typedef void *(*SOAPParseFunction)(char *, int);

struct SOAPOperation {
	char *action;
	char *host;
	int port;
	SOAPFunction func;
	SOAPConstructFunction constructinfunc;
	SOAPParseFunction parseinfunc;
	SOAPConstructFunction constructoutfunc;
	SOAPParseFunction parseoutfunc;
};

extern struct SOAPOperation Operations[];

