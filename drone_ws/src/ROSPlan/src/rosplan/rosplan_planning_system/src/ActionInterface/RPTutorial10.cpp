#include <iostream>
#include "rosplan_action_interface/RPTutorial10.h"
#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"
#include "mavros_msgs/WaypointList.h"
#include <mavros_msgs/CommandBool.h>
#include <geometry_msgs/PoseStamped.h>
#include <bits/stdc++.h> 
#include <mavros_msgs/WaypointPush.h>
#include <mavros_msgs/Waypoint.h>
#include <mavros_msgs/WaypointReached.h>
#include <mavros_msgs/CommandTOL.h>
#include <mavros_msgs/SetMode.h>
#include <mavros_msgs/State.h>
#include <mavros_msgs/ExtendedState.h>
#include <decision_support/newMission.h>
#include <mavros_msgs/WaypointClear.h>
#include <mavros_msgs/WaypointSetCurrent.h>


#include <fstream>
#include<iomanip>

#include <cstdlib>
using namespace std;


/* The implementation of RPTutorial.h */



struct GeoPoint{
	string  name;
	double longitude;
	double latitude;
	double altitude;
};

class Drone
{
public:
	GeoPoint position;
	mavros_msgs::State current_state;
	mavros_msgs::ExtendedState ex_current_state;
	void chatterCallback_GPS(const sensor_msgs::NavSatFix::ConstPtr& msg);
	void chatterCallback_currentState(const mavros_msgs::State::ConstPtr& msg);
	void chatterCallback_currentStateExtended(const mavros_msgs::ExtendedState::ConstPtr& msg);
};

void Drone::chatterCallback_GPS(const sensor_msgs::NavSatFix::ConstPtr& msg)
{
	position.longitude = msg->longitude;
	position.latitude = msg->latitude;
	position.altitude = msg->altitude;
}
void Drone::chatterCallback_currentState(const mavros_msgs::State::ConstPtr& msg){
    current_state = *msg;
}

void Drone::chatterCallback_currentStateExtended(const mavros_msgs::ExtendedState::ConstPtr& msg){
    ex_current_state = *msg;
}


class Mission
{
	public:		
		int WPqtd; // waypoint qtd
		int currentWP;
		bool Ended = true;
		// decision_support::newMission missionWP;
		void send_mission();
		void chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg);
		void chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg);
		// void chatterCallback_newMission(const decision_support::newMission::ConstPtr& msg);
		Mission();


	
};
Mission::Mission(void)
{
	Ended = true;
}


void Mission::chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg)
{
	WPqtd = msg->waypoints.size()-1;
}

void Mission::chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg)
{
	ROS_INFO("Waypoint: %i", msg->wp_seq+1);
	currentWP = msg->wp_seq;
	if(WPqtd == msg->wp_seq)
	{
		Ended = true;
	} 
	else
		Ended = false;

}

Mission mission;
Drone drone;


void land(Drone drone)
{
	ros::NodeHandle n;
	ros::ServiceClient land_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/land");
    mavros_msgs::CommandTOL srv_land;
    srv_land.request.altitude = 0;
    srv_land.request.latitude = drone.position.latitude;//0;
    srv_land.request.longitude = drone.position.longitude;//0;
    srv_land.request.min_pitch = 0;
    srv_land.request.yaw = 0;
    if(land_cl.call(srv_land))
    {
        ROS_INFO("srv_land send ok %d", srv_land.response.success);
    }
    else
    {
        ROS_ERROR("Failed Land");
    }
}

void set_loiter()
{
	ros::NodeHandle n;
	ros::ServiceClient cl = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
    mavros_msgs::SetMode srv_setMode;
    srv_setMode.request.base_mode = 0;
    //srv_setMode.request.custom_mode = "AUTO.LOITER";
    srv_setMode.request.custom_mode = "AUTO.LOITER";
    if(cl.call(srv_setMode))
    {
        //ROS_INFO("AUTO.LOITER");
        ROS_INFO("LOITER");
    }
    else
    {
        ROS_ERROR("Failed SetMode");
    }
}

