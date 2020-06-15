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

KB_UPDATE_ADD_KNOWLEDGE = 0
KB_UPDATE_RM_KNOWLEDGE = 2
KB_UPDATE_ADD_GOAL = 1
KB_UPDATE_RM_GOAL = 3
KB_UPDATE_ADD_METRIC = 4

KB_ITEM_INSTANCE = 0
KB_ITEM_FACT = 1
KB_ITEM_FUNCTION = 2
KB_ITEM_EXPRESSION = 3
KB_ITEM_INEQUALITY = 4

PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)


# ---
# Classes

class Region:
	def __init__(self, idi, name, geo_points, cart_points, geo_center, cart_center):
		self.idi = idi
		self.name = name
		self.geo_points = geo_points
		self.points = cart_points
		self.geo_center = geo_center
		self.cart_center = cart_center


class CartesianPoint:
	def __init__(self, x, y, z=0):
		self.x = x
		self.y = y
		self.z = z

	def __repr__(self):
		return f"[{self.x}, {self.y}, {self.z}]"


class GeoPoint:
	def __init__(self, latitude, longitude, altitude):
		self.latitude = latitude
		self.longitude = longitude
		self.altitude = altitude


# ---
# Utils


def geo_to_cart(geo_point, geo_home):
	def calc_y(lat, lat_):
		return (lat - lat_) * (10000000.0 / 90)

	def calc_x(longi, longi_, lat_):
		return (longi - longi_) * (
			6400000.0 * (math.cos(lat_ * math.pi / 180) * 2 * math.pi / 360)
		)

	x = calc_x(geo_point.longitude, geo_home.longitude, geo_home.latitude)
	y = calc_y(geo_point.latitude, geo_home.latitude)

	return CartesianPoint(x, y, geo_point.altitude)
	# return CartesianPoint(x, y)


def cart_to_geo(cartesian_point, geo_home):
	def calc_latitude_y(lat_, y):
		return ((y * 90) / 10000000.0) + lat_

	def calc_longitude_x(lat_, longi_, x):
		return ((x * 90) / (10008000 * math.cos(lat_ * math.pi / 180))) + longi_

	longitude_x = calc_longitude_x(
		geo_home.latitude, geo_home.longitude, cartesian_point.x
	)
	latitude_y = calc_latitude_y(geo_home.latitude, cartesian_point.y)

	return GeoPoint(longitude_x, latitude_y, cartesian_point.z)
	# return GeoPoint(longitude_x, latitude_y, 10)


def calc_distances(regions):
	distances = []
	for ri in regions:
		distance = []
		for rj in regions:
			distance.append(euclidean_distance(ri.cart_center, rj.cart_center))
		distances.append(distance)
	return distances


def euclidean_distance(A, B):
	return math.sqrt((B.x - A.x) ** 2 + (B.y - A.y) ** 2)


def get_regions(mapa_json):
	regions = []
	for region in mapa_json["regions"]:
		regions.append(region["name"])
	return regions


def get_bases(mapa_json):
	bases = []
	for base in mapa_json["bases"]:
		bases.append(base["name"])
	return bases


def get_objectives(missao_json, command=""):
	obj = []
	for step in missao_json["mission_execution"]:
		if step["command"] == command:
			obj.append(step["instructions"]["area"])
	return obj


def list_to_geopoint(l):
	return GeoPoint(l[0], l[1], l[2])


def read_json(mission, mapa):

	regions = []
	names = []
	labels = []

	geo_home = list_to_geopoint(mapa["geo_home"])

	for region in mapa["regions"]:

		geo_points = [list_to_geopoint(gp) for gp in region["geo_points"]]
		cart_points = [geo_to_cart(gp, geo_home) for gp in geo_points]
		geo_center = list_to_geopoint(region["center"])
		cart_center = geo_to_cart(geo_center, geo_home)

		names.append(region["name"])

		region = Region(
			region["id"], region["name"], geo_points, cart_points, geo_center, cart_center
		)

		regions.append(region)
		labels.append("region")

	for base in mapa["bases"]:

		geo_points = [list_to_geopoint(gp) for gp in base["geo_points"]]
		cart_points = [geo_to_cart(gp, geo_home) for gp in geo_points]
		geo_center = list_to_geopoint(base["center"])
		cart_center = geo_to_cart(geo_center, geo_home)

		names.append(base["name"])

		region = Region(
			base["id"], base["name"], geo_points, cart_points, geo_center, cart_center
		)

		regions.append(region)
		labels.append("base")

	return regions, names, labels, geo_home


