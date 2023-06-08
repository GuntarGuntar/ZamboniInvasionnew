
echo "compile"
javac -classpath . utilities/*.java backgrounds/*.java sprites/*.java universes/*.java  gui/*.java main/*.java -d ./bin
echo "run Main.java"
java -classpath ./bin Main
