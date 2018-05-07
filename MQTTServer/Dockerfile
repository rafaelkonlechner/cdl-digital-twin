FROM gradle:4.7.0-jdk8-alpine

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY . .

USER root

RUN ./gradlew build -x test

EXPOSE 8080

CMD [ "./gradlew", "run", "-PappArgs=['--docker']"]
