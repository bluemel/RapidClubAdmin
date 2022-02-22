/*
 * Rapid Club Admin Application: WebFileManager.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 06.10.2007
 */
package org.rapidbeans.clubadmin.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.presentation.CustomerSettings;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.UtilException;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.security.CryptoHelper;

/**
 * This manager is responsible for processing remote files located on a web
 * server. Basically it supports two kinds of remote file access:
 *
 * - httpfileio: Download and upload via server side PHP script The preferred
 * method. Less security risks than FTP File changes could be restricted to a
 * special web server sub directory More flexible because with HTTP you usually
 * don't have a problem with proxies or firewalls
 *
 * -ftp: Download and upload via simple FTP connection. TODO implement using
 * commons ftpclient => no warnings, more flexible
 *
 * -httpreadonly: simple Java HTTP download (upload not possible)
 *
 * @author Martin Bluemel
 */
public class WebFileManager {

	private static final Logger log = Logger.getLogger(WebFileManager.class.getName());

	private static final String PREFIX_URL_FILEIO = "http://fileio.host";

	private String server = null;

	private String rootDataHttp = null;

	private String rootDataFtp = null;

	private String ftpUser = null;

	private String ftpPwd = null;

	private final HttpClientPhp httpClient;

	private WebFileAccessMode accessMethod = null;

	public WebFileManager(final String initServer, final String rootAppHttp) {
		this.server = initServer;
		this.rootDataHttp = rootAppHttp + "/data";
		this.httpClient = new HttpClientPhp("http://" + server + '/' + rootAppHttp + "/fileio.php", "musashi09");
	}

	public void init() {
		final CustomerSettings settings = ((RapidClubAdminClient) ApplicationManager.getApplication())
				.getCustomerSettings();
		this.ftpUser = CryptoHelper.decrypt(settings.getFtpuser(), RapidClubAdminClient.HEIDI);
		this.ftpPwd = CryptoHelper.decrypt(settings.getFtppwd(), RapidClubAdminClient.HEIDI);
		this.rootDataFtp = settings.getRootAppFtp() + "/data";
		if (this.accessMethod == null && settings.getWebfileaccess() != null) {
			this.accessMethod = settings.getWebfileaccess();
		}
	}

	public String[] list(final String path) {
		switch (this.accessMethod) {
		case php:
			try {
				return this.httpClient.list(path);
			} catch (MalformedURLException e) {
				throw new RapidBeansRuntimeException(e);
			} catch (IOException e) {
				throw new RapidBeansRuntimeException(e);
			}
		default:
//            throw new RapidBeansRuntimeException("list for web folder \""
//                    + path + "\" is not supported for access mode \""
//                    + this.accessMethod.toString() +"\"");
			return new String[0];
		}
	}

	/**
	 * Creates a folder an all its parent folders if not already existing.
	 * 
	 * @param path the pure file path not the complete URL e. g. for URL =
	 *             "fileio://current/Chanbara" path = "current/Chanbara"
	 */
	public void mkdirs(final String path) {
		switch (this.accessMethod) {
		case php:
			try {
				if (!this.httpClient.exists(path)) {
					if (!this.httpClient.mkdirs(path)) {
						throw new WebFileAccessException(
								"Faild to create remote directory \"" + path + "\" via HTTP fileio.php.");
					}
				}
			} catch (MalformedURLException e) {
				throw new RapidBeansRuntimeException(e);
			} catch (IOException e) {
				throw new RapidBeansRuntimeException(e);
			}
			break;
		default:
			ApplicationManager.getApplication().messageInfo(
					"Bitte Existenz des Verzeichnisses \"" + path + "\" sicherstellen.",
					"Potentielle Handarbeit f�r FTP �bertragungsmodus");
//            throw new RapidBeansRuntimeException("mkdirs for web folder \""
//                    + path + "\" is not supported for access mode \""
//                    + this.accessMethod.toString() +"\"");
		}
	}

	public void delete(final String path) {
		switch (this.accessMethod) {
		case php:
			try {
				this.httpClient.delete(path);
			} catch (MalformedURLException e) {
				throw new RapidBeansRuntimeException(e);
			} catch (IOException e) {
				throw new RapidBeansRuntimeException(e);
			}
			break;
		default:
			throw new RapidBeansRuntimeException("delete for web folder \"" + path
					+ "\" is not supported for access mode \"" + this.accessMethod.toString() + "\"");
		}
	}

	public URL getFtpUrl(final String filename, final BillingPeriod bp, final Department dep) {
		try {
			return new URL(getFtpUrlString(filename, bp, dep, false));
		} catch (MalformedURLException e) {
			throw new RapidBeansRuntimeException(e);
		}
	}

