#include "ros/ros.h"
#include "planners/JarPlanners.h"
//#include <string>





int main(int argc, char  *argv[])
{
	ros::init(argc, argv,"path_re_planning_client"); //Initialize Node Name
	ros::NodeHandle nh; //Node handle declaration for communication with ROS system

	//Declares service client server_planners
	//using the SrvDemo service file in the ros_service_demo package
	//The service name is "server_planners"

	ros::ServiceClient server_planners = nh.serviceClient<planners::JarPlanners>("ros_service_demo");

	//Declares the srv service that uses the SrvDemo service file
	planners::JarPlanners srv;

	if(server_planners.call(srv)){
		ROS_INFO("receive srv, srv.Response.result - replanejador escolhido: %s", (std::string)srv.response.result);
	}
	else{
		ROS_ERROR("Failed to call service ros_service_demo");
		return 1;
	}
	return 0;
}