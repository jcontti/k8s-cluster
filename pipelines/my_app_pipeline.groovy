pipeline {
  agent {
    label {
      label "docker-slave"
    }
  }
  environment {
    GITHUB_PROJECT = 'https://github.com/guipal/apiSampleJava'
  }
  stages {
    stage('Checkout project git') {
      steps {			
        git credentialsId: 'GHsvcAccnt', url: "${GITHUB_PROJECT}.git"
      }
    }
    stage('Build docker image') {
      steps {
        container('dind') {
          script {
              image = docker.build("my-docker-image")
          }
        }
      }
    }
/* 			stage('Push docker image') {
      steps {
        container('dind') {
          withAWS(region: config.region, credentials: config.awsCred) {
            sh ecrLogin()
            script {
              random_suffix = utils.getRandomString(5)
              image.push("${BUILD_NUMBER}-${random_suffix}")
              image.push('latest')
            }
          }
        }
      }
    } */
  }
}