	public URL getFileioUrl(final String filename, final BillingPeriod bp, final Department dep) {
		try {
			return new URL(getFileioUrlString(filename, bp, dep));
		} catch (MalformedURLException e) {
			throw new RapidBeansRuntimeException(e);
		}
	}

	/**
	 * Determine an URL to access a file via HTTP as follows:<br/>
	 * <b><code>http://&lt;server&gt;/&lt;rootDataHttp&gt;/&lt;filename&gt;</code></b>
	 * 
	 * @param filename the file name
	 * @param dep      department (optional). If not null the HTTP URL is:<br/>
	 *                 <b><code>http://&lt;server&gt;/&lt;rootDataHttp&gt;/&lt;dep.name&gt;/&lt;filename&gt;</code></b>
	 * 
	 * @return the HTTP URL
	 */
	public URL getHttpUrl(final String filename, final BillingPeriod bp, final Department dep) {
		try {
			StringBuffer httpUrl = new StringBuffer();
			httpUrl.append("http://");
			httpUrl.append(server);
			httpUrl.append('/');
			httpUrl.append(rootDataHttp);
			appendDepartmentAndFile(httpUrl, bp, dep, filename);
			return new URL(httpUrl.toString());
		} catch (MalformedURLException e) {
			throw new RapidBeansRuntimeException(e);
		}
	}

	public URL getUploadUrl(final String filename, final BillingPeriod bp, final Department dep) {
		if (this.accessMethod == null) {
			throw new RapidBeansRuntimeException(
					"Can not determine the Web Access Method.\n" + "No download happened so far.");
		}
		switch (this.accessMethod) {
		case php:
			return this.getFileioUrl(filename, bp, dep);
		case ftp:
			return this.getFtpUrl(filename, bp, dep);
		default:
			throw new RapidBeansRuntimeException(
					"can not upload files over web." + " Neither with fileio (PHP) nor with FTP");
		}
	}

	/**
	 * Determine the string for a URL to access a file via fileio as follows:<br/>
	 * <b><code>ftp://&lt;ftpUser&gt;:&lt;ftpPwd&gt;@&lt;server&gt;/&lt;rootDataFtp&gt;/&lt;filename&gt;</code></b>
	 * 
	 * @param filename             the file name
	 * @param dep                  department (optional). If not null the HTTP URL
	 *                             is:<br/>
	 *                             <b><code>ftp://&lt;ftpUser&gt;:&lt;ftpPwd&gt;@&lt;server&gt;/&lt;rootDataFtp&gt;/&lt;dep.name&gt;/&lt;filename&gt;</code></b>
	 * @param obfuscateCredentials if the string should just be used for tracing
	 *                             output you can determine to obfuscate user and
	 *                             password with "***".
	 * 
	 * @return the FTP URL string
	 */
	private String getFileioUrlString(final String filename, final BillingPeriod bp, final Department dep) {
		StringBuffer url = new StringBuffer();
		url.append(PREFIX_URL_FILEIO);
		appendDepartmentAndFile(url, bp, dep, filename);
		return url.toString();
	}

	/**
	 * Determine the string for a URL to access a file via FTP as follows:<br/>
	 * <b><code>ftp://&lt;ftpUser&gt;:&lt;ftpPwd&gt;@&lt;server&gt;/&lt;rootDataFtp&gt;/&lt;filename&gt;</code></b>
	 * 
	 * @param filename             the file name
	 * @param dep                  department (optional). If not null the HTTP URL
	 *                             is:<br/>
	 *                             <b><code>ftp://&lt;ftpUser&gt;:&lt;ftpPwd&gt;@&lt;server&gt;/&lt;rootDataFtp&gt;/&lt;dep.name&gt;/&lt;filename&gt;</code></b>
	 * @param obfuscateCredentials if the string should just be used for tracing
	 *                             output you can determine to obfuscate user and
	 *                             password with "***".
	 * 
	 * @return the FTP URL string
	 */
	private String getFtpUrlString(final String filename, final BillingPeriod bp, final Department dep,
			final boolean obfuscateCredentials) {
		StringBuffer ftpUrl = new StringBuffer();
		ftpUrl.append("ftp://");
		if (obfuscateCredentials) {
			ftpUrl.append("***");
		} else {
			ftpUrl.append(ftpUser);
		}
		ftpUrl.append(':');
		if (obfuscateCredentials) {
			ftpUrl.append("***");
		} else {
			ftpUrl.append(ftpPwd);
		}
		ftpUrl.append('@');
		ftpUrl.append(StringHelper.splitFirst(server, "/"));
		ftpUrl.append('/');
		ftpUrl.append(rootDataFtp);
		appendDepartmentAndFile(ftpUrl, bp, dep, filename);
		ftpUrl.append(";type=i");
		return ftpUrl.toString();
	}

