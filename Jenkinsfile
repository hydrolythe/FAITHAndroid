pipeline {
  agent {
    node {
      label 'ubuntu-1604-android-slave'
    }

  }
  stages {
    stage('Accept Licenses') {
      steps {
        sh '''sudo chown -R jenkins $ANDROID_HOME
yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses'''
      }
    }
    stage('Compile') {
      parallel {
        stage('Compile') {
          steps {
            sh './gradlew compileDebugSources'
          }
        }
        stage('Linting') {
          steps {
            sh './gradlew ktlint'
          }
        }
      }
    }
    stage('Unit Test') {
      steps {
        sh './gradlew testDebugUnitTest testDebugUnitTest'
        junit '**/TEST-*.xml'
      }
    }
    stage('Build APK') {
      steps {
        sh './gradlew assembleDebug'
        archiveArtifacts '**/*.apk'
      }
    }
  }
}