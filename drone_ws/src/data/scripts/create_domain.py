#!/usr/bin/env python

import sys
import rospy
import math
import json
import time
import os
from std_srvs.srv import Empty
from rosplan_knowledge_msgs.srv import *
from rosplan_knowledge_msgs.msg import *

PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)


def read_json(domain):

	requirements = []
	types = []
	functions = []
	predicates = []
	actions = []

	name = domain["name"]

	for r in domain["requirements"]:
		requirements.append(r)

	for t in domain["types"]:
		types.append(t)

	for f in domain["functions"]:
		functions.append(f)

	for p in domain["predicates"]:
		predicates.append(p)

	for a in domain["actions"]:
		actions.append(a)

	return name, requirements, types, functions, predicates, actions

def create_domain_file(path, name):
	pddl = open(path, "w+")

	pddl.write("(define (domain "+name+")\n")
	pddl.write("\t(:requirements \n\t)\n")
	pddl.write("\t(:types \n\t)\n")
	pddl.write("\t(:functions \n\t)\n")
	pddl.write("\t(:predicates \n\t)\n")
	pddl.write("\t;actions \n\n\n)")

	pddl.close()

	# return pddl

def add_requirements(path, requirements):
	index = 0

	f = open(path, "r")
	contents = f.readlines()
	for x in contents:
		index += 1
		if(x.find("requirements") != -1):
			print(x)
			break
	f.close()

	contents.insert(index, "\t\t")
	index += 1

	for r in requirements:
		contents.insert(index, " :"+r)
		index +=1

	contents.insert(index, "\n")


	f = open(path, "w")
	contents = "".join(contents)
	f.write(contents)
	f.close()


def add_types(path, types):
	index = 0

	f = open(path, "r")
	contents = f.readlines()
	for x in contents:
		index += 1
		if(x.find("types") != -1):
			print(x)
			break
	f.close()



	for t in types:
		value = "\t\t"+t["name"] + " - " + t["type"] + "\n"
		contents.insert(index, value)
		index +=1


	f = open(path, "w")
	contents = "".join(contents)
	f.write(contents)
	f.close()


def add_functions(path, functions):
	index = 0

	f = open(path, "r")
	contents = f.readlines()
	for x in contents:
		index += 1
		if(x.find("functions") != -1):
			print(x)
			break
	f.close()



	for f in functions:
		value = "\t\t("+f["name"]
		for v in f["var"]:
			value = value + " ?" + v["name"] + " - " + v["type"]
		value = value + ")\n"
		contents.insert(index, value)
		index +=1


	f = open(path, "w")
	contents = "".join(contents)
	f.write(contents)
	f.close()

def add_predicates(path, predicates):
	index = 0

	f = open(path, "r")
	contents = f.readlines()
	for x in contents:
		index += 1
		if(x.find("predicates") != -1):
			print(x)
			break
	f.close()



	for p in predicates:
		value = "\t\t("+p["name"]
		for v in p["var"]:
			value = value + " ?" + v["name"] + " - " + v["type"]
		value = value + ")\n"
		contents.insert(index, value)
		index +=1


	f = open(path, "w")
	contents = "".join(contents)
	f.write(contents)
	f.close()

def add_actions(path, actions):
	index = 0

	f = open(path, "r")
	contents = f.readlines()
	for x in contents:
		index += 1
		if(x.find(";actions") != -1):
			print(x)
			break
	f.close()



	for a in actions:
		value = "\t(:durative-action "+a["name"] + "\n"

		value = value + "\t\t:parameters"
		value = value + "\n\t\t\t("
		for p in a["parameters"]:
			value = value + "?" + p["name"] + " - " + p["type"] + "\n\t\t\t"
		value = value + ")\n"

		value = value + "\t\t:duration\n"
		value = value + "\t\t\t(= duration " + a["duration"] + ")\n"

		value = value + "\t\t:condition"
		value = value + "\n\t\t\t(and"
		for c in a["condition"]:
			value = value + "\n\t\t\t\t("
			if(c["is-negative"]):
				value = value + "not ("
			value = value + c["time"] + " (" + c["predicate"]
			for p in c["parameters"]:
				value = value + " " + p
			if(c["is-negative"]):
				value = value + ")"
			value = value + "))"
		value = value + "\n\t\t\t)\n"


		value = value + "\t\t:effect"
		value = value + "\n\t\t\t(and"
		for c in a["effect"]:
			value = value + "\n\t\t\t\t(" 
			if(c["is-negative"]):
					value = value + "not ("
			value = value + c["time"] + " (" + c["predicate"]
			for p in c["parameters"]:
				value = value + " " + p
			if(c["is-negative"]):
				value = value + ")"
			value = value + "))"
		value = value + "\n\t\t\t)\n"

		value = value + "\t)\n"

		contents.insert(index, value)
		index +=1


	f = open(path, "w")
	contents = "".join(contents)
	f.write(contents)
	f.close()

args = sys.argv
domain_filename = PATH + "domain.json"
domain_id = 0

with open(domain_filename, "r") as domain_file:
		domain_file = json.load(domain_file)
		domain = domain_file[domain_id]


name, requirements, types, functions, predicates, actions = read_json(domain)

pddl_path = PATH+"PDDL/domain.pddl"

create_domain_file(pddl_path, name)

add_requirements(pddl_path, requirements)
add_types(pddl_path, types)
add_functions(pddl_path, functions)
add_predicates(pddl_path, predicates)
add_actions(pddl_path, actions)
