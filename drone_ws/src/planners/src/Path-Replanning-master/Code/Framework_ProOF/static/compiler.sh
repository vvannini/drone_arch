rm Java.jar
cp /opt/ibm/ILOG/CPLEX_Studio126/cplex/lib/cplex.jar ./lib/.
chmod +x ./lib/cplex.jar
cd bin/
rm -r *
cd ..
javac -verbose -sourcepath src/ -classpath lib/cplex.jar:lib/jsc.jar:lib/jdom.jar src/ProOF/ProOF.java -d bin/
cd bin/
jar -cvfm ../Java.jar ../manifest.mf .
cd ..
chmod +x Java.jar

