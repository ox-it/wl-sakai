#!/bin/sh
#
# Short build script.


mvn -Dmaven.test.skip=true -Dmaven.tomcat.home=${pwd}/build/ clean install sakai:deploy -Pfull,oxford
(cd build; tar zcf ../tomcat.tgz .)
