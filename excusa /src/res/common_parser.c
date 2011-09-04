
/*
 * Parser internals
 */

static struct Parser_StackEntry { char level; GUNIT rule; int pos; } Parser_Stack[STACK_SIZE];
static int Parser_StackCount = 0;
#define Parser_Push(L,R,P) { \
	Parser_Stack[Parser_StackCount].level=(L); \
	Parser_Stack[Parser_StackCount].rule=(R); \
	Parser_Stack[Parser_StackCount].pos=(P); \
	Parser_StackCount++; \
}
#define Parser_Pop() (Parser_StackCount--)
static char *Parser_Buf;
static int Parser_BufLen;
static GUNIT **Parser_Grammar;

#define eatStartTag(P,T) __eat_tag((P),(T),0)
#define eatEndTag(P,T) __eat_tag((P),(T),1)

int __eat_tag(int pos, char *tag, char ending)
{
	int tl;
	if (pos >= Parser_BufLen || Parser_Buf[pos] != '<') return 0; pos++;
	if (ending) {
		if (pos >= Parser_BufLen || Parser_Buf[pos] != '/') return 0; pos++;
	}
	tl = strlen(tag);

	/* TODO: correctly check for tags prefixed with namespace (namespace is [A-Za-z_][A-Za-z0-9.-_]*) */
	if (pos+tl <= Parser_BufLen && !strncmp(Parser_Buf+pos, tag, tl)) {
		pos += tl;
	} else
	/* HACK: accept soap: namespace */
	if (pos+tl+5 <= Parser_BufLen && !strncmp(Parser_Buf+pos, "soap:", 5) && !strncmp(Parser_Buf+pos+5, tag, tl)) {
		pos += tl+5;
	} else
	/* HACK: accept env: namespace */
	if (pos+tl+4 <= Parser_BufLen && !strncmp(Parser_Buf+pos, "env:", 4) && !strncmp(Parser_Buf+pos+4, tag, tl)) {
		pos += tl+4;
	} else
	{
		return 0;
	}

	for (;;) {
		if (Parser_Buf[pos] == '>') break;
		if (pos > Parser_BufLen) return 0;
		pos++;
	}
	return pos+1;
}

int eatValue(int pos, int type) {
	while (pos < Parser_BufLen && Parser_Buf[pos] != '<') pos++;
	return pos;
}

char *getValue(int idx) {
	int i = Parser_Stack[idx].pos;
	while (i<Parser_BufLen && Parser_Buf[i] != '<') i++;
	Parser_Buf[i] = 0;
	return Parser_Buf+Parser_Stack[idx].pos;
}

#define eatWhiteSpace(B,L,P) for (; (P)<(L) && ((B)[(P)]==' ' || (B)[(P)]=='\t' || (B)[(P)]=='\n' || (B)[(P)]=='\r'); (P)++)
#define forEachRule(P,X) for ((P)=((X)&GMASK); (P)<=(((X)&GMASK)+((X)>>GSHIFT)); (P)++)

int eatRule(char level, int pos, GUNIT desc)
{
	GUNIT i, j, r, *rule;
	forEachRule(i, desc) {
		rule = Parser_Grammar[i];
		if (rule[0]) Parser_Push(level, i, pos);
		r = pos;
		switch(rule[0]) {
			case 0:
				j = 1;
				while (rule[j] != -1) {
					r = eatRule(level, r, rule[j]);
					j++;
					if (!r) break;
				}
				break;
			case 1:
				eatWhiteSpace(Parser_Buf, Parser_BufLen, r);
				r = eatStartTag(r, grammar_tag[rule[1]]);
				if (!r) break;
				r = eatRule(level+1, r, rule[2]);
				if (!r) break;
				r = eatEndTag(r, grammar_tag[rule[1]]);
				if (!r) break;
				eatWhiteSpace(Parser_Buf, Parser_BufLen, r);
				break;
			default:
				r = eatValue(r, rule[0]);
				break;
		}
		if (r) {
			break;
		} else {
			if (rule[0]) Parser_Pop();
		}
	}
	return r;
}

/* TODO: clean this mess! :) */
#define findTagSame(S,T) __find_tag((S), (T), 0, 1)
#define findNextTag(S,T) __find_tag((S), (T), 1, 0)
#define findNextTagSame(S,T) __find_tag((S), (T), 1, 1)

int __find_tag(int start, int tag, char next, char same) {
	int i = start + next;
	while (i<Parser_StackCount && Parser_Stack[i].level+same>Parser_Stack[start].level) {
		if (Parser_Grammar[Parser_Stack[i].rule][0] == 1 &&
		    Parser_Grammar[Parser_Stack[i].rule][1] == tag) {
			return i;
		}
		i++;
	}
	return -1;
}

int countTags(int start, int tag) {
	int i = start, count = 0;
	while (i<Parser_StackCount && Parser_Stack[i].level>=Parser_Stack[start].level) {
		if (Parser_Grammar[Parser_Stack[i].rule][0] == 1 &&
		    Parser_Grammar[Parser_Stack[i].rule][1] == tag &&
		    Parser_Stack[i].level == Parser_Stack[start].level + 1) {
			count++;
		}
		i++;
	}
	return count;
}

