This is a group project for the 2017/2018 DAS class. Members of the group are:
Nikolas Pitsillos, Michal Vinarek, Le Duc Quy, Arnas Kapustinskas, Tsvetan
Rangelov


This project is implemented in java using RMI. Check the full specifications in
the provided pdf. Check the report in the provided pdf.


Instructions to run the code supplied
-------------------------------------
0) Make sure to start RMI registry in the source folder

   rmiregistry &

1) Run javac *.java in the source folder to compile all files

2) Run the program and pipe the output to a file:

   java TestingClass [wrr|q|swrr] debug [small|large|<empty>] > <name_of_file.txt>

   wrr = dynamic weighted round robin
   q = quasi-dynamic
   swrr = static weighted round robin
   small|large|<empty> specifies which test file to use, no argument specifies "regular" test file

   You should use "tail -f <name_of_file.txt>", and stop the java program when it reaches "Load balancer shutting down" and outputs server stats. If you stop it sooner its output will be incomplete.

3) Run the python script, passing in the name of the file with debug data:

   python testing/decode_debug.py <name_of_file.txt>

   You need to have matplotlib installed with all requirements.
   If you don't have it installed and don't want to, you can explore already generated outputs with graphs in testing/initial_*/ folders.

=====

TO RUN ANOTHER ALGORITHM MAKE SURE TO STOP EXECUTION OF THE JAVA PROGRAM AS ADVISED IN POINT 2).
