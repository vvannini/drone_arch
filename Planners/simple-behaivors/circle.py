#!/usr/bin/env python

import sys
import rospy
import math
import json
import time
import pyproj
from shapely.geometry import Point, mapping
from functools import partial
from shapely.ops import transform
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


args = sys.argv
# print(args)

#point = Point(-90.0667, 29.9500)
point = Point(float(args[1]), float(args[2]))
alt = float(args[3])
local_azimuthal_projection = f"+proj=aeqd +R=6371000 +units=m +lat_0={point.y} +lon_0={point.x}"

wgs84_to_aeqd = partial(
    pyproj.transform,
    pyproj.Proj('+proj=longlat +datum=WGS84 +no_defs'),
    pyproj.Proj(local_azimuthal_projection),
)

aeqd_to_wgs84 = partial(
    pyproj.transform,
    pyproj.Proj(local_azimuthal_projection),
    pyproj.Proj('+proj=longlat +datum=WGS84 +no_defs'),
)

point_transformed = transform(wgs84_to_aeqd, point)

buffer = point_transformed.buffer(10_000)

buffer_wgs84 = transform(aeqd_to_wgs84, buffer)

# print(buffer_wgs84)
coord = mapping(buffer_wgs84)
#print(coord)

#Create polygon from lists of points
x = []
y = []

some_poly = buffer_wgs84
# Extract the point values that define the perimeter of the polygon
x, y = some_poly.exterior.coords.xy

f = open("/home/vannini/drone_arch/Data/route.txt", "w")


for i, j in zip(x, y):
    f.write(str(i)+" "+str(j)+" "+ str(alt)+"\n")

f.close()
