#!/usr/bin/env python

import sys
import os
import rospy
import json
import copy
import rosnode, psutil
from xmlrpc import client
from itertools import combinations
from std_srvs.srv import Empty
from rosplan_knowledge_msgs.msg import *
from rosplan_knowledge_msgs.srv import *



PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)


def FileCheck_id(fn):
	try:
		print("try")
		with open(fn, "r") as mission_file:
			mission_file = json.load(mission_file)
		
		mission_id = mission_file[-1]["id"] + 1
		print(mission_id)
		return mission_id, mission_file
	except IOError as e:
	  
	  print("File does not appear to exist.")
	  open(fn, "w")
	  print("0")
	  return 0, []


def write_mission(mission, mission_file):
	with open(mission_file, 'w') as outfile:
		json.dump(mission, outfile)




def get_regions():
	map_filename = PATH + "mapa.json"

	with open(map_filename, "r") as map_file:
			map_file = json.load(map_file)
			mapa = map_file[0]

	bases = []
	regions = []
	for step in mapa["bases"]:
		if step["id"] not in bases:
			bases.append(step["id"])

	for step in mapa["regions"]:
		if step["id"] not in regions:
			regions.append(step["id"])
	return bases, regions



'''
Ok, so, I'm doing a combination of all possible missions given regions (12 at the time), 
bases (4) and commands (2), that said, it should start at a simple mission with 1 goal, as:
	take_pic/pulverize region_1

and end with the longest possible, as:
	take_pic region_[1..12]
	pulverize region_[1..12]
	end base 4

To do such thing, It was necessary to make all kind of combinations of other combinations
(thank God for the python command combinations()). At this moment, I really know what I'm 
doing, but in the future I think it will be unreadble, so, if you have to change this code, 
good luck.

'''

def make_clean(cmds, bases, regions, commands, id_mission):
	# for each diff command
	for command in commands:
		# for 1 to qtd of regions (all length of combinations)
		for i in range(1,len(regions)+1):
			comb =  list(combinations(regions, i))

			# for each combination found at this length 
			for c in comb:

				# new mission
				mission = {}

				# print(cmds)
				# key = input()

				mission["id"] = id_mission
				mission["name"] = command + "_" + str(id_mission)
				exe_commands = []

				# for each region in the combination, apped to commands list
				for region in c:
					exe = {}
					exe["command"] = command
					exe["instructions"] =  {}
					exe["instructions"]["area"] =  region
					exe_commands.append(exe)
				
				mission["mission_execution"] = exe_commands
				aux1 = {}
				aux1['id'] =  mission['id']
				aux1['name'] =  mission['name']
				aux1['mission_execution'] =  mission['mission_execution']



				cmds.append(aux1)
				# print(aux1)
				# key =  input()
				id_mission += 1

				# we going to add and new mission with an end at each base 
				for b in bases:
					mission["id"] = id_mission
					mission["name"] = command + "_" + str(id_mission)
					exe = {}
					exe["command"] = "end"
					exe["instructions"] =  {}
					exe["instructions"]["area"] =  b
					exe_commands.append(exe)
				
					mission["mission_execution"] = exe_commands

					aux =  copy.deepcopy(mission)


					cmds.append(aux)
					# print(aux)
					# key = input()
					id_mission += 1

					# Removing the end, so that in the new iteration dosn't have 2 ends commands
					exe_commands.remove(exe)
	return id_mission


