package org.rapidbeans.clubadmin.closingdays;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.clubadmin.domain.ClosingPeriod;

public class SchulferienReader {

    public List<ClosingPeriod> readSchulferien(final InputStream in) throws IOException {
        final List<ClosingPeriod> result = new ArrayList<ClosingPeriod>();
        InputStreamReader reader = null;
        ByteArrayOutputStream bos = null;
        try {
            reader = new InputStreamReader(in);
            bos = new ByteArrayOutputStream();
            int c;
            while ((c = reader.read()) != -1) {
                bos.write(c);
            }
            final String tableText = cutOutTable(bos.toString(), 0);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println(tableText);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            final Table table = Table.fromHtmlText(tableText);
            for (int i = 0; i < table.getColumCount(); i++) {
                System.out.println("header[" + i + "]: \"" + table.getHeaderCell(i));
            }
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; i < table.getColumCount(); j++) {
                    System.out.println("cell[" + i + ", " + j + "]: \"" + table.getCell(i, j));
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    private String cutOutTable(final String htmlText, int tableIndex) {
        int i = 0;
        int posStart = 0;
        while (posStart != -1 && i <= tableIndex) {
            posStart = htmlText.indexOf("<table>", posStart);
            i++;
        }
        if (posStart == 0 || posStart == -1) {
            throw new RuntimeException("Start of table[" + Integer.toString(tableIndex) + "] not found.");
        }
        int posEnd = posStart;
        i = 0;
        while (posEnd != -1 && i <= tableIndex) {
            posEnd = htmlText.indexOf("</table>");
            i++;
        }
        if (posEnd == 0 || posEnd == -1) {
            throw new RuntimeException("End of table[" + Integer.toString(tableIndex) + "] not found.");
        }
        posEnd += "</table>".length();
        return htmlText.substring(posStart, posEnd);
    }
}
