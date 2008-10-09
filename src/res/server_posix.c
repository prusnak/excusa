/* Communication POSIX */

#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>

#define BACKLOG 10

static char bufreq[BUF_REQ_SIZE];
static char bufres[BUF_RES_SIZE];
extern char *WSDLdefinition;

int create_socket(int port)
{
	struct sockaddr_in addr;
	int sock;

	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = INADDR_ANY;
	addr.sin_port = htons(port);
	sock = socket(PF_INET, SOCK_STREAM, 6); /* 6 is TCP*/
	if (sock < 0) { fprintf(stderr, "Could not create socket\n"); return-1; }
	int opt = 1;
	if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) {
		fprintf(stderr, "Could not set socket options\n");
		return -4;
	}
	if (bind(sock, (struct sockaddr *)&addr, sizeof(addr))) {
		fprintf(stderr, "Could not bind socket\n");
		return -5;
	}
	if (listen(sock, BACKLOG)) {
		fprintf(stderr, "Could not start listening\n");
		return -6;
	}
	return sock;
}

int write_response_header(char *buf, int maxlen, char soap) {
	int len;
	len = snprintf(buf, maxlen,
		"HTTP/1.1 200 OK\r\n"
		/* "Content-Length: 0000\r\n" */
		"Content-Type: text/xml; charset=utf-8\r\n"
		"\r\n");
	if (soap) {
		len += snprintf(buf+len, maxlen-len,
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n"
			"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
			"<soap:Body>\r\n");
	}
	return len;
}

int count_until_eol(char *buf, int maxlen, int pos) {
	while (pos<maxlen && buf[pos]!='\r' && buf[pos]!='\n') pos++;
	if (pos<maxlen-1 && buf[pos] == '\r' && buf[pos] == '\r') pos++;
	return pos;
}

int find_empty_line(char *buf, int maxlen, int pos) {
	while (pos<maxlen) {
		if (buf[pos-3] == '\r' && buf[pos-2] == '\n' && buf[pos-1] == '\r' && buf[pos] == '\n') break;
		if (buf[pos-1] == '\n' && buf[pos] == '\n') break;
		pos++;
	}
	return (pos<maxlen) ? pos+1 : -1;
}

int write_response_footer(char *buf, int maxlen) {
	return snprintf(buf, maxlen, 
		"\r\n</soap:Body>\r\n</soap:Envelope>\r\n");
}

void find_data_start(char *buf, int maxlen, char **start, char **action) {
	int pos;
	*start = NULL;
	*action = NULL;
	if (maxlen < 32) return;
	if (strncmp(buf, "POST ", 5)) return;
	pos = 4;
	while (pos<maxlen-1 && buf[pos] == ' ') pos++;
	*action = buf+pos;
	while (pos<maxlen-1 && buf[pos] != ' ') pos++;
	buf[pos] = 0;
	pos = find_empty_line(buf, maxlen, pos);
	if (pos<0) return;
	if (!strncmp(buf+pos, "<?xml ", 6)) {
		while (pos<maxlen && buf[pos]!='>') pos++;
	}
	pos++;
	while (pos<maxlen && (buf[pos] == ' ' || buf[pos] == '\r' || buf[pos] == '\n')) pos++;
	*start = buf+pos;
}

int multiread(int sock, char *buf, int buflen)
{
	fd_set rfds;
	int r, len = 0;
	struct timeval tv = {0, 1000}; /* seconds, microseconds */
	while (buflen > 0) {
		r = read(sock, buf+len, 8192);
		if (r<0) return r;
		len += r;
		buflen -= r;
		if (r < 8192) {
			break;
		}
		FD_ZERO(&rfds);
		FD_SET(sock, &rfds);
		if (select(sock+1, &rfds, 0, 0, &tv) != 1) {
			break;
		}
	}
	return len;
}

void handle(int cl)
{
	int i, lenreq, lenres, err;
	char *start, *action;
	void *in, *out = NULL;
	struct SOAPOperation *operation;

	lenreq = multiread(cl, bufreq, BUF_REQ_SIZE);
	if (lenreq <= 0) return;
	
	if (!strncmp(bufreq, "GET ", 4)) {
		lenres = write_response_header(bufres, BUF_RES_SIZE, 0);
		strncpy(bufres+lenres, WSDLdefinition, BUF_RES_SIZE-lenres);
		lenres += strlen(WSDLdefinition);
		if (lenres > BUF_RES_SIZE) lenres = BUF_RES_SIZE;
		write(cl, bufres, lenres);
		return;
	}

	find_data_start(bufreq, lenreq, &start, &action);
	if (!start) {
		write(cl, "HTTP/1.1 400 Bad Request\r\n\r\n", 28);
		return;
	}

	lenres = write_response_header(bufres, BUF_RES_SIZE, 1);
	err = 255;
	operation = Operations;
	while (operation && operation->action) {
		if (!strcmp(action, operation->action)) {
			in = (operation->parseinfunc)(start, lenreq - (start-bufreq));
			if (in) err = (operation->func)(in, &out);
			if (!err) lenres += (operation->constructoutfunc)(bufres+lenres, BUF_RES_SIZE-lenres, out);
			break;
		}
		operation++;
	}
	
	if (err) {
		write(cl, "HTTP/1.1 400 Bad Request\r\n\r\n", 28);
		return;
	}

	lenres += write_response_footer(bufres+lenres, BUF_REQ_SIZE-lenres);
	if (lenres != write(cl, bufres, lenres)) {
		write(cl, "HTTP/1.1 500 Internal Server Error\r\n\r\n", 38);
	}
}

int main(int argc, char **argv)
{
	int sock;
	struct sockaddr_in addr;
	socklen_t addr_len = sizeof(addr);

	sock = create_socket(%d);
	if (sock > 0) {
		for (;;) {
			int cl = accept(sock, (struct sockaddr *)&addr, &addr_len);
			if (cl < 0) continue;
			handle(cl);
			close(cl);
		}
		close(sock);
	}
	return sock < 0 ? sock : 0;
}
