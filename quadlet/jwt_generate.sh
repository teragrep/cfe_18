#!/bin/bash

 # Generates key.pub for authentication
 # Echoes the bearer token when ran and provides key.pub in target folder
 # Path to key.pub needs to be declared in application.properties
 # spring.security.oauth2.resourceserver.jwt.public-key-location=file:/absolute/path/in/os

rm -rf target
mkdir -p target
openssl genpkey -algorithm RSA -out target/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in target/private_key.pem -out target/public_key.pem
openssl rsa -in target/private_key.pem -pubout > key.pub

JWTHEADER=$(echo -n '{"alg":"RS256","typ":"JWT"}' | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
JWTPAYLOAD=$(echo -n "{\"sub\":\"subject\",\"iat\":$(date +%s)}" | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
echo -n ${JWTHEADER}.${JWTPAYLOAD} >> target/jwt.unsigned

openssl dgst -sha256 -sign target/private_key.pem -out target/jwt.signature target/jwt.unsigned

JWTSIGNATURE=$(openssl base64 -e -in target/jwt.signature | tr -d '=' | tr '/+' '_-' | tr -d '\n')

echo ${JWTHEADER}.${JWTPAYLOAD}.${JWTSIGNATURE}