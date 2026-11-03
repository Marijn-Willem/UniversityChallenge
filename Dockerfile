FROM sbtscala/scala-sbt:amazoncorretto-al2023-21.0.13_1.x AS sbt

COPY / /tmp/

WORKDIR /tmp/
RUN sbt assembly

FROM sourcemation/jdk-25:jdk-25.0.4-7 AS jdk

RUN mkdir -p /usr/universitychallenge
COPY --from=sbt /tmp/target/scala-*/universitychallenge.jar /usr/universitychallenge/universitychallenge.jar

WORKDIR /usr/universitychallenge

CMD ["java", "-jar", "universitychallenge.jar"]

EXPOSE 8082
