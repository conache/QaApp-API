# AWS login
$(aws ecr get-login --region us-east-2 --no-include-email)

# pull docker image for running the server application
docker pull 972312342870.dkr.ecr.us-east-2.amazonaws.com/qa-platform:latest

# kill all running containers
docker container kill $(docker ps -q)

# run the new container
docker run -d -p 8080:8085 972312342870.dkr.ecr.us-east-2.amazonaws.com/qa-platform:latest
