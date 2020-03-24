#!/usr/bin/env python

import sys
import rospy
import math
import json
import time
from std_srvs.srv import Empty
from rosplan_knowledge_msgs.srv import *
from rosplan_knowledge_msgs.msg import *

KB_UPDATE_ADD_KNOWLEDGE = 0
KB_UPDATE_RM_KNOWLEDGE = 2
KB_UPDATE_ADD_GOAL = 1
KB_UPDATE_RM_GOAL = 3

KB_ITEM_INSTANCE = 0
KB_ITEM_FACT = 1
KB_ITEM_FUNCTION = 2
KB_ITEM_EXPRESSION = 3
KB_ITEM_INEQUALITY = 4

PATH = '/home/vannini/drone_arch/Data/' #set 


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


def get_regions(missao_json):
    regions = []
    for step in missao_json["mission_execution"]:
        regions.append(step["instructions"]["area"]["name"])
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
            obj.append(step["instructions"]["area"]["name"])
    return obj


def list_to_geopoint(l):
    return GeoPoint(l[0], l[1], l[2])


def read_json(mission, mapa):

    regions = []
    names = []
    labels = []

    geo_home = list_to_geopoint(mapa["geo_home"])

    for miss in mission["mission_execution"]:
        step = miss["instructions"]["area"]

        geo_points = [list_to_geopoint(gp) for gp in step["geo_points"]]
        cart_points = [geo_to_cart(gp, geo_home) for gp in geo_points]
        geo_center = list_to_geopoint(step["center"])
        cart_center = geo_to_cart(geo_center, geo_home)

        names.append(step["name"])

        region = Region(
            step["name"], step["name"], geo_points, cart_points, geo_center, cart_center
        )

        regions.append(region)
        labels.append(miss["command"])

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


call_clear()

obj = create_predicate("been-at", [diagnostic_msgs.msg.KeyValue("region", "region_5")])
add_goal(obj)



