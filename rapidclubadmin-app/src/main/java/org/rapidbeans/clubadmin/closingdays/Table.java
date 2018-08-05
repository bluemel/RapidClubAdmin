package org.rapidbeans.clubadmin.closingdays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.util.XmlNode;

public class Table {

	private List<String> headerRow;

	private List<List<String>> cells;

	public static Table fromHtmlText(final String htmlText) {
		final String cvt = adaptToXml(Umlaut.convertFromHtmlToAscii(htmlText).replace("&nbsp;", "\n"));
		final XmlNode tableNode = XmlNode.getDocumentTopLevel(cvt);
		return new Table(tableNode);
	}

	// simple HTML to XML adaption
	private static String adaptToXml(String html) {
		final StringBuilder sb = new StringBuilder();
		final Map<String, String> attributeMap = new HashMap<String, String>();
		final Stack<String> elementStack = new Stack<String>();
		final StringBuilder sbElementName = new StringBuilder();
		final StringBuilder sbAttributeName = new StringBuilder();
		final StringBuilder sbAttributeValue = new StringBuilder();
		final int len = html.length();
		int state = 0;
		for (int i = 0; i < len; i++) {
			final char c = html.charAt(i);
			switch (state) {
			case 0: // out of element or within value
				switch (c) {
				case '<':
					if (html.charAt(i + 1) == '/') {
						i++;
						state = 7;
					} else {
						sb.append(c);
						state = 1;
					}
					break;
				default:
					sb.append(c);
					break;
				}
				break;

			case 1: // element name
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
					sb.append(sbElementName);
					elementStack.push(sbElementName.toString());
					sbElementName.setLength(0);
					state = 2;
					break;
				case '>':
					sb.append(sbElementName);
					elementStack.push(sbElementName.toString());
					sbElementName.setLength(0);
					sb.append(c);
					state = 0;
					break;
				default:
					sbElementName.append(c);
					break;
				}
				break;

			case 2: // before attribute name
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
					// do nothing
					break;
				case '>':
					writeAttrMap(sb, attributeMap);
					sb.append(c);
					state = 0;
					break;
				default:
					sbAttributeName.append(c);
					state = 3;
					break;
				}
				break;

			case 3: // attribute name
				switch (c) {
				case '=':
					state = 4;
					break;
				default:
					sbAttributeName.append(c);
					break;
				}
				break;

			case 4: // 1st char of attribute value
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
					// do nothing
					break;
				case '"':
					state = 8;
					break;
				default:
					sbAttributeValue.append(c);
					state = 5;
					break;
				}
				break;

