#include <cstdlib>
#include <ros/ros.h>
#include <mavros_msgs/CommandBool.h>
#include <mavros_msgs/CommandTOL.h>
#include <mavros_msgs/SetMode.h>
#include <mavros_msgs/State.h>
#include <geometry_msgs/PoseStamped.h>
#include <termios.h>


mavros_msgs::State current_state;
void state_cb(const mavros_msgs::State::ConstPtr& msg){
    current_state = *msg;
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "Actions_ARM");
    ros::NodeHandle n;
   // ROS_INFO("Commands: 1- ARM 2- set LOITER 3- set AUTO 4- LAND  5- TAKEOFF");
    if(ros::ok())
    {
        ros::ServiceClient arming_cl = n.serviceClient<mavros_msgs::CommandBool>("/mavros/cmd/arming");
        mavros_msgs::CommandBool srv;
        srv.request.value = true;
        if(arming_cl.call(srv))
        {
                ROS_INFO("ARM send ok %d", srv.response.success);
        }
        else
        {
                ROS_ERROR("Failed arming or disarming");
        }
    }


    return 0;
}
