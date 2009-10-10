
package vavi.util.rpm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class RPMIndexEntry {

    public int tag;
    public int type;
    public int offset;
    public int count;

    private List<?> values;

    public List<?> getValues() {
        return this.values;
    }

    public void setValues(List<?> v) {
        this.values = (List<?>) ((ArrayList<?>) v).clone();
    }

    public void dump(PrintStream out, String prefix) {
        out.println(prefix + "   Tag:    " + this.tag);
        out.println(prefix + "   Offset: " + this.offset);
        out.println(prefix + "   Count:  " + this.count);
        out.println(prefix + "   Type:   " + this.type + " '" + this.typeString() + "'");

        if (this.values == null) {
            out.println(prefix + "   Values: null");
            return;
        }

        out.println(prefix + "   Values: " + this.values.size());

        prefix = prefix + "      ";

        for (int i = 0; i < this.count; ++i) {
            Object value = null;
            switch (this.type) {
            case RPMTYPE_NULL:
                break;
            case RPMTYPE_CHAR:
                value = this.values.get(i);
                Character ch = (Character) value;
                out.println(prefix + "[" + i + "] = '" + ch.charValue() + "'");
                break;
            case RPMTYPE_INT8:
                value = this.values.get(i);
                int int8 = ((Integer) value).intValue();
                out.println(prefix + "[" + i + "] = " + int8 + "");
                break;
            case RPMTYPE_INT16:
                value = this.values.get(i);
                int int16 = ((Integer) value).intValue();
                out.println(prefix + "[" + i + "] = " + int16 + "");
                break;
            case RPMTYPE_INT32:
                value = this.values.get(i);
                int int32 = ((Integer) value).intValue();
                out.println(prefix + "[" + i + "] = " + int32 + "");
                break;
            case RPMTYPE_INT64:
                value = this.values.get(i);
                long int64 = ((Long) value).longValue();
                out.println(prefix + "[" + i + "] = " + int64 + "");
                break;
            case RPMTYPE_STRING:
                String str = (String) this.values.get(i);
                out.println(prefix + "[" + i + "] = " + str + "");
                break;
            case RPMTYPE_BIN:
                byte[] data = (byte[]) this.values.get(0);
                out.println(RPMUtil.hexDumpString(prefix, data, 0, data.length));
                i = this.count; // terminate the loop.
                break;
            case RPMTYPE_STRING_ARRAY:
                String[] sAry = (String[]) this.values.get(0);
                out.println(prefix + "[" + i + "] = " + sAry[i] + "");
                break;
            case RPMTYPE_I18NSTRING:
                // UNDONE
                break;
            }
        }
    }

    public String typeString() {
        switch (this.type) {
        case RPMTYPE_NULL:
            return "NULL";
        case RPMTYPE_CHAR:
            return "CHAR";
        case RPMTYPE_INT8:
            return "INT8";
        case RPMTYPE_INT16:
            return "INT16";
        case RPMTYPE_INT32:
            return "INT32";
        case RPMTYPE_INT64:
            return "INT64";
        case RPMTYPE_STRING:
            return "STRING";
        case RPMTYPE_BIN:
            return "BIN";
        case RPMTYPE_STRING_ARRAY:
            return "STRINGARRAY";
        case RPMTYPE_I18NSTRING:
            return "I18NSTRING";
        }

        return "UNKNOWN";
    }

    public static final int RPMSIGTAG_SIZE = 1000;
    public static final int RPMSIGTAG_LEMD5_1 = 1001;
    public static final int RPMSIGTAG_PGP = 1002;
    public static final int RPMSIGTAG_LEMD5_2 = 1003;
    public static final int RPMSIGTAG_MD5 = 1004;
    public static final int RPMTAG_NAME = 1000;
    public static final int RPMTAG_VERSION = 1001;
    public static final int RPMTAG_RELEASE = 1002;
    public static final int RPMTAG_SERIAL = 1003;
    public static final int RPMTAG_SUMMARY = 1004;
    public static final int RPMTAG_DESCRIPTION = 1005;
    public static final int RPMTAG_BUILDTIME = 1006;
    public static final int RPMTAG_BUILDHOST = 1007;
    public static final int RPMTAG_INSTALLTIME = 1008;
    public static final int RPMTAG_SIZE = 1009;
    public static final int RPMTAG_DISTRIBUTION = 1010;
    public static final int RPMTAG_VENDOR = 1011;
    public static final int RPMTAG_GIF = 1012;
    public static final int RPMTAG_XPM = 1013;
    public static final int RPMTAG_COPYRIGHT = 1014;
    public static final int RPMTAG_PACKAGER = 1015;
    public static final int RPMTAG_GROUP = 1016;
    public static final int RPMTAG_CHANGELOG = 1017;
    public static final int RPMTAG_SOURCE = 1018;
    public static final int RPMTAG_PATCH = 1019;
    public static final int RPMTAG_URL = 1020;
    public static final int RPMTAG_OS = 1021;
    public static final int RPMTAG_ARCH = 1022;
    public static final int RPMTAG_PREIN = 1023;
    public static final int RPMTAG_POSTIN = 1024;
    public static final int RPMTAG_PREUN = 1025;
    public static final int RPMTAG_POSTUN = 1026;
    public static final int RPMTAG_FILENAMES = 1027;
    public static final int RPMTAG_FILESIZES = 1028;
    public static final int RPMTAG_FILESTATES = 1029;
    public static final int RPMTAG_FILEMODES = 1030;
    public static final int RPMTAG_FILEUIDS = 1031;
    public static final int RPMTAG_FILEGIDS = 1032;
    public static final int RPMTAG_FILERDEVS = 1033;
    public static final int RPMTAG_MTIMES = 1034;
    public static final int RPMTAG_MD5S = 1035;
    public static final int RPMTAG_LINKTOS = 1036;
    public static final int RPMTAG_FLAGS = 1037;
    public static final int RPMTAG_ROOT = 1038;
    public static final int RPMTAG_FILEUSERNAME = 1039;
    public static final int RPMTAG_FILEGROUPNAME = 1040;
    public static final int RPMTAG_EXCLUDE = 1041;
    public static final int RPMTAG_EXCLUSIVE = 1042;
    public static final int RPMTAG_ICON = 1043;
    public static final int RPMTAG_SOURCERPM = 1044;
    public static final int RPMTAG_FILEVERIFYFLAGS = 1045;
    public static final int RPMTAG_ARCIVESIZE = 1046;
    public static final int RPMTAG_PROVIDES = 1047;
    public static final int RPMTAG_REQUIRESFLAGS = 1048;
    public static final int RPMTAG_REQUIRENAME = 1049;
    public static final int RPMTAG_REQUIREVERSION = 1050;
    public static final int RPMTAG_NOSOURCE = 1051;
    public static final int RPMTAG_NOPATCH = 1052;
    public static final int RPMTAG_CONFLICTFLAGS = 1053;
    public static final int RPMTAG_CONFLICTNAME = 1054;
    public static final int RPMTAG_CONFLICTVERSION = 1055;
    public static final int RPMTAG_DEFAULTPREFIX = 1056;
    public static final int RPMTAG_BUILDROOT = 1057;
    public static final int RPMTAG_INSTALLPREFIX = 1058;
    public static final int RPMTAG_EXCLUDEARCH = 1059;
    public static final int RPMTAG_EXCLUDEOS = 1060;
    public static final int RPMTAG_EXCLUSIVEARCH = 1061;
    public static final int RPMTAG_EXCLUSIZEOS = 1062;
    public static final int RPMTAG_AUTOREQPROV = 1063;
    public static final int RPMTAG_RPMVERSION = 1064;
    public static final int RPMTAG_TRIGGERSCRIPTS = 1065;
    public static final int RPMTAG_TRIGGERNAME = 1066;
    public static final int RPMTAG_TRIGGERVERSION = 1067;
    public static final int RPMTAG_TRIGGERFLAGS = 1068;
    public static final int RPMTAG_TRIGGERINDEX = 1069;
    public static final int RPMTAG_VERIFYSCRIPT = 1079;
    public static final int RPMTAG_CHANGELOGTIME = 1080;
    public static final int RPMTAG_CHANGELOGNAME = 1081;
    public static final int RPMTAG_CHANGELOGTEXT = 1082;
    public static final int RPMTAG_BROKENMD5 = 1083;
    public static final int RPMTAG_PREREQ = 1084;
    public static final int RPMTAG_PREINPROG = 1085;
    public static final int RPMTAG_POSTINPROG = 1086;
    public static final int RPMTAG_PREUNPROG = 1087;
    public static final int RPMTAG_POSTUNPROG = 1088;
    public static final int RPMTAG_BUILDARCHS = 1089;
    public static final int RPMTAG_OBSOLETES = 1090;
    public static final int RPMTAG_VERIFYSCRIPTPROG = 1091;
    public static final int RPMTAG_TRIGGERSCRIPTPROG = 1092;
    public static final int RPMTAG_DOCDIR = 1093;
    public static final int RPMTAG_COOKIE = 1094;
    public static final int RPMTAG_FILEDEVICES = 1095;
    public static final int RPMTAG_FILEINODES = 1096;
    public static final int RPMTAG_FILELANGS = 1097;
    public static final int RPMTAG_PREFIXES = 1098;
    public static final int RPMTAG_INSTPREFIXES = 1099;
    public static final int RPMTAG_TRIGGERIN = 1100;
    public static final int RPMTAG_TRIGGERUN = 1101;
    public static final int RPMTAG_TRIGGERPOSTUN = 1102;
    public static final int RPMTAG_AUTOREQ = 1103;
    public static final int RPMTAG_AUTOPROV = 1104;
    public static final int RPMTAG_CAPABILITY = 1105;
    public static final int RPMTYPE_NULL = 0;
    public static final int RPMTYPE_CHAR = 1;
    public static final int RPMTYPE_INT8 = 2;
    public static final int RPMTYPE_INT16 = 3;
    public static final int RPMTYPE_INT32 = 4;
    public static final int RPMTYPE_INT64 = 5;
    public static final int RPMTYPE_STRING = 6;
    public static final int RPMTYPE_BIN = 7;
    public static final int RPMTYPE_STRING_ARRAY = 8;
    public static final int RPMTYPE_I18NSTRING = 9;
}
