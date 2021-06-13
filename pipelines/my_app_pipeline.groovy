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
              image = docker.build("jcontti/challenge-repo:${env.BUILD_ID}")
          }
        }
      }
    }
 		stage('Push docker image') {
      steps {
        container('dind') {
          script {
            docker.withRegistry('', 'DHsvcAccnt') {
              image.push("${env.BUILD_NUMBER}") 
              image.push('latest')
            }
          }
        }
      }
    }
    stage('Deploy EKS') {   
      steps {
        script {
          kubectl -n jenkins run --image="jcontti/challenge-repo:${env.BUILD_ID}" --replicas=2 my-pod
          kubectl -n jenkins expose deployment/my-pod --port=8081 --name=my-pod-svc --type=NodePort
          kubectl get pods -n jenkins
          kubectl get svc -n jenkins
        }
      }
    }
  }
}