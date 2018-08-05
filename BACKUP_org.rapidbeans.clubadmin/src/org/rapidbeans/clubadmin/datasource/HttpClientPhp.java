/*
 * Rapid Club Admin Application: HttpClientPhp.java
 *
 * Copyright Benjamin Hummel, Martin Bluemel, 2009
 *
 * 10.01.2009
 */
package org.rapidbeans.clubadmin.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.rapidbeans.clubadmin.service.Obfcte;
import org.rapidbeans.core.util.StringHelper;

/**
 * Wrapper code for accessing server content using HTTP. The web server has to
 * provide the fileio.php script.
 * 
 * @author Benjamin Hummel, Martin Bluemel
 */
public class HttpClientPhp {

    /** Prefix of the fileio PHP script. */
    private final String fileioUrl;

    /** The password used. */
    private final String password;

    /** Constructor. */
    public HttpClientPhp(String fileioUrl, String password) {
        this.fileioUrl = fileioUrl;
        this.password = password;
    }

    /**
     * Reads a remote text file with the given relative path.
     * 
     * @param path
     *            the path of the remote file to read relatively to the
     *            executing PHP script's location.
     * 
     * @return the text file's content as string
     */
    public String read(String path) throws IOException {
        final URLConnection conn = getConnection(path, "read");
        return readConnection(conn);
    }

    /** Reads the file of the given name. */
    public InputStream readAsStream(String file) throws IOException {
        return getConnection(file, "read").getInputStream();
    }

    /** Writes the file of the given name. */
    public String write(final String path, final String contents) throws IOException {
        final URLConnection conn = getConnection(path, "write");
        conn.setDoOutput(true);

        final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write("contents=" + encode(contents));
        wr.flush();

        final String result = readConnection(conn);
        wr.close();
        return result;
    }

    /**
     * Lists the content of a remote directory (folder) with the given relative
     * path.
     * 
     * @param path
     *            the path of the remote file to read relatively to the
     *            executing PHP script's location.
     * 
     * @return the text file's content as string
     */
    public String[] list(String path) throws IOException {
        final URLConnection conn = getConnection(path, "list");
        final String result = readConnection(conn);
        final List<String> content = StringHelper.split(result, "\n");
        if (content.size() <= 2) {
            return new String[0];
        }
        final String[] sa = new String[content.size() - 2];
        int i = 0;
        for (String s : content) {
            if (!s.equals(".") && !s.equalsIgnoreCase("..")) {
                sa[i++] = s;
            }
        }
        return sa;
    }

    /**
     * Creates a remote directory with the given path.
     * 
     * @param path
     *            the path of the remote directory to create relatively to the
     *            executing PHP script's location.
     */
    public boolean mkdirs(final String path) throws MalformedURLException, IOException {
        return readConnAndEvalResult(path, "mkdirs");
    }

    /**
     * Deletes a remote file with the given path.
     * 
     * @param path
     *            the path of the remote file to delete relatively to the
     *            executing PHP script's location.
     */
    public boolean delete(final String path) throws MalformedURLException, IOException {
        return readConnAndEvalResult(path, "delete");
    }

    /**
     * Deletes a remote directory with the given path.
     * 
     * @param path
     *            the path of the remote directory to delete relatively to the
     *            executing PHP script's location.
     */
    public boolean rmdir(final String path) throws MalformedURLException, IOException {
        return readConnAndEvalResult(path, "rmdir");
    }

    /**
     * Checks existence of a remote file with the given path.
     * 
     * @param path
     *            the path of the remote file to check relatively to the
     *            executing PHP script's location.
     */
    public boolean exists(final String path) throws MalformedURLException, IOException {
        return readConnAndEvalResult(path, "exists");
    }

    /**
     * Sends a test email to the specified address.
     * 
     * @param to
     *            recipient address.
     * @param subject
     *            the subject
     * @param from
     *            sender address
     * @param message
     *            message content
     */
    public boolean sendMail(final String to, final String subject, final String from, final String message)
            throws MalformedURLException, IOException {
        final String content = Obfcte.ofcte(to + '|' + subject + '|' + from + '|' + message);
        return readConnAndEvalResult(content, "sendmail");
    }

    /** Reads from a connection and returns the contents as string. */
    private static String readConnection(final URLConnection conn) throws IOException {
        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),
                Charset.forName("UTF-8")));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        rd.close();
        return sb.toString();
    }

    private boolean readConnAndEvalResult(final String path, final String cmd) throws IOException {
        final URLConnection conn = this.getConnection(path, cmd);
        final String result = readConnection(conn);
        if (result.startsWith("success")) {
            return true;
        } else {
            return false;
        }
    }

    /** Constructs the URL for a given file name and operation. */
    private URLConnection getConnection(final String file, final String op) throws MalformedURLException, IOException {
        final URL url = new URL(fileioUrl + "?password=" + encode(password) + "&file=" + encode(file) + "&op="
                + encode(op));
        final URLConnection conn = url.openConnection();
        return conn;
    }

    /** Encodes a string as UTF-8. */
    private static String encode(final String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("will not happen for UTF-8");
        }
    }
}
