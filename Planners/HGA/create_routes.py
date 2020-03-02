#!/usr/bin/env python

import sys
import rospy
import math
import json
import time
import os

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



mapa_filename = PATH + "mapa.json"
mission_filename = PATH + "missao.json"
hw_filename = PATH + "hardware.json"
with open(mission_filename, "r") as mission_file:
        mission_file = json.load(mission_file)
        mission = mission_file[0]

mapa_id = 0

with open(mapa_filename, "r") as mapa_file:
        mapa_file = json.load(mapa_file)
        mapa = mapa_file[mapa_id]


#format_distances(calc_distances(regions_obj), regions_names)
regions_obj, regions_names, labels, geo_home = read_json(mission, mapa)

for i in regions_obj:
    for j in regions_obj:
        if (i.name !=j.name):
            # if(i.name == "base_1" and j.name == "base_2"):
            print(i.name, j.name)
            print(i.geo_center.latitude, i.geo_center.longitude)
            # command = "java -jar hga-interface.jar " + str(i.geo_center.longitude) + " " + str(i.geo_center.latitude )+" 15 "+ str(j.geo_center.longitude) + " "+ str(j.geo_center.latitude )+" 13 0 20 600 5 10 8 0.01 5.0 true 200 \"/home/vannini/drone_arch/Data/mapa.json\" \"/home/vannini/drone_arch/Planners/HGA/pasta-executavel\" \"/home/vannini/drone_arch/Data/Rotas/"+ i.name+"_"+j.name+".txt\" \"java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./\"";
            command = "java -jar hga-interface.jar " + str(i.geo_center.latitude) + " " + str(i.geo_center.longitude )+" 15 "+ str(j.geo_center.latitude) + " "+ str(j.geo_center.longitude )+" 13 0 20 600 5 10 8 0.01 5.0 true 200 \"/home/vannini/drone_arch/Data/mapa.json\" \"/home/vannini/drone_arch/Planners/HGA/pasta-executavel\" \"/home/vannini/drone_arch/Data/Rotas/"+ i.name+"_"+j.name+".txt\" \"java -jar -Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio128/cplex/bin/x86-64_linux hga.jar run job ./\"";
            #print(command)
            os.system(command)
#crete_routes(mapa, mission)
