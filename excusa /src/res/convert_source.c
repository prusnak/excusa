#include <string.h>
#include "convert.h"

/* === convert from BasicType === */

/* parseString is a macro */

Boolean parseBoolean(char *val) {
	if (!val) return 0;
	if (!strncmp(val, "true", 4)) return 1;
	if (val[0] == '1') return 1;
	return 0;
}

Base64Binary parseBase64Binary(char *val, int *retlen) {
/* TODO: detect malformed */
	static const char decode64[]="|$$$}rstuvwxyz{$$$$$$$>?@ABCDEFGHIJKLMNOPQRSTUVW$$$$$$XYZ[\\]^_`abcdefghijklmnopq";
	int i, len;
	char *s;
	unsigned char *valu;
	if (!val || (len = strlen(val))%4) { /* malformed base64 data */
		*retlen = 0;
		return NULL;
	}
	valu = (unsigned char *)val;
	s = (char *)malloc(len/4*3);
	for (i=0; i<len; i++) {
		valu[i] = (unsigned char) ((valu[i] < 43 || valu[i] > 122) ? 0 : decode64[ valu[i] - 43 ]);
		if (valu[i]) valu[i] = (unsigned char)((valu[i] == '$') ? 0 : valu[i] - 61);
	}
	for (i=0; i<len/4; i++) {
		s[i*3  ] = (char) (valu[i*4  ] << 2 | valu[i*4+1] >> 4);
		s[i*3+1] = (char) (valu[i*4+1] << 4 | valu[i*4+2] >> 2);
		s[i*3+2] = (char) (((valu[i*4+2] << 6) & 0xc0) | valu[i*4+3]);
	}	
	*retlen = len/4*3;
	return s;
}

HexBinary parseHexBinary(char *val, int *retlen) {
	int i, len;
	char *s,a,b;
	if (!val || (len = strlen(val))%2) { /* malformed hex data */
		*retlen = 0;
		return NULL;
	}
	s = (char *)malloc(len/2);
	for (i=0; i<len/2 ;i++) {
		a = b = -1;
		if (val[i*2  ]>='0' && val[i*2  ]<='9') a = val[i*2  ]-'0';
		if (val[i*2  ]>='A' && val[i*2  ]<='F') a = val[i*2  ]-'A'+10;
		if (val[i*2  ]>='a' && val[i*2  ]<='f') a = val[i*2  ]-'a'+10;
		if (val[i*2+1]>='0' && val[i*2+1]<='9') b = val[i*2+1]-'0';
		if (val[i*2+1]>='A' && val[i*2+1]<='F') b = val[i*2+1]-'A'+10;
		if (val[i*2+1]>='a' && val[i*2+1]<='f') b = val[i*2+1]-'a'+10;
		if (a == -1 || b == -1) { /* malformed hex data */
			*retlen = 0;
			return NULL;
		}
		s[i] = (a<<4) + b;
	}	
	*retlen = len/2;
	return s;
}

/* parseFloat is a macro */
/* parseDouble is a macro */
/* parseLong is a macro */
/* parseInt is a macro */
/* parseShort is a macro */
/* parseByte is a macro */
/* parseUnsignedLong is a macro */
/* parseUnsignedInt is a macro */
/* parseUnsignedShort is a macro */
/* parseUnsignedByte is a macro */

/* === convert from BasicTypeList === */

int countList(char *ptr) {
	int i = 0, cnt = 0;
	while (ptr[i]) {
		if (ptr[i] == ' ' || ptr[i] == '\n' || ptr[i] == '\r') {
			ptr[i] = 0;
		} else {
			if (i > 0 && !ptr[i-1]) {
				cnt++;
			}
		}
	}
	return cnt;
}

