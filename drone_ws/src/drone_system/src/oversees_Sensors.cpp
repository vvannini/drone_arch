#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/NavSatFix.h"
#include "std_msgs/Float64.h"
#include "sensor_msgs/Imu.h"

using namespace std;

void chatterCallback_Global(const sensor_msgs::NavSatFix::ConstPtr& msg)
{
	//cout << *msg << endl;
	cout << endl << "-----------------------------" << endl << endl;
	cout << msg->header.stamp << endl;
	cout <<"global/altitude: " << msg->altitude << endl;
}


void chatterCallback_Relative(const std_msgs::Float64::ConstPtr& msg)
{
	cout << "rel_alt/data: "; 
	cout << msg->data;
	cout << endl;
}

void chatterCallback_Raw(const sensor_msgs::NavSatFix::ConstPtr& msg)
{
	cout << "raw_fix/altitude: ";
	cout << msg->altitude;
	cout << endl;
}

void chatterCallback_IMU(const sensor_msgs::Imu::ConstPtr& msg)
{
	cout << "imu_pub/data/orientation: ";
	cout << endl;
	cout << msg->orientation;
	
}

void chatterCallback_imuRaw(const sensor_msgs::Imu::ConstPtr& msg)
{
	cout << "imu_pub/data_raw/linear_acceleration: ";
	cout << endl;
	cout << msg->linear_acceleration;
}

int main(int argc, char **argv)
{

	ros::init(argc, argv, "oversees_Sensors");


	ros::NodeHandle n;

	ros::Subscriber global = n.subscribe("/mavros/global_position/global", 100, chatterCallback_Global);

	ros::Subscriber relative = n.subscribe("/mavros/global_position/rel_alt", 100, chatterCallback_Relative);
	
	ros::Subscriber raw = n.subscribe("/mavros/global_position/raw/fix", 100, chatterCallback_Raw);	

	ros::Subscriber Imu = n.subscribe("/mavros/imu/data", 100, chatterCallback_IMU);
	
	ros::Subscriber Imu_raw = n.subscribe("/mavros/imu/data_raw", 100, chatterCallback_imuRaw);




	ros::spin();

return 0;
}