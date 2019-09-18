#include "ros/ros.h"
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

    wp_push_srv.request.start_index = msg->waypoints.size()+1;
  	wp_push_srv.request.waypoints.push_back(wp_msg);

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