void set_auto()
{
	ros::NodeHandle n;
	ros::ServiceClient cl = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
    mavros_msgs::SetMode srv_setMode;
    srv_setMode.request.base_mode = 0;
    srv_setMode.request.custom_mode = "AUTO.MISSION";
    //srv_setMode.request.custom_mode = "AUTO";
    if(cl.call(srv_setMode))
    {
        //ROS_INFO("AUTO.MISSION");
        ROS_INFO("AUTO");
    }
    else
    {
        ROS_ERROR("Failed SetMode");
    }
}

void arm()
{
	ros::NodeHandle n;
	ros::ServiceClient arming_cl = n.serviceClient<mavros_msgs::CommandBool>("/mavros/cmd/arming");
    mavros_msgs::CommandBool srv;
    srv.request.value = true;
    if(arming_cl.call(srv)){
        ROS_ERROR("ARM send ok %d", srv.response.success);
    }else{
        ROS_ERROR("Failed arming or disarming");
    }
}

void takeoff(Drone drone)
{
	ros::NodeHandle n;
	ros::ServiceClient takeoff_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/takeoff");

    mavros_msgs::CommandTOL srv_takeoff;
    // ROS_INFO("Takeoff at %f, at %f", drone.position.altitude+5, drone.position.altitude);
    srv_takeoff.request.altitude = 15;
    srv_takeoff.request.latitude = drone.position.latitude;//-12.82046769976293;
    srv_takeoff.request.longitude = drone.position.longitude;//-50.33633513165995;
    //srv_takeoff.request.latitude = 0;
    //srv_takeoff.request.longitude = 0;
    srv_takeoff.request.min_pitch = 0;
    srv_takeoff.request.yaw = 0;
    if(takeoff_cl.call(srv_takeoff)){
        ROS_ERROR("srv_takeoff send ok %d", srv_takeoff.response.success);
    }else{
        ROS_ERROR("Failed Takeoff");
    }
}

void getGeoPoint(GeoPoint *geo)
{
	string command = "python3 ~/drone_arch/drone_ws/src/ROSPlan/src/rosplan/rosplan_planning_system/src/ActionInterface/pyemb7.py "+geo->name+" >> ~/drone_arch/Data/out.txt";
	int result = 1;
	system(command.c_str());
	// cout << result; 
	string line;
  	ifstream myfile ("/home/vannini/drone_arch/Data/out.txt");
  	if (myfile.is_open())
 	{
 		cout << "file opened" << endl;

    	while ( getline (myfile,line) )
    	{
    		if(result)
    		{
      			geo->latitude =  stod(line);
      			cout << geo->latitude;
      			result = 0;
    		}
    		else
    			geo->longitude = stod(line);
    			cout << geo->longitude;
    	}
    	myfile.clear();
    	myfile.close();
  	}
  	else 
  		cout << "Unable to open file"<<endl;  
}

int getRadius(string region)
{
	string command = "python3 ~/drone_arch/drone_ws/src/ROSPlan/src/rosplan/rosplan_planning_system/src/ActionInterface/getRadius.py "+region+" >> ~/drone_arch/Data/out.txt";
	system(command.c_str());
	// cout << result; 
	string line;
  	ifstream myfile ("/home/vannini/drone_arch/Data/out.txt");
  	if (myfile.is_open())
 	{
 		cout << "file opened" << endl;
 		getline (myfile,line);
 		int radius = stod(line);
    	myfile.clear();
    	myfile.close();

    	return radius;
  	}
  	else 
  		cout << "Unable to open file";  
  		return 10.0;
}

void calcRoute(GeoPoint from, GeoPoint to)
{
	//string command = "java -jar ~/drone_arch/Planners/HGA/hga-interface.jar" + to_string(from.longitude) + " " + to_string(from.latitude) +"15 "+ to_string(to.longitude) + " "+ to_string(to.latitude) +" 13 0 20 10 5 10 8 0.01 1.0 true 100 \"java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux  hga.jar run job ./\"";
	string command = "java -jar /home/vannini/drone_arch/Planners/HGA/hga-interface.jar " + to_string(from.longitude) + " " + to_string(from.latitude) +" "+to_string(from.altitude) +" "+ to_string(to.longitude) + " "+ to_string(to.latitude) +" "+to_string(to.altitude)+" 0 20 600 5 10 8 0.01 5.0 false 200 \"/home/vannini/drone_arch/Data/mapa.json\" \"/home/vannini/drone_arch/Planners/HGA/pasta-executavel\" \"/home/vannini/drone_arch/Data/route.txt\" \"java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./\"";
					//java -jar /home/vannini/drone_arch/Planners/HGA/hga-interface.jar    -50.33276461                        -12.82225103                 15  -50.3567626                      -12.8111267             13 0 20 600 5 10 8 0.01 5.0 true 200 "/home/vannini/drone_arch/Data/mapa.json" "/home/vannini/drone_arch/Planners/HGA/pasta-executavel" "/home/vannini/drone_arch/Data/route.txt" 		  "java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux  hga.jar run job ./"

	system(command.c_str());
}

