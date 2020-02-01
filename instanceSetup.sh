#!/bin/sh

apt-get update

apt-get upgrade -y

# install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

sudo usermod -aG docker ubuntu

# download and install Minikube
curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
  && chmod +x minikube

sudo mkdir -p /usr/local/bin/
sudo install minikube /usr/local/bin/

# install AWS CLI
sudo apt install awscli

# AWS login
$(aws ecr get-login --region us-east-2 --no-include-email)

# pull docker image for running the server application
docker pull 972312342870.dkr.ecr.us-east-2.amazonaws.com/qa-platform:latest
