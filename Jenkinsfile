pipeline {
  agent {
    node {
      label 'ubuntu-1604-android-slave'
    }

  }
  stages {
    stage('Accept Licenses') {
      steps {
        sh '''
        sudo apt-get install android-tools-adb android-tools-fastboot
        sudo chown -R jenkins $ANDROID_HOME
        export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH"
        yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses
        '''
      }
    }
    stage('Set SDK directory correct') {
      steps {
        sh '''dir="$ANDROID_HOME/platforms"
            if [ -d "$dir" ]; then
                sudo chown jenkins $dir
                sudo chmod ug+rw $dir
            else
                mkdir $dir
            fi

            dir="$ANDROID_HOME/platform-tools"
                if [ -d "$dir" ]; then
                    sudo chown jenkins $dir
                    sudo chmod ug+rw $dir
                else
                    mkdir $dir
                fi
            '''
      }
    }

    stage('Give permissions for Jenkins') {
      steps {
        sh '''dir="/home/jenkins/.android"
            if [ -d "$dir" ]; then
                sudo chown jenkins $dir
                sudo chmod ug+rw $dir
            else
                mkdir $dir
            fi
            '''
      }
    }
    stage('Set up emulator') {
        steps{
            sh '''
            ANDROID_SYSTEM='system-images;android-29;google_apis_playstore;x86_64'
            ANDROID_ABI=armeabi-v7a
            $ANDROID_HOME/tools/bin/sdkmanager $ANDROID_SYSTEM
            yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses
            echo no | $ANDROID_HOME/tools/bin/avdmanager create avd --force --name test -k "$ANDROID_SYSTEM"
            emulator -avd test -no-skin -no-audio -no-window &
            chmod u+rwx waitForEmulator.sh
            ./waitForEmulator.sh
            '''
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
    stage('Integration Tests') {
      steps {
        sh '$ANDROID_HOME/tools/bin/sdkmanager'
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
    stage('Integration test') {
      steps {
        sh '''
            adb shell settings put global window_animation_scale 0
            adb shell settings put global transition_animation_scale 0
            adb shell settings put global animator_duration_scale 0
            adb shell input keyevent 82 &
            adb shell setprop dalvik.vm.dexopt-flags v=n,o=v
            ./gradlew connectedCheck

        '''
      }
    }
  }
}