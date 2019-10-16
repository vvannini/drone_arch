#include "ros/ros.h"
#include "planners/JarPlanners.h"
#include <string>





int main(int argc, char  *argv[])
{
	ros::init(argc, argv,"path_re_planning_client"); //Initialize Node Name
	ros::NodeHandle nh; //Node handle declaration for communication with ROS system

	//Declares service client service_planners_client
	//using the JarPlanners service file in the planners package
	//The service name is "service_planners_client"

	ros::ServiceClient service_planners_client = nh.serviceClient<planners::JarPlanners>("planners");

	//Declares the srv service that uses the JarPlanners service file
	planners::JarPlanners srv;

	if(service_planners_client.call(srv)){
		ROS_INFO("receive srv, srv.Response.result - replanejador escolhido: %s", (std::string)srv.response.result);
	}
	else{
		ROS_ERROR("Failed to call service planners");
		return 1;
	}
	return 0;
}