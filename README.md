Insant, a set of annotations to provide debugging at runtime.

# How to use

## Building
    git clone https://github.com/mrwilson/insant
    cd insant
    mvn package

## Uses

Import the annotation files, and use them to annotate methods in your code.

* LocalVars - output local variables to standard out on method exit
* MethodAccess - output "Entering <methodname>" on entering method

##
    # To use debugging at runtime
    java -javaagent:/path/to/insant.jar <your stuff here>
    
    # Or to modify classes in place
    java -jar /path/to/insant.jar /path/to/Foo.class

Note, the latter will replace the original classes
