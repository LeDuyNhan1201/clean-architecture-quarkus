ARG POSTGRES_DOCKER_TAG
FROM postgres:${POSTGRES_DOCKER_TAG}

ENV WAL2JSON_TAG=wal2json_2_6
ENV DECODERBUFS_TAG=v3.1.1.Final

# Update và cài build dependencies + libprotobuf-c1 (runtime!)
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates \
       gcc clang llvm git make libssl-dev \
       libprotobuf-c-dev libprotobuf-c1 \
       pkg-config protobuf-c-compiler \
       postgresql-server-dev-$PG_MAJOR \
    && update-ca-certificates \

    # build wal2json
    && git clone https://github.com/eulerto/wal2json -b master --single-branch \
    && cd /wal2json \
    && git checkout tags/$WAL2JSON_TAG -b $WAL2JSON_TAG \
    && make && make install \
    && cd .. && rm -rf /wal2json \

    # build decoderbufs
    && git clone https://github.com/debezium/postgres-decoderbufs --single-branch \
    && cd /postgres-decoderbufs \
    && git checkout tags/$DECODERBUFS_TAG -b $DECODERBUFS_TAG \
    && make && make install \
    && cd .. && rm -rf /postgres-decoderbufs \

    # cleanup: chỉ remove dev tools, GIỮ libprotobuf-c1
    && apt-get remove --purge -y \
       gcc clang llvm git make libssl-dev \
       libprotobuf-c-dev pkg-config protobuf-c-compiler postgresql-server-dev-$PG_MAJOR \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
