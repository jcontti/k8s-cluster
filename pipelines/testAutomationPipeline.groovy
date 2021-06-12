def call(body) {
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body()

    pipeline {
        agent {
            label {
                label 'test-automation-slave'
            }
        }
        environment {
            BITBUCKET_PROJECT = 'agrofyautomation'
        }
        stages {
            stage('Checkout project git') {
                steps {			
                    git credentialsId: 'BBsvcAccnt', url: "${BITBUCKET_URL}/${BITBUCKET_PROJECT}.git", branch: GIT_BRANCH_NAME.trim()
                }
            }
            stage('Execute tests') {
                steps {
                    container('test-automation') {
                        script{
                            try {
                                sh """
                                    mkdir /app
                                    cp pom.xml /app
                                    cp -r src /app
                                    cd /app
                                    cp -f src/main/java/config/${ENV_CONFIG.trim()} src/main/java/config/config.properties
                                    mvn -Dmaven.test.failure.ignore=true -Dtest=${SUITES.replaceAll(' ','')} test -l src/main/resources/outputtest.log
                                    ls -ltr src/main/resources/reports
                                    echo "*** READING LOG FROM MAVEN TEST EXECUTION ***"
                                    cat src/main/resources/outputtest.log
                                    echo "*** COPY SUITES REPORTS ***"
                                    mkdir ${WORKSPACE}/output-reports
                                    ls -ltr ${WORKSPACE}/output-reports
                                    echo ${SUITES}
                                    for name in ${SUITES.replaceAll(",", " ")}; do cp src/main/resources/reports/\$name.html ${WORKSPACE}/output-reports/\$name.html; done
                                    echo "*** SEARCHING ERRORS FROM MAVEN LOG TEST ***"
                                    if grep -q "There are test failures" src/main/resources/outputtest.log; then exit 1; else exit 0; fi
                                """
                                }
                            catch(Exception e) {
                                echo "WARNING: errors were detected in your mvn test output"
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