package org.rapidbeans.clubadmin.closingdays;

import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.util.XmlNode;

public class Table {

    private List<String> headerRow;

    private List<List<String>> cells;

    public static Table fromHtmlText(final String htmlText) {
        final String cvt = Umlaut.convertFromHtmlToAscii(htmlText).replace("&nbsp;", "\n");
        final XmlNode tableNode = XmlNode.getDocumentTopLevel(cvt);
        return new Table(tableNode);
    }

    public Table(final XmlNode tableNode) {
        this.headerRow = new ArrayList<String>();
        final XmlNode headerRowNode = tableNode.getFirstSubnode("thead/tr");
        for (final XmlNode headerCell : headerRowNode.getSubnodes("th")) {
            this.headerRow.add(headerCell.getValue());
        }
        this.cells = new ArrayList<List<String>>();
        final XmlNode bodyRowNode = tableNode.getFirstSubnode("tbody");
        for (final XmlNode rowNode : bodyRowNode.getSubnodes("tr")) {
            final List<String> row = new ArrayList<String>();
            for (final XmlNode cellNode : rowNode.getSubnodes("td")) {
                row.add(cellNode.getValue());
            }
            this.cells.add(row);
        }
    }

    public int getRowCount() {
        return this.headerRow.size();
    }

    public int getColumCount() {
        return this.cells.size();
    }

    public String getHeaderCell(final int columnIndex) {
        return this.headerRow.get(columnIndex);
    }

    public String getCell(final int rowIndex, final int columnIndex) {
        return this.cells.get(rowIndex).get(columnIndex);
    }
}
