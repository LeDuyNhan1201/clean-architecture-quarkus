FROM alpine:latest

RUN apk add --no-cache \
    bash \
    openssl \
    openjdk11

# Optional: make keytool globally available
ENV PATH="/usr/lib/jvm/java-11-openjdk/bin:$PATH"

WORKDIR /app

ENTRYPOINT ["/app/generate_cert.sh"]
