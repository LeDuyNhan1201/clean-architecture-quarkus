#!/bin/bash

if [ "$EUID" -ne 0 ]; then
  echo "Please run as root"
  exit 1
fi

docker compose -f ../sasl_ssl_oauth.yml down -v

echo "Removing certs..."

rm -f ../kafka/broker1/certs/*
rm -f ../kafka/broker2/certs/*
rm -f ../kafka/broker3/certs/*
rm -f ../kafka/keypair/*
rm -f ../kafka/schema-registry1/certs/*
rm -f ../keycloak/certs/*
rm -rf ../../src/adapter/src/main/resources/certs/*

echo "Done."