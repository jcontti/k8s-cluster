def call(body) {
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body()

    pipeline {
        agent {
            label {
                label 'slave'
            }
        }
        environment {
            GITHUB_PROJECT = 'automation'
        }
        stages {
            stage('Checkout project git') {
                steps {			
                    git credentialsId: 'GHsvcAccnt', url: "${GITHUB_URL}/${GITHUB_PROJECT}.git", branch: GIT_BRANCH_NAME.trim()
                }
            }
            stage('Execute tests') {
                steps {
                    container('my-container') {
                        script{
                            try {
                                sh """
                                    mkdir /app
                                """
                                }
                            catch(Exception e) {
                                echo "WARNING: errors were detected in your output"
                                currentBuild.result = "UNSTABLE"
                            }
                        }
                    }
                }
            }
            stage('Publish reports') {
                steps {
                    script {
                        def htmlFiles
                        dir ('output-reports') {
                            htmlFiles = findFiles glob: '*.html'
                        }
                        publishHTML (target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
                          reportDir: 'output-reports', reportFiles: htmlFiles.join(','), reportName: 'Test Reports'])
                    }
                }
            }
        }
    }
}