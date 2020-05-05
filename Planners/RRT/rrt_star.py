# -*- coding: utf-8 -*-
import math,random,sys,collections,re
from math import *
import time
import json

GeoPoint = collections.namedtuple('GeoPoint', 'latitude, longitude, altitude')
CartesianPoint = collections.namedtuple('CartesianPoint', 'x, y, z')

XDIM = 2000
YDIM = 2000
SIZEX = 8
SIZEY = 8
k = 0.1
WINSIZE = [XDIM, YDIM]
JUMP = 15.0
RADIUS = 20.0
NEIGHBORHOOD = 10.0
NUMNODES = 10000
white = 255, 240, 200
black = 20, 20, 40
red = 255, 0 , 0
green = 15, 220, 182
home = GeoPoint(-12.825397,-50.349937,0)
execution_time = 60


class Node():
	def __init__(self,point,parent,cost):
		self.point = point
		self.parent = parent
		self.cost = cost

	def get_point(self):
		return self.point

	def get_x(self):
		return self.point[0]

	def get_y(self):
		return self.point[1]

	def get_parent(self):
		return self.parent

	def set_parent(self,newparent):
		self.parent = newparent

	def get_cost(self):
		return self.cost

	def set_cost(self,newcost):
		self.cost = newcost





def main():


	graph = True

	param = sys.argv[1:]
	initPoint = [float(param[0]),float(param[1])]
	goalPoint = [float(param[2]),float(param[3])]
	goal = [param[2],param[3]]
	map_file = param[4]

	obstacles = map_json(map_file)

	
	initPoint = to_cartesian(initPoint,home)
	goalPoint = to_cartesian(goalPoint,home)

	
	distx = initPoint[0]
	disty = initPoint[1]
	initPoint  = ((initPoint[0] - distx)/SIZEX,(initPoint[1] - disty)/SIZEY)
	goalPoint = ((goalPoint[0] - distx)/SIZEX,(goalPoint[1] - disty)/SIZEY)


	for j in range(len(obstacles)):
		for l in range(len(obstacles[j])):
			obstacles[j][l] = ((obstacles[j][l][0]-distx)/SIZEX,(obstacles[j][l][1]-disty)/SIZEY)



	init = time.time()
	nodes = []
	initNode = Node(initPoint,None,0)
	bestNode = Node((0,0),None,9999999)
	node1 = Node((0,0),None,dist(initNode.get_point(),(0,0)))
	nodes.append(initNode)

	for i in range(NUMNODES):
		rand = random.random()*2*goalPoint[0]*pos_neg() , random.random()*2*goalPoint[1]*pos_neg()
		nn = nodes[0]
		for p in nodes:
			if dist(p.get_point(),rand) < dist(nn.get_point(),rand):
				nn = p 
				parentNode = p
		newnode = step_from_to(nn.get_point(),rand)
		node1 = Node(newnode,nn,nn.get_cost()+dist(newnode,nn.get_point()))
		for t in nodes:
			if dist(t.get_point(),node1.get_point())<NEIGHBORHOOD and node1.get_cost()>t.get_cost()+dist(node1.get_point(),t.get_point()):
				node1.set_parent(t)
				node1.set_cost(t.get_cost()+dist(node1.get_point(),t.get_point()))
		colision = 0
		for q in range(len(obstacles)):
			if not_colides(node1.get_x(),node1.get_y(),obstacles[q]):
				colision = colision+1
		if colision >= len(obstacles):
			nodes.append(node1)
			if dist(node1.get_point(),goalPoint) < 10.0:
				if node1.get_cost()<bestNode.get_cost():
					bestNode = node1
		colision = 0



		fim = time.time()
		if dist(bestNode.get_point(),goalPoint) < 10.0 and (fim-init)>execution_time:
			linhas = []
			arquivo = open('/home/vannini/drone_arch/Data/route.txt','w')
			text = "           lng            lat            alt\n"
			arquivo.write(text)
			
			while bestNode.get_parent() != None:
				point = CartesianPoint((bestNode.get_x()*SIZEX)+distx,(bestNode.get_y()*SIZEY)+disty,15)
				point = to_geo_point(point,home)
				text = str(round(point[1],9))+"  "+str(round(point[0],9))+"         15.000\n"
				linhas.append(text)
				result = []
				result.append(point)
				bestNode = bestNode.get_parent()
				aux = bestNode.get_parent()
			text = goal[0]+"  "+goal[1]+"         15.000\n"
			linhas.append(text)

			for w in range(1,len(linhas)):
				arquivo.write(linhas[len(linhas)-1-w])
				

			text = str(result[0][0])+"  "+str(result[0][1])+"         15.000"
			arquivo.close()
		
			break
		elif((fim-init)>execution_time):
			print("Caminho não encontrado")
			break



