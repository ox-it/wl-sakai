#!/bin/sh
#
# Short build script.

local_version=2.5-ox3.1-rc1
local_sakai=2.5.x

MAVEN_OPTS="-Xms168m -Xmx512m -XX:PermSize=96m -XX:NewSize=64m -Dmaven.test.skip=true -Dsakai.withjsmath=true"

export MAVEN_OPTS

rm -rf build
mvn clean install sakai:deploy -Pfull,oxford -Dlocal.service=$local_version -Dlocal.sakai=$local_sakai -Dmaven.tomcat.home=$(pwd)/build/ || exit 1
(cd build && tar zcf ../sakai-${local_version}.tgz .)
