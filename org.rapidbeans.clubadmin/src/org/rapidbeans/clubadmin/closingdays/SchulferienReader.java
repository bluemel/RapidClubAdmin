package org.rapidbeans.clubadmin.closingdays;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.ant.StringHelper;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.core.basic.PropertyDate;

public class SchulferienReader {

    public List<ClosingPeriod> readSchulferienAndFeiertage(final String websiteUrl, final String country,
            final String year) {
        final List<ClosingPeriod> result = new ArrayList<ClosingPeriod>();
        if (!websiteUrl.equals("www.schulferien.org")) {
            throw new IllegalArgumentException("Unsupported web site URL: " + websiteUrl);
        }
        if (!country.equals("bayern")) {
            throw new IllegalArgumentException("Unsupported country: " + country);
        }
        try {
            // final URL urlSchulferien = new URL("http://" + websiteUrl + "/" +
            // StringHelper.upperFirstCharacter(country)
            // + "/" + country + ".html");
            // System.out.println("Opening connection to \"" +
            // urlSchulferien.toString() + "\"...");
            // final URLConnection conSchulferien =
            // urlSchulferien.openConnection();
            // System.out.println("Reading data from \"" +
            // urlSchulferien.toString() + "\"...");
            // result.addAll(readSchulferien(conSchulferien.getInputStream()));
            final URL urlFeiertage = new URL("http://" + websiteUrl + "/Feiertage/" + year + "/feiertage_" + year
                    + ".html");
            System.out.println("Opening connection to \"" + urlFeiertage.toString() + "\"...");
            final URLConnection conFeiertage = urlFeiertage.openConnection();
            System.out.println("Reading data from \"" + urlFeiertage.toString() + "\"...");
            result.addAll(readFeiertage(conFeiertage.getInputStream(), country));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public List<ClosingPeriod> readFeiertage(final InputStream in, final String country) throws IOException {
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
            // System.out.println(bos.toString());
            final String tableText = cutOutTable(bos.toString(), 3);
            // System.out.println(tableText);
            final Table table = Table.fromHtmlText(tableText);
            evalTableFeiertage(table, result, country);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    public List<ClosingPeriod> readSchulferien(final InputStream in, final String year) throws IOException {
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
            final Table table = Table.fromHtmlText(tableText);
            evalTableSchulferien(table, result, year);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    private void evalTableSchulferien(final Table table, final List<ClosingPeriod> result, final String yearFilter) {
        int rowCount = table.getRowCount();
        int columnCount = table.getColumCount();
        for (int i = 0; i < rowCount; i++) {
            for (int j = 1; j < columnCount; j++) {
                final String name = table.getHeaderCell(j).trim();
                final String yearFrom = table.getCell(i, 0).trim();
                String yearTo = yearFrom;
                if (name.equals("Weihnachtsferien")) {
                    yearTo = Integer.toString(Integer.parseInt(yearFrom) + 1);
                }
                final List<String> dates = StringHelper.split(table.getCell(i, j).trim(), "-");
                if (dates.size() == 2) {
                    final String from = yearFrom + dates.get(0).trim().substring(3, 5)
                            + dates.get(0).trim().substring(0, 2);
                    final String to = yearTo + dates.get(1).trim().substring(3, 5)
                            + dates.get(1).trim().substring(0, 2);
                    final ClosingPeriod period = new ClosingPeriod(new String[] { from, name });
                    period.setTo(PropertyDate.parse(to));
                    if (yearFrom.equals(yearFilter) || yearTo.equals(yearFilter)) {
                        result.add(period);
                    }
                }
            }
        }
    }

    private void evalTableFeiertage(final Table table, final List<ClosingPeriod> result, final String country) {
        int rowCount = table.getRowCount();
        String countryShort = null;
        if (country.equals("bayern")) {
            countryShort = "BY";
        } else {
            throw new IllegalArgumentException("Unsupported country \"" + country + "\"");
        }
        final String countryShortPlus = countryShort + ", ";
        for (int i = 0; i < rowCount; i++) {
            final String name = table.getCell(i, 0).trim();
            final String date = table.getCell(i, 1).substring(6, 10) + table.getCell(i, 1).substring(3, 5)
                    + table.getCell(i, 1).substring(0, 2);
            final String from = date;
            final String to = date;
            final String validity = table.getCell(i, 2).trim();
            final ClosingPeriod period = new ClosingPeriod(new String[] { from, name });
            period.setTo(PropertyDate.parse(to));
            if (validity.equals("alle Bundeslaender") || validity.endsWith(countryShort)
                    || validity.contains(countryShortPlus)) {
                result.add(period);
            }
        }
    }

    private String cutOutTable(final String htmlText, int tableIndex) {
        int i = 0;
        int posStart = 0;
        int posEnd = posStart;
        while (posStart != -1 && i <= tableIndex) {
            posStart = htmlText.indexOf("<table", posStart);
            posEnd = htmlText.indexOf("</table>", posStart + 7) + "</table>".length();
            i++;
            if (posStart != -1 && i <= tableIndex) {
                posStart = posEnd;
            }
        }
        if (posStart == 0 || posStart == -1) {
            throw new RuntimeException("Start of table[" + Integer.toString(tableIndex) + "] not found.");
        }
        if (posEnd == 0 || posEnd == -1) {
            throw new RuntimeException("End of table[" + Integer.toString(tableIndex) + "] not found.");
        }
        return htmlText.substring(posStart, posEnd);
    }
}
