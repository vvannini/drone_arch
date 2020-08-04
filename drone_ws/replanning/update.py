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


#from mavros_msgs.msg import Altitude, ExtendedState, HomePosition, State, WaypointList
from mavros_msgs.msg import *
from mavros_msgs.srv import *
#from mavros_msgs.srv import CommandBool, ParamGet, SetMode, WaypointClear,WaypointPush
from sensor_msgs.msg import NavSatFix, Imu

KB_UPDATE_ADD_KNOWLEDGE = 0
KB_UPDATE_RM_KNOWLEDGE = 2
KB_UPDATE_ADD_GOAL = 1
KB_UPDATE_RM_GOAL = 3

KB_ITEM_INSTANCE = 0
KB_ITEM_FACT = 1
KB_ITEM_FUNCTION = 2
KB_ITEM_EXPRESSION = 3
KB_ITEM_INEQUALITY = 4

PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)

latitude = 0
longitude = 0

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

def list_to_geopoint(l):
    return GeoPoint(l[0], l[1], l[2])

def euclidean_distance(A, B):
    return math.sqrt((B.x - A.x) ** 2 + (B.y - A.y) ** 2)
#-----------------------


def setLoiterMode():
   rospy.wait_for_service('/mavros/set_mode')
   try:
       flightModeService = rospy.ServiceProxy('/mavros/set_mode', mavros_msgs.srv.SetMode)
       isModeChanged = flightModeService(custom_mode='AUTO.LOITER') #return true or false
   except rospy.ServiceException as e:
       print ("service set_mode call failed: %s. AUTO.LOITER Mode could not be set. Check that GPS is enabled %s"%e)

def global_position_callback(data):
    global latitude 
    latitude = data.latitude
    global longitude 
    longitude = data.longitude

#-------------------------
def call_clear():
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/clear')
    try:
        # print ("Calling Service Clear")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/clear', Empty)
        query_proxy()
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)


def add_instance(item):
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        # print ("ADD",  item.instance_name, item.instance_type)
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_ADD_KNOWLEDGE, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)


def remove_instance(item):
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        # print ("Calling Service RM")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_RM_KNOWLEDGE, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)


def add_goal(item):
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        # print ("Calling Service ADD GOAL")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_ADD_GOAL, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)


def remove_goal(item):
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        # print ("Calling Service RM GOAL")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_RM_GOAL, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)


def get_goal(predicate_name):
    instance = KnowledgeItem()
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/goals')
    try:
        # print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/goals', GetAttributeService)
        instance = query_proxy(predicate_name)
        #instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

def get_function(function_name):
    instance = KnowledgeItem()
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/functions')
    try:
        # print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/functions', GetAttributeService)
        instance = query_proxy(function_name)
        #instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

def get_predicate(predicate_name):
    instance = KnowledgeItem()
    # print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/propositions')
    try:
        # print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/propositions', GetAttributeService)
        instance = query_proxy(predicate_name)
        #instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

def cancel_dispatch():
    os.system("rosservice call /rosplan_plan_dispatcher/cancel_dispatch")
    # print ("Waiting for service cancel")
    # rospy.wait_for_service('/rosplan_plan_dispatcher/cancel_dispatch')
    # try:
    #     print ("Calling cancel_dispatch")
    #     query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/propositions', Empty)
    #     query_proxy()
    #     #instance = instance.attributes[0]
    # except rospy.ServiceException as e:
    #     print ("Service call failed: %s"%e)

def create_object(item_name,item_type):
    instance = KnowledgeItem()
    instance.knowledge_type = KnowledgeItem.INSTANCE
    instance.instance_type = item_type
    instance.instance_name = item_name

    return instance

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

def get_location(mapa):
    
    #see distance to all regions
    geo_home = list_to_geopoint(mapa["geo_home"])
    
    #get current lat long
    print(latitude, longitude)
    cart_location = geo_to_cart(GeoPoint(latitude, longitude, 15), geo_home)


    distance = float('inf')
    closest_region = ''
    for region in mapa["regions"]:
        geo_center = list_to_geopoint(region["center"])
        cart_center = geo_to_cart(geo_center, geo_home)
        d = euclidean_distance(cart_location, cart_center)
        if d < distance:
            print(d, distance, region["name"])
            distance = d
            region_name = region["name"]
            # print(region_name)

    for base in mapa["bases"]:
        geo_center = list_to_geopoint(base["center"])
        cart_center = geo_to_cart(geo_center, geo_home)
        d = euclidean_distance(cart_center, cart_location)
        if d < distance:
            print(d, distance, base["name"])
            distance = d
            region_name = base["name"]
            # print(region_name)
    # region_name = "region_2"
    print(region_name)
    return region_name

def get_objectives(missao_json, command=""):
    obj = []
    for step in missao_json["mission_execution"]:
        if step["command"] == command:
            obj.append(step["instructions"]["area"])
    return obj


