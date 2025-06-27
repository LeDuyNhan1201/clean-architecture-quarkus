#!/bin/bash
set -euo pipefail

# === 🔧 Load từ biến môi trường ===
MAIN_DOMAIN=${MAIN_DOMAIN:?❌ MAIN_DOMAIN is not set}
CERT_PASSWORD=${CERT_PASSWORD:?❌ CERT_PASSWORD is not set}
ALIAS=${ALIAS:-${MAIN_DOMAIN//./-}}  # fallback alias = domain with dots replaced
CERT_DIR="/$MAIN_DOMAIN"
CA_CERT="/ca/ca.crt"
CA_KEY="/ca/ca.key"

mkdir -p "$CERT_DIR"

# === 🔐 Generate private key (Không có passphrase) ===
echo "🔐 Generating private key (no password)..."
openssl genpkey -algorithm RSA \
  -out "$CERT_DIR/key.pem" \
  -pkeyopt rsa_keygen_bits:4096

# === 🧾 OpenSSL config with SANs ===
echo "📄 Creating OpenSSL config with SANs..."
cat > "$CERT_DIR/$ALIAS.openssl.cnf" <<EOF
[ req ]
default_bits       = 4096
prompt             = no
default_md         = sha256
distinguished_name = dn
req_extensions     = req_ext

[ dn ]
C = VN
ST = HCM
L = HCM
O = MyOrg
OU = Dev
CN = $MAIN_DOMAIN

[ req_ext ]
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = $MAIN_DOMAIN
DNS.2 = localhost
DNS.3 = 127.0.0.1
DNS.4 = ${MAIN_DOMAIN}.local
DNS.5 = ${MAIN_DOMAIN}.docker
DNS.6 = ${MAIN_DOMAIN}.k8s
EOF

# === 📑 Generate CSR ===
echo "📑 Generating certificate signing request (CSR)..."
openssl req -new \
  -key "$CERT_DIR/key.pem" \
  -out "$CERT_DIR/csr.pem" \
  -config "$CERT_DIR/$ALIAS.openssl.cnf"

# === ✅ Sign certificate with CA ===
echo "✅ Signing certificate with CA..."
openssl x509 -req \
  -in "$CERT_DIR/csr.pem" \
  -CA "$CA_CERT" -CAkey "$CA_KEY" -CAcreateserial \
  -out "$CERT_DIR/cert.pem" -days 365 -sha256 \
  -extfile "$CERT_DIR/$ALIAS.openssl.cnf" -extensions req_ext

# === 📦 Create PKCS#12 keystore (no key pass) ===
echo "📦 Creating PKCS#12 keystore (with private key and cert)..."
openssl pkcs12 -export \
  -inkey "$CERT_DIR/key.pem" \
  -in "$CERT_DIR/cert.pem" \
  -certfile "$CA_CERT" \
  -passout pass:"$CERT_PASSWORD" \
  -out "$CERT_DIR/keystore.p12" \
  -name "$ALIAS"

# === 🔒 Create PKCS#12 truststore ===
echo "🔒 Creating PKCS#12 truststore with CA..."
keytool -importcert \
  -noprompt \
  -trustcacerts \
  -alias myCA \
  -file "$CA_CERT" \
  -keystore "$CERT_DIR/truststore.p12" \
  -storetype PKCS12 \
  -storepass "$CERT_PASSWORD"

# === 🔐 File permissions ===
chmod 644 "$CERT_DIR"/*.pem "$CERT_DIR"/*.p12

echo "✅ [$MAIN_DOMAIN] Keystore and truststore generation complete!"
