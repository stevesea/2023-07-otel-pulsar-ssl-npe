#!/usr/bin/sh


if [ -f "/app/otel/opentelemetry-javaagent.jar" ]; then
  export JAVA_TOOL_OPTIONS="-javaagent:/app/otel/opentelemetry-javaagent.jar ${JAVA_TOOL_OPTIONS}"
else
  echo "Couldn't find OpenTelemetry agent Jar at /app/otel/opentelemetry-javaagent.jar. Disabling OTel."
  export OTEL_JAVAAGENT_ENABLED="false"
fi

# can we find an otel.properties file?
OTEL_JAVAAGENT_CONFIGURATION_FILE=/app/otel/otel.properties
if [ -f $OTEL_JAVAAGENT_CONFIGURATION_FILE ] ; then
  export OTEL_JAVAAGENT_CONFIGURATION_FILE
fi


# if OTEL_SERVICE_NAME not set, fall back to NEW_RELIC_APP_NAME. If that's not set, error.
export OTEL_SERVICE_NAME=${OTEL_SERVICE_NAME:-${NEW_RELIC_APP_NAME:?'unable to determine application name. Set OTEL_SERVICE_NAME'}}

# if POD_NAME not set, use HOSTNAME. If neither are set, error
POD_NAME=${POD_NAME:-${HOSTNAME:?'unable to determine hostname'}}

# if APP_VERSION not set, supply unknown but valid semver
APP_VERSION=${APP_VERSION:-'0.0.1+unknown'}

# if OTEL_RESOURCE_ATTRIBUTES not set, configure w/ service.id and version
export OTEL_RESOURCE_ATTRIBUTES=${OTEL_RESOURCE_ATTRIBUTES:-service.instance.id=${POD_NAME},service.version=${APP_VERSION}}}

# echo out config to console, including indented env vars that start with 'OTEL_'
echo "OpenTelemetry configuration: "
env | grep ^OTEL_ | sort | sed 's/^/  /'

if [ -f $OTEL_JAVAAGENT_CONFIGURATION_FILE ] ; then
  echo "\nOTEL_JAVAAGENT_CONFIGURATION_FILE: ${OTEL_JAVAAGENT_CONFIGURATION_FILE}"
  cat $OTEL_JAVAAGENT_CONFIGURATION_FILE | sed 's/^/  /'
fi

[ ! -f /app/jib-classpath-file ] && echo "/app/jib-classpath-file missing" && exit 1
[ ! -f /app/jib-main-class-file ] && echo "/app/jib-main-class-file missing" && exit 1

# NOTE: jib creates these files in the container. We're using this '@' syntax (Java 9+) to read the files.
#       If on Java 8, you'd use $( cat <filename> )
exec java -cp @/app/jib-classpath-file @/app/jib-main-class-file