	/**
	 * Append department and file to URL in StringBuffer.
	 * 
	 * @param urlBuffer the URL buffer
	 * @param bp        the history billing period (null for current trainings list)
	 * @param dep       the department
	 * @param filename  the file name
	 */
	private void appendDepartmentAndFile(final StringBuffer urlBuffer, final BillingPeriod bp, final Department dep,
			final String filename) {
		if (dep != null) {
			if (bp == null) {
				urlBuffer.append("/current/");
			} else {
				urlBuffer.append("/history/");
				urlBuffer.append(bp.getIdString());
				urlBuffer.append('/');
			}
			if (((RapidClubAdminClient) ApplicationManager.getApplication()).getMasterData().getClubs().size() == 1) {
				urlBuffer.append(dep.getName());
			} else {
				urlBuffer.append(((Club) dep.getParentBean()).getName());
				urlBuffer.append('/');
				urlBuffer.append(dep.getName());
			}
		}
		urlBuffer.append('/');
		urlBuffer.append(filename);
	}

	/**
	 * Load a document over the web via - HTTP/PHP (fileio.php), - FTP, or
	 * alternatively - simple HTTP
	 * 
	 * @param docname  the document name
	 * @param filename the filename
	 *
	 * @return the Document downloaded
	 */
	public Document downloadDocument(final String docname, final String filename, final boolean tryHttp,
			final boolean interact, final boolean askHttp, final BillingPeriod bp, final Department dep) {
		final Application client = ApplicationManager.getApplication();
		final RapidBeansLocale locale = client.getCurrentLocale();

		if (this.accessMethod == null) {
			try {
				final Document doc = downloadDocumentHttpfileio(filename, docname, bp, dep);
				this.accessMethod = WebFileAccessMode.php;
				return doc;
			} catch (IOException e) {
				// ignore and fall back via FTP
				e.printStackTrace();
				log.info("Fall back to FTP: " + e.getMessage());
			} catch (RapidBeansRuntimeException e) {
				e.printStackTrace();
				// ignore and fall back via FTP
				log.info("Fall back to FTP: " + e.getMessage());
			}
			try {
				final Document doc = downloadDocumentFtp(filename, docname, bp, dep);
				this.accessMethod = WebFileAccessMode.ftp;
				return doc;
			} catch (RapidBeansRuntimeException ex) {
				if (ex.getCause() instanceof UtilException) {
					final UtilException e = (UtilException) ex.getCause();
					if (e.getCause() instanceof FileNotFoundException) {
						if (interact) {
							client.messageError(
									locale.getStringMessage("error.load.file.web.trainingslist.filenotfound",
											this.getFtpUrlString(filename, bp, dep, true), this.server),
									locale.getStringMessage("error.load.file.web.title"));
						} else {
							throw e;
						}
					} else if (e.getCause() instanceof UnknownHostException) {
						if (interact) {
							client.messageError(locale.getStringMessage("error.load.file.web.trainingslist.connection"),
									locale.getStringMessage("error.load.file.web.title"));
						} else {
							throw e;
						}
					} else if (e.getCause().getClass().getName().equals("sun.net.ftp.FtpProtocolException")
							|| e.getCause() instanceof SocketException
							|| e.getCause() instanceof IOException) {
						if (!tryHttp) {
							throw new RapidBeansRuntimeException(e);
						}
					} else {
						throw e;
					}
				} else {
					throw ex;
				}
			}
			if (!askHttp || client.messageYesNo(locale.getStringMessage("error.load.file.web.trainingslist.ftp"),
					locale.getStringMessage("error.load.file.web.title"))) {
				final Document doc = downloadDocumentFtp(filename, docname, bp, dep);
				this.accessMethod = WebFileAccessMode.httpreadonly;
				return doc;
			}
			throw new RapidBeansRuntimeException("Could not laod document " + docname + " [" + filename + "]");
		} else {
			switch (this.accessMethod) {
			case php:
				try {
					return downloadDocumentHttpfileio(filename, docname, bp, dep);
				} catch (IOException e) {
					throw new RapidBeansRuntimeException(e);
				}
			case ftp:
				return downloadDocumentFtp(filename, docname, bp, dep);
			case httpreadonly:
				if (tryHttp && (!askHttp
						|| client.messageYesNo(locale.getStringMessage("error.load.file.web.trainingslist.ftp"),
								locale.getStringMessage("error.load.file.web.title")))) {
					return downloadDocumentHttpreadonly(filename, docname, bp, dep);
				} else {
					throw new RapidBeansRuntimeException("Download via HTTP not possible.");
				}
			default:
				throw new RapidBeansRuntimeException("Could not laod document " + docname + " [" + filename + "]");
			}
		}
	}

