echo "Compiling .java files..."
javac -d . src/*.java
if [ $? -eq 0 ]; then
    echo "Compilation Successful!"
    exit 0
else
    exit 1
fi
