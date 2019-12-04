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

int getch()
{
  static struct termios oldt, newt;
  tcgetattr( STDIN_FILENO, &oldt);           // save old settings
  newt = oldt;
  newt.c_lflag &= ~(ICANON);                 // disable buffering      
  tcsetattr( STDIN_FILENO, TCSANOW, &newt);  // apply new settings

  int c = getchar();  // read character (non-blocking)

  tcsetattr( STDIN_FILENO, TCSANOW, &oldt);  // restore old settings
  return c;
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "Actions_Auto");
    ros::NodeHandle n;
    //ROS_INFO("Commands: 1- ARM 2- set LOITER 3- set AUTO 4- LAND  5- TAKEOFF");
    
            ros::ServiceClient cl = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
            mavros_msgs::SetMode srv_setMode;
            srv_setMode.request.base_mode = 0;
            //srv_setMode.request.custom_mode = "AUTO.MISSION";
            srv_setMode.request.custom_mode = "AUTO";
            if(cl.call(srv_setMode)){
                //ROS_INFO("AUTO.MISSION");
                ROS_INFO("AUTO");
                //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
            }else{
                ROS_ERROR("Failed SetMode");
                return -1;
            }
    return 0;
}
