pipeline {
  agent {
    node {
      label 'ubuntu-1604-android-slave'
    }

  }
  stages {
    stage('Setup VM') {
      steps {
        sh '''sudo apt-get update
sudo apt-get install -y default-jdk unzip
'''
      }
    }
    stage('Setup VM for Android') {
      steps {
        sh '''sudo apt-get install -y android-tools-adb android-tools-fastboot
export ANDROID_HOME=\'/opt/android-sdk\'
export ANDROID_SDK_ROOT=$ANDROID_HOME 
wget https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip
mkdir android-sdk
unzip sdk-tools-linux-4333796.zip -d android-sdk

sudo mv android-sdk/ $ANDROID_HOME
sudo chown -R jenkins:jenkins $ANDROID_HOME
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH"

dir=".android"
if [ -d "$dir" ]; then
 sudo chown jenkins:jenkins $dir
 sudo chmod ug+rw $dir
 else
  mkdir $dir
fi

touch ~/.android/repositories.cfg'''
      }
    }
  }
}