def call_clear():
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/clear')
	try:
		print ("Calling Service Clear")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/clear', Empty)
		query_proxy()
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)


def add_instance(item):
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/update')
	try:
		print ("ADD",  item.instance_name, item.instance_type)
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
		query_proxy(KB_UPDATE_ADD_KNOWLEDGE, item)
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)


def remove_instance(item):
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/update')
	try:
		print ("Calling Service RM")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
		query_proxy(KB_UPDATE_RM_KNOWLEDGE, item)
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)


def add_goal(item):
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/update')
	try:
		print ("Calling Service ADD GOAL")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
		query_proxy(KB_UPDATE_ADD_GOAL, item)
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)


def remove_goal(item):
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/update')
	try:
		print ("Calling Service RM GOAL")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
		query_proxy(KB_UPDATE_RM_GOAL, item)
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)

def add_metric(item):
	print ("Waiting for service")
	rospy.wait_for_service('/rosplan_knowledge_base/update')
	try:
		print ("Calling Service ADD METRIC")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
		query_proxy(KB_UPDATE_ADD_METRIC, item)
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)

def create_object(item_name,item_type):
	instance = KnowledgeItem()
	instance.knowledge_type = KnowledgeItem.INSTANCE
	instance.instance_type = item_type
	instance.instance_name = item_name

	return instance

def format_distances(distances, names):
	out = []
	for di, li in zip(distances, names):
		for dj, lj in zip(di, names):
			if(li != lj):
				instance = KnowledgeItem()
				instance.knowledge_type = KnowledgeItem.FUNCTION
				instance.values.append(diagnostic_msgs.msg.KeyValue("region", li))
				instance.values.append(diagnostic_msgs.msg.KeyValue("region", lj))
				instance.function_value = dj
				out.append(instance)
	return out

def create_function(attribute_name, values, function_value):
	instance = KnowledgeItem()
	instance.knowledge_type = KnowledgeItem.FUNCTION
	instance.attribute_name = attribute_name
	instance.values = values
	instance.function_value = function_value
	
	return instance

def create_predicate(attribute_name, values, is_negative = False):
	instance = KnowledgeItem()
	instance.knowledge_type = KnowledgeItem.FACT
	instance.attribute_name = attribute_name
	instance.values = values
	# instance.is_negative = is_negative
	
	return instance

def create_metric(optimization, item):
	instance = KnowledgeItem()
	instance.knowledge_type = KnowledgeItem.EXPRESSION
	instance.optimization = optimization
	instance.expr.tokens = item
	# instance.is_negative = is_negative
	
	return instance



args = sys.argv
mapa_filename = PATH + "mapa.json"
mission_filename = PATH + "missao.json"
hw_filename = PATH + "hardware.json"

mission_id = int(args[1])
with open(mission_filename, "r") as mission_file:
		mission_file = json.load(mission_file)
		mission = mission_file[mission_id]

mapa_id = 0

with open(mapa_filename, "r") as mapa_file:
		mapa_file = json.load(mapa_file)
		mapa = mapa_file[mapa_id]

hw_id = 0
with open(hw_filename, "r") as hw_file:
		hw_file = json.load(hw_file)
		hardware = hw_file[hw_id]

		# rostopic echo /rosplan_problem_interface/problem_instance -n 1


regions_obj, regions_names, labels, geo_home = read_json(mission, mapa)

regions  = get_regions(mapa)
base = get_bases(mapa)
pulverize = get_objectives(mission, command='pulverize')
photo = get_objectives(mission, command='take_picture')
end = get_objectives(mission, command='end')
total_goals = 0

# inputs = ["input1"]
# cameras = ["camera1"]
rover = ["kenny"]


distances = format_distances(calc_distances(regions_obj), regions_names)

call_clear()

# set drone init


obj = create_predicate("at", [diagnostic_msgs.msg.KeyValue("rover", hardware["name"]), diagnostic_msgs.msg.KeyValue("region", "base_1")])
add_instance(obj)

obj = create_predicate("can-go", [diagnostic_msgs.msg.KeyValue("rover", hardware["name"])])
add_instance(obj)

obj = create_predicate("can-take-pic", [diagnostic_msgs.msg.KeyValue("rover", hardware["name"])])
add_instance(obj)

