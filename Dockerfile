FROM alpine:3.10

LABEL maintainer="herdin86@gmail.com"

RUN apk add git
RUN apk add openjdk11

RUN mkdir /msa

WORKDIR /msa
RUN git clone https://github.com/herdin/GradleSpringBootMybatis.git

WORKDIR /msa/GradleSpringBootMybatis
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN mv ./build/libs/GradleSpringBootMybatis-1.0-SNAPSHOT.jar /msa

CMD ["java", "-jar", "/msa/GradleSpringBootMybatis-1.0-SNAPSHOT.jar"]
