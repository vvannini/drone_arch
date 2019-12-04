#pip install dataclasses
import collections
import math
from dataclasses import dataclass

@dataclass
class Commands:
    command_type:  int
    geo_points:    collections.namedtuple('GeoPoint', 'latitude, longitude, altitude')
    waypoints:     int


CartesianPoint = collections.namedtuple('CartesianPoint', 'x y')
GeoPoint = collections.namedtuple('GeoPoint', 'latitude, longitude, altitude')


mission = []
mission.append(Commands(1, GeoPoint(1, 1, 10), 2)) 
print(mission)