# Get info from bash - in a possible future, this should be passed by topic (I think, maybe not)
args = sys.argv
on_move = int(args[1])
mission_id = int(args[2])


# Names and paths for the json files
mapa_filename = PATH + "mapa.json"
hw_filename = PATH + "hardware.json"
mission_filename = PATH + "missao.json"

# At the moment I'm using only a map and a drone, so I didn't spend time implementing something that I won't use (sorry future me)
mapa_id = 0
hw_id = 0

# opening and reading the files
with open(mapa_filename, "r") as mapa_file:
        mapa_file = json.load(mapa_file)
        mapa = mapa_file[mapa_id]

with open(hw_filename, "r") as hw_file:
        hw_file = json.load(hw_file)
        hardware = hw_file[hw_id]

with open(mission_filename, "r") as mission_file:
        mission_file = json.load(mission_file)
        mission = mission_file[mission_id]


# Subscribing to get drone position
rospy.Subscriber('mavros/global_position/global',  NavSatFix, global_position_callback)   


'''
    from this point on will be the actual update of the mission, this code is kind of a monster, so you can call me Dr. Frankistein
'''

''' 
    this condition should be a user choice:
    on_move = 1 -> the user want to update the mission at the moment, so drone should stop and replan
    on_move = 0 -> the user want to update the mission at a a scenic region, so this code shoud wait for 
                   the drone arrive at the next region
    when the codition is satisfied, the code cancel previously plan 
'''
if(on_move):
    cancel_dispatch()
    at_move =  get_predicate("at-move")
    print(at_move.attributes, len(at_move.attributes))
    if (len(at_move.attributes)>0):
        setLoiterMode()
        remove_instance(at_move.attributes[0])
        #codigo achar at
        # at = create_predicate("at", [diagnostic_msgs.msg.KeyValue("rover", hardware["name"]), diagnostic_msgs.msg.KeyValue("region", get_location(mapa))])
        at = create_predicate("at", [diagnostic_msgs.msg.KeyValue("region", get_location(mapa))])
    else:
        at = get_predicate("at").attributes[0]


else:
    while (len(get_predicate("at-move").attributes)>0):
        rospy.sleep(10)
        print("Waiting to stop...")

    cancel_dispatch()
    at = get_predicate("at").attributes[0]

# saving the current total goals to manage goals
f = get_function("total-goals").attributes[0]


 
# has_end is a variable to verify if a mission has already a designed end, as I'm doing automatize tests, I'm ignoring new commands 'end'
has_end = False



# This is me tring to do the right ting and failing with class

# current_gols should be a dic to save goals and not re-add the same goal
# but, I still can't agree with myself of the better way to do it
current_goals = {}

# getting current goals and verifying if it already done
for goal in get_goal('').attributes:
    if(goal.attribute_name != "at"):
        # print(goal.attribute_name, goal.values[0].value)
        for goal_achived in get_predicate(goal.attribute_name).attributes:
            if(goal.values[0].value == goal_achived.values[0].value):
                f.function_value = f.function_value - 1
                remove_goal(goal)
    else:
        has_end =  True
        #verify if has already an 'end' goal - ok


# get from json file new objectives
pulverize = get_objectives(mission, command='pulverize')
photo = get_objectives(mission, command='take_picture')
end = get_objectives(mission, command='end')

# add flag to pddl for types of missions
if pulverize:
        obj = create_predicate("has-pulverize-goal", [])
        add_instance(obj)

if photo:
    obj = create_predicate("has-picture-goal", [])
    add_instance(obj)


# add new goals and aux functions/predicates
for i in pulverize:
    obj = create_predicate("pulverize-goal", [diagnostic_msgs.msg.KeyValue("region", i)])
    add_instance(obj)
    obj = create_function("pulverize-path-len",[diagnostic_msgs.msg.KeyValue("region", i)], 314)
    add_instance(obj)
    obj = create_predicate("pulverized", [diagnostic_msgs.msg.KeyValue("region", i)])
    add_goal(obj)
    f.function_value = f.function_value + 1
    


for i in photo:
    obj = create_predicate("picture-goal", [diagnostic_msgs.msg.KeyValue("region", i)])
    add_instance(obj)
    obj = create_function("picture-path-len",[diagnostic_msgs.msg.KeyValue("region", i)], 1000)
    add_instance(obj)
    obj = create_predicate("taken-image", [diagnostic_msgs.msg.KeyValue("region", i)])
    add_goal(obj)
    f.function_value = f.function_value + 1

if not has_end:
    for i in end:
        obj = create_predicate("at", [diagnostic_msgs.msg.KeyValue("region", i)])
        add_goal(obj)


# get current bat amount
bat = get_function("battery-amount").attributes[0]
print("bat:")
print(bat.function_value)

print(at)
add_instance(at)


# add new qtd of goals
# print(f.function_value)
add_instance(f)
