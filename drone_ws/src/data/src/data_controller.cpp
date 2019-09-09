#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"

using namespace std;
struct drone_message
{
	char *email_user;
    char *date;             //yyyy/MM/dd
    char *hour;             //HH:mm:ss
    double time;             //in seconds
    int nextWaypoint;
    int countWaypoint;
    double distToHome;       //in meters
    double distToCurrentWpt; //in meters    
    char *typeFailure;
    double voltage;          //in millivolts
    double current;          //in 10 * milliamperes
    double level;            //in percentage (%)
    double lat;	//ok
    double lng; //ok
    double alt_rel;          //in meters (in relation to launch level)
    double alt_abs;//ok      //in meters (in relation to sea level)
    double pitch;            //in radians
    double yaw;              //in radians
    double roll;             //in radians
    double vx;//ok           //velocity in m/s
    double vy;//ok           //velocity in m/s
    double vz;//ok           //velocity in m/s
    int fixType;//ok         //0-1: no fix, 2: 2D fix, 3: 3D fix
    int satellitesVisible;   //number of visible satellites
    int eph;                 //GPS horizontal dilution of position (HDOP)
    int epv;                 //GPS vertical   dilution of position (VDOP)
    double heading;//ok      //in degrees (0 ... 360)
    double groundspeed;      //in metres/second
    double airspeed;         //in metres/second
    char *mode;
    char *systemStatus;
    bool armed;
    bool isArmable;    
    bool ekfOk;
};

drone_message oracle_msg;

void chatterCallback_Global(const sensor_msgs::NavSatFix::ConstPtr& msg)
{
	 oracle_msg.alt_abs = msg->altitude;
	 oracle_msg.fixType = msg->status.status;
	 oracle_msg.lat     = msg->latitude;
	 oracle_msg.lng	 = msg->longitude;

}

void chatterCallback_heading(const std_msgs::Float64::ConstPtr& msg)
{
	 oracle_msg.heading = msg->data; 

}


void chatterCallback_Relative(const std_msgs::Float64::ConstPtr& msg)
{
	oracle_msg.alt_rel = msg->data;
}


void chatterCallback_IMU(const sensor_msgs::Imu::ConstPtr& msg)
{
	oracle_msg.vx = msg->angular_velocity.x;
	oracle_msg.vy = msg->angular_velocity.y;
	oracle_msg.vz = msg->angular_velocity.z;
	//oracel = msg->orientation->x,y,z
	
}

void chatterCallback_imuRaw(const sensor_msgs::Imu::ConstPtr& msg)
{
	//cout << "imu_pub/data_raw/linear_acceleration: ";
	//cout << endl;
	//cout << msg->linear_acceleration;
}

int main(int argc, char **argv)
{

	ros::init(argc, argv, "data_controller");


	ros::NodeHandle n;

	ros::Subscriber global = n.subscribe("/mavros/global_position/global", 100, chatterCallback_Global);

	ros::Subscriber relative = n.subscribe("/mavros/global_position/rel_alt", 100, chatterCallback_Relative);
	
	//ros::Subscriber raw = n.subscribe("/mavros/global_position/raw/fix", 100, chatterCallback_Raw);	

	ros::Subscriber Imu = n.subscribe("/mavros/imu/data", 100, chatterCallback_IMU);
	
	ros::Subscriber Imu_raw = n.subscribe("/mavros/imu/data_raw", 100, chatterCallback_imuRaw);

	ros::Subscriber Heading = n.subscribe("/mavros/global_position/compass_hdg", 100, chatterCallback_heading);
	//global_position/compass_hdg (std_msgs/Float64)
	//global_position/raw/gps_vel (geometry_msgs/TwistStamped)

	cout << "olar" <<oracle_msg.alt_abs << endl;


	ros::spin();


return 0;
}