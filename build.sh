echo "Compiling .java files..."
javac -d . src/*.java
if [ $? -eq 0 ]; then
    echo "Compilation Successful!"
fi
