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

class Position(object):
	def __init__(self):
		self.sub = rospy.Subscriber('/mavros/global_position/global',  NavSatFix, self.global_position_callback)  
	
	def global_position_callback(self, data): 
	    latitude = data.latitude
	    longitude = data.longitude
	    self.unsubscribe()

	def unsubscribe(self):
	    self.sub.unregister()

rospy.init_node('listener', anonymous=True)
p = Position()
print(p.latitude)
rospy.sleep(10)