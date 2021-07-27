#!/bin/bash

SSH_HOST="denklux.local"
DIR="oh2mqtt"

echo "Using ssh host: ${SSH_HOST} to  deploy..."
echo ""
read -n1 -r -p "Press any key to continue!" key
echo ""

echo "Create '${DIR}/'"
echo ""
ssh ${SSH_HOST} "mkdir -p ${DIR}"

echo "Copy files:"
echo ""
scp -r docker-compose.yml docker/ ${SSH_HOST}:${DIR}
echo""

echo "Copy scripts:"
echo ""
scp -r sshk ${SSH_HOST}:${DIR}
ssh ${SSH_HOST} "chmod +x ${DIR}/sshk"
echo""

echo "Done"
