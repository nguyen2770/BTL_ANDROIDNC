export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH=$JAVA_HOME/bin:$PATH
echo "Đã chuyển sang JDK 11, kiểm tra phiên bản:"
java -version
echo "Bây giờ bạn có thể chạy: ./gradlew clean build"
