echo "Compiling .java files..."
javac -d . src/*.java
if [ $? -eq 0 ]; then
    echo "Compilation Successful!"
    #cp -r img com/adarwin/edrum
    exit 0
else
    exit 1
fi
