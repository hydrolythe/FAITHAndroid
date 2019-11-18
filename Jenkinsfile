pipeline {
    agent {
        node {
            label 'ubuntu-1604-android-slave'
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
                sh './gradlew ktlint'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew --refresh-dependencies clean assemble'
            }
        }
        stage('Unit Test') {
            steps {
                sh './gradlew testDebugUnitTest testDebugUnitTest'
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