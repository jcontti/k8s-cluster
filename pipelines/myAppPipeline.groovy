def call(body) {
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body()

    pipeline {
        agent any
        environment {
            GITHUB_PROJECT = 'apiSampleJava'
            GITHUB_URL = 'https://github.com/guipal'
        }
        stages {
            stage('Checkout project git') {
                steps {			
                    git credentialsId: 'GHsvcAccnt', url: "${GITHUB_URL}/${GITHUB_PROJECT}.git", branch: GIT_BRANCH_NAME.trim()
                }
            }
            stage('Execute test') {
                steps {
                    script{
                        try {
                            sh """
                                mkdir /app
                                echo "stage execute"
                            """
                            }
                        catch(Exception e) {
                            echo "WARNING: errors were detected in your output"
                            currentBuild.result = "UNSTABLE"
                        }
                    }
                }
            }
            stage('Next Stage') {
                steps {
                    script {
                        sh """
                            echo "stage 2 "
                        """
                    }
                }
            }
        }
    }
}