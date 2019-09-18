#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"
#include <iostream> 
#include <string> 
#include <termios.h>


using namespace std;

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

	ros::init(argc, argv, "data_acquisition");


	ros::NodeHandle n;

    ros::Publisher map_pub = n.advertise<std_msgs::String>("map", 1000);
    ros::Publisher objectives_pub = n.advertise<std_msgs::String>("objectives", 1000);

    ros::Rate loop_rate(10);

    while (ros::ok())
    {               

        string str; 
        getline(cin, str);

        int c = getch();  
        std_msgs::String msg; 

        std::stringstream ss;
        ss << str;
        msg.data = ss.str();

        if (c == '1') //getting objectives
        {
            objectives_pub.publish(msg);
            ROS_INFO("objectives: %s", msg.data.c_str());
        }
        else if (c == '2') //getting map
        {
            map_pub.publish(msg);
            ROS_INFO("map: %s", msg.data.c_str());

        }

        ros::spin();
        loop_rate.sleep();
    }


return 0;
}