#!/usr/bin/env sh
current_location=`pwd`
script_location=`realpath $0`
script_directory=`dirname ${script_location}`
cd ${script_directory}
source java_opts.sh

jar_location=`dirname ${script_directory}`
cd ${jar_location}
echo "Starting Bloodhound..."
echo "Memory settings ${JAVA_OPTS}"
java -Dspring.config.location=etc/application.properties ${JAVA_OPTS} -Dloader.path=modules -jar bloodhound.jar > /dev/null &
cd ${current_location}