#!/bin/bash

 # Generates public_key.pem for authentication
 # Echoes the bearer token when ran and provides public_key.pem in target directory
 # Path to public_key.pem needs to be declared in application.properties
 # public.key.write=file:/absolute/path/in/os/public_key.pem
 # Similar thing is generated for read only token. This is separated with _read suffix.

rm -rf target
mkdir -p target

# Readwrite token

echo "Generating privileged token "\

openssl genpkey -algorithm RSA -out target/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in target/private_key.pem -out target/public_key.pem

JWTHEADER=$(echo -n '{"alg":"RS256","typ":"JWT"}' | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
JWTPAYLOAD=$(echo -n "{\"sub\":\"subject\",\"iat\":$(date +%s)}" | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
echo -n ${JWTHEADER}.${JWTPAYLOAD} >> target/jwt.unsigned

openssl dgst -sha256 -sign target/private_key.pem -out target/jwt.signature target/jwt.unsigned

JWTSIGNATURE=$(openssl base64 -e -in target/jwt.signature | tr -d '=' | tr '/+' '_-' | tr -d '\n')

echo "write token = " ${JWTHEADER}.${JWTPAYLOAD}.${JWTSIGNATURE}

echo \

# Read only token

echo "Generating read only token "\

openssl genpkey -algorithm RSA -out target/private_key_read.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in target/private_key_read.pem -out target/public_key_read.pem

JWTHEADERREAD=$(echo -n '{"alg":"RS256","typ":"JWT"}' | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
JWTPAYLOADREAD=$(echo -n "{\"sub\":\"readingSubject\",\"iat\":$(date +%s)}" | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
echo -n ${JWTHEADERREAD}.${JWTPAYLOADREAD} >> target/jwt_read.unsigned

openssl dgst -sha256 -sign target/private_key_read.pem -out target/jwt_read.signature target/jwt_read.unsigned

JWTSIGNATUREREAD=$(openssl base64 -e -in target/jwt_read.signature | tr -d '=' | tr '/+' '_-' | tr -d '\n')

echo "read token = " ${JWTHEADERREAD}.${JWTPAYLOADREAD}.${JWTSIGNATUREREAD}

echo \
