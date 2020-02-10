#include <cstdlib>
#include <ros/ros.h>
#include "std_srvs/Empty.h"
#include <mavros_msgs/CommandBool.h>
#include <mavros_msgs/CommandTOL.h>
#include <mavros_msgs/SetMode.h>
#include <mavros_msgs/State.h>
#include <geometry_msgs/PoseStamped.h>
#include <termios.h>

//#include <rosplan_knowledge_base/KnowledgeBase.h>

//#include "rosplan_knowledge_base/KnowledgeBase.h"
//#include "rosplan_knowledge_base/KnowledgeBaseFactory.h"


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
    ros::init(argc, argv, "data_controller");
    ros::NodeHandle n;
    ros::spinOnce();
    // while (ros::ok())
    // {

        //int c = getch();  
        int c = atoi(argv[1]); 
         //clear
        if (c == 0)
        {
        	ROS_INFO("Sending clear to problem");
            ros::ServiceClient clear_cl = n.serviceClient<std_srvs::Empty>("/rosplan_knowledge_base/clear");
            std_srvs::Empty srv;
            clear_cl.call(srv);
        }
        else if (c == 1) //add type 
        {
         	// //ROS_INFO("argv 1: %s", argv[2]);
          //   ros::ServiceClient add = n.serviceClient<rosplan_knowledge_msgs::KnowledgeUpdateService>("/rosplan_knowledge_base/update");
          //   rosplan_knowledge_msgs::KnowledgeUpdateService srv;
          //   srv.request.update_type = 0;
          //   if(add.call(srv)){
          //       ROS_ERROR("ARM send ok %d", srv.response.success);
          //   }else{
          //       ROS_ERROR("Failed arming or disarming");
          //   }
        }
        else if (c == 2)
        {
            ros::ServiceClient cl = n.serviceClient<mavros_msgs::SetMode>("/mavros/set_mode");
            mavros_msgs::SetMode srv_setMode;
            srv_setMode.request.base_mode = 0;
            //srv_setMode.request.custom_mode = "AUTO.LOITER";
            srv_setMode.request.custom_mode = "LOITER";
            if(cl.call(srv_setMode)){
                //ROS_INFO("AUTO.LOITER");
                ROS_INFO("LOITER");
                //ROS_ERROR("setmode send ok %d value:", srv_setMode.response.success);
            }else{
                ROS_ERROR("Failed SetMode");
                return -1;
            }
        }
        else if (c == 3)
        {
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
        }
        else if (c == 4)
        {
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
        else if (c == 5)
        {
            ros::ServiceClient takeoff_cl = n.serviceClient<mavros_msgs::CommandTOL>("/mavros/cmd/takeoff");
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
            }
        }


    //}
    return 0;
}
