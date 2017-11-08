/*
 * 
 * Copyright 2015-2017 IBM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge.template.content;

/**
 * Represents a String from with in the tempalte content
 * 
 * @author Mike Storey
 *
 */
public class TextSegment extends Segment {
	String text = "";
	
	/**
	 * Instantiate a text object
	 * @param text
	 */
	public TextSegment(String text) {
		super();
		this.text = text;
	}


	@Override
	public Segment getMergable() {
		TextSegment mergable = new TextSegment(this.text);
		return mergable;
	}

	/**
	 * @param format the text using a format string
	 */
	public void format(String format) {
		if (!format.isEmpty()) {
			float value = Float.valueOf(text);
			text = String.format(format, value);
		}
	}
	
	/**
	 * Encode a text value based on an encoding scheme
	 *
	 * @param from the from
	 * @param encoding the encoding
	 * @return the string
	 */
	/**
	 * @param encoding
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
	
	/**
	 * Encoding for string in XML 
	 */
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
	
	/**
	 * Encoding for string within a JSON value
	 */
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
	
	/**
	 * Encoding for a SQL Query STring
	 */
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

	@Override
	public String getValue() {
		return text;
	}

}
