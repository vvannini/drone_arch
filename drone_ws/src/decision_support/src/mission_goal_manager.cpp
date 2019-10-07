/*#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"
#include "mavros_msgs/WaypointList.h"
#include <bits/stdc++.h> 
#include <mavros_msgs/WaypointPush.h>
#include <mavros_msgs/Waypoint.h>


using namespace std;

// (mavros_msgs/WaypointList)

void chatterCallback_wp(const mavros_msgs::WaypointList::ConstPtr& msg)
{
	//cout << *msg << endl;
	cout <<"Qtd de waypoints: " << msg->waypoints.size() << endl;

	ros::NodeHandle p;
	ros::ServiceClient wp_srv_client = p.serviceClient<mavros_msgs::WaypointPush>("mavros/mission/push");
	mavros_msgs::WaypointPush wp_push_srv;

	mavros_msgs::Waypoint wp_msg;

	wp_msg.frame = 3; // mavros_msgs::Waypoint::FRAME_GLOBAL;
  	wp_msg.command = 16;
   	wp_msg.is_current = false;
   	wp_msg.autocontinue = true;
   	wp_msg.param1 = 0;
   	wp_msg.param2 = 0;
   	wp_msg.param3 = 0;
   	wp_msg.param4 = 0;
   	wp_msg.x_lat = 	-22.00164580;
   	wp_msg.y_long = -47.93324770;
   	wp_msg.z_alt = 15.0;

    wp_push_srv.request.start_index = msg->waypoints.size();
  	wp_push_srv.request.waypoints.push_back(wp_msg);
  	cout << wp_srv_client.call(wp_push_srv) << endl;
  	if(wp_srv_client.call(wp_push_srv))
  	{
 	   ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
 	}
 	else
 	{
  	  ROS_ERROR("Waypoint couldn't been sent");
  	  ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
  	}


}

int main(int argc, char **argv)
{

	ros::init(argc, argv, "mission_goal_manager");


	ros::NodeHandle n;



	ros::Subscriber global = n.subscribe("mavros/mission/waypoints", 100, chatterCallback_wp);

	ros::spin();



	//ros::spin();

return 0;
}
*/
#include <ros/ros.h>
#include <cstdlib>
#include <std_msgs/String.h>
#include <mavros_msgs/Waypoint.h>
#include <mavros_msgs/WaypointPush.h>
#include <mavros_msgs/WaypointClear.h>
#include <mavros_msgs/WaypointList.h>
#include <mavros_msgs/CommandHome.h>
#include <decision_support/newMission.h>

// Boost includes for parsing QGC plan file (JSON)
#include <boost/bind.hpp>
//#include <boost/cstdfloat.hpp>
#include <boost/foreach.hpp>
#include <boost/property_tree/json_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <exception>
#include <iostream>
#include <set>
#include <string>
using namespace std;


// For parsing QGC Waypoint plan (JSON)
namespace bpt = boost::property_tree;

// Parses QGroundControl Waypoint plan into MAVROS Waypoint list.
void getWaypointsFromQGCPlan(const std::string& qgc_plan_file, mavros_msgs::WaypointList* wp_list)
{
  try
  {
    std::ifstream file(qgc_plan_file);
    std::stringstream ss;

    ss << file.rdbuf();
    file.close();

    // Parse QGC plan begins
    //////////////////////////////////////////////////////////
    bpt::ptree mission_pt;
    bpt::read_json(ss, mission_pt);

    // NOTE: Unexpected type while reading values will cause an exception.
    bool first = true;
    // FOREACH mission item in the list
    int nWP = 0;
    for (auto& mi : mission_pt.get_child("mission.items"))
    {
    	nWP++;
    }
    ROS_INFO("NUMERO DE Waypoints: %i", nWP);
    for (auto& mi : mission_pt.get_child("mission.items"))
    {
      // See http://docs.ros.org/api/mavros_msgs/html/msg/Waypoint.html
      mavros_msgs::Waypoint wp{};
      // we have mission item now
      wp.frame = mi.second.get<int>("frame");
      wp.command = mi.second.get<int>("command");
      wp.autocontinue = mi.second.get<bool>("autoContinue");
      // Only 1st mission item should be set to true.
      wp.is_current = first ? true : false;
      first = false;
      // Parameters
      std::vector<double> params;
      for (auto& p : mi.second.get_child("params"))
        params.push_back(p.second.get<double>(""));
      wp.param1 = params[0];
      wp.param2 = params[1];
      wp.param3 = params[2];
      wp.param4 = params[3];
      wp.x_lat = params[4];
      wp.y_long = params[5];
      wp.z_alt = params[6];
      // Add it to Waypoint List
      //wp_list->request.waypoints.push_back(wp);
    }
    //////////////////////////////////////////////////////////
    // Parse QGC plan ends
  }
  catch (std::exception const& e)
  {
    ROS_ERROR("%s", e.what());
    throw;
  }
}


int main(int argc, char **argv)
{
	ros::init(argc,argv, "mission_goal_manager");

	ros::NodeHandle n;
	ros::Publisher chatter_pub = n.advertise<decision_support::newMission>("newMission", 1000);

	ros::Rate loop_rate(10);
	decision_support::newMission mission;
	mavros_msgs::Waypoint wp_msg[2];

	wp_msg[0].frame = 3; // mavros_msgs::Waypoint::FRAME_GLOBAL;
  	wp_msg[0].command = 16;
   	wp_msg[0].is_current = false;
   	wp_msg[0].autocontinue = true;
   	wp_msg[0].param1 = 0;
   	wp_msg[0].param2 = 0;
   	wp_msg[0].param3 = 0;
   	wp_msg[0].param4 = 0;
   	wp_msg[0].x_lat = 	-22.00164580;
   	wp_msg[0].y_long = -47.93324770;
   	wp_msg[0].z_alt = 15.0;



   	//mission.waypoints.assign(1, wp_msg[0]);

	wp_msg[1].frame = 3; // mavros_msgs::Waypoint::FRAME_GLOBAL;
  	wp_msg[1].command = 16;
   	wp_msg[1].is_current = false;
   	wp_msg[1].autocontinue = true;
   	wp_msg[1].param1 = 0;
   	wp_msg[1].param2 = 0;
   	wp_msg[1].param3 = 0;
   	wp_msg[1].param4 = 0;
   	wp_msg[1].x_lat = 	-22.00229248;
   	wp_msg[1].y_long = -47.93261492;
   	wp_msg[1].z_alt = 15.0;

   	mission.waypoints.push_back(wp_msg[0]);
    mission.waypoints.push_back(wp_msg[1]);


   	mission.option = 3;
   	mission.qtd = 2;

	int count = 0;
	while (ros::ok())
	{
		

		chatter_pub.publish(mission);
		cout << mission << endl;

		ros::spinOnce();

		loop_rate.sleep();
		++count;
	}


  /*if (argc < 2)
  {
    ROS_ERROR("Usage: rosrun decision_suport mission_goal_manager optionNumber file:=<Absolute path of QGroundControl plan>");
    return EXIT_FAILURE;
  }

  ros::init(argc, argv, "mission_goal_manager");

  mavros_msgs::WaypointList wp_list{};

  try
  {
    getWaypointsFromQGCPlan(argv[2], &wp_list);
  }
  catch (std::exception const& e)
  {
    // NOTE: QGC waypointplan (JSON) may contain 'params' valueas 'null';
    // in that case we may get execption. Make sure to keep 'params' values to be 0.
    ROS_ERROR("Fatal: error in loading waypoints from the file %s!!", argv[2]);
    abort();
  }

  /*ros::init(argc, argv, "srv_waypoint");
  ros::NodeHandle p;
  ros::NodeHandle n;
  ros::NodeHandle l;

  ros::ServiceClient wp_clear_client = p.serviceClient<mavros_msgs::WaypointClear>("/mavros/mission/clear");
  ros::ServiceClient wp_srv_client = n.serviceClient<mavros_msgs::WaypointPush>("mavros/mission/push");
  ros::ServiceClient set_home_client = l.serviceClient<mavros_msgs::CommandHome>("mavros_msgs/CommandHome");

  mavros_msgs::WaypointPush wp_push_srv;
  mavros_msgs::WaypointClear wp_clear_srv;
  mavros_msgs::CommandHome set_home_srv;


  mavros_msgs::Waypoint wp_msg;

  wp_msg.frame = 0; // mavros_msgs::Waypoint::FRAME_GLOBAL;
  wp_msg.command = 16;
  wp_msg.is_current = false;
  wp_msg.autocontinue = false;
  wp_msg.param1 = 0;
  wp_msg.param2 = 0;
  wp_msg.param3 = 0;
  wp_msg.param4 = 0;
  wp_msg.x_lat = 	-22.00164580;
  wp_msg.y_long = -47.93324770;
  wp_msg.z_alt = 15.0;

  wp_clear_srv.request = {};

  wp_push_srv.request.start_index = 0;
  wp_push_srv.request.waypoints.push_back(wp_msg);
set_home_srv.request.current_gps = false;
  set_home_srv.request.latitude = -22.002178	;
  set_home_srv.request.longitude = -47.932588  ;
  set_home_srv.request.altitude = 5;

  if (set_home_client.call(set_home_srv))
{
    ROS_INFO("Home was set to new value ");
}
else
{
    ROS_ERROR("Home position couldn't been changed");
}


if (wp_clear_client.call(wp_clear_srv))
{
    ROS_INFO("Waypoint list was cleared");
}
else
{
    ROS_ERROR("Waypoint list couldn't been cleared");
}

  if (wp_srv_client.call(wp_push_srv))
  {
    ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
  }
  else
  {
    ROS_ERROR("Waypoint couldn't been sent");
    ROS_INFO("Success:%d", (bool)wp_push_srv.response.success);
  }

   ros::spinOnce();*/

  return 0;
}

/*
#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"
#include "mavros_msgs/WaypointList.h"
#include <bits/stdc++.h> 
#include <mavros_msgs/WaypointPush.h>
#include <mavros_msgs/Waypoint.h>
#include <mavros_msgs/WaypointReached.h>
#include <mavros_msgs/CommandTOL.h>
#include <mavros_msgs/SetMode.h>




using namespace std;

class Mission
{
	public:		
		int WPqtd; // waypoint qtd
		bool Ended = false;
		int HasNewMission = 0;
		void chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg);
		void chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg);
		void chatterCallback_new(const mavros_msgs::WaypointList::ConstPtr& msg);

	
};

void Mission::chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg)
{
	WPqtd = msg->waypoints.size()-1;
}

void Mission::chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg)
{
	ROS_INFO("Waypoint: %i", msg->wp_seq);
	if(WPqtd == msg->wp_seq)
	{
		ROS_INFO("ENTREI CARAIO");
		Ended = true;
	}

}

void chatterCallback_new(const mavros_msgs::WaypointList::ConstPtr& msg)
{
	ROS_INFO("OLAR");
}


//bool Listener::Ended()
//{
//}
void land()
{
	ros::NodeHandle n;
	ros::ServiceClient land_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/land");
    mavros_msgs::CommandTOL srv_land;
    srv_land.request.altitude = 10;
    srv_land.request.latitude = 0;
    srv_land.request.longitude = 0;
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
    srv_setMode.request.custom_mode = "AUTO.LOITER";
    if(cl.call(srv_setMode)){
        ROS_INFO("AUTO.LOITER");
        //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
    }else{
        ROS_ERROR("Failed SetMode");
        //return -1;
    }
}

int main(int argc, char **argv)
{
	//bool Land = false, isInMission = true;
	//int HasNewMission = 0;
	ros::init(argc, argv, "mission_goal_manager");


	ros::NodeHandle n;
	Mission mission;
	ros::Rate loop_rate(100);
	ros::Subscriber global = n.subscribe("mavros/mission/waypoints", 1, &Mission::chatterCallback_wpqtd, &mission);
	ros::Subscriber current = n.subscribe("mavros/mission/reached", 1, &Mission::chatterCallback_current, &mission);
	ros::Subscriber newMission = n.subscribe("newMission", 1, &Mission::chatterCallback_new, &mission);
	while(ros::ok())
	{
		ros::spinOnce();
		if(mission.Ended)
			if(!mission.HasNewMission)
				land();
			else
				set_loiter();


		//ROS_INFO("MERDA DE CODIGO: %i", listener.WPqtd);
		loop_rate.sleep();
	}

	/*while(!Land || HasNewMission || isInMission)
	{

	}


	return 0;
}*/