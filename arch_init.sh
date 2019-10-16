#Veronica Vannini - 05/13/2019
#init workspace
if [ $1 == 'sitl' ]; then
	gnome-terminal --tab --command="bash -c 'cd ~/Firmware/; export PX4_HOME_LAT=-22.002178; export PX4_HOME_LON=-47.932588; export PX4_HOME_ALT=847.142652; export NAV_RCL_ACT=0; make px4_sitl jmavsim'"
	#gnome-terminal -x bash -c "cd ~/src/Firmware/ && export PX4_HOME_LAT=-22.002178 && export PX4_HOME_LON=-47.932588 && export PX4_HOME_ALT=847.142652 && export NAV_RCL_ACT=0 && make px4_sitl jmavsim"
	gnome-terminal --tab --command="roslaunch mavros px4.launch fcu_url:="udp://:14540@127.0.0.1:14557""
	gnome-terminal --tab --command="bash -c 'cd ~/Downloads; ./QGroundControl.AppImage'"


	gnome-terminal --tab --command="rosservice call /mavros/set_stream_rate 0 10 1"
	gnome-terminal --tab --command="source ~/drone_arch/drone_ws/devel/setup.bash; rosrun multiple_fault_diagnostic risk_mitigation"
	gnome-terminal --tab --command="source ~/drone_arch/drone_ws/devel/setup.bash; rosrun decision_support mission_planning"
	gnome-terminal --tab --command="source ~/drone_arch/drone_ws/devel/setup.bash; rosrun decision_support decision_manager"
	gnome-terminal --tab --command="source ~/drone_arch/drone_ws/devel/setup.bash; cd ~/drone_arch/drone_ws/src/planners/scripts/static_pathplanning; python3 main.py"
	gnome-terminal --tab --command="source ~/drone_arch/drone_ws/devel/setup.bash; rosrun decision_support mission_goal_manager"

	#rosrun mavros mavwp load /home/vannini/Missions/file3.wp
else
	gnome-terminal -x bash -c "roslaunch mavros px4.launch fcu_url:=/dev/ttyUSB0:57600"
	#gnome-terminal -x bash -c "roslaunch mavros px4.launch fcu_url:=/dev/ttyUSB0:57600 gcs_host:=localhost:14555; read line"
	#gnome-terminal -x bash -c "rosrun tarkin target_Alderaan; read line"
	#gnome-terminal -x bash -c "rosrun tarkin fire_Alderaan; read line"
fi
