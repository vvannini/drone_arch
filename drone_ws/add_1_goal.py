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


def get_goal(predicate_name):
    instance = KnowledgeItem()
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/goals')
    try:
        print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/goals', GetAttributeService)
        instance = query_proxy(predicate_name)
        #instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

def get_function(function_name):
    instance = KnowledgeItem()
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/functions')
    try:
        print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/functions', GetAttributeService)
        instance = query_proxy(function_name)
        #instance = instance.attributes[0]
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)
    return instance

def get_predicate(predicate_name):
    instance = KnowledgeItem()
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/state/propositions')
    try:
        print ("Calling Service get_function")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/propositions', GetAttributeService)
        instance = query_proxy(predicate_name)
        #instance = instance.attributes[0]
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

at = get_predicate("at").attributes[0]

f = get_function("total-goals").attributes[0]

print(get_goal(''))
for goal in get_goal('').attributes:
    if(goal.attribute_name != "at"):
        for goal_achived in get_predicate(goal.attribute_name).attributes:
            if(goal.values[0].value == goal_achived.values[0].value):
                f.function_value = f.function_value - 1
                remove_goal(goal)

#remove_instance(at)
#at.values[1].value = "region_1"

goal = create_predicate("taken-image", [diagnostic_msgs.msg.KeyValue("region", "region_5")])
add_goal(goal)
f.function_value = f.function_value + 1

bat = get_function("battery-amount").attributes[0]
print("bat:")
print(bat.function_value)

print(at.values[1].value)
add_instance(at)

print(f.function_value)
add_instance(f)
# remove_instance(f)
# f.function_value = f.function_value + 1
# 
# add_instance(f)