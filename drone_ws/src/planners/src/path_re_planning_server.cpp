#include "ros/ros.h"
#include "planners/JarPlanners.h"
//#include <string>
using namespace std;


bool replaners(planners::JarPlanners::Request &req, planners::JarPlanners::Response &res){
	ros::NodeHandle nh;

	req.pedido = 1;
	if(req.pedido == 1)
	{
		system("cd ~/drone_arch/drone_ws/src/planners/src/Path-Replanning-master/DE4s && rosrun planners create_instance_file ~/drone_arch/drone_ws/src/planners/src/Path-Replanning-master/DE4s/");
		ROS_INFO("Instance created!! Starting planner!");
		system("cd ~/drone_arch/drone_ws/src/planners/src/Path-Replanning-master/DE4s && ./exec-replanner.sh");
		ROS_INFO("Planner compleated!!");
		res.result = "../replaners/Path-Replanning-master/DE4s";
	}
	if(req.pedido == 2){
	system("cd Path-Replanning-master/APSTD4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/APSTD4s";
	}
	if(req.pedido == 3){
	system("cd Path-Replanning-master/GA4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/GA4s";
	}
	if(req.pedido == 4){
	system("cd Path-Replanning-master/GH4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/GH4s";
	}
	if(req.pedido == 5){
	system("cd Path-Replanning-master/MILP4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/MILP4s";
	}
	if(req.pedido == 6){
	system("cd Path-Replanning-master/MPGA4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/MPGA4s";
	}
	if(req.pedido == 7){
	system("cd Path-Replanning-master/MS4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/MS4s";
	}
	if(req.pedido == 8){
	system("cd Path-Replanning-master/SGA4s && ./exec-replanner.sh");
	res.result = "../replaners/Path-Replanning-master/SGA4s";
	}
	if(req.pedido == 9){
 		//adicionar chamada para o path planning statico em pyton
	}

	//ROS_INFO("request: %s",(std::string)srv.response.result);
	//res.result = 999;
	return true;
}



int main(int argc, char **argv){
	ros::init(argc, argv, "path_re_planning_server"); //Initialize node
	ros::NodeHandle nh; //Node handle declaration

	ros::ServiceServer ros_service_demo_server = nh.advertiseService("planners",replaners); //declare the node server //maxAngle is the function callback

	ROS_INFO("server is ready!");
	ros::spin(); //wait for service request
	return 0;
}





