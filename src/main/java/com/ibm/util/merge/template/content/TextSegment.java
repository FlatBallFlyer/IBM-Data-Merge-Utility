package com.ibm.util.merge.template.content;

public class TextSegment extends Segment {
	String text = "";
	
	public TextSegment(String text) {
		super();
		this.text = text;
	}

	@Override
	public String getValue() {
		return text;
	}

	public void format(String format) {
		if (!format.isEmpty()) {
			float value = Float.valueOf(text);
			text = String.format(format, value);
		}
	}
	
	/**
	 * Encode.
	 *
	 * @param from the from
	 * @param encoding the encoding
	 * @return the string
	 */
	public void encode(int encoding) {
		switch (encoding) {
		case ENCODE_HTML:
		case ENCODE_XML:
			encodeXML();
			break;
    	case ENCODE_JSON:
    		encodeJson();
    	case ENCODE_SQL:
    		encodeSqlString();
    		break;
		}
	}
	
	private void encodeXML() {
        StringBuilder sBuilder = new StringBuilder(this.text.length() * 11/10);
        int stringLength = this.text.length();
        for (int i = 0; i < stringLength; ++i) {
            char c = this.text.charAt(i);
            switch (c) {
            case '&': 
                sBuilder.append("&amp;");
                break;
            case '>':
                sBuilder.append("&gt;");
                break;
            case '<':
                sBuilder.append("&lt;");
                break;
            case '"':
                sBuilder.append("&quot;");
                break;
            default:
                sBuilder.append(c);
            }
        }
        this.text = sBuilder.toString();
	}
	
	private void encodeJson() {
        StringBuilder sBuilder = new StringBuilder(this.text.length() * 11/10);
        int stringLength = this.text.length();
        for (int i = 0; i < stringLength; ++i) {
            char c = this.text.charAt(i);
            switch (c) {
            case '\\': 
                sBuilder.append('\\');
                sBuilder.append('\\');
                break;
            case '\t':
                sBuilder.append('\\');
                sBuilder.append('t');
                break;
            case '\n':
                sBuilder.append('\\');
                sBuilder.append('n');
                break;
            case '"':
                sBuilder.append('\\');
                sBuilder.append('\"');
                break;
            default:
                sBuilder.append(c);
            }
        }
        this.text = sBuilder.toString();
	}
	
	private void encodeSqlString() {
        StringBuilder sBuilder = new StringBuilder(this.text.length() * 11/10);
        int stringLength = this.text.length();
        for (int i = 0; i < stringLength; ++i) {
            char c = this.text.charAt(i);
            switch (c) {
            case 0: /* Must be escaped for 'mysql' */
                sBuilder.append('\\');
                sBuilder.append('0');
                break;
            case '\n': /* Must be escaped for logs */
                sBuilder.append('\\');
                sBuilder.append('n');
                break;
            case '\r':
                sBuilder.append('\\');
                sBuilder.append('r');
                break;
            case '\\':
                sBuilder.append('\\');
                sBuilder.append('\\');
                break;
            case '\'':
                sBuilder.append('\\');
                sBuilder.append('\'');
                break;
            case '"': /* Better safe than sorry */
                sBuilder.append('\\');
                sBuilder.append('"');
                break;
            case '\032': /* This gives problems on Win32 */
                sBuilder.append('\\');
                sBuilder.append('Z');
                break;
            case '\u00a5':
            case '\u20a9':
                // escape characters interpreted as backslash by mysql
                // fall through
            default:
                sBuilder.append(c);
            }
        }
        this.text = sBuilder.toString();
    }

}
