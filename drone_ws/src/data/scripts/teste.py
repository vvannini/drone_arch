#!/usr/bin/env python

import sys
import rospy
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


query = []

def call_service():
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/query_state')
    try:
        print("Calling Service")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/query_state', KnowledgeQueryService)
        query_proxy()
        #print "Response is:", resp1.results
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)

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
        print ("Calling Service ADD")
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

def add_fact(item):
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        print ("Calling Service ADD GOAL")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_ADD_GOAL, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)

def remove_fact(item):
    print ("Waiting for service")
    rospy.wait_for_service('/rosplan_knowledge_base/update')
    try:
        print ("Calling Service RM GOAL")
        query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/update', KnowledgeUpdateService)
        query_proxy(KB_UPDATE_RM_GOAL, item)
    except rospy.ServiceException as e:
        print ("Service call failed: %s"%e)



if __name__ == "__main__":

    # QUERY 1 (robot_at kenny wp0)
    query1 = KnowledgeItem()
    query1.knowledge_type = KnowledgeItem.FACT
    query1.attribute_name = "robot_at"
    query1.values.append(diagnostic_msgs.msg.KeyValue("v", "kenny"))
    query1.values.append(diagnostic_msgs.msg.KeyValue("wp", "wp0"))
    query.append(query1)

    # rover1 rover
    instance = KnowledgeItem()
    instance.knowledge_type = KnowledgeItem.INSTANCE
    instance.instance_type = "rover"
    instance.instance_name = "kenny1"

    # (at kenny wp0)
    # instance = KnowledgeItem()
    # instance.knowledge_type = KnowledgeItem.FACT
    # instance.attribute_name = "at"
    # instance.values.append(diagnostic_msgs.msg.KeyValue("v", "kenny"))
    # instance.values.append(diagnostic_msgs.msg.KeyValue("wp", "wp0"))

    # instance = KnowledgeItem()
    # instance.knowledge_type = KnowledgeItem.FUNCTION
    # instance.attribute_name = "distance"
    # instance.values.append(diagnostic_msgs.msg.KeyValue("wp", "wp1"))
    # instance.values.append(diagnostic_msgs.msg.KeyValue("wp", "wp0"))
    # instance.function_value = 1000


    add_goal(query1)
    remove_instance(instance)
    # call_service()
    #call_clear()
    sys.exit(1)