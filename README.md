Insant, a set of annotations to provide debugging at runtime.

## # Building
    git clone https://github.com/mrwilson/insant
    cd insant
    mvn package

## # Uses
# To use debugging at runtime
    java -javaagent:/path/to/insant.jar <your stuff here>
# Or to modify classes in place
    java -jar /path/to/insant.jar /path/to/Foo.class

Note, the latter will replace the original classes