obj = create_function("battery-capacity",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], hardware["battery-capacity"])
#print(obj)
add_instance(obj)

obj = create_function("velocity",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], hardware["efficient_velocity"])
add_instance(obj)

obj = create_function("battery-amount",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], 100)
add_instance(obj)


obj = create_function("recharge-rate-battery",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], hardware["recharge-rate-battery"])
add_instance(obj)

obj = create_function("discharge-rate-battery",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], hardware["discharge-rate-battery"])
add_instance(obj)

obj = create_function("input-amount",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], 0)
add_instance(obj)

obj = create_function("input-capacity",[diagnostic_msgs.msg.KeyValue("rover", hardware["name"])], hardware["input-capacity"])
add_instance(obj)



for r in regions:
	obj = create_object(str(r), "region")
	obj2 = create_predicate("its-not-base", [diagnostic_msgs.msg.KeyValue("region", r)])
	add_instance(obj)    
	add_instance(obj2)


for b in base:
	obj = create_object(str(b), "base")
	add_instance(obj)

# for i in inputs:
#     obj = create_object(str(i), "input")
#     add_instance(obj)

# for p in pulverize:
#     obj = create_object(str(p), "objective")
#     add_instance(obj)

# for p in photo:
#     obj = create_object(str(p), "photo")
#     add_instance(obj)

# for c in cameras:
#     obj = create_object(str(c), "camera")
#     add_instance(obj)

for r in rover:
	obj = create_object(str(r), "rover")
	add_instance(obj)

maior = 0
for d in distances:
	obj = create_function("distance", d.values, d.function_value)
	if (maior < d.function_value):
		maior = d.function_value
	add_instance(obj)


# for i in get_objectives(mission, command='take_picture', sufix='_photo'):
# 	for j in get_objectives(mission, command='take_picture', sufix=''):
# 		# obj = create_predicate("is-visible", [diagnostic_msgs.msg.KeyValue("photo", i), diagnostic_msgs.msg.KeyValue("region", j)])
# 		# print(obj)
# 		add_instance(obj)

# for i in get_objectives(mission, command='pulverize', sufix='_objective'):
# 	for j in get_objectives(mission, command='pulverize', sufix=''):
# 		# obj = create_predicate("is-visible", [diagnostic_msgs.msg.KeyValue("objective", i), diagnostic_msgs.msg.KeyValue("region", j)])
# 		add_instance(obj)

if pulverize:
	obj = create_predicate("has-pulverize-goal", [])
	add_instance(obj)

if photo:
	obj = create_predicate("has-picture-goal", [])
	add_instance(obj)


for i in pulverize:
	obj = create_predicate("pulverize-goal", [diagnostic_msgs.msg.KeyValue("region", i)])
	add_instance(obj)
	obj = create_function("pulverize-path-len",[diagnostic_msgs.msg.KeyValue("region", i)], 314)
	add_instance(obj)
	obj = create_predicate("pulverized", [diagnostic_msgs.msg.KeyValue("region", i)])
	add_goal(obj)
	total_goals = total_goals + 1
	


for i in photo:
	obj = create_predicate("picture-goal", [diagnostic_msgs.msg.KeyValue("region", i)])
	add_instance(obj)
	obj = create_function("picture-path-len",[diagnostic_msgs.msg.KeyValue("region", i)], 1000)
	add_instance(obj)
	obj = create_predicate("taken-image", [diagnostic_msgs.msg.KeyValue("region", i)])
	add_goal(obj)
	total_goals = total_goals + 1

for i in end:
	obj = create_predicate("at", [diagnostic_msgs.msg.KeyValue("rover", hardware["name"]), diagnostic_msgs.msg.KeyValue("region", i)])
	add_goal(obj)

 # {list_to_str([f'(pulverized {i} {b})' for i in inputs for b in get_objectives(missao_json, command='pulverize', sufix='_objective')], prefix=' ;', last_prefix=' ;', diff=True)}


obj = create_function("total-goals",[],total_goals)
print(obj)
add_instance(obj)

obj = create_function("goals-achived",[], 0)
#print(obj)
add_instance(obj)

# obj = create_function("total-cost",[], 0)
# add_instance(obj)


obj = create_metric("minimize (total-time)",[] )
# obj = create_metric("minimize", [diagnostic_msgs.msg.KeyValue("total-time", "total-time")])
# print(obj)
add_metric(obj)

print(maior)