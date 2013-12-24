function outline(s) {
	print s carriageReturn > outfile
}

function convertMonth(smonth) {
	month = "xx"
	if (smonth ~ /^Jan.*/) {
		month = "01"
	} else if (smonth ~ /^Feb.*/) {
		month = "02"
	} else if (smonth ~ /^Mar.*/) {
		month = "03"
	} else if (smonth ~ /^Apr.*/) {
		month = "04"
	} else if (smonth ~ /^May.*/) {
		month = "05"
	} else if (smonth ~ /^Jun.*/) {
		month = "06"
	} else if (smonth ~ /^Jul.*/) {
		month = "07"
	} else if (smonth ~ /^Aug.*/) {
		month = "08"
	} else if (smonth ~ /^Sep.*/) {
		month = "09"
	} else if (smonth ~ /^Oct.*/) {
		month = "10"
	} else if (smonth ~ /^Nov.*/) {
		month = "11"
	} else if (smonth ~ /^Dec.*/) {
		month = "12"
	}
	return month
}

function generateHeader(cyear, filename, author, crdate) {
	outline("<!--")
	outline(" * Rapid Beans Framework: " filename)
	outline(" *")
	outline(" * Copyright (C) " cyear " " author)
	outline(" *")
	outline(" * Creation Date: " crdate)
	outline(" *")
	outline(" * This program is free software; you can redistribute it and/or modify it under the terms of the")
	outline(" * GNU Lesser General Public License as published by the Free Software Foundation;")
	outline(" * either version 3 of the License, or (at your option) any later version.")
	outline(" * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;")
	outline(" * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.")
	outline(" * See the GNU Lesser General Public License for more details.")
	outline(" * You should have received a copies of the GNU Lesser General Public License and the")
	outline(" * GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.")
	outline("-->")
	outline("")
}

BEGIN {
#	print "scanning header of file \"" infile "\"..."
#	print "@@@ filename = \"" filename "\""
	state = -1;
	carriageReturn = ""
	crdate = "xx/xx/xxxx"
}

{
	// to satisfy windows
	if (sub("\r", "") > 0) {
		carriageReturn = "\r"
	}
	if (state == -1) {
		if (match($0, "<\\?xml") > 0) {
			outline($0)
			state = 0
		}
	} else if (state == 0) {
		if ($0 == "<!--") {
			state = 1;
		} else {
#			print "WARNING file \"" infile ":"
#			print "  First line does not start with \"^<!--$\""
#			print "  Generating new header..."
#			print "  Please define the creation date"
			generateHeader(cyear, filename, author, "??/??/????")
			outline($0)
			state = 3
		}
	} else if (state == 1) {
		if (match($0, " * Partially generated code file") > 0) {
			outline("<!--")
			outline($0)
			state = 3;
		} else if (match($0, "[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]") > 0) {
			imatch = match($0, "[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]")
			day = substr($0, imatch + 0, 2)
			month = substr($0, imatch + 3, 2)
			year = substr($0, imatch + 6, 4)
			crdate = month "/" day "/" year
#			print "crdate de !!!!!!!!!!!!!!!!!!!!!!!!!!! \"" crdate "\""
		} else if (match($0, "[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]") > 0) {
			imatch = match($0, "[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]")
			month = substr($0, imatch + 0, 2)
			day = substr($0, imatch + 3, 2)
			year = substr($0, imatch + 6, 4)
			crdate = month "/" day "/" year
#			print "crdate en short !!!!!!!!!!!!!!!!!!!!!!!!!!! \"" crdate "\""
		} else if (match($0, "[A-Za-z]* [0-9][0-9], [0-9][0-9][0-9][0-9]") > 0) {
			imatch = match($0, "[A-Za-z]* [0-9][0-9], [0-9][0-9][0-9][0-9]")
			imatch1 = match($0, "[0-9][0-9], [0-9][0-9][0-9][0-9]")
			smonth = substr($0, imatch, imatch1 - imatch - 1)
			month = convertMonth(smonth)
			if (month == "xx") {
				print "WARNING file \"" infile ":"
				print "  Month \"" smonth "\" of creation date not recognized"
				print "  Please define the creation date's month"
			}
			day = substr($0, imatch1 + 0, 2)
			year = substr($0, imatch1 + 4, 4)
			crdate = month "/" day "/" year
#			print "crdate en medium !!!!!!!!!!!!!!!!!!!!!!!!!!! \"" crdate "\""
		} else if (match($0, "[A-Za-z]* [0-9], [0-9][0-9][0-9][0-9]") > 0) {
			imatch = match($0, "[A-Za-z]* [0-9], [0-9][0-9][0-9][0-9]")
			imatch1 = match($0, "[0-9], [0-9][0-9][0-9][0-9]")
			smonth = substr($0, imatch, imatch1 - imatch - 1)
			month = convertMonth(smonth)
			if (month == "xx") {
				print "WARNING file \"" infile ":"
				print "  Month \"" smonth "\" of creation date not recognized"
				print "  Please define the creation date's month"
			}
			day = substr($0, imatch1 + 0, 1)
			year = substr($0, imatch1 + 3, 4)
			crdate = month "/" day "/" year
#			print "crdate en medium !!!!!!!!!!!!!!!!!!!!!!!!!!! \"" crdate "\""
		} else if (match($0, "[ \\t]*-->") > 0) {
			if (crdate == "xx/xx/xxxx") {
				print "WARNING file \"" infile ":"
				print "  Creation date not recognized"
				print "  Please define the creation date"
			}
			state = 2
		}
	} else if (state == 2) {
		if (match($0, "^[ \t\n]*$") > 0) {
			# do nothing state stays 2
		} else if (match($0, "^[ \t\n]*<") > 0) {
			generateHeader(cyear, filename, author, crdate)
			outline($0)
			state = 3
		} else {
		}
	} else if (state == 3) {
		outline($0)
	} else {
		print "ERROR maintaining header of file \"" infile ":"
		print "  Invalid parser state: " state
	}
}

END {
	if (state != 3) {
		print "ERROR file \"" infile ":"
		print "  invalid parser end state = " state
		exit 1		
	}		
}