def make_all(cmds, bases, regions, commands, id_mission):

	# for each diff command
	for command in commands:
		# for 1 to qtd of regions (all length of combinations)
		for i in range(1,len(regions)+1):
			comb =  list(combinations(regions, i))

			# for each combination found at this length 
			for c in comb:

				# new mission
				mission = {}

				exe_commands = []
				# print("\n clen exe \n",exe_commands)
				# key = input()

				# for each region in the combination, apped to commands list
				for region in c:
					exe = {}
					exe["command"] = command
					exe["instructions"] =  {}
					exe["instructions"]["area"] =  region
					exe_commands.append(exe)
				
				# mission["mission_execution"] = exe_commands
				save_point = []
				for i in exe_commands: save_point.append(i)

				#I'm going to do the rigth thing here for the future when will be more then 2 commands
				commands2 = commands
				if command in commands2:
					commands2.remove(command)

				for command2 in commands2:
					# for 1 to qtd of regions, trying to combine other command into this
					for j in range(1,len(regions)+1):
						comb2 =  list(combinations(regions, j))

						# for each combination found at this length 
						for c2 in comb2:
							exe_commands.clear()
							for i in save_point: exe_commands.append(i)

							mission["id"] = id_mission
							mission["name"] = command + "_" +command2 +"_" + str(id_mission)

							# print("print",exe_commands)

							# for each region in the combination, apped to commands list
							for region2 in c2:
								exe = {}
								exe["command"] = command2
								exe["instructions"] =  {}
								exe["instructions"]["area"] =  region2
								exe_commands.append(exe)

							# we going to add and new mission with an end at each base 
							for b in bases:
								mission["id"] = id_mission
								mission["name"] = command + "_" + str(id_mission)
								exe2 = {}
								exe2["command"] = "end"
								exe2["instructions"] =  {}
								exe2["instructions"]["area"] =  b
								exe_commands.append(exe2)
							
								mission["mission_execution"] = exe_commands
								# Removing the end, so that in the new iteration dosn't have 2 ends commands
								cmds.append(mission)
								# print("\n bases \n",mission)
								# key = input()
								id_mission += 1
								exe_commands.remove(exe2)
							
							mission["mission_execution"] = exe_commands

							cmds.append(mission)
							# print("\n comb2 \n", mission)
							# key =  input()

							id_mission += 1
							# exe_commands.remove(exe)


def make_mix(cmds, bases, regions, commands, id_mission, begin, end):

	# for each diff command
	for command in commands:
		# for 1 to qtd of regions (all length of combinations)
		for i in range(begin,end):
			comb =  list(combinations(regions, i))
			comb2 =  list(combinations(regions, i))

			# for each combination found at this length 
			for c in comb:

				# new mission
				mission = {}

				exe_commands = []
				# print("\n clen exe \n",exe_commands)
				# key = input()

				# for each region in the combination, apped to commands list
				for region in c:
					exe = {}
					exe["command"] = command
					exe["instructions"] =  {}
					exe["instructions"]["area"] =  region
					exe_commands.append(exe)
				
				# mission["mission_execution"] = exe_commands
				save_point = []
				for a in exe_commands: save_point.append(a)

				#I'm going to do the rigth thing here for the future when will be more then 2 commands
				commands2 = commands
				if command in commands2:
					commands2.remove(command)

				for command2 in commands2:
					# for 1 to qtd of regions, trying to combine other command into this
					# for j in range(1,len(regions)+1):

					# for each combination found at this length 
					for c2 in comb2:
						exe_commands.clear()
						for i in save_point: exe_commands.append(i)

						mission["id"] = id_mission
						mission["name"] = command + "_" +command2 +"_" + str(id_mission)

						# print("print",exe_commands)

						# for each region in the combination, apped to commands list
						for region2 in c2:
							exe = {}
							exe["command"] = command2
							exe["instructions"] =  {}
							exe["instructions"]["area"] =  region2
							exe_commands.append(exe)

						# we going to add and new mission with an end at each base 
						for b in bases:
							mission["id"] = id_mission
							mission["name"] = command + "_" + str(id_mission)
							exe2 = {}
							exe2["command"] = "end"
							exe2["instructions"] =  {}
							exe2["instructions"]["area"] =  b
							exe_commands.append(exe2)
						
							mission["mission_execution"] = exe_commands
							# Removing the end, so that in the new iteration dosn't have 2 ends commands
							cmds.append(mission)
							# print("\n bases \n",cmds)
							# key = input()
							id_mission += 1
							exe_commands.remove(exe2)
						
						mission["mission_execution"] = exe_commands

						cmds.append(mission)
						# print("\n comb2 \n", mission)
						# key =  input()

						id_mission += 1
						# exe_commands.remove(exe)
	return cmds


if __name__ == '__main__':
	#args = sys.argv
	mission_path = PATH + "missao.json"
	id_mission, mission_file = FileCheck_id(mission_path)
	bases, regions = get_regions()

	commands = ['take_picture', 'pulverize']
	
	# cmds should be the list of missions to in the json file, don't aks me why not missions
	cmds = []

	id_mission = make_clean(cmds, bases, regions, commands, id_mission)
	# print(cmds[-1])

	# cmds = make_mix(cmds, bases, regions, commands, id_mission, 8, len(regions)+1)

	print(cmds[0])
	

	# for step in cmds:
	# 	if step['id'] == 0:
	# 		print(step)
	# 		break

	# key = input()
	print(cmds[-1])

	print(len(cmds))

	# print("Append")
	# mission_file.append(cmds)

	print("Write")
	write_mission(cmds, mission_path)