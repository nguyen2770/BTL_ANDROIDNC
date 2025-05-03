
#!/bin/bash
echo "Attempting to build project using Android Studio JDK..."
AS_JDK_PATH="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
if [ -d "$AS_JDK_PATH" ]; then
  export JAVA_HOME="$AS_JDK_PATH"
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "Using JDK from Android Studio: $JAVA_HOME"
else
  echo "Android Studio JDK not found at $AS_JDK_PATH"
  echo "Trying alternative path: /Applications/Android Studio.app/Contents/jre/Contents/Home"
  AS_JDK_PATH="/Applications/Android Studio.app/Contents/jre/Contents/Home"
  if [ -d "$AS_JDK_PATH" ]; then
    export JAVA_HOME="$AS_JDK_PATH"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Using alternative JDK path: $JAVA_HOME"
  else
    echo "Error: Could not find JDK from Android Studio"
    echo "Please install JDK 11 and set JAVA_HOME manually"
    exit 1
  fi
fi

# Verify Java version
java -version

# Run Gradle with disable jlink options
echo "Building project with the following command:"
echo "./gradlew clean assembleDebug -Dorg.gradle.internal.workers.disable.jlink=true -Pandroid.overrideVersionCheck=true"

./gradlew clean assembleDebug -Dorg.gradle.internal.workers.disable.jlink=true -Pandroid.overrideVersionCheck=true "$@"
