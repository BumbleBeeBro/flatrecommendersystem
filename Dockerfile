FROM anapsix/alpine-java:8_jdk-dcevm
RUN apk add --no-cache maven
RUN mkdir -p /app
WORKDIR /app
#VOLUME /var/local/
VOLUME /root/.m2
VOLUME /app
CMD sh -c "mvn spring-boot:run"