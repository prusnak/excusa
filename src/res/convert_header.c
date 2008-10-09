#ifndef _CONVERT_
#define _CONVERT_ 1

#include <stdio.h>
#include <stdlib.h>

/* types */

typedef char* String;
typedef char Boolean;
typedef char* Base64Binary;
typedef char* HexBinary;
typedef float Float;
typedef double Double;
#define Integer Long
typedef long Long;
typedef int Int;
typedef short Short;
typedef char Byte;
typedef unsigned long UnsignedLong;
typedef unsigned int UnsignedInt;
typedef unsigned short UnsignedShort;
typedef unsigned char UnsignedByte;

typedef String* StringList;
typedef Boolean* BooleanList;
typedef Base64Binary* Base64BinaryList;
typedef HexBinary* HexBinaryList;
typedef Float* FloatList;
typedef Double* DoubleList;
#define IntegerList LongList
typedef Long* LongList;
typedef Int* IntList;
typedef Short* ShortList;
typedef Byte* ByteList;
typedef UnsignedLong* UnsignedLongList;
typedef UnsignedInt* UnsignedIntList;
typedef UnsignedShort* UnsignedShortList;
typedef UnsignedByte* UnsignedByteList;

/* convert from BasicType */

#define       parseString(val) ((val) ? (String)val : (String)NULL)
Boolean       parseBoolean(char *val);
Base64Binary  parseBase64Binary(char *val, int *retlen);
HexBinary     parseHexBinary(char *val, int *retlen);
#define       parseFloat(val) ((val) ? (Float)atof(val) : (Float)0.0)
#define       parseDouble(val) ((val) ? (Double)atof(val) : (Double)0.0)
#define       parseInteger parseLong
#define       parseLong(val) ((val) ? (Long)atol(val) : (Long)0)
#define       parseInt(val) ((val) ? (Int)atoi(val) : (Int)0)
#define       parseShort(val) ((val) ? (Short)atoi(val) : (Short)0)
#define       parseByte(val) ((val) ? (Byte)atoi(val): (Byte)0)
#define       parseUnsignedLong(val) ((val) ? (UnsignedLong)strtoul(val, NULL, 10) : (UnsignedLong)0)
#define       parseUnsignedInt(val) ((val) ? (UnsignedInt)atol(val) : (UnsignedInt)0)
#define       parseUnsignedShort(val) ((val) ? (UnsignedShort)atoi(val) : (UnsignedShort)0)
#define       parseUnsignedByte(val) ((val) ? (UnsignedByte)atoi(val) : (UnsignedByte)0)

/* convert from BasicTypeList */
StringList        parseStringList(char *val, int *count);
BooleanList       parseBooleanList(char *val, int *count);
Base64BinaryList  parseBase64BinaryList(char *val, int *count, int **retlen);
HexBinaryList     parseHexBinaryList(char *val, int *count, int **retlen);
FloatList         parseFloatList(char *val, int *count);
DoubleList        parseDoubleList(char *val, int *count);
#define           parseIntegerList parseLongList
LongList          parseLongList(char *val, int *count);
IntList           parseIntList(char *val, int *count);
ShortList         parseShortList(char *val, int *count);
ByteList          parseByteList(char *val, int *count);
UnsignedLongList  parseUnsignedLongList(char *val, int *count);
UnsignedIntList   parseUnsignedIntList(char *val, int *count);
UnsignedShortList parseUnsignedShortList(char *val, int *count);
UnsignedByteList  parseUnsignedByteList(char *val, int *count);

/* convert to BasicType */

#define constructString(ptr, maxlen, val) snprintf((ptr), (maxlen), "%s", (val))
int     constructBoolean(char *ptr, int maxlen, char val);
int     constructBase64Binary(char *ptr, int maxlen, char *val, int len);
int     constructHexBinary(char *ptr, int maxlen, char *val, int len);
#define constructFloat(ptr, maxlen, val) snprintf((ptr), (maxlen), "%f", (double)(val))
#define constructDouble(ptr, maxlen, val) snprintf((ptr), (maxlen), "%f", (val))
#define constructInteger constructLong
#define constructLong(ptr, maxlen, val) snprintf((ptr), (maxlen), "%ld", (val))
#define constructInt(ptr, maxlen, val) snprintf((ptr), (maxlen), "%d", (val))
#define constructShort(ptr, maxlen, val) snprintf((ptr), (maxlen), "%d", (int)(val))
#define constructByte(ptr, maxlen, val) snprintf((ptr), (maxlen), "%d", (int)(val))
#define constructUnsignedLong(ptr, maxlen, val) snprintf((ptr), (maxlen), "%lu", (val))
#define constructUnsignedInt(ptr, maxlen, val) snprintf((ptr), (maxlen), "%u", (val))
#define constructUnsignedShort(ptr, maxlen, val) snprintf((ptr), (maxlen), "%u", (unsigned int)(val))
#define constructUnsignedByte(ptr, maxlen, val) snprintf((ptr), (maxlen), "%u", (unsigned int)(val))

/* convert to BasicTypeList */

#define constructStringList(ptr, maxlen, val, count) constructList("%s", (ptr), (maxlen), (val), (count))
int     constructBooleanList(char *ptr, int maxlen, char *val, int count);
int     constructBase64BinaryList(char *ptr, int maxlen, char **val, int *len, int count);
int     constructHexBinaryList(char *ptr, int maxlen, char **val, int *len, int count);
#define constructFloatList(ptr, maxlen, val, count) constructList("%f", (ptr), (maxlen), (double)(val), (count))
#define constructDoubleList(ptr, maxlen, val, count) constructList("%f", (ptr), (maxlen), (val), (count))
#define constructIntegerList constructLongList
#define constructLongList(ptr, maxlen, val, count) constructList("%ld", (ptr), (maxlen), (val), (count))
#define constructIntList(ptr, maxlen, val, count) constructList("%d", (ptr), (maxlen), (val), (count))
#define constructShortList(ptr, maxlen, val, count) constructList("%d", (ptr), (maxlen), (int)(val), (count))
#define constructByteList(ptr, maxlen, val, count) constructList("%d", (ptr), (maxlen), (int)(val), (count))
#define constructUnsignedLongList(ptr, maxlen, val, count) constructList("%lu", (ptr), (maxlen), (val), (count))
#define constructUnsignedIntList(ptr, maxlen, val, count) constructList("%u", (ptr), (maxlen), (val), (count))
#define constructUnsignedShortList(ptr, maxlen, val, count) constructList("%u", (ptr), (maxlen), (unsigned int)(val), (count))
#define constructUnsignedByteList(ptr, maxlen, val, count) constructList("%u", (ptr), (maxlen), (unsigned int)(val), (count))

#endif
