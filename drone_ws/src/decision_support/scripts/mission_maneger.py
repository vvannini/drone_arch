#!/usr/bin/env python
import rospy
import sys
import json
import re
from mavros_msgs.srv import CommandBool
from mavros_msgs.srv import CommandTOL
from mavros_msgs.srv import SetMode
#from data_definitions import Mission
from sensor_msgs.msg import Imu, NavSatFix
from mavros_msgs.msg import State, ExtendedState
from std_msgs.msg import Float64
from execute_genetic import run_genetic


#run_genetic(origin_lat=-50.5555, origin_long=24.555,  ..., map_id=0 )
#continua essa lista com os seguintes argumentos: origin_lat, origin_long, origin_alt, destination_lat, destination_long, destination_alt, map_id=0



regions = {
    "region1": [-12.806830, -50.348215, 10],
    "region2" : [22,11],
    "region3" : [33,11],
    "region4" : [44,11],
    "base1" : [-12.800309, -50.342277, 10],
    "base2" : [66,11],
    "base3" : [77,11]
}

objectives = {
    "orange_objective1": [88,11],
    "orange_objective2" : [99,11],
    "orange_objective3" : [10,11],
    "green_objective1": [11,11],
    "green_objective2" : [12,11],
    "green_objective3" : [13,11],
    "blue_objective1": [14,11],
    "blue_objective2" : [15,11],
    "purple_objective3" : [16,11],
}

status = 1
altitude = 0
wp_altitude = 10
latitude_gps = ''
longitude_gps = ''
altitude_gps = ''
callback_gps_f = 0

def callback(data):
    #rospy.loginfo(rospy.get_caller_id() + "\nmode: [{}]\nstatus:[{}]".
    #format(data.landed_state, data.vtol_state))
    global status
    status = data.landed_state

def callback_IMU(data):
    global altitude
    altitude = data
    #print(altitude)

def callback_gps(data):
    global callback_gps_f
    global latitude_gps 
    global longitude_gps
    global altitude_gps
    latitude_gps = data.latitude
    longitude_gps= data.longitude
    altitude_gps = data.altitude
    callback_gps_f = 1





def setGuidedMode():
   rospy.wait_for_service('/mavros/set_mode')
   try:
       flightModeService = rospy.ServiceProxy('/mavros/set_mode', SetMode)
       isModeChanged = flightModeService(custom_mode='GUIDED') #return true or false
   except rospy.ServiceException as e:
       print ("service set_mode call failed: %s. GUIDED Mode could not be set. Check that GPS is enabled"%e)

def setStabilizeMode():
   rospy.wait_for_service('/mavros/set_mode')
   try:
       flightModeService = rospy.ServiceProxy('/mavros/set_mode', SetMode)
       isModeChanged = flightModeService(custom_mode='STABILIZE') #return true or false
   except rospy.ServiceException as e:
       print ("service set_mode call failed: %s. GUIDED Mode could not be set. Check that GPS is enabled"%e)

def setArm():
   rospy.wait_for_service('/mavros/cmd/arming')
   try:
       armService = rospy.ServiceProxy('/mavros/cmd/arming', CommandBool)
       armService(True)
   except rospy.ServiceException as e:
       print( "Service arm call failed: %s"%e)

def setDisarm():
   rospy.wait_for_service('/mavros/cmd/arming')
   try:
       armService = rospy.ServiceProxy('/mavros/cmd/arming', CommandBool)
       armService(False)
   except rospy.ServiceException as e:
       print( "Service arm call failed: %s"%e)

def setTakeoffMode():
   rospy.wait_for_service('/mavros/cmd/takeoff')
   r = rospy.Rate(5)
   try:
       takeoffService = rospy.ServiceProxy('/mavros/cmd/takeoff', CommandTOL)
       setDisarm()
       r.sleep()
       setArm()
       if(callback_gps_f):
          takeoffService(altitude = altitude_gps+10, latitude = latitude_gps, longitude = longitude_gps)
   except rospy.ServiceException as e:
       print( "Service takeoff call failed: %s"%e)

        

def setLandMode():
   rospy.wait_for_service('/mavros/cmd/land')
   try:
       landService = rospy.ServiceProxy('/mavros/cmd/land', CommandTOL)
       #http://wiki.ros.org/mavros/CustomModes for custom modes
       isLanding = landService(altitude = 0, latitude = 0, longitude = 0, min_pitch = 0, yaw = 0)
   except rospy.ServiceException as e:
       print( "service land call failed: %s. The vehicle cannot land "%e)



def goto_AG(command):
    while(status != 2):
        setArm()
        setTakeoffMode()
    #run_genetic(origin_lat=regions[command[3]][1], origin_long=regions[command[3]][2], origin_alt= regions[command[3]][3], destination_lat= regions[command[4]][1], destination_long=regions[command[4]][2], destination_atl=regions[command[4]][3] map_id=0 )
#continua essa lista com os seguintes argumentos: origin_lat, origin_long, origin_alt, destination_lat, destination_long, destination_alt, map_id=0
    print("goto "+str(regions[command[3]][1])+ " "+str(regions[command[4]]))

def discharge(command):
    while(status != 1 and status!=4):
        setLandMode()
        print("while discharge")
    if(status == 1):
        setDisarm()
    print("discharge")

def recharge(command):
    while(status != 1 and status != 4):
        setLandMode()
        print("while recharge")
    if(status == 1):
        setDisarm()
    print("recharge")

def clean_camera(command):
    while(status != 1 and status != 4):
        setLandMode()
        print("while clean_camera")
    if(status == 1):
        setDisarm()
    print("clean_camera")

def do_pattern(command):
    while(status != 2):
        setArm()
        setTakeoffMode()
    print("do_pattern (pulverize-region)"+str(objectives[command[5]]))

def call_fill(command):
    while(status != 2):
        setArm()
        setTakeoffMode(10)
    print("call_fill"+str(regions[command[4]]))

def recharge_battery(command):
    while(status != 1 and status != 4):
        setLandMode()
        print("while recharge_battery")
    if(status == 1):
        setDisarm()
    print("recharge_battery")

commands = {
    "go-to": goto_AG,
    "recharge-input": recharge,
    "discharge-input": discharge,
    "clean-camera": clean_camera,
    "pulverize-region": do_pattern,
    "take_image": call_fill,
    "recharge-battery": recharge_battery
}



def main():
    filepath = "/home/vannini/drone_arch/Data/plan.pddl"
    with open(filepath) as fp:
       line = fp.readline()
       cnt = 1
       while line:
            if 'States evaluated' in line:
                print(line)
            if 'Cost' in line:
                print(line)
            if 'Time' in line:
                print(line)
            for x in commands:
                if x in line:
                    #print(x)
                    res = re.sub('[^a-zA-Z0-9 _\n\.]', '', line)
                    words = res.split()
                    commands[x](words)


           #print("Line {}: {}".format(cnt, line.strip()))
            line = fp.readline()
            cnt += 1


def listener():
    rospy.init_node('listener', anonymous=True)
    rospy.Subscriber("/mavros/extended_state", ExtendedState, callback)
    rospy.Subscriber("/mavros/global_position/rel_alt", Float64, callback_IMU)
    rospy.Subscriber("/mavros/global_position/global", NavSatFix, callback_gps)
    #r = rospy.Rate(10)
    #while not rospy.is_shutdown():
    main()
    #setTakeoffMode()
    #    r.sleep()
    rospy.spin()


if __name__ == '__main__':
    listener()

    

#if __name__ == '__main__':
#    main()
