pipeline {
    agent {
        node {
            label 'android-test-slave2'
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
        /** stage('Linting') {steps {sh './gradlew ktlint'}}**/
        stage('Build') {
            steps {
                sh './gradlew :app:assembleDebug'
                sh './gradlew :app:assembleDebugAndroidTest'
            }
        }
        /**stage('Unit Test') {steps {sh './gradlew testDebugUnitTest testDebugUnitTest'}}*/
        stage('Integration tests') {
            steps {
                sh 'gcloud auth activate-service-account --key-file=/keys/fireKey.json'
                sh 'gcloud firebase test android run --app app/build/outputs/apk/debug/app-debug.apk' +
                        '--test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
            }
        }
    }
    /**post {
        always {
            echo 'Getting the test results'
            junit '/TEST-*.xml'
        }
    }*/
}