void calcRoute_pulverize(GeoPoint at, int radius)
{
	//string command = "python3 /home/vannini/drone_arch/Planners/simple-behaivors/circle.py "+to_string(at.longitude)+" "+to_string(at.latitude)+" "+to_string(at.altitude)+ " "+ to_string(radius);
	string command = "python3 /home/vannini/drone_arch/Planners/simple-behaivors/circle.py "+to_string(at.longitude)+" "+to_string(at.latitude)+" "+to_string(at.altitude)+ " 50";
	//string command = "python3 /home/vannini/drone_arch/Planners/simple-behaivors/square.py "+to_string(at.longitude)+" "+to_string(at.latitude)+" "+to_string(at.altitude)+ " 250";
	
	system(command.c_str());
}

void calcRoute_picture(GeoPoint at, int distance)
{
	//string command = "python3 /home/vannini/drone_arch/Planners/simple-behaivors/circle.py "+to_string(at.longitude)+" "+to_string(at.latitude)+" "+to_string(at.altitude)+ " "+ to_string(radius);
	string command = "python3 /home/vannini/drone_arch/Planners/simple-behaivors/square.py "+to_string(at.longitude)+" "+to_string(at.latitude)+" "+to_string(at.altitude)+ " 250";
	system(command.c_str());
}

int sendWPFile()
{
	ros::NodeHandle p;
	GeoPoint geo;
	string line;
	int wp_count = 1 ;
	mavros_msgs::Waypoint* mission_wp = NULL;
	mavros_msgs::WaypointPush wp_push_srv;
	mavros_msgs::WaypointClear wp_clear_srv;



	ros::ServiceClient wp_srv_client = p.serviceClient<mavros_msgs::WaypointPush>("mavros/mission/push");
	ros::ServiceClient wp_clear_client = p.serviceClient<mavros_msgs::WaypointClear>("/mavros/mission/clear");


	int index = 0;
  	ifstream myfile ("/home/vannini/drone_arch/Data/route.txt");


  	if (myfile.is_open())
 	{
 		//find numbers of lines
    	while ( getline (myfile,line) ) 
    		 if(strcmp(line.c_str(), "           lng            lat            alt") != 0)
    		 	wp_count++;
    	
    }
    else
    {
    	cout << "Unable to open file" << endl;
    	return 0;
	}

    if(wp_count>1)
    {
    	//begin of file
    	myfile.clear();
		myfile.seekg(0, ios::beg);

		mission_wp = new mavros_msgs::Waypoint[wp_count];
		mission_wp[index].frame = 3; // mavros_msgs::Waypoint::FRAME_GLOBAL;
	  	mission_wp[index].command = 16;
	   	mission_wp[index].is_current = true;
	   	mission_wp[index].autocontinue = true;
	   	mission_wp[index].param1 = 0;
	   	mission_wp[index].param2 = 0;
	   	mission_wp[index].param3 = 0;
	   	mission_wp[index].param4 = 0;
	   	mission_wp[index].x_lat = drone.position.latitude;
	   	mission_wp[index].y_long = drone.position.longitude;
	   	mission_wp[index].z_alt = 15; 

	   	index++;

		//get wp from file
    	while ( getline (myfile,line) )
    	{
    		string number;
    		if(strcmp(line.c_str(), "           lng            lat            alt") != 0)
    		{
    			std::istringstream iss(line.c_str());
				std::vector<std::string> results(std::istream_iterator<std::string>{iss},
                                 				 std::istream_iterator<std::string>());
    			// std::cout << std::setprecision(2);
      	// 		cout << results[0] << endl;
      			geo.latitude  = atof(results[1].c_str());
      			geo.longitude = atof(results[0].c_str());
      			geo.altitude  = atof(results[2].c_str());


				mission_wp[index].frame = 3; // mavros_msgs::Waypoint::FRAME_GLOBAL;
			  	mission_wp[index].command = 16;
			   	mission_wp[index].is_current = false;
			   	mission_wp[index].autocontinue = true;
			   	mission_wp[index].param1 = 0;
			   	mission_wp[index].param2 = 0;
			   	mission_wp[index].param3 = 0;
			   	mission_wp[index].param4 = 0;
			   	mission_wp[index].x_lat = 	geo.latitude;
			   	mission_wp[index].y_long = geo.longitude;
			   	mission_wp[index].z_alt = geo.altitude;

			   	// ROS_INFO("%.10f %.10f",mission_wp[index].x_lat, atof(results[0].c_str()));

      			index++;

			      			

			}
			    		
    	}
    	myfile.clear();
    	myfile.close();
  	}
  	else 
  	{
  		remove("/home/vannini/drone_arch/Data/route.txt");
  		return 0;  
  	}
 	wp_clear_srv.request = {};

  	if (wp_clear_client.call(wp_clear_srv))
	{
	    ROS_INFO("Waypoint list was cleared");
	}
	else
	{
	    ROS_ERROR("Waypoint list couldn't been cleared");
	}

  	wp_push_srv.request.start_index = 0;
  	for(int n=0; n<wp_count; n++)
  		wp_push_srv.request.waypoints.push_back(mission_wp[n]);

  	cout << wp_srv_client.call(wp_push_srv) << endl;

  	if(wp_srv_client.call(wp_push_srv))
  	{
 	   ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
 	   remove("/home/vannini/drone_arch/Data/route.txt");
 	}
 	else
 	{
  	  ROS_ERROR("Waypoint couldn't been sent");
  	  ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
 	  remove("/home/vannini/drone_arch/Data/route.txt");
  	  return 0;
  	}
  	

  	return 1;
}

