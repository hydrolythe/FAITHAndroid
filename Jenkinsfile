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
  }
}