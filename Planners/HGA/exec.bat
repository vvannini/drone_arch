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
REM			path-map
REM			path-exec
REM			path-out
java -jar hga-interface.jar -47.932546 -22.002237 15 -47.932608 -22.002674 13 0 20 10 5 10 8 0.01 1.0 true 10.0 "C:/Users/1745 MX/Dropbox/nDevelop/Papers/HGA/pasta-map/mapa.json" "C:/Users/1745 MX/Dropbox/nDevelop/Papers/pasta-executavel" "C:/Users/1745 MX/Dropbox/nDevelop/Papers/pasta-saida/route.txt" "java -jar hga.jar run job ./"
java -jar hga-interface.jar -50.33633513165995 -12.82046769976293 15 -50.35676269547999 -12.81126776150411 13 0 20 10 5 10 8 0.01 1.0 true 100 "~/drone_arch/Data/mapa.json" "~/drone_arch/Planners/HGA/pasta-executavel" "~/drone_arch/Data/route.txt" "java -jar hga.jar run job ./"


java -jar /home/vannini/drone_arch/Planners/HGA/hga-interface.jar -50.33633513165995 -12.82046769976293 15 -50.33623112388553 -12.8199095593358 13 																  0 20 10  5 10 8 0.01 1.0 true 100 "/home/vannini/drone_arch/Data/mapa.json" "/home/vannini/drone_arch/Planners/HGA/pasta-executavel" "/home/vannini/drone_arch/Data/route.txt" "java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./"
java -jar 										hga-interface.jar " + str(i.geo_center.longitude) + " " + str(i.geo_center.latitude )+" 15 "+ str(j.geo_center.longitude) + " "+ str(j.geo_center.latitude )+" 13 0 20 600 5 10 8 0.01 5.0 true 200 \"/home/vannini/drone_arch/Data/mapa.json\" \"/home/vannini/drone_arch/Planners/HGA/pasta-executavel\" \"/home/vannini/drone_arch/Data/Rotas/"+ i.name+"_"+j.name+".txt\" \"java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./\"";
java -jar hga-interface.jar -										12.82046769976293 -50.33633513165995 15 -12.8199095593358 -50.33623112388553 13 0 20 600 5 10 8 0.01 5.0 true 200 "/home/vannini/drone_arch/Data/mapa.json" "/home/vannini/drone_arch/Planners/HGA/pasta-executavel" "/home/vannini/drone_arch/Data/Rotas/base_1_base_2.txt" "java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./"



-12.82046769976293 -50.33633513165995 15.0 -12.8199095593358 -50.33623112388553 13.0 


pause