void reset_mission()
{
	ros::NodeHandle nh;
	ros::ServiceClient set_current_client = nh.serviceClient<mavros_msgs::WaypointSetCurrent>("mavros/mission/set_current");
	mavros_msgs::WaypointSetCurrent set_current_srv;

	set_current_srv.request.wp_seq = 0;

  	if (set_current_client.call(set_current_srv))
	{
	    ROS_INFO("Reset Mission");
	}
	else
	{
	    ROS_ERROR("Reset couldn't been done");
	}

}

void callRoute(GeoPoint from, GeoPoint to)
{
	ROS_INFO("Sending WP file for route %s _ %s.wp", from.name.c_str(), to.name.c_str());
	string command = "rosrun mavros mavwp load ~/drone_arch/Data/Rotas/wp/"+from.name+"_"+to.name+".wp";
	ROS_INFO("%s", command.c_str());
	system(command.c_str());
}

namespace KCL_rosplan {

	/* constructor */
	RPTutorialInterface::RPTutorialInterface(ros::NodeHandle &nh) {
		// perform setup
	}

	/* action dispatch callback */
	bool RPTutorialInterface::concreteCallback(const rosplan_dispatch_msgs::ActionDispatch::ConstPtr& msg) {
		
		

		// The action implementation goes here.
		if (strcmp(msg->name.c_str(), "go_to") == 0)
		{	//adicionar um while(mission.Ended != true ) wait() 
			// while(!mission.Ended)
			// 	ros::Duration(10).sleep();


			mission.Ended = false;
			GeoPoint from, to;
			//get coordinates
			from.name = msg->parameters[1].value.c_str();
			to.name = msg->parameters[2].value.c_str();
			ROS_INFO("go_to %s -> %s", from.name.c_str(), to.name.c_str());
			getGeoPoint(&from);
			from.altitude = 15;

			getGeoPoint(&to);
			to.altitude =13;
			ROS_INFO("GEO GeoPoint %f %f %f -> %f %f %f", from.latitude, from.longitude, from.altitude, to.latitude, to.longitude, to.altitude);
			//calc route
			calcRoute(from, to);



			//is flying?
				//is armed
			while(!drone.current_state.armed && drone.ex_current_state.landed_state != 2)
			{
				set_loiter();
				arm();
				takeoff(drone);
			}
			ros::Duration(10).sleep();
			set_loiter();
			arm();
			takeoff(drone);
			//send route 
			if(!sendWPFile())
				callRoute(from, to);
			ros::Duration(20).sleep();

			//change to auto
			// while(drone.current_state.mode != "AUTO.MISSION"){
				// ROS_INFO("current: %d", mission.currentWP);
			set_auto();	
			// }

			
			while(!mission.Ended){
				ros::Duration(10).sleep();
			}
			//while not at the end, wait
			//reset_mission();
			set_loiter();
			

			// land(drone);
			// while(drone.ex_current_state.landed_state != 1)
			// {
			// 	ROS_INFO("landing... %d", drone.ex_current_state.landed_state);
			// 	ros::Duration(10).sleep();
			// }
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;

		} 
		else if (strcmp(msg->name.c_str(), "go_to_base") == 0)
		{	//adicionar um while(mission.Ended != true ) wait() 
			// while(!mission.Ended)
			// 	ros::Duration(10).sleep();


			mission.Ended = false;
			GeoPoint from, to;
			//get coordinates
			from.name = msg->parameters[1].value.c_str();
			to.name = msg->parameters[2].value.c_str();
			ROS_INFO("go_to_base %s->%s", from.name.c_str(), to.name.c_str());
			getGeoPoint(&from);
			from.altitude = 15;

			getGeoPoint(&to);
			to.altitude =13;
			ROS_INFO("GEO GeoPoint %f %f %f -> %f %f %f", from.latitude, from.longitude, from.altitude, to.latitude, to.longitude, to.altitude);
			//calc route
			calcRoute(from, to);



			//is flying?
				//is armed
			while(!drone.current_state.armed && drone.ex_current_state.landed_state != 2)
			{
				set_loiter();
				arm();
				takeoff(drone);
			}
			ros::Duration(10).sleep();
			set_loiter();
			//arm();
			//takeoff(drone);
			//send route 
			if(!sendWPFile())
				callRoute(from, to);
			ros::Duration(20).sleep();

			//change to auto
			// while(drone.current_state.mode != "AUTO.MISSION"){
				// ROS_INFO("current: %d", mission.currentWP);
			set_auto();	
			// }

			
			while(!mission.Ended){
				ros::Duration(10).sleep();
			}
			//while not at the end, wait
			//reset_mission();
			set_loiter();

			//land after arive to base

			while(!drone.current_state.armed && drone.ex_current_state.landed_state != 2)
			{
				set_loiter();
				arm();
				takeoff(drone);
			}
			

			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;

		} 
		else if (strcmp(msg->name.c_str(), "pulverize_region") == 0)
		{			
			// string region = msg->parameters[1].value.c_str();

			// ROS_INFO("pulverize_region %s", region.c_str());

			ROS_INFO("pulverize_region");
			string region = msg->parameters[1].value.c_str();
			// ros::Duration(msg->duration).sleep();
			mission.Ended = false;

			//get coordinates
			//int radius = getRadius(region);

			//calc route
			GeoPoint at;
			at.latitude = drone.position.latitude;
			at.longitude = drone.position.longitude;
			at.altitude = 15;
			calcRoute_pulverize(at, 50);



			//is flying?
				//is armed
			while(!drone.current_state.armed && drone.ex_current_state.landed_state != 2)
			{
				set_loiter();
				arm();
				takeoff(drone);
			}
			ros::Duration(20).sleep();
			
			//send route 
			if(!sendWPFile())
				ROS_ERROR("Error call route pulverize_region");
			ros::Duration(20).sleep();

			//change to auto
			// while(drone.current_state.mode != "AUTO.MISSION"){
				// ROS_INFO("current: %d", mission.currentWP);
			set_auto();	
			// }

			
			while(!mission.Ended){
				ros::Duration(10).sleep();
			}
			//while not at the end, wait
			//reset_mission();
			set_loiter();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		}
		else if (strcmp(msg->name.c_str(), "take_image") == 0)
		{
			ROS_INFO("take_image");
			string region = msg->parameters[1].value.c_str();
			// ros::Duration(msg->duration).sleep();
			mission.Ended = false;

			//get coordinates
			//int radius = getRadius(region);

			//calc route
			GeoPoint at;
			at.latitude = drone.position.latitude;
			at.longitude = drone.position.longitude;
			at.altitude = 15;
			calcRoute_picture(at, 50);



			//is flying?
				//is armed
			while(!drone.current_state.armed && drone.ex_current_state.landed_state != 2)
			{
				set_loiter();
				arm();
				takeoff(drone);
			}
			ros::Duration(20).sleep();
			
			//send route 
			if(!sendWPFile())
				ROS_ERROR("Error call route pulverize_region");
			ros::Duration(20).sleep();

			//change to auto
			// while(drone.current_state.mode != "AUTO.MISSION"){
				// ROS_INFO("current: %d", mission.currentWP);
			set_auto();	
			// }

			
			while(!mission.Ended){
				ros::Duration(10).sleep();
			}
			//while not at the end, wait
			//reset_mission();
			set_loiter();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		} 
		else if (strcmp(msg->name.c_str(), "recharge_input") == 0)
		{
			ROS_INFO("recharge_input");

			if(drone.current_state.mode != "AUTO.LAND")
				land(drone);

			while(drone.ex_current_state.landed_state != 1)
			{
				ROS_INFO("landing... %d", drone.ex_current_state.landed_state);
				ros::Duration(10).sleep();
			}

			
			ros::Duration(msg->duration).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		}  
		else if (strcmp(msg->name.c_str(), "clean_camera") == 0)
		{
			ROS_INFO("clean_camera");
			if(drone.current_state.mode != "AUTO.LAND")
				land(drone);

			while(drone.ex_current_state.landed_state != 1)
			{
				ROS_INFO("landing... %d", drone.ex_current_state.landed_state);
				ros::Duration(10).sleep();
			}
			ros::Duration(msg->duration).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		}  
		else if (strcmp(msg->name.c_str(), "recharge_battery") == 0)
		{
			ROS_INFO("recharge_battery");
			if(drone.current_state.mode != "AUTO.LAND")
				land(drone);

			while(drone.ex_current_state.landed_state != 1)
			{
				ROS_INFO("landing... %d", drone.ex_current_state.landed_state);
				ros::Duration(10).sleep();
			}
			ros::Duration(msg->duration).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		} 
		else if (strcmp(msg->name.c_str(), "has_all_goals_achived") == 0)
		{
			ROS_INFO("has-all-goals-achived");

			ros::Duration(1).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		} 
		else if (strcmp(msg->name.c_str(), "need_battery") == 0)
		{
			ROS_INFO("need-battery");

			ros::Duration(msg->duration).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		} 
		else if (strcmp(msg->name.c_str(), "need_input") == 0)
		{
			ROS_INFO("need-input");

			ros::Duration(msg->duration).sleep();
			// complete the action
			ROS_INFO("KCL: (%s) TUTORIAL Action completing.", msg->name.c_str());
			return true;
		} 


		
	}
} // close namespace

	

	/*-------------*/
	/* Main method */
	/*-------------*/

	int main(int argc, char **argv) {

		ros::init(argc, argv, "rosplan_tutorial_action", ros::init_options::AnonymousName);
		ros::NodeHandle nh("~");
		//ros::NodeHandle nh;

		ros::Subscriber global 			= nh.subscribe("/mavros/mission/waypoints", 	1, &Mission::chatterCallback_wpqtd, 			&mission);
		ros::Subscriber current 		= nh.subscribe("/mavros/mission/reached", 		1, &Mission::chatterCallback_current, 			&mission);
		ros::Subscriber GPS 			= nh.subscribe("/mavros/global_position/global",1, &Drone::chatterCallback_GPS, 				&drone);
		ros::Subscriber state 	 		= nh.subscribe("/mavros/state", 				1, &Drone::chatterCallback_currentState, 		&drone);
		ros::Subscriber state_ext 		= nh.subscribe("/mavros/extended_state", 		1, &Drone::chatterCallback_currentStateExtended,&drone);





		// create PDDL action subscriber
		KCL_rosplan::RPTutorialInterface rpti(nh);

		rpti.runActionInterface();

		return 0;
	}