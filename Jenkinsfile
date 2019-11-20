pipeline {
    agent {
        node {
            label 'android-test-slave'
        }

    }
    stages {
        stage('Setup VM') {
            steps {
                sh '''
        export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH"
        echo $PATH
        '''
            }
        }
        stage('Linting') {
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
                    sh 'gcloud auth activate-service-account --key-file=/key/firekey.json'
                    sh 'gcloud config set project jenkins-server-250512'
                    sh 'gcloud firebase test android run ' +
                            '--type instrumentation ' +
                            '--app app/build/outputs/apk/debug/app-debug.apk ' +
                            '--test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk ' +
                            '--device model=Nexus9,version=21,locale=nl_BE,orientation=landscape'
                }
            }
        }
    }
    post {
        always {
            echo 'Getting the test results'
            junit '**/TEST-*.xml'
        }
    }
}
