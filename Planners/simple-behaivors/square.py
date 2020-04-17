#!/usr/bin/env python

import sys
import rospy
import math
import json
import time


args = sys.argv
# print(args)

#point = Point(-90.0667, 29.9500)

longitude =   float(args[1]) 
latitude =    float(args[2])


alt = float(args[3])
distance = float(args[4])

f = open("/home/vannini/drone_arch/Data/route.txt", "w")

theta = 135
for i in range(4):
    dx = distance* math.cos(math.radians(theta)) # theta measured clockwise from due east
    # print (math.cos(math.radians(theta)))
    # print (distance)
    # print(distance* math.cos(math.radians(theta)))
    dy = distance* math.sin(math.radians(theta)) # dx, dy same units as R

    delta_longitude = dx/(111320*math.cos(latitude))  # dx, dy in meters
    delta_latitude = dy/110540                   # result in degrees long/lat

    longitude = longitude + delta_longitude
    latitude = latitude + delta_latitude

    f.write(str(longitude)+" "+str(latitude)+" "+ str(alt)+"\n")
    theta = theta + 90



f.close()
