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
    
    if (env.BRANCH_NAME == 'master') {
      stage('Deploy') {
        steps {
          sh '''echo hello I am master'''
        }
      }
    }
    
  }
}
