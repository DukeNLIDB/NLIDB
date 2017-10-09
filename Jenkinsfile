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
scp -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem /tmp/nlidb-main.img centos@nl2sql.com:/home/centos/
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@nl2sql.com "docker rmi $(docker images -aq --filter dangling=true)"
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@nl2sql.com "docker stop $(docker ps -aq --filter ancestor=nlidb/main)"
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@nl2sql.com "docker rm $(docker ps -aq --filter ancestor=nlidb/main)"
ssh -o "StrictHostKeyChecking no" -i $HOME/.ssh/aws-keping94-us-east1.pem centos@nl2sql.com "docker load -i nlidb-main.img; docker run -d -p 80:80 nlidb/main;"
           '''
      }
    }
  }
}
