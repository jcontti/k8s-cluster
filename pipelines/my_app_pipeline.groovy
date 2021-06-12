/* pipeline {
  agent any
  stages {
    stage('Stage 1') {
      steps {
        echo 'Hello world!'
      }
    }
  }
} */

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
        git credentialsId: 'GHsvcAccnt', url: "${GITHUB_PROJECT}.git", branch: GIT_BRANCH_NAME.trim()
      }
    }
    /* stage('Build docker image') {
      steps {
        container('dind') {
          script {
              image = docker.build(dockerImage, "${pipelineParams.buildArgs} .")
          }
        }
      }
    } */
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