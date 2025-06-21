ARG CONFLUENT_DOCKER_TAG
FROM confluentinc/cp-server-connect-base:$CONFLUENT_DOCKER_TAG

RUN   confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:$CONFLUENT_DOCKER_TAG \
   && confluent-hub install --no-prompt confluentinc/kafka-connect-avro-converter:$CONFLUENT_DOCKER_TAG