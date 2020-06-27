#!/usr/bin/env python

import sys
import os
import rospy
import json
import rosnode, psutil
from xmlrpc import client
from std_srvs.srv import Empty
from rosplan_knowledge_msgs.msg import *
from rosplan_knowledge_msgs.srv import *



PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)


def plan_listener():
	rospy.init_node('listener', anonymous=True)
	rospy.Subscriber("rosplan_parsing_interface/complete_plan", String, callback)

	rospy.spin()

def FileCheck_id(fn):
	try:
		with open(fn, "r") as log_file:
			log_file = json.load(log_file)
		
		log_id = log_file[-1]["id"] + 1

		return log_id, log_file

	except IOError:
	  
	  print("File does not appear to exist.")
	  open(fn, "w")
	  
	  return 0, []

def get_domain():
	rospy.wait_for_service("/rosplan_knowledge_base/domain/name")
	try:
		domain = rospy.ServiceProxy("/rosplan_knowledge_base/domain/name", GetDomainNameService)
		srv = domain()
		print(srv.domain_name)
		return srv.domain_name
	except rospy.ServiceException as e:
		print("Service Call Failed %s"%e)
		return ""

def write_log(log, log_file):
	with open(log_file, 'w') as outfile:
		json.dump(log, outfile)


def get_exe_path(node_name):
	ID = '/NODEINFO'
	node_api = rosnode.get_api_uri(rospy.get_master(), node_name)
	code, msg, pid = client.ServerProxy(node_api[2]).getPid(ID)
	p = psutil.Process(pid)
	#print(p.name())
	return p.name()

#rosservice call /rosplan_knowledge_base/domain/name

'''
This function should be a subscriver in a topic provide by another node, 
but at the moment I'm to deep in POG (programação orientada a gambiarra), 
so, I'm gonna do the wrong thing and reopen the file
'''
def goals(id_mission):
	mission_filename = PATH + "missao.json"
	mision = {}
	with open(mission_filename, "r") as mission_file:
			mission_file = json.load(mission_file)
			mission = mission_file[mission_id]

	obj = []
	total_goals = 0
	for step in mission["mission_execution"]:
		total_goals += 1
		if step["command"] not in obj: #should exclude goal 'end'?
			obj.append(step["instructions"]["area"])
	return total_goals, len(obj)

'''
Hello again, dont judge me, I'm just trying to complete my obligations, 
please, future Veronica (and strager reading this) FIX THIS SHIT
'''
def parse_file_plan():
	plan_path = '~/drone_arch/drone_ws/src/data/pddl/plan.pddl' #set 
	plan_path = os.path.expanduser(plan_path)
	plan = open(plan_path, 'r')
	lines = plan.readlines()

	sucsses = 0
	cpu_time = float('Inf')
	total_time = 0

	for line in lines:
		if 'Solution Found' in line: sucsses = 1
		if '; Time' in line:
			aux = line.split(' ')
			cpu_time = eval(aux[-1].rstrip())
		if ': (' in line:
			aux = line.split(' ')
			total_time += eval(aux[-1].rstrip())[0]
	return sucsses, cpu_time, total_time






if __name__ == '__main__':
	#args = sys.argv
	log = {}
	log_path = PATH + "mission_log.json"
	id_log, log_file = FileCheck_id(log_path)
	domain = get_domain()
	planner = get_exe_path("rosplan_planner_interface")
	args = sys.argv
	mission_id = int(args[1])
	total_goals, type_qtd = goals(mission_id)

	sucsses, cpu_time, total_time = parse_file_plan()


	log['id'] = id_log
	log['domain'] = domain
	log['planner'] = planner
	log['id_mission'] = mission_id
	log['total_goals'] = total_goals
	log['qtd_types_goals'] = type_qtd
	log['sucsses'] = sucsses
	log['cpu_time'] = cpu_time
	log['total_time'] = total_time

	
	log_file.append(log)
	
	#plan_listener()
	write_log(log_file, log_path)