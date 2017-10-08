pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        sh '''#!/bin/bash
docker build -t nlidb/test --file=Dockerfile.test ${WORKSPACE}
docker run nlidb/test'''
      }
    }
  }
}