			case 5: // consecutive chars of attribute value
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
					finishAttribute(sbAttributeName, sbAttributeValue, attributeMap);
					state = 2;
					break;
				case '>':
					finishAttribute(sbAttributeName, sbAttributeValue, attributeMap);
					writeAttrMap(sb, attributeMap);
					sb.append(c);
					state = 0;
					break;
				case '"':
					finishAttribute(sbAttributeName, sbAttributeValue, attributeMap);
					state = 2;
					break;
				default:
					sbAttributeValue.append(c);
					break;
				}
				break;

			case 8: // consecutive chars of attribute value within quotes
				switch (c) {
				case '"':
					finishAttribute(sbAttributeName, sbAttributeValue, attributeMap);
					state = 2;
					break;
				default:
					sbAttributeValue.append(c);
					break;
				}
				break;

			case 6: // element value
				switch (c) {
				case '<':
					if (html.charAt(i + 1) == '/') {
						i++;
						state = 7;
					} else {
						sb.append(c);
						state = 1;
					}
					break;
				default:
					sb.append(c);
					break;
				}

			case 7: // element closing name
				switch (c) {
				case '>':
					String currentElement = elementStack.pop();
					if (!(sbElementName.toString().equals(currentElement))) {
						if (currentElement.equals("b")) {
							sb.append("</b>");
							currentElement = elementStack.pop();
						}
					}
					if (!(sbElementName.toString().equals(currentElement))) {
						throw new IllegalStateException("Element stack mismatch: current element \"" + currentElement
								+ "\", closed element \"" + sbElementName.toString() + "\"");
					}
					sb.append("</");
					sb.append(sbElementName);
					sbElementName.setLength(0);
					sb.append(c);
					state = 0;
					break;
				default:
					sbElementName.append(c);
					break;
				}
				break;

			default:
				throw new IllegalStateException("Unexpected parser state " + Integer.toString(state));
			}
		}
		return sb.toString();
	}

	private static void writeAttrMap(final StringBuilder sb, final Map<String, String> attributeMap) {
		for (final Entry<String, String> attr : attributeMap.entrySet()) {
			sb.append(' ').append(attr.getKey()).append("=\"").append(attr.getValue()).append('"');
		}
		attributeMap.clear();
	}

	private static void finishAttribute(final StringBuilder sbAttributeName, final StringBuilder sbAttributeValue,
			final Map<String, String> attributeMap) {
		attributeMap.put(sbAttributeName.toString(), sbAttributeValue.toString());
		sbAttributeName.setLength(0);
		sbAttributeValue.setLength(0);
	}

	public Table(final XmlNode tableNode) {
		if (tableNode.getSubnodes("thead").size() == 1) {
			this.headerRow = new ArrayList<String>();
			final XmlNode headerNode = tableNode.getFirstSubnode("thead");
			final XmlNode headerRowNode = headerNode.getFirstSubnode("tr");
			for (final XmlNode headerCellNode : headerRowNode.getSubnodes("th")) {
				final StringBuilder sb = new StringBuilder();
				if (headerCellNode.getValue().trim().length() > 0) {
					sb.append(headerCellNode.getValue());
				}
				for (final XmlNode linkNode : headerCellNode.getSubnodes("a")) {
					if (linkNode.getValue().trim().length() > 0) {
						sb.append(linkNode.getValue());
					}
				}
				if (sb.toString().trim().length() > 0) {
					this.headerRow.add(sb.toString());
				} else {
					this.headerRow.add("");
				}
			}
		}
		this.cells = new ArrayList<List<String>>();
		List<XmlNode> rowNodes;
		if (tableNode.getSubnodes("tbody").size() == 1) {
			final XmlNode bodyNode = tableNode.getFirstSubnode("tbody");
			rowNodes = bodyNode.getSubnodes("tr");
		} else {
			rowNodes = tableNode.getSubnodes("tr");
		}
		for (final XmlNode rowNode : rowNodes) {
			final List<String> row = new ArrayList<String>();
			for (final XmlNode cellNode : rowNode.getSubnodes("td")) {
				final StringBuilder sb = new StringBuilder();
				if (cellNode.getValue() != null && cellNode.getValue().trim().length() > 0) {
					sb.append(cellNode.getValue());
				}
				for (final XmlNode boldNode : cellNode.getSubnodes("b")) {
					if (boldNode.getValue() != null && boldNode.getValue().trim().length() > 0) {
						sb.append(boldNode.getValue());
					}
					for (final XmlNode linkNode : boldNode.getSubnodes("a")) {
						if (linkNode.getValue().trim().length() > 0) {
							sb.append(linkNode.getValue());
						}
					}
				}
				for (final XmlNode divNode : cellNode.getSubnodes("div")) {
					if (divNode.getValue().trim().length() > 0) {
						sb.append(divNode.getValue());
					}
					for (final XmlNode linkNode : divNode.getSubnodes("a")) {
						if (linkNode.getValue().trim().length() > 0) {
							sb.append(linkNode.getValue());
						}
						for (final XmlNode spanNode : linkNode.getSubnodes("span")) {
							if (spanNode.getValue().trim().length() > 0) {
								sb.append(spanNode.getValue());
							}
						}
					}
				}
				if (sb.toString().trim().length() > 0) {
					row.add(sb.toString());
				} else {
					row.add("");
				}
			}
			this.cells.add(row);
		}
	}

	public int getColumCount() {
		if (this.headerRow != null) {
			return this.headerRow.size();
		} else {
			return this.cells.get(0).size();
		}
	}

	public int getRowCount() {
		return this.cells.size();
	}

	public String getHeaderCell(final int columnIndex) {
		return this.headerRow.get(columnIndex);
	}

	public String getCell(final int rowIndex, final int columnIndex) {
		return this.cells.get(rowIndex).get(columnIndex);
	}
}
