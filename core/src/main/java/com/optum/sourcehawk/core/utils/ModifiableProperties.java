package com.optum.sourcehawk.core.utils;

import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Modified from original source:
 * https://www.dreamincode.net/forums/topic/53734-java-code-to-modify-properties-file-and-preserve-comments/
 *
 * @author Walt F - https://www.dreamincode.net/forums/user/320878-waltf/
 * @author Brian Wyka
 */
@SuppressWarnings("squid:S2160")
public class ModifiableProperties extends java.util.Properties {

    private static final long serialVersionUID = -736423775646688046L;

    /**
     * Use a list to keep a copy of lines that are a comment or 'blank'
     */
    private final List<String> lines = new ArrayList<>(0);

    /**
     * Use a list to keep a copy of lines containing a key, i.e. they are a property.
     */
    private final List<String> keys = new ArrayList<>(0);

    /**
     * Create an instance and load the input stream
     *
     * @param inputStream the input stream to load
     * @return the properties
     * @throws IOException if an error occurs loading the input stream
     */
    public static ModifiableProperties create(final InputStream inputStream) throws IOException {
        val properties = new ModifiableProperties();
        properties.load(inputStream);
        return properties;
    }

    /**
     * Load properties from the specified InputStream.
     * Overload the load method in Properties so we can keep comment and blank lines.
     *
     * @param inputStream the input stream to read
     */
    @Override
    @SuppressWarnings({"squid:S3776", "squid:S135"})
    public synchronized void load(final InputStream inputStream) throws IOException {
        val reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        String line;

        while ((line = reader.readLine()) != null) {

            char c = 0;
            int pos = 0;

            // Leading whitespaces must be deleted first.
            while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                pos++;
            }

            // If empty line or begins with a comment character, save this line in lineData and save a "" in keyData.
            if ((line.length() - pos) == 0 || line.charAt(pos) == '#' || line.charAt(pos) == '!') {
                lines.add(line);
                keys.add("");
                continue;
            }

            /*
             * The characters up to the next Whitespace, ':', or '=' describe the key.  But look for escape sequences.
             * Try to short-circuit when there is no escape char.
             */
            int start = pos;
            val needsEscape = line.indexOf('\\', pos) != -1;
            val key = needsEscape ? new StringBuffer() : null;

            while (pos < line.length() && !Character.isWhitespace(c = line.charAt(pos++)) && c != '=' && c != ':') {
                if (needsEscape && c == '\\') {
                    if (pos == line.length()) {
                        // The line continues on the next line.  If there is no next line, just treat it as a key with an empty value.
                        line = reader.readLine();
                        if (line == null) {
                            line = "";
                        }
                        pos = 0;
                        while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                            pos++;
                        }
                    } else {
                        c = line.charAt(pos++);
                        switch (c) {
                            case 'n':
                                key.append('\n');
                                break;
                            case 't':
                                key.append('\t');
                                break;
                            case 'r':
                                key.append('\r');
                                break;
                            case 'u':
                                if (pos + 4 <= line.length()) {
                                    char uni = (char) Integer.parseInt(line.substring(pos, pos + 4), 16);
                                    key.append(uni);
                                    pos += 4;
                                }
                                break;
                            default:
                                key.append(c);
                                break;
                        }
                    }
                } else if (needsEscape) {
                    key.append(c);
                }
            }

            val isDelimiter = (c == ':' || c == '=');

            String keyString;
            if (needsEscape) {
                keyString = key.toString();
            } else if (isDelimiter || Character.isWhitespace(c)) {
                keyString = line.substring(start, pos - 1);
            } else {
                keyString = line.substring(start, pos);
            }