#define parseList(type) \
	int i; \
	type##List r; \
	*count = countList(val); \
	r = (type##List)malloc(sizeof(type)*(*count)); \
	for (i = 0; i < *count; i++) { \
		while (!*val) val++; \
		r[i] = parse##type(val); \
		while (*val) val++; \
	} \
	return r;

StringList parseStringList(char *val, int *count) {
	parseList(String)
}

BooleanList parseBooleanList(char *val, int *count) {
	parseList(Boolean)
}

Base64BinaryList parseBase64BinaryList(char *val, int *count, int **retlen) {
	int i;
	Base64BinaryList r;
	*count = countList(val);
	r = (Base64BinaryList)malloc(sizeof(Base64Binary)*(*count));
	*retlen = (int *)malloc(sizeof(int)*(*count));
	for (i = 0; i < *count; i++) {
		while (!*val) val++;
		r[i] = parseBase64Binary(val, retlen[i]);
		while (*val) val++;
	}
	return r;
}

HexBinaryList parseHexBinaryList(char *val, int *count, int **retlen) {
	int i;
	HexBinaryList r;
	*count = countList(val);
	r = (HexBinaryList)malloc(sizeof(HexBinary)*(*count));
	*retlen = (int *)malloc(sizeof(int)*(*count));
	for (i = 0; i < *count; i++) {
		while (!*val) val++;
		r[i] = parseHexBinary(val, retlen[i]);
		while (*val) val++;
	}
	return r;
}

FloatList parseFloatList(char *val, int *count) {
	parseList(Float)
}

DoubleList parseDoubleList(char *val, int *count) {
	parseList(Double)
}

LongList parseLongList(char *val, int *count) {
	parseList(Long)
}

IntList parseIntList(char *val, int *count) {
	parseList(Int)
}

ShortList parseShortList(char *val, int *count) {
	parseList(Short)
}

ByteList parseByteList(char *val, int *count) {
	parseList(Byte)
}

UnsignedLongList parseUnsignedLongList(char *val, int *count) {
	parseList(UnsignedLong)
}

UnsignedIntList parseUnsignedIntList(char *val, int *count) {
	parseList(UnsignedInt)
}

UnsignedShortList parseUnsignedShortList(char *val, int *count) {
	parseList(UnsignedShort)
}

UnsignedByteList parseUnsignedByteList(char *val, int *count) {
	parseList(UnsignedByte)
}

/* === convert to BasicType === */

/* constructString is a macro */

int constructBoolean(char *ptr, int maxlen, char val) {
	if (maxlen <= 0) return 0;
	*ptr = val ? '1' : '0';
	return 1;
	/* return snprintf(ptr, maxlen, "%s", val ? "true" : "false"); */
}

int constructBase64Binary(char *ptr, int maxlen, char *val, int len) {
	static const unsigned char encode64[64] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	int i;
	if (maxlen < len*4/3) return 0;
	for (i = 0; i*3 < len; i++) {
		ptr[i*4+0] = encode64[ ((unsigned char)val[i*3+0] & 0xFC) >> 2];
		ptr[i*4+1] = encode64[(((unsigned char)val[i*3+0] & 0x03) << 4) | (((unsigned char)val[i*3+1] & 0xF0) >> 4)];
		ptr[i*4+2] = encode64[(((unsigned char)val[i*3+1] & 0x0F) << 2) | (((unsigned char)val[i*3+2] & 0xC0) >> 6)];
		ptr[i*4+3] = encode64[ (unsigned char)val[i*3+2] & 0x3F];
	}
	i = len - len % 3;
	if (len % 3 == 1) {
		ptr[i*4+0] = encode64[ ((unsigned char)val[i*3+0] & 0xFC) >> 2];
		ptr[i*4+1] = encode64[(((unsigned char)val[i*3+0] & 0x03) << 4)];
		ptr[i*4+2] = '=';
		ptr[i*4+3] = '=';
	} else
	if (len % 3 == 2) {
		ptr[i*4+0] = encode64[ ((unsigned char)val[i*3+0] & 0xFC) >> 2];
		ptr[i*4+1] = encode64[(((unsigned char)val[i*3+0] & 0x03) << 4) | (((unsigned char)val[i*3+1] & 0xF0) >> 4)];
		ptr[i*4+2] = encode64[(((unsigned char)val[i*3+1] & 0x0F) << 2)];
		ptr[i*4+3] = '=';
	}
	return len*4/3;
}

int constructHexBinary(char *ptr, int maxlen, char *val, int len) {
	static const char hex[16] = "0123456789ABCDEF";
	if (maxlen < len*2) return 0;
	int i;
	for (i = 0; i < len; i++) {
		ptr[i*2  ] = hex[(unsigned char)val[i] & 0xF0 >> 4];
		ptr[i*2+1] = hex[(unsigned char)val[i] & 0x0F];
	}
	return len*2;
}

/* constructFloat is a macro */
/* constructDouble is a macro */
/* constructLong is a macro */
/* constructInt is a macro */
/* constructShort is a macro */
/* constructByte is a macro */
/* constructUnsignedLong is a macro */
/* constructUnsignedInt is a macro */
/* constructUnsignedShort is a macro */
/* constructUnsignedByte is a macro */

/* === convert to BasicTypeList === */

int constructList(char *type, char *ptr, int maxlen, char *val, int count)
{
	int i = 0, l;
	while (i < (count) && (maxlen) > 0) {
		l = snprintf((ptr), (maxlen), (type), (val)[i]);
		(ptr) += l;
		(maxlen) -= l;
		if (i < (count)-1 && (maxlen) > 0) {
			*(ptr) = ' ';
			(ptr)++;
		}
		i++;
	}
	return l;
}

/* constructStringList is a macro */

int constructBooleanList(char *ptr, int maxlen, char *val, int count) {
	int i = 0;
	if (maxlen < 2*count-1) return 0;
	while (i < count-1) {
		*ptr = val ? '1' : '0';
		*(ptr+1) = ' ';
		ptr += 2;
	}
	*ptr = val[count-1] ? '1' : '0';
	return 2*count-1;
}

int constructBase64BinaryList(char *ptr, int maxlen, char **val, int *len, int count) {
	int i, l = 0;
	for (i = 0; i < count-1; i++) {
		l += constructBase64Binary(ptr+l, maxlen-l, val[i], len[i]);
		if (l<maxlen) {
			*ptr = ' ';
			l++;
		}
	}
	l += constructBase64Binary(ptr+l, maxlen-l, val[i], len[i]);
	return l;
}

int constructHexBinaryList(char *ptr, int maxlen, char **val, int *len, int count) {
	int i, l = 0;
	for (i = 0; i < count-1; i++) {
		l += constructHexBinary(ptr+l, maxlen-l, val[i], len[i]);
		if (l<maxlen) {
			*ptr = ' ';
			l++;
		}
	}
	l += constructHexBinary(ptr+l, maxlen-l, val[i], len[i]);
	return l;
}

/* constructFloatList is a macro */

/* constructDoubleList is a macro */

/* constructLongList is a macro */

/* constructIntList is a macro */

/* constructShortList is a macro */

/* constructByteList is a macro */

/* constructUnsignedLongList is a macro */

/* constructUnsignedIntList is a macro */

/* constructUnsignedShortList is a macro */

/* constructUnsignedByteList is a macro */
