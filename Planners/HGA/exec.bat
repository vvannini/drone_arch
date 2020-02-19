REM java -jar hga-interface.jar
REM 		long(from) lat(from) alt(from)
REM 		long(to) lat(to) alt(to)
REM 		id(map)
REM 		waypoints
REM 		fly-time(seconds)
REM 		run-time(seconds)
REM 		max-speed(m/s)
REM 		max-acceleration(m/s^2)
REM 		delta
REM 		gps-error(meters)
REM 		plot
REM			map-scale
REM			exec-path
java -jar hga-interface.jar -47.932546 -22.002237 15 -47.932608 -22.002674 13 0 20 10 5 10 8 0.01 1.0 true "java -jar hga.jar run job ./"
java -jar ~/drone_arch/Planners/HGA/hga-interface.jar -47.932546 -22.002237 15 -47.932608 -22.002674 13 0 20 10 5 10 8 0.01 1.0 true "java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux  hga.jar run job ./"
pause

java -jar ~/drone_arch/Planners/HGA/hga-interface.jar -50.3276461 -12.82225103 15 -50.3567626 -12.8111267 13 0 20 600 20 10 8 0.01 1.0 200 true "java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux  hga.jar run job ./"
