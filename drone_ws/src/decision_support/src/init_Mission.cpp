#include <cstdlib>
#include <ros/ros.h>
#include <mavros_msgs/CommandBool.h>
#include <mavros_msgs/CommandTOL.h>
#include <mavros_msgs/SetMode.h>
#include <mavros_msgs/State.h>
#include <geometry_msgs/PoseStamped.h>


mavros_msgs::State current_state;
void state_cb(const mavros_msgs::State::ConstPtr& msg){
    current_state = *msg;
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "offb_node");
    ros::NodeHandle n;

        ////////////////////////////////////////////
    /////////////////GUIDED/////////////////////
    //AUTO.PRECLAND      -> DISARMED by auto disarm on land
    //AUTO.FOLLOW_TARGET -> DISARMED by auto disarm on land
    //AUTO.RTGS ->  Unsupported auto mode DISARMED by auto disarm on land
    //AUTO.LAND -> DISARMED by auto disarm on land
    //AUTO.RTL  ->  RTL HOME activated
    //AUTO.MISSION 
    //RATTITUDE 
    //AUTO.LOITER 
    //STABILIZED 
    //AUTO.TAKEOFF 
    //OFFBOARD 
    //POSCTL 
    //ALTCTL 
    //AUTO.READY 
    //ACRO 
    //MANUAL
    ////////////////////////////////////////////
    ros::ServiceClient cl = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
    mavros_msgs::SetMode srv_setMode;
    /*srv_setMode.request.base_mode = 0;
    srv_setMode.request.custom_mode = "ALTCTL";
    if(cl.call(srv_setMode)){
        //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
        ROS_INFO("ALTCTL");
    }else{
        ROS_ERROR("Failed SetMode");
        return -1;
    }*/

    ////////////////////////////////////////////
    ///////////////////ARM//////////////////////
    ////////////////////////////////////////////
    ros::ServiceClient arming_cl = n.serviceClient<mavros_msgs::CommandBool>("/mavros/cmd/arming");
    mavros_msgs::CommandBool srv;
    srv.request.value = true;
    if(arming_cl.call(srv)){
        ROS_ERROR("ARM send ok %d", srv.response.success);
        //ros::ServiceClient cl1 = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
   		//mavros_msgs::SetMode srv_setMode;
   		srv_setMode.request.base_mode = 0;
    	//srv_setMode.request.custom_mode = "AUTO";
    	srv_setMode.request.custom_mode = "AUTO.MISSION";
    	if(cl.call(srv_setMode)){
    	    //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
    	    ROS_INFO("AUTO");
    	}else{
      	  ROS_ERROR("Failed SetMode");
        	return -1;
    	}
    }else{
        ROS_ERROR("Failed arming or disarming");
    }

    ////////////////////////////////////////////
    /////////////////TAKEOFF////////////////////
    ////////////////////////////////////////////
   /* ros::ServiceClient takeoff_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/takeoff");
    mavros_msgs::CommandTOL srv_takeoff;
    srv_takeoff.request.altitude = 5;
    srv_takeoff.request.latitude = -22.002178;
    srv_takeoff.request.longitude = -47.932588;
    //srv_takeoff.request.latitude = 0;
    //srv_takeoff.request.longitude = 0;
    srv_takeoff.request.min_pitch = 0;
    srv_takeoff.request.yaw = 0;
    if(takeoff_cl.call(srv_takeoff)){
        ROS_ERROR("srv_takeoff send ok %d", srv_takeoff.response.success);
    }else{
        ROS_ERROR("Failed Takeoff");
    }*/


    ////////////////////////////////////////////
    ///////////CHANGE MODE TO AUTO//////////////
    //////////FOLLOW THE MISSION////////////////
	//system("rosrun mavros mavwp load /home/vannini/file.wp");
  	//ROS_INFO("load done!");
    /*srv_setMode.request.base_mode = 0;
    srv_setMode.request.custom_mode = "AUTO.MISSION";
    if(cl.call(srv_setMode)){
        ROS_INFO("AUTO.MISSION");
        //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
    }else{
        ROS_ERROR("Failed SetMode");
        return -1;
    }
    sleep(30);


    ////////////////////////////////////////////
    ///////////////////LAND/////////////////////
    ////////////////////////////////////////////
    /*ros::ServiceClient land_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/land");
    mavros_msgs::CommandTOL srv_land;
    srv_land.request.altitude = 10;
    srv_land.request.latitude = 0;
    srv_land.request.longitude = 0;
    srv_land.request.min_pitch = 0;
    srv_land.request.yaw = 0;
    //if(land_cl.call(srv_land)){
    //    ROS_INFO("srv_land send ok %d", srv_land.response.success);
    //}else{
     //   ROS_ERROR("Failed Land");
    //}*/
    return 0;
}