	/**
	 * Download a document over fileio PHP script.
	 *
	 * @param filename the filename
	 * @param docname  the document's name
	 * @param bp       the billing period (null for current documents)
	 * @param dep      the department
	 *
	 * @return the document downloaded
	 *
	 * @throws IOException
	 */
	private Document downloadDocumentHttpfileio(final String filename, final String docname, final BillingPeriod bp,
			final Department dep) throws IOException {
		final URL fileioUrl = this.getFileioUrl(filename, bp, dep);
		final String remoteFile = fileioUrl.toString().substring(PREFIX_URL_FILEIO.length() + 1);
		InputStream istream = null;
		try {
			log.info("Loading remote file " + remoteFile + " via fileio.php, FileioUrl = \"" + fileioUrl + "\"");
			istream = httpClient.readAsStream(remoteFile);
			final Document doc = new Document(docname, null, fileioUrl, istream);
			return doc;
		} catch (IOException e) {
			throw e;
		} finally {
			if (istream != null) {
				try {
					istream.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException(e);
				}
			}
		}
	}

	private Document downloadDocumentFtp(final String filename, final String docname, final BillingPeriod bp,
			final Department dep) {
		log.info("Loading " + filename + " via FTP");
		final Document doc = new Document(docname, this.getFtpUrl(filename, bp, dep));
		return doc;
	}

	public Document downloadDocumentHttpreadonly(final String filename, final String docname, final BillingPeriod bp,
			final Department dep) {
		log.info("Loading " + filename + " via HTTP");
		Document doc = new Document(docname, this.getHttpUrl(filename, bp, dep));
		doc.setReadonly(true);
		return doc;
	}

	/**
	 * Download an arbitrary file for read only access via HTTP. Currently used for
	 * trainer icon download. Could should be replaced by the strategies already
	 * applied for documents.
	 *
	 * @param srcPath remote source file
	 * @param target  local target file
	 */
	public void downloadFileHtpp(final String srcPath, final File target) {
		URL url = this.getHttpUrl(srcPath.replace(" ", "%20"), null, null);
		InputStream is = null;
		FileOutputStream os = null;
		try {
			final URLConnection urlc = url.openConnection();
			is = urlc.getInputStream();
			os = new FileOutputStream(target);
			int c;
			int retry = 0;
			while (retry < 5) {
				c = is.read();
				if (c == -1) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						throw new RapidBeansRuntimeException(e);
					}
					retry++;
				} else {
					os.write(c);
				}
			}
		} catch (IOException e) {
			throw new RapidBeansRuntimeException("reading document file \"" + url.toString() + "\"failed", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException("writing document file \"" + url.toString() + "\"failed", e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException("writing document file \"" + url.toString() + "\"failed", e);
				}
			}
		}
	}

	/**
	 * Uploads the given document.
	 */
	public void upload(final Document activeDocument, final String defaultEncoding, boolean forceEncoding) {
		if ("fileio.host".equals(activeDocument.getUrl().getHost())) {
			final String xml = activeDocument.toXmlString(defaultEncoding, forceEncoding);
			final String path = activeDocument.getUrl().getPath().substring(1);
			try {
				log.info("Storing via fileio: " + path + "...");
				httpClient.write(path, xml);
			} catch (IOException e) {
				throw new RapidBeansRuntimeException("Could not perform upload!", e);
			}
		} else {
			log.info("Stored via URL: " + activeDocument.getUrl());
			activeDocument.save(defaultEncoding, forceEncoding, null);
		}
	}

	public void uploadIcon(File icontargetfile, String iconPath) {
			try {
				log.info("Storing via fileio: " + icontargetfile.getAbsolutePath() + "...");
				httpClient.write(iconPath, icontargetfile);
			} catch (IOException e) {
				throw new RapidBeansRuntimeException("Could not perform upload!", e);
			}
	}

	public void uploadFileFtp(final File src, final String targetPath, final BillingPeriod bp, final Department dep) {
		URL url = this.getFtpUrl(targetPath, bp, dep);
		FileInputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			final URLConnection urlc = url.openConnection();
			os = urlc.getOutputStream();
			int c;
			while ((c = is.read()) != -1) {
				os.write(c);
			}
		} catch (IOException e) {
			throw new RapidBeansRuntimeException("writing document file \"" + url.toString() + "\"failed", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException("writing document file \"" + url.toString() + "\"failed", e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException("writing document file \"" + url.toString() + "\"failed", e);
				}
			}
		}
	}
}
