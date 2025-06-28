#!/bin/bash
# Generate keys and certificates used by MDS

if [ ! -e /data/keypair.pem ]; then
    echo -e "Generate keys and certificates used for MDS"

    # Tạo khóa riêng 4096 bit
    openssl genrsa -out /data/keypair.pem 4096

    # Xuất khóa công khai từ khóa riêng
    openssl rsa -in /data/keypair.pem -outform PEM -pubout -out /data/public.pem

    # Cấp quyền truy cập
    chmod 644 /data/keypair.pem /data/public.pem
fi
