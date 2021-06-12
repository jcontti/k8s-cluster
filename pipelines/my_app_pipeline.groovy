def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

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
                  image = docker.build(dockerImage, "${pipelineParams.buildArgs} .")
              }
            }
          }
        }
      }
    }
}