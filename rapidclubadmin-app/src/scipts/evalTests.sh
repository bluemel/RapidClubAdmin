grep '<testcase' rapidclubadmin-appTests-eclipse.xml | wc -l
ls -w1 target/surefire-reports/TEST* | wc -l
sh-4.3$ cat target/surefire-reports/TEST* > rapidclubadmin-appTests-maven.xml
grep '<testcase' rapidclubadmin-appTests-maven.xml | sed 's/^.*classname="\([^"]*\)".*name="\([^"]*\)".*$/\1::\2/' | sort > rapidclubadmin-appTests-maven.txt
grep '<testcase' rapidclubadmin-appTests-eclipse.xml | sed 's/^.*name="\([^"]*\)".*classname="\([^"]*\)".*$/\2::\1/' | sort > rapidclubadmin-appTests-eclipse.txt
