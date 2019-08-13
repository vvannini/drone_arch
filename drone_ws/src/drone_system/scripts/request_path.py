#!/usr/bin/env python

from drone_system.srv import *

import sys
import rospy

def square_path_client(option):
    rospy.wait_for_service('square_path')
    try:
        square_path = rospy.ServiceProxy('square_path', path_msg)
        resp1 = square_path(option)
        print(resp1)
        return resp1.path
    except(rospy.ServiceException, e):
        print ("Service call failed: %s"%e)

def usage():
    return "%s --pathplanning"%sys.argv[0]

if __name__ == "__main__":
    if len(sys.argv) == 2:
        option = sys.argv[1]
    else:
        print(usage())
        sys.exit(1)
    print ("Requesting %s"%(option))
    print ("Path to mission: %s"%(square_path_client(option)))
