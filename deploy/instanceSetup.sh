#!/bin/sh

apt-get update

apt-get upgrade -y

# install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

sudo usermod -aG docker ubuntu

# install AWS CLI
sudo apt install awscli

# AWS login
$(aws ecr get-login --region eu-central-1 --no-include-email)

# pull docker image for running the server application
docker pull 972312342870.dkr.ecr.eu-central-1.amazonaws.com/qa-platform:latest
