pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        sh '''#!/bin/bash
docker build -t nlidb/test --file=Dockerfile.test ${WORKSPACE}
docker run nlidb/test
docker rm $(docker ps -aq --filter status=exited)
docker rmi $(docker images -aq --filter dangling=true)
           '''
      }
    }
    stage('Deploy') {
      when {
        branch 'master'
      }
      steps {
        sh '''#!/bin/bash
docker build -t nlidb/main --file=Dockerfile ${WORKSPACE}
docker save -o /tmp/nlidb-main.img nlidb/main
echo scping the image file...
scp -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem /tmp/nlidb-main.img centos@34.231.141.223:/home/centos/
echo stopping and removing the previously running nlidb container
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@34.231.141.223 'docker stop $(docker ps -aq --filter ancestor=nlidb/main)'
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@34.231.141.223 'docker rm $(docker ps -aq --filter ancestor=nlidb/main)'
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@34.231.141.223 'docker rmi $(docker images -aq --filter dangling=true)'
echo loading the new image and start the container
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@34.231.141.223 'docker load -i nlidb-main.img; docker run -d -p 443:443 nlidb/main;'
           '''
      }
    }
  }
}
