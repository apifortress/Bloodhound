FROM openjdk:8-alpine
RUN mkdir /opt/afthem
RUN mkdir /opt/afthem/modules
WORKDIR /opt/afthem
COPY target/afthem.jar /opt/afthem/
COPY bin /opt/afthem/bin
CMD ["/opt/afthem/bin/startdocker.sh"]
