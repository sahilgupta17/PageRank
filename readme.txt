Dependencies:
javac version 12.0.2 required
java version 12.0.2 required

Building the code
In order to build the program, run the following command from the root folder:
javac ./src/Main.java

In order to run the program, execute the following command from the root folder:
java src/Main 0.15 0.0001 . 0.15 is the value of lambda and 0.0001 is the value of tau. You can change these values but lambda should be between 0 and 1. Tau is used for checking for convergence so ideally its value should not be changed.