            while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                pos++;
            }

            if (!isDelimiter && (c == ':' || c == '=')) {
                pos++;
                while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                }
            }

            // Short-circuit if no escape chars found.
            if (!needsEscape) {
                put(keyString, line.substring(pos));
                // Save a "" in lineData and save this keyString in keyData.
                lines.add("");
                keys.add(keyString);
                continue;
            }

            // Escape char found so iterate through the rest of the line.
            val element = new StringBuilder(line.length() - pos);
            while (pos < line.length()) {
                c = line.charAt(pos++);
                if (c == '\\') {
                    if (pos == line.length()) {
                        // The line continues on the next line.
                        line = reader.readLine();

                        // We might have seen a backslash at the end of the file.  The JDK ignores the backslash in this case, so we follow for compatibility.
                        if (line == null) {
                            break;
                        }

                        pos = 0;
                        while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                            pos++;
                        }
                        element.ensureCapacity(line.length() - pos + element.length());
                    } else {
                        c = line.charAt(pos++);
                        switch (c) {
                            case 'n':
                                element.append('\n');
                                break;
                            case 't':
                                element.append('\t');
                                break;
                            case 'r':
                                element.append('\r');
                                break;
                            case 'u':
                                if (pos + 4 <= line.length()) {
                                    char uni = (char) Integer.parseInt(line.substring(pos, pos + 4), 16);
                                    element.append(uni);
                                    pos += 4;
                                }
                                break;
                            default:
                                element.append(c);
                                break;
                        }
                    }
                } else {
                    element.append(c);
                }
            }
            put(keyString, element.toString());
            // Save a "" in lineData and save this keyString in keyData.
            lines.add("");
            keys.add(keyString);
        }
    }

    /**
     * Write the properties to the specified OutputStream.
     * <p>
     * Overloads the store method in Properties so we can put back comment
     * and blank lines.
     *
     * @param writer the writer to use for storing
     * @param header Ignored, here for compatibility w/ Properties.
     */
    @Override
    public void store(final Writer writer, final String header) throws IOException {
        // We ignore the header, because if we prepend a commented header
        // then read it back in it is now a comment, which will be saved
        // and then when we write again we would prepend Another header...

        String line;
        String key;
        val stringBuilder = new StringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            line = lines.get(i);
            key = keys.get(i);
            if (key.length() > 0) {  // This is a 'property' line, so rebuild it
                formatForOutput(key, stringBuilder, true);
                stringBuilder.append('=');
                formatForOutput(getProperty(key), stringBuilder, false);
                writer.write(stringBuilder.toString());
            } else {  // was a blank or comment line, so just restore it
                writer.write(line);
            }
            writer.write('\n');
        }
        writer.flush();
    }

    /**
     * Need this method from Properties because original code has StringBuilder,
     * which is an element of Java 1.5, used StringBuffer instead (because
     * this code was written for Java 1.4)
     *
     * @param outputString - the string to format
     * @param stringBuilder - stringBuilder to hold the string
     * @param key - true if outputString the key is formatted, false if the value is formatted
     */
    private void formatForOutput(final String outputString, final StringBuilder stringBuilder, final boolean key) {
        if (key) {
            stringBuilder.setLength(0);
            stringBuilder.ensureCapacity(outputString.length());
        } else {
            stringBuilder.ensureCapacity(stringBuilder.length() + outputString.length());
        }
        boolean head = true;
        val size = outputString.length();
        for (int i = 0; i < size; i++) {
            char c = outputString.charAt(i);
            switch (c) {
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case ' ':
                    stringBuilder.append(head ? "\\ " : " ");
                    break;
                case '\\':
                case '!':
                case '#':
                case '=':
                case ':':
                    stringBuilder.append('\\').append(c);
                    break;
                default:
                    if (c < ' ' || c > '~') {
                        String hex = Integer.toHexString(c);
                        stringBuilder.append("\\u0000");
                        stringBuilder.append(hex);
                    } else {
                        stringBuilder.append(c);
                    }
            }
            if (c != ' ') {
                head = key;
            }
        }
    }

    /**
     * Add a Property to the end of the CommentedProperties.
     *
     * @param keyString The Property key.
     * @param value The value of this Property.
     */
    public void add(String keyString, String value) {
        put(keyString, value);
        lines.add("");
        keys.add(keyString);
    }

    /**
     * Add a comment or blank line or comment to the end of the CommentedProperties.
     *
     * @param line The string to add to the end, make sure this is a comment
     * or a 'whitespace' line.
     */
    public void addLine(String line) {
        lines.add(line);
        keys.add("");
    }

}

