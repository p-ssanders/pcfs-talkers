FROM openjdk:11-jdk
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENV SLACK_API_TOKEN=""
ENTRYPOINT ["java","-cp","app:app/lib/*","com.pivotal.slack.talkers.SlackTalkersApplication"]