#Distance between two points
def dist(p1,p2):
	return sqrt((p1[0]-p2[0])*(p1[0]-p2[0])+(p1[1]-p2[1])*(p1[1]-p2[1]))


#jump from p1 to p2
def step_from_to(p1,p2):
	if dist(p1,p2) < JUMP:
		return p2
	else:
		theta = atan2(p2[1]-p1[1],p2[0]-p1[0])
		return p1[0] + JUMP*cos(theta), p1[1] + JUMP*sin(theta)

#converts string to cartesian point
def string_to_cart(text):
	vetor = []
	vetor = text.split(',')
	point = []
	point.append(float(vetor[0]))
	point.append(float(vetor[1]))
	return point



#creates fix obstacles for testing
def obstacles_fix():
	points = [(200,10), (200,210), (240,210), (240,10)]
	return points

#verify if the route colides with the obstacle
def not_colides(x,y,poly):

	n = len(poly)
	inside = True

	p1x,p1y = poly[0]
	for i in range(n+1):
		p2x,p2y = poly[i % n]
		if y > min(p1y,p2y):
			if y <= max(p1y,p2y):
				if x <= max(p1x,p2x):
					if p1y != p2y:
						xints = (y-p1y)*(p2x-p1x)/(p2y-p1y)+p1x
					if p1x == p2x or x <= xints:
						inside = not inside
		p1x,p1y = p2x,p2y
	return inside



#converts geograpfic to cartesian
def to_cartesian(geo_point, home):
	x = calc_x(geo_point[1], home[1], home[0])
	y = calc_y(geo_point[0], home[0])

	return (x,y)

#calculates y
def calc_y(lat, lat_):
	return (lat - lat_) * (10000000.0 / 90)

#calculates x
def calc_x(longi, longi_, lat_):
	pi = math.pi
	return (longi - longi_) * (6400000.0 * (math.cos(lat_ * pi / 180) * 2 * pi / 360)) # ToDo: verificar math

#converts cartesian to geographic
def to_geo_point(cartesian_point, home):
	longitude_x = calc_longitude_x(home.latitude, home.longitude, cartesian_point.x)
	latitude_y = calc_latitude_y(home.latitude, cartesian_point.y)

	return GeoPoint(latitude_y, longitude_x, cartesian_point.z)

#calculates latitude
def calc_latitude_y(lat_, y):
	return ((y * 90) / 10000000.0) + lat_

#calculates longitude
def calc_longitude_x(lat_, longi_, x):
	return ((x * 90) / (10008000 * math.cos(lat_ * math.pi / 180))) + longi_


#returns 1 or -1
def pos_neg():
	aleatorio = random.random()
	if(aleatorio>=0.5):
		return 1
	else:
		return -1

#reads json map file
def map_json(map_file):
	mapa = open(map_file,'r')
	content = mapa.read()
	y = json.loads(content)
	coordinates = []
	x = json.dumps(y[0]["areas_nao_navegaveis"])
	z = json.loads(x)

	for i in range(len(z)):
		coordinates.append(z[i]["geo_points"])

	for l in range(len(coordinates)):
		for m in range(len(coordinates[l])):
			aux = coordinates[l][m][0]
			coordinates[l][m][0] = coordinates[l][m][1]
			coordinates[l][m][1] = aux
			del(coordinates[l][m][2])
			coordinates[l][m] = to_cartesian(coordinates[l][m],home)

	mapa.close()
	return coordinates

	transformada

 

if __name__ == '__main__':
	main()
	