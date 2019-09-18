#include <stdio.h>
#include <iostream>
#include<string>
#include <fstream>
#include "ros/ros.h"
#include "std_msgs/String.h"
using namespace std;
//string path_to_file;

// NOTE: to run this code the terminal has to be in the same folder as the 3 txt files

void instance(const std_msgs::String::ConstPtr& msg){

	string line;
	ofstream out;
	ifstream inicio, final;
	string path_to_file;

	out.open(path_to_file+"instance.txt");
	inicio.open(path_to_file+"1.txt");
	final.open(path_to_file+"2.txt");

	if(out.is_open()){
		cout<<"sucesso na abertura do arquivo\n";
	}
	else{
		cout<<"falha na abertura do arquivo\n";
	}

	while(getline(inicio,line)){
		out<<line<<"\n";
	}
	
	out<<msg->data.c_str()<<"\n";


	while(getline(final,line)){
		out<<line<<"\n";
	}


	inicio.close();
	final.close();
	out.close();
	ROS_INFO("I heard: [%s]", msg->data.c_str());
}



	
int main(int argc, char **argv)
{	
	ros::init(argc, argv, "create_instance_file");   
	ros::NodeHandle n;
	string path_to_files = argv[1];
	string map_name = argv[2];

	string line;
	ofstream out;
	ifstream inicio, final;

	out.open(path_to_files+"instance.txt");
	inicio.open(path_to_files+"1.txt");
	final.open(path_to_files+"2.txt");

	if(out.is_open()){
		cout<<"sucesso na abertura do arquivo\n";
	}
	else{
		cout<<"falha na abertura do arquivo\n";
	}

	while(getline(inicio,line)){
		out<<line<<"\n";
	}
	
	out<<map_name<<"\n";


	while(getline(final,line)){
		out<<line<<"\n";
	}


	inicio.close();
	final.close();
	out.close();
	//ros::Subscriber sub = n.subscribe("map", 1000,instance); 
	ros::spinOnce();

	return 0;
}