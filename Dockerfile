FROM adoptopenjdk:11-jre-hotspot
WORKDIR /
COPY ["target/classes", "classes"]
COPY ["target/dependency", "dependency"]
EXPOSE 8080
EXPOSE 5005
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-cp", "classes:dependency/*", "fi.invian.codingassignment.app.BackendApplication"]
