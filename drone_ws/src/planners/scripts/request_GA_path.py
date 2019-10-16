#!/usr/bin/env python

from planners.srv import *

import sys
import rospy

def ga_path_client(origin_lat, origin_long, origin_alt, destination_lat, destination_long, destination_alt, map_id):
    rospy.wait_for_service('genetic')
    try:
        square_path = rospy.ServiceProxy('genetic', GA_Planner)
        resp1 = square_path(origin_lat, origin_long, origin_alt, destination_lat, destination_long, destination_alt, map_id)
        print(resp1)
        return resp1.path
    
    except Exception as e :
    #except(rospy.ServiceException, e):
    #except (rospy.ServiceException, rospy.ROSException), e:
        print ("Service call failed: %s"%e)

def usage():
    return "%s --pathplanning"%sys.argv[0]

if __name__ == "__main__":
    #if len(sys.argv) == 2:
    #    option = sys.argv[1]
    #else:
    #print(usage())
    #sys.exit(1)
    #print ("Requesting %s"%(option))
    origin_lat       = -22.002237
    origin_long      = -47.932546
    origin_alt       = 15
    destination_lat  = -22.002674
    destination_long = -47.932608
    destination_alt  = 15
    map_id           = 0

    print ("Path to mission: %s"%(ga_path_client(origin_lat, origin_long, origin_alt, destination_lat, destination_long, destination_alt, map_id)))
