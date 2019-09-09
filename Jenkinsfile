pipeline {
  agent {
    node {
      label 'ubuntu-1604-android-slave'
    }

  }
  stages {
    stage('Setup VM') {
      steps {
        sh '''export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH"
echo $PATH

EMULATOR_API_LEVEL=24
ANDROID_ABI="default;armeabi-v7a"
sdkmanager "system-images;android-$EMULATOR_API_LEVEL;$ANDROID_ABI"
yes | sdkmanager --licenses
touch ~/.android/repositories.cfg
sdkmanager --update
echo no | avdmanager create avd --force -n test -k "system-images;android-$EMULATOR_API_LEVEL;$ANDROID_ABI"
emulator -avd test -no-audio -no-window -no-snapshot -gpu auto &
chmod u+rwx waitForEmulator.sh
./waitForEmulator.sh
	


'''
      }
    }
  }
}