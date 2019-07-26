#!/usr/bin/env bash
if [ -z "${memorySettings}" ]; then
        echo "Memory settings not provided. Defaulting to -Xms256m -Xmx512m";
        export memorySettings="-Xms256m -Xmx512m";
else
        echo "Memory settings provided: ${memorySettings}";
fi;

JAVA_OPTS="-server ${memorySettings}"
echo "Options string: ${CATALINA_OPTS}"