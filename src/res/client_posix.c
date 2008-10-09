/* Communication POSIX */

#include <arpa/inet.h>
#include <netdb.h>
#include <string.h>
#include <unistd.h>

static char bufreq[BUF_REQ_SIZE];
static char bufres[BUF_RES_SIZE];

int create_socket(char *host, int port)
{
	struct hostent *he;
	struct sockaddr_in addr;
	int sock;

	addr.sin_family = AF_INET;
	if (!(he = gethostbyname(host))) return -2;
	memcpy(&addr.sin_addr.s_addr, he->h_addr_list[0], sizeof(addr.sin_addr.s_addr));
	addr.sin_port = htons(port);
	sock = socket(PF_INET, SOCK_STREAM, 6); /* 6 is TCP */
	if (sock < 0) return -1;
	if (connect(sock, (struct sockaddr *)&addr, sizeof(addr))) return -3;
	return sock;
}

int write_request_header(char *buf, int maxlen, int operationid) {
	int pos = 0;
	pos += snprintf(buf, maxlen,
		"POST %1$s HTTP/1.1\r\n"
		"Host: %2$s\r\n"
		/* "Content-Length: 0000\r\n" */
		"Content-Type: text/xml; charset=utf-8\r\n"
		"SoapAction: http://%2$s", Operations[operationid].action, Operations[operationid].host);
	if (Operations[operationid].port != 80) {
		pos += snprintf(buf+pos, maxlen-pos, ":%d", Operations[operationid].port);
	}
	pos += snprintf(buf+pos, maxlen-pos,
		"%s\r\n"
		"\r\n"
		"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n"
		"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
		"<soap:Body>\r\n", Operations[operationid].action);
	return pos;
}

int write_request_footer(char *buf, int maxlen) {
	return snprintf(buf, maxlen,
		"\r\n</soap:Body>\r\n</soap:Envelope>\r\n");
}

int find_empty_line(char *buf, int maxlen, int pos) {
	while (pos<maxlen) {
		if (buf[pos-3] == '\r' && buf[pos-2] == '\n' && buf[pos-1] == '\r' && buf[pos] == '\n') break;
		if (buf[pos-1] == '\n' && buf[pos] == '\n') break;
		pos++;
	}
	return (pos<maxlen) ? pos+1 : -1;
}

int skip_response_header(char *buf, int maxlen) {
	int pos;
	if (strncmp(buf, "HTTP/1.1 200 OK", 15)) return -10;
	pos = find_empty_line(buf, maxlen, 15);
	if (pos < 0) return pos;
	if (!strncmp(buf+pos, "<?xml ", 6)) {
		while (pos<maxlen && buf[pos]!='>') pos++;
	}
	pos++;
	while (pos<maxlen && (buf[pos] == ' ' || buf[pos] == '\r' || buf[pos] == '\n')) pos++;
	return pos;
}

int multiread(int sock, char *buf, int buflen)
{
	fd_set rfds;
	int r, len = 0;
	struct timeval tv = {0, 10000}; /* seconds, microseconds */
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

int request_response(int operationid, char *bufreq, int lenreq, char *bufres, int *lenres)
{
	int sock;
	sock = create_socket(Operations[operationid].host, Operations[operationid].port);
	if (sock<0) {
		return sock;
	}
	if (lenreq != write(sock, bufreq, lenreq)) {
		close(sock);
		return 1;
	}
	*lenres = multiread(sock, bufres, BUF_RES_SIZE);
	close(sock);
	if (*lenres < 0) {
		*lenres = 0;
		return 2;
	}
	return 0;
}

int client_method(int id, void *in, void **out)
{
	int lenreq, lenres, err, skip;
	lenreq  = write_request_header(bufreq, BUF_REQ_SIZE, id);
	lenreq += (Operations[id].constructinfunc)(bufreq+lenreq, BUF_REQ_SIZE-lenreq, in);
	lenreq += write_request_footer(bufreq+lenreq, BUF_REQ_SIZE-lenreq);
	err = request_response(id, bufreq, lenreq, bufres, &lenres);
	*out = NULL;
	if (!err) {
		skip = skip_response_header(bufres, lenres);
		if (skip >= 0) {
			*out = (Operations[id].parseoutfunc)(bufres+skip, lenres-skip);
		} else {
			err = skip;
		}
	}
	return err;
}

