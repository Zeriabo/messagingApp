FROM adoptopenjdk:11-jre-hotspot
WORKDIR /
COPY ["target/classes", "classes"]
COPY ["target/dependency", "dependency"]
COPY ["key.key", "key.key"]
COPY ["key.pub","key.pub"]
COPY ["semetrickey.key","semetrickey.key"]
COPY ["GFGsheetEncrypted.xlsx", "GFGsheetEncrypted.xlsx"]
EXPOSE 8080
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-cp", "classes:dependency/*", "fi.messaging.app.BackendApplication"]
