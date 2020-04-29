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

def get_function(function_name):
    instance = KnowledgeItem()
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/functions')
    try:
        print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/functions', GetAttributeService)
        instance = query_proxy(function_name)
        instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

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


#call_clear()

obj = create_predicate("taken-image", [diagnostic_msgs.msg.KeyValue("region", "region_5")])
add_goal(obj)
f = get_function("total-goals")
remove_instance(f)
f.function_value = f.function_value + 1
print(f.function_value)
add_instance(f)
