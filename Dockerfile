FROM maven:3.6.1-openjdk-14
WORKDIR /tests
COPY . .
CMD mvn clean test