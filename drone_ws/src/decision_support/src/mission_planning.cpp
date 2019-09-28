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
#include <decision_support/newMission.h>
#include <new>
//#include "newMission.h"

using namespace std;

class Mission
{
	public:		
		int WPqtd; // waypoint qtd
		bool Ended = false;
		decision_support::newMission missionWP;
		void send_mission();
		void chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg);
		void chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg);
		void chatterCallback_newMission(const decision_support::newMission::ConstPtr& msg);
		Mission();


	
};
Mission::Mission(void)
{
	missionWP.option = 0;
}


void Mission::chatterCallback_wpqtd(const mavros_msgs::WaypointList::ConstPtr& msg)
{
	WPqtd = msg->waypoints.size()-1;
}

void Mission::chatterCallback_current(const mavros_msgs::WaypointReached::ConstPtr& msg)
{
	ROS_INFO("Waypoint: %i", msg->wp_seq);
	if(WPqtd == msg->wp_seq)
	{
		//ROS_INFO("ENTREI CARAIO");
		Ended = true;
	}

}

void Mission::chatterCallback_newMission(const decision_support::newMission::ConstPtr& msg)
{
	missionWP.option = msg->option;
	missionWP.qtd = msg->qtd;
	for (int i = 0; i < missionWP.qtd; ++i)
	{
		missionWP.waypoints.assign(1, msg->waypoints[i]);
	}
}


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
    if(cl.call(srv_setMode))
    {
        ROS_INFO("AUTO.LOITER");
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
    if(cl.call(srv_setMode))
    {
        ROS_INFO("AUTO.MISSION");
    }
    else
    {
        ROS_ERROR("Failed SetMode");
    }
}

void Mission::send_mission()
{
	Ended =  false;
	ros::NodeHandle p;
	ros::ServiceClient wp_srv_client = p.serviceClient<mavros_msgs::WaypointPush>("mavros/mission/push");
	mavros_msgs::WaypointPush wp_push_srv;


    wp_push_srv.request.start_index = 0;
    for (int i = 0; i < missionWP.qtd; ++i)
    {
		wp_push_srv.request.waypoints.push_back(missionWP.waypoints[i]);

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
  	

}

int main(int argc, char **argv)
{
	//bool Land = false, isInMission = true;
	//int HasNewMission = 0;
	ros::init(argc, argv, "mission_planning");


	ros::NodeHandle n,p;
	Mission mission;
	ros::Rate loop_rate(100);
	ros::Subscriber global = 	 n.subscribe("mavros/mission/waypoints", 	1, &Mission::chatterCallback_wpqtd, 	&mission);
	ros::Subscriber current = 	 n.subscribe("mavros/mission/reached", 		1, &Mission::chatterCallback_current, 	&mission);
	ros::Subscriber newMission = p.subscribe("newMission", 	1, &Mission::chatterCallback_newMission,&mission);
	while(ros::ok())
	{
		ros::spinOnce();
		if(mission.Ended)
			if(mission.missionWP.option == 0)
				land();
			else
			{
				set_loiter();
				mission.send_mission();
				set_auto();
				system("rosnode kill mission_goal_manager");
				mission.missionWP.option = 0;
			}


		//ROS_INFO("MERDA DE CODIGO: %i", listener.WPqtd);
		loop_rate.sleep();
	}

	/*while(!Land || HasNewMission || isInMission)
	{

	}*/


	return 0;
}