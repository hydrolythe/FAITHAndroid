pipeline {
    agent {
        node {
            label 'android-test-slave'
        }

    }
    stages {
        stage('Setup VM') {
            steps {
                withCredentials([file(credentialsId: 'firekey', variable: 'SERVICEACCOUNT'),file(credentialsId: 'googlejson', variable: 'googlejson')]) {
                    sh '''
                        export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH"
                        echo $PATH
                        cp $googlejson app/google-services.json
                        gcloud auth activate-service-account --key-file=$SERVICEACCOUNT
                        gcloud config set project faith-dev-38aa1
                    '''
                }
            }
        }
        /**stage('Linting') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew ktlint'
                }
            }
        }

        stage('Build') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew :app:assembleDebug'
                    sh './gradlew :app:assembleDebugAndroidTest'
                }
            }
        }
        stage('Unit Test') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew testDebugUnitTest testDebugUnitTest'
                }
            }
        }

        stage('Integration tests') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'gcloud firebase test android run ' +
                            '--type instrumentation ' +
                            '--app app/build/outputs/apk/debug/app-debug.apk ' +
                            '--test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk ' +
                            '--device model=Nexus9,version=21,locale=nl_BE,orientation=landscape'
                }
            }
        }
         */
    }
    post {
        always {
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                    subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}"

        }
    }
}
