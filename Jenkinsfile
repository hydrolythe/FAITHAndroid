pipeline {
  agent {
    node {
      label 'ubuntu-1604-android-slave'
    }

  }
  stages {
    stage('Accept Licenses') {
      parallel {
        stage('Accept Licenses') {
          steps {
            sh '''sudo chown -R jenkins $ANDROID_HOME
yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses'''
          }
        }
        stage('Give permissions for Jenkins') {
          steps {
            sh '''sudo chmod ug+rw /home/jenkins/.android
sudo chown jenkins /home/jenkins/.android'''
          }
        }
      }
    }
    stage('Compile') {
      steps {
        sh './gradlew compileDebugSources'
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
    stage('Linting') {
      steps {
        sh './gradlew ktlint'
      }
    }
  }
}