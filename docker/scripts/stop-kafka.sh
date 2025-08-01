#!/bin/bash

if [ "$EUID" -ne 0 ]; then
  echo "Please run as root"
  exit 1
fi

docker compose -f ../kafka-cluster1.yml down -v

echo "Removing certs..."

rm -f ../kafka/broker1/certs/*
rm -f ../kafka/keypair/*
rm -f ../kafka/schema-registry1/certs/*

echo "Done."
