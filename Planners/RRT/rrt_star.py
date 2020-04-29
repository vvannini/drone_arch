# -*- coding: utf-8 -*-
import math,random,sys,collections,re
#import argparse
from math import *
import pygame
import time
import json

GeoPoint = collections.namedtuple('GeoPoint', 'latitude, longitude, altitude')
CartesianPoint = collections.namedtuple('CartesianPoint', 'x, y, z')

XDIM = 1000
YDIM = 1000
WINSIZE = [XDIM, YDIM]
#JUMP = 7.0
JUMP = 15.0
RADIUS = 20.0
NEIGHBORHOOD = 10.0
NUMNODES = 10000
white = 255, 240, 200
black = 20, 20, 40
red = 255, 0 , 0
green = 15, 220, 182
home = GeoPoint(-12.825397,-50.349937,0)
#home = GeoPoint(-22.002467,-47.932949,0)
execution_time = 5
#home = GeoPoint(-22.002178,-47.932588,847.142652)


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

#Distance between two points
def dist(p1,p2):
	return sqrt((p1[0]-p2[0])*(p1[0]-p2[0])+(p1[1]-p2[1])*(p1[1]-p2[1]))


#Function that 
def step_from_to(p1,p2):
	if dist(p1,p2) < JUMP:
		return p2
	else:
		theta = atan2(p2[1]-p1[1],p2[0]-p1[0])
		return p1[0] + JUMP*cos(theta), p1[1] + JUMP*sin(theta)



def main():

	graph = True

	param = sys.argv[1:]
	initPoint = [float(param[0]),float(param[1])]
	goalPoint = [float(param[2]),float(param[3])]
	map_file = param[4]

	obstacles = map_json(map_file)
	print("obstaculos:\n ")
	print(obstacles)

	
	initPoint = to_cartesian(initPoint,home)
	goalPoint = to_cartesian(goalPoint,home)

	distx = min([initPoint[0],goalPoint[0]])-(XDIM/2)
	disty = min([initPoint[1],goalPoint[1]])-(YDIM/2)
		
	
	initPoint  = (initPoint[0]/4 - distx,initPoint[1]/4 - disty)
	
	goalPoint = (goalPoint[0]/4 - distx,goalPoint[1]/4 - disty)
	
	for j in range(len(obstacles)):
		for l in range(len(obstacles[j])):
			obstacles[j][l] = (obstacles[j][l][0]/4 - distx,obstacles[j][l][1]/4 - disty)
		print obstacles[j]


	print("inicio "+str(initPoint[0])+" , "+str(initPoint[1]))
	print("final "+str(goalPoint[0])+" , "+str(goalPoint[1]))

	if graph:
		pygame.init()
		screen = pygame.display.set_mode(WINSIZE)
		pygame.display.set_caption('RRT')
		screen.fill(black)
		pygame.draw.circle(screen,red,(int(initPoint[0]),int(initPoint[1])),2,2)
		pygame.draw.circle(screen,red,(int(goalPoint[0]),int(goalPoint[1])),int(RADIUS),int(RADIUS))
		for i in range(len(obstacles)):
			#print "obstacle = ",obstacles[i]
			pygame.draw.polygon(screen,green,obstacles[i])
		pygame.display.update()

	init = time.time()
	nodes = []
	initNode = Node(initPoint,None,0)
	bestNode = Node((0,0),None,9999999)
	node1 = Node((0,0),None,dist(initNode.get_point(),(0,0)))
	nodes.append(initNode)

	for i in range(NUMNODES):
		rand = random.random()*XDIM , random.random()*(YDIM)
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
				'''node1 = Node(newnode,nn)
				nodes.append(node1)
				if graph:
					pygame.draw.line(screen,white,nn.get_point(),newnode)
					pygame.display.update()'''
		if colision >= len(obstacles):
			'''node1 = Node(newnode,nn,nn.get_cost()+dist(newnode,nn.get_point()))
			for t in nodes:
				if dist(t.get_point(),node1.get_point())<NEIGHBORHOOD and node1.get_cost()>t.get_cost()+dist(node1.get_point(),t.get_point()):
					node1.set_parent(t)
					node1.set_cost(t.get_cost()+dist(node1.get_point(),t.get_point()))
			'''
			nodes.append(node1)
			if dist(node1.get_point(),goalPoint) < 10.0:
				if node1.get_cost()<bestNode.get_cost():
					bestNode = node1
					print("custo: "+str(bestNode.get_cost()))

			if graph:
				pygame.draw.line(screen,white,nn.get_point(),newnode)
				pygame.display.update()
		colision = 0

		if graph:
			for e in pygame.event.get():
				if e.type == pygame.QUIT: #or (e.type == KEYUP and e.key == K_ESCAPE):
					sys.exit("Leaving because you requested it.")

		fim = time.time()
		if dist(bestNode.get_point(),goalPoint) < 10.0 and (fim-init)>execution_time:
			print("custo ideal: "+str(dist(initPoint,goalPoint)))
			print("custo real: "+str(bestNode.get_cost()))
			print("tempo de execucao: "+str(fim-init))
			arquivo = open('/home/vannini/drone_arch/Data/route.txt','w')
			text = "           lng            lat            alt\n"
			arquivo.write(text)
			print(text)
			while bestNode.get_parent() != None:
				point = CartesianPoint((bestNode.get_x() + distx)*4,(bestNode.get_y() + disty)*4,15)
				point = to_geo_point(point,home)
				text = str(round(point[1],9))+"  "+str(round(point[0],9))+"         15.000\n"
				print(text)
				arquivo.write(text)
				result = []
				result.append(point)
				#print node1.get_x()+distx,",",node1.get_y()+disty,"\n"
				#print point[0]," , ",point[1]
				bestNode = bestNode.get_parent()
				aux = bestNode.get_parent()
				if graph:
					if aux != None:
						pygame.draw.line(screen,[255, 0 , 0],bestNode.get_point(),aux.get_point())
						pygame.display.update()
			if graph:
				time.sleep(3)

			text = str(result[0][0])+"  "+str(result[0][1])+"         15.000"
			arquivo.close
			#create_route(result)
			break


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
	#if not inside:
		#print "colidiu"
	return inside

