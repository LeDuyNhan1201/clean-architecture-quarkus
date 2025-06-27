#!/bin/sh

set -euo pipefail

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <output-directory> <keystore-password>"
    exit 1
fi

PASSWORD="$2"
DIR="/$1"
P12_TRUSTSTORE="$DIR/truststore.p12"
CA_CERT="/ca/ca.crt"

keytool -importcert \
  -file "$CA_CERT" \
  -alias ca \
  -keystore "$P12_TRUSTSTORE" \
  -storetype PKCS12 \
  -storepass "$PASSWORD" \
  -noprompt

chmod 777 "$P12_TRUSTSTORE"

# Output summary
echo ""
echo "📦 Generated files in $DIR:"
echo " - 🔒 Truststore:  $P12_TRUSTSTORE"
