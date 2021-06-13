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
              image = docker.build("my-image:${env.BUILD_ID}")
          }
        }
      }
    }
 		stage('Push docker image') {
      steps {
        container('dind') {
          script {
            docker.withRegistry('https://hub.docker.com') {
              image.push("${env.BUILD_NUMBER}") 
              image.push('latest')
            }
          }
        }
      }
    }
  }
}