#verifys if the point colides with the obstacle
'''def not_colides(p,obstacles):
	cross = 0
	for i in range(len(obstacles)):
		for l in range(len(obstacles[i])):
			n = l+1
			if l == 3:
				n = 1
			a = obstacles[i][l]
			b = obstacles[i][n]
			r = (a[0]-b[0])*(p[1]-b[1])-(p[0]-b[0])*(a[1]-b[1])+a[0]*b[1]-a[1]-b[0]

			if a[1]>b[1]:
				aux = a
				a = b
				b = aux
			if r>0:
				cross = cross+1
		if cross >= 4:
			#print "colidiu no ponto: ",p
			return False
	return True'''


#converts geograpfic to cartesian
def to_cartesian(geo_point, home):
	x = calc_x(geo_point[1], home[1], home[0])
	y = calc_y(geo_point[0], home[0])

	#return CartesianPoint(x, y, geo_point.altitude)
	return (x,y)

def calc_y(lat, lat_):
	return (lat - lat_) * (10000000.0 / 90)


def calc_x(longi, longi_, lat_):
	pi = math.pi
	return (longi - longi_) * (6400000.0 * (math.cos(lat_ * pi / 180) * 2 * pi / 360)) # ToDo: verificar math

def to_geo_point(cartesian_point, home):
	longitude_x = calc_longitude_x(home.latitude, home.longitude, cartesian_point.x)
	latitude_y = calc_latitude_y(home.latitude, cartesian_point.y)

	return GeoPoint(latitude_y, longitude_x, cartesian_point.z)

def calc_latitude_y(lat_, y):
	return ((y * 90) / 10000000.0) + lat_


def calc_longitude_x(lat_, longi_, x):
	return ((x * 90) / (10008000 * math.cos(lat_ * math.pi / 180))) + longi_


def read_mission(mission_file):
	mission = open(mission_file,'r')
	coordinates =[]
	for line in mission:
		words = line.strip()
		words = words.replace('"',"")
		words = words.split(":")
		if words[0] == "geo_origin":
			words[1] = words[1].replace('[','')
			words[1] = words[1].replace(']','')
			words[1] = words[1].replace(' ','')
			words[1] = words[1].rstrip()
			point = words[1].split(",")
			point[0] = float(point[0])
			point[1] = float(point[1])
			del(point[3])
			del(point[2])
			coordinates.append(point)
		elif words[0]  == "geo_destination":
			words[1] = words[1].replace('[','')
			words[1] = words[1].replace(']','')
			words[1] = words[1].replace(' ','')
			words[1] = words[1].rstrip()
			point = words[1].split(",")
			del(point[2])
			point[0] = float(point[0])
			point[1] = float(point[1])
			coordinates.append(point)

	mission.close()
	return coordinates    #[initPoint, goalPoint]


def read_map(map_file):
	mapa = open(map_file,'r')
	coordinates = []
	obst_id = 1
	home = 0
	polygon = []
	mission_lines = mapa.readlines()
	for i in range(len(mission_lines)):
		if re.search('\\bgeo_home\\b', mission_lines[i], re.IGNORECASE):
			home = mission_lines[i]
			home = home.strip()
			home = home.replace('"','')
			home = home.replace("geo_home:",'')
			home = home.replace('[','')
			home = home.replace(']','')
			home = home.split(',')
			home[0] = float(home[0])
			home[1] = float(home[1])
			home[2] = float(home[2])
			del(home[3])
			#coordinates.append(home)
		'''parsed_json = json.loads(mission_lines[i])
		print parsed_json
		if parsed_json == "geo_home":
			home = json.loads(parsed_json)
			print "home é: ",home'''
		mission_lines[i] = mission_lines[i].strip()
		mission_lines[i] = mission_lines[i].replace('"','')
		#print mission_lines[i]
	for i in range(len(mission_lines)):
			#coordinates.append('p')
		if mission_lines[i] == "geo_points:[":
			#coordinates.append(obst_id)
			obst_id = obst_id+1
			i = i+1
			while mission_lines[i] != "]":
				line = mission_lines[i].replace('[','')
				line = line.replace(']','')
				line = line.split(',')
				line[0] = float(line[0])
				line[1] = float(line[1])
				line[2] = float(line[2])
				#del(line[3])
				line = to_cartesian(line,home)
				polygon.append(line)
				if len(line) == 4:
					del(line[3])
				i=i+1
			coordinates.append(polygon)
			polygon = []
		#if mission_lines[i] == "areas_nao_navegaveis:[":
			#coordinates.append('n')
	#print coordinates[0]


	mapa.close()
	return coordinates

def map_json(map_file):
	mapa = open(map_file,'r')
	content = mapa.read()
	y = json.loads(content)
	coordinates = []
	x = json.dumps(y[0]["areas_nao_navegaveis"])
	z = json.loads(x)

	for i in range(len(z)):
		coordinates.append(z[i]["geo_points"])
		'''aux = z[i]["geo_points"]
		print("coordenadas:::")
		print(aux)
		aux1 = [aux[i][1],aux[i][0]]
		print(aux1)
		coordinates.append(aux1)'''

	for l in range(len(coordinates)):
		for m in range(len(coordinates[l])):
			aux = coordinates[l][m][0]
			coordinates[l][m][0] = coordinates[l][m][1]
			coordinates[l][m][1] = aux
			#aux = (coordinates[l][m][1],coordinates[l][m][0])
			del(coordinates[l][m][2])
			coordinates[l][m] = to_cartesian(coordinates[l][m],home)

	#print(coordinates)
	mapa.close()
	return coordinates


def create_route(result):
	arquivo = open('/home/vannini/drone_arch/Data/route.txt','w')
	text = "           lng            lat            alt"
	arquivo.write(text)
	text = str(result[0][0])+"  "+str(result[0][1])+"         15.000"
	arquivo.close
	return
 

if __name__ == '__main__':
	main()
	