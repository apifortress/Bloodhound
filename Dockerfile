FROM openjdk:8-alpine
RUN mkdir /opt/bloodhound
RUN mkdir /opt/bloodhound/modules
WORKDIR /opt/bloodhound
COPY target/bloodhound.jar /opt/bloodhound/
COPY bin /opt/bloodhound/bin
CMD ["/opt/bloodhound/bin/startdocker.sh"]
