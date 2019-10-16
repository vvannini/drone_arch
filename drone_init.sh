#Veronica Vannini - 05/13/2019
#init workspace
if [ $1 == 'sitl' ]; then
	gnome-terminal bash -c "cd ~/src/Firmware/ && export PX4_HOME_LAT=-22.002178 && export PX4_HOME_LON=-47.932588 && export PX4_HOME_ALT=847.142652 && export NAV_RCL_ACT=0 && make px4_sitl jmavsim"	
	#gnome-terminal -x bash -c "cd ~/src/Firmware/ && export PX4_HOME_LAT=-22.002178 && export PX4_HOME_LON=-47.932588 && export PX4_HOME_ALT=847.142652 && export NAV_RCL_ACT=0 && make px4_sitl jmavsim"
	gnome-terminal bash -c "roslaunch mavros px4.launch fcu_url:="udp://:14540@127.0.0.1:14557""
	gnome-terminal bash -c "./Downloads/QGroundControl.AppImage"
	#sleep 60
	#gnome-terminal -x bash -c "rosrun mavros mavwp load /home/vannini/Missions/mavros_novo.wp; read line"
	#gnome-terminal -x bash -c "source ~/drone_ws/devel/setup.bash; rosrun drone_system oversees_Sensors; read line"
	#sleep 30
	#gnome-terminal -x bash -c "source ~/drone_ws/devel/setup.bash; rosrun drone_system init_Mission; read line"
	#gnome-terminal -x bash -c "source ~/drone_ws/devel/setup.bash; rosrun drone_system actions; read line"
else
	gnome-terminal -x bash -c "roslaunch mavros px4.launch fcu_url:=/dev/ttyUSB0:57600"
	#gnome-terminal -x bash -c "roslaunch mavros px4.launch fcu_url:=/dev/ttyUSB0:57600 gcs_host:=localhost:14555; read line"
	#gnome-terminal -x bash -c "rosrun tarkin target_Alderaan; read line"
	#gnome-terminal -x bash -c "rosrun tarkin fire_Alderaan; read line"
fi
