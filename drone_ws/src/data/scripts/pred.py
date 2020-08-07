#!/usr/bin/env python
#@authors Pedro Natali and Veronica Vannini

# Import libraries
import pyAgrum as gum
import pyAgrum.lib.notebook as gnb
import sys
import rospy
import math
import json
import time
import os
import csv
from std_srvs.srv import Empty
from rosplan_knowledge_msgs.srv import *
from rosplan_knowledge_msgs.msg import *
from std_msgs.msg import String

PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)


# Classes

# Classe responsável pela ações do sistema.

class Actions:
	def __init__(self, typeA,region, time):
		self.typeA = typeA
		self.region = region
		self.time = time 

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

def format_distances(distances, names):
	out = []
	for di, li in zip(distances, names):
		for dj, lj in zip(di, names):
			if(li != lj):
				instance = KnowledgeItem()
				instance.knowledge_type = KnowledgeItem.FUNCTION
				instance.values.append(diagnostic_msgs.msg.KeyValue("region", li))
				instance.values.append(diagnostic_msgs.msg.KeyValue("region", lj))
				instance.function_value = dj
				out.append(instance)
	return out

def get_regions(mapa_json):
	regions = []
	for region in mapa_json["regions"]:
		regions.append(region["name"])
	return regions

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

def get_bases(mapa_json):
	bases = []
	for base in mapa_json["bases"]:
		bases.append(base["name"])
	return bases


def get_objectives(missao_json, command=""):
	obj = []
	for step in missao_json["mission_execution"]:
		if step["command"] == command:
			obj.append(step["instructions"]["area"])
	return obj


def list_to_geopoint(l):
	return GeoPoint(l[0], l[1], l[2])

def read_json(mission, mapa):

	regions = []
	names = []
	labels = []

	geo_home = list_to_geopoint(mapa["geo_home"])

	for region in mapa["regions"]:

		geo_points = [list_to_geopoint(gp) for gp in region["geo_points"]]
		cart_points = [geo_to_cart(gp, geo_home) for gp in geo_points]
		geo_center = list_to_geopoint(region["center"])
		cart_center = geo_to_cart(geo_center, geo_home)

		names.append(region["name"])

		region = Region(
			region["id"], region["name"], geo_points, cart_points, geo_center, cart_center
		)

		regions.append(region)
		labels.append("region")

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

#Calcula a probabilidade de chegar em uma regiao X dado que a bateria restante nela é Y (lembrando que a bateria é inconsistente abaixo de 15%)
def dist_prob(bateria):
    if(0 <= bateria and bateria < 15):
        return 0.20
    elif(15 <= bateria and bateria < 30):
        return 0.50
    elif(30 <= bateria and bateria < 60):
        return 0.75
    elif(60 <= bateria and bateria < 80):
        return 0.85
    elif(80 <= bateria and bateria < 100):
        return 0.95
    else:
        return 0

#Rede Bayesiana definida no sistema que utiliza:
## Bateria inicial, consumo definido à partir da bateria,
## ArrayActions (vetor de objeto da classe ação) e as probabilidades definidas
def bayesian_network(bateria_init, consumo, ArrayActions, prob) :
    bateria = []
    bateria.append(bateria_init)

    # Indica se é a primeira região ou não
    FLAG = 0
    print("\n")
    print("------------------------------------------------------------------")
    print("---------------------- PROBABILITY INFO --------------------------")
    print("------------------------------------------------------------------")
    print("\n")
	
    # Para cada regiao
    for obj in ArrayActions:
        # se a regiao é inicial
        if(obj.typeA == "Recharge Battery"):
        	bateria.append(100)
        	print("Bateria recarregada")
        	print()

        elif(FLAG == 0): 
            #calcula a probabilidade inicial = bateria inicial - consumo = prob(bateria restante) 
            bateria.append(bateria[len(bateria)-1] - consumo*float(obj.time))
            prob.append(dist_prob(bateria[len(bateria) - 1]))
            FLAG = 1
            print("Probabilidade de " + obj.typeA + str(obj.region) + " : " + str(prob[len(prob)-1]))
            print("bateria restante = " + str(bateria[len(bateria)-1]))
            print()

        # senao
        else:
            # calcula a prob e a bateria restante para chegar a proxima regiao
            bateria.append(bateria[len(bateria)-1] - consumo*float(obj.time))
            prob.append(dist_prob(bateria[len(bateria) - 1]))
            print("Probabilidade de " + obj.typeA + str(obj.region) + " : " + str(prob[len(prob)-1]))
            print("bateria restante = " + str(bateria[len(bateria)-1]))
            print()
    return prob

#Essa funcao determina se a ação acontece ou não, se seu valor for 0.1, significa que ações com 0.1 de probabilidade iriam contar como (SIM)
def value(valor):
    if(valor >= 0.5):
        return 1;
    else:
        return 0;



def main():

	#define umas parada
	args = sys.argv
	lines = []
	wanted = []
	actions = []
	ArrayActions = []
	PATH2 = '~/drone_arch/drone_ws/src/data/pddl/plan.pddl'
	arquivo = os.path.expanduser(PATH2)

	with open(arquivo,'r') as file:
		for line in file:
			lines.append(line.rstrip('\n'))

	
	#Aqui é onde o parseamento é feito, cada substring pega no arquivo pddl os termos seguintes à ela em uma linhas
	#dividindo por fim nos modelos apresentados no resultado (Ou PATH em log)
	#Opcao eh transformar isso em funcao (assim nao ficaria tanto codigo repetitivo)

	substr = "go_to"
	substr2 = "take_image"
	substr3 = "recharge_battery"
	substr4 = "pulverize_region"
	substr5 = "go_to_base"
	substr6 = "go_to_picture"

	FLAG_passed = 0
	for line in lines:
		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr5,index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr5)
				index += len(substr5)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1

				string = string+" 4"
				wanted.append(string)

		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr2, index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr2)
				index += len(substr2)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1
				
				string = string+" 1"
				wanted.append(string)

		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr3, index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr3)
				index += len(substr3)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1
				
				string = string+" 2"
				wanted.append(string)

		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr4, index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr4)
				index += len(substr4)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1
				
				string = string+" 3"
				wanted.append(string)

		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr6, index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr6)
				index += len(substr6)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1
				
				string = string+" 6"
				wanted.append(string)

		aux = 0
		index = 0
		prev = 0

		if(FLAG_passed == 0 ):

			while(index < len(line)):
				index = line.find(substr, index)
				if index == -1:
					break
				else:
					FLAG_passed = 1

				prev = index + len(substr)
				index += len(substr)

				string = ''
				while(line[index] != ']'):
					string += line[index]
					index = index + 1
				
				string = string+" 0"
				wanted.append(string)

		FLAG_passed = 0


	# print()
	# print(wanted)
	# print()

	#Aqui é onde as transformações finais são feitas para criar objetos de fato.
	for w in range(len(wanted)):
		actions.append(wanted[w].split())
		action = actions[w]

		if(action[-1] == '0'):
		# 	under = action[0]
		# 	if(under[0] == '_'):
		# 		action[0] = under[1:]

			ArrayActions.append(Actions("Go to", [action[0].rstrip('_'), action[1].rstrip(')')], action[2][1:]))
		elif(action[-1] == '1'):
			ArrayActions.append(Actions("Take Image", [action[0].rstrip(')')], action[1][1:]))
		elif(action[-1] == '2'):
			ArrayActions.append(Actions("Recharge Battery", [action[0].rstrip(')')], action[1][1:]))
		elif(action[-1] == '3'):
			ArrayActions.append(Actions("Pulverize", [action[0].rstrip(')')], action[1][1:]))
		elif(action[-1] == '4'):
			ArrayActions.append(Actions("Go to base", [action[0].rstrip('_'), action[1].rstrip(')')], action[2][1:]))
		elif(action[-1] == '6'):
			ArrayActions.append(Actions("Go to picture", [action[0].rstrip('_'), action[1].rstrip(')')], action[2][1:]))

	# print(actions)
	# print()

	

	print("\n")
	print("------------------------------------------------------------------")
	print("------------------------- PATH INFO ------------------------------")
	print("------------------------------------------------------------------")
	for obj in ArrayActions:
		print(obj.typeA, obj.region, obj.time,  sep = " ")

	#Vero

	#get file names
	mapa_filename = PATH + "mapa.json"
	mission_filename = PATH + "missao.json"
	hw_filename = PATH + "hardware.json"

	#open files
	mission_id = int(args[1])
	with open(mission_filename, "r") as mission_file:
			mission_file = json.load(mission_file)
			mission = mission_file[mission_id]

	mapa_id = 0

	with open(mapa_filename, "r") as mapa_file:
			mapa_file = json.load(mapa_file)
			mapa = mapa_file[mapa_id]

	hw_id = 0
	with open(hw_filename, "r") as hw_file:
			hw_file = json.load(hw_file)
			hardware = hw_file[hw_id]

	print("\n")
	print("------------------------------------------------------------------")
	print("----------------------- HARDWARE INFO ----------------------------")
	print("------------------------------------------------------------------")

	print("Discharge rate battery = "+ str(hardware["discharge-rate-battery"]))
	print("Battery ammount = " + str(100))
	print("Efficient velocity = " + str(hardware["efficient_velocity"]))
	print("Name of the Battery = " + str(hardware["name"]))
			# rostopic echo /rosplan_problem_interface/problem_instance -n 1

	#read objects
	regions_obj, regions_names, labels, geo_home = read_json(mission, mapa)

	regions  = get_regions(mapa)
	base = get_bases(mapa)
	pulverize = get_objectives(mission, command='pulverize')
	photo = get_objectives(mission, command='take_picture')
	end = get_objectives(mission, command='end')
	total_goals = 0


	calc_dist = calc_distances(regions_obj)

	# for calc in calc_dist:
	# 	print(calc)
	# 	print("\n\n")

	distances = format_distances(calc_dist, regions_names)

	#define bateria e consumo inicial
	bateria_init = 100

	#Define qual o tipo de bateria (AQUI SERIAM MUDADOS PARA OUTROS TIPOS DE BATERIA À PARTIR DE OUTROS IF's)
	if(str(hardware["name"]) == "kenny"):
		consumo = hardware["discharge-rate-battery"]    
		recarga = hardware["recharge-rate-battery"]  

    #define os conjuntos de modelos e probabilidades
	prob = []
	model = []
	ie = []

	#flag do sistema
	FLAG = 0

	#Cria um contador para as regioes
	count = 0

	#Pega os valores das probabilidades definidas na rede
	prob = bayesian_network(bateria_init, consumo, ArrayActions, prob)

	print("\n")
	print("------------------------------------------------------------------")
	print("---------------------- PREDICTION INFO ---------------------------")
	print("------------------------------------------------------------------")

	for obj in ArrayActions:

	    if (count == 0):
	        #Criando o modelo
	        modelo = gum.InfluenceDiagram()
	        model.append(modelo)

	        #Criando acoes
	        StringA = 'actions_' + str(count)
	        print(StringA)
	        acao = gum.LabelizedVariable(StringA,'.',2)
	        acao.changeLabel(0,"Stay in " + str(obj.region[0]));
	        acao.changeLabel(1,"Go to " + str(obj.region[1]))
	        model[0].addDecisionNode(acao)

	        #Criando nos de probabilidade
	        StringP = 'region_' + str(count)
	        regiao = gum.LabelizedVariable(StringP,'.', 2)
	        regiao.changeLabel(0,'NO')
	        regiao.changeLabel(1,'YES')
	        model[0].addChanceNode(regiao)

	        #Criando o no de utilidade
	        StringU = 'utility_' + str(count)
	        utilidade = gum.LabelizedVariable(StringU,'.',1)
	        model[0].addUtilityNode(utilidade)

	        #Criando as conexoes
	        model[0].addArc(model[0].idFromName(StringP), model[0].idFromName(StringU))
	        model[0].addArc(model[0].idFromName(StringA), model[0].idFromName(StringU))


	        # Add utilities
	        model[0].utility(model[0].idFromName(StringU)) [{StringA:0, StringP: 0}] = 100
	        model[0].utility(model[0].idFromName(StringU)) [{StringA:0, StringP: 1}] = 0
	        model[0].utility(model[0].idFromName(StringU)) [{StringA:1, StringP: 0}] = 0
	        model[0].utility(model[0].idFromName(StringU)) [{StringA:1, StringP: 1}] = 100


	        # Add CPT:s
	        model[0].cpt(model[0].idFromName(StringP))[0]= abs(1 - prob[0]) # NO
	        model[0].cpt(model[0].idFromName(StringP))[1]= abs(prob[0]) # YES


	        # Create an inference model
	        inference = gum.InfluenceDiagramInference(model[0])
	        ie.append(inference)

	        # Erase all evidence
	        ie[0].eraseAllEvidence()

	        # Erase all evidence and set new evidences --> Evidence set with Bayesian Networks! 
	        ie[0].setEvidence({StringP:value(prob[0])})

	        # Make an inference with evidence
	        ie[0].makeInference()

	        #Printing the results
	        print()
	        print('--- Inference with evidence ---')
	        print(ie[0].displayResult())
	        print('Best decision for actions: {0}'.format(ie[0].getBestDecisionChoice(model[0].idFromName(StringA))))
	        print()

	        #Se precisar ficar na base, não tem porque fazer as próximas contas
	        if(ie[0].getBestDecisionChoice(model[0].idFromName(StringA)) == 0):
	            print()
	            print("\n")
	            print("------------------------------------------------------------------")
	            print("------------------------- REPLANNING -----------------------------")
	            print("------------------------------------------------------------------")
	            print()
	            FLAG = 1
	            return FLAG, ArrayActions
	        else:
	            print()
	            print('--------------------- Atualmente em '+str(count+1)+' ---------------------')

	        count = count + 1
	        print()

	    else:
	        #Criando o modelo
	        modelo = gum.InfluenceDiagram()
	        model.append(modelo)

	        #Criando acoes
	        StringA = 'actions_' + str(count)
	        StringDecisao = obj.typeA + str(obj.region)
	        acao = gum.LabelizedVariable(StringA,'.',2)
	        acao.changeLabel(0,"Replan")
	        acao.changeLabel(1,StringDecisao)
	        model[count].addDecisionNode(acao)

	        #Referenciando as acoes anteriores
	        StringA_ant = 'actions_' + str(count-1)
	        print(StringA_ant)
	        StringDecisao_ant = "Go to " + str(count-1)
	        acao_ant = gum.LabelizedVariable(StringA_ant, '.', 2)
	        acao_ant.changeLabel(0, "Replan")
	        acao_ant.changeLabel(1, StringDecisao_ant)
	        model[count].addChanceNode(acao_ant)

	        #Criando nos de probabilidade
	        StringP = 'region_' + str(count)
	        regiao = gum.LabelizedVariable(StringP,'.', 2)
	        regiao.changeLabel(0,'NO')
	        regiao.changeLabel(1,'YES')
	        model[count].addChanceNode(regiao)

	        #Criando o no de utilidade
	        StringU = 'utility_' + str(count)
	        utilidade = gum.LabelizedVariable(StringU,'.',1)
	        model[count].addUtilityNode(utilidade)

	        #Criando as conexoes
	        model[count].addArc(model[count].idFromName(StringP), model[count].idFromName(StringU))
	        model[count].addArc(model[count].idFromName(StringA), model[count].idFromName(StringU))
	        model[count].addArc(model[count].idFromName(StringA_ant), model[count].idFromName(StringU))

	        for l in range(2): 
	            for k in range(2):
	                for j in range(2):
	                    model[count].utility(model[count].idFromName(StringU)) [{StringA: l, StringA_ant:k, StringP:j}] = 0
	                    if(l == 0 and k == 1 and j == 0 ): 
	                        model[count].utility(model[count].idFromName(StringU)) [{StringA: l, StringA_ant:k, StringP:j}] = 100
	                    elif(l == 1 and k == 1 and j == 1):
	                        model[count].utility(model[count].idFromName(StringU)) [{StringA: l, StringA_ant:k, StringP:j}] = 100

	        # Add CPT:s
	        model[count].cpt(model[count].idFromName(StringP))[0]= abs(1 - prob[count]) # NO
	        model[count].cpt(model[count].idFromName(StringP))[1]= abs(prob[count]) # YES

	        model[count].cpt(model[count].idFromName(StringA_ant))[0]=0.01 # Stay
	        model[count].cpt(model[count].idFromName(StringA_ant))[1]=0.99 # Go


	        inference2 = gum.InfluenceDiagramInference(model[count])
	        ie.append(inference2)

	        # Erase all evidence and set new evidences --> Evidence set with Bayesian Networks! 
	        #Setando como evidencia as acoes que foram encontradas pelos modelos passados
	        ie[count].setEvidence({StringA_ant:ie[count-1].getBestDecisionChoice(model[count-1].idFromName(StringA_ant)), StringP:value(prob[count])})

	        # Make an inference with evidence
	        ie[count].makeInference()

	        #Printing the results
	        print('--- Inference with evidence ---')
	        print(ie[count].displayResult())
	        print('Best decision for actions: {0}'.format(ie[count].getBestDecisionChoice(model[count].idFromName(StringA))))
	        print()

	        #Se precisar ficar na base, não tem porque fazer as próximas contas
	        if(ie[count].getBestDecisionChoice(model[count].idFromName(StringA)) == 0 ):

	            print()
	            print("\n")
	            print("------------------------------------------------------------------")
	            print("------------------------- REPLANNING -----------------------------")
	            print("------------------------------------------------------------------")
	            print()
	            FLAG = 1
	            return FLAG, ArrayActions
	        else:
	            print()
	            print('--------------------- Atualmente em '+str(count+1)+' ---------------------')

	        count = count + 1
	        print()

	return FLAG, ArrayActions
	        


	########### FIM

def get_domain():
	rospy.wait_for_service("/rosplan_knowledge_base/domain/name")
	try:
		domain = rospy.ServiceProxy("/rosplan_knowledge_base/domain/name", GetDomainNameService)
		srv = domain()
		print(srv.domain_name)
		return srv.domain_name
	except rospy.ServiceException as e:
		print("Service Call Failed %s"%e)
		return ""

def write_log(log, log_file):
	with open(log_file, 'w') as outfile:
		json.dump(log, outfile, indent=4)

def parse_file_plan():
	plan_path = '~/drone_arch/drone_ws/src/data/pddl/plan.pddl' #set 
	plan_path = os.path.expanduser(plan_path)
	plan = open(plan_path, 'r')
	lines = plan.readlines()

	sucsses = 0
	cpu_time = float('Inf')
	total_time = 0

	for line in lines:
		if 'Solution Found' in line: sucsses = 1
		if '; Time' in line:
			aux = line.split(' ')
			cpu_time = eval(aux[-1].rstrip())
		if ': (' in line:
			aux = line.split(' ')
			total_time += eval(aux[-1].rstrip())[0]
	return sucsses, cpu_time, total_time


# def get_exe_path(node_name):
# 	ID = '/NODEINFO'
# 	node_api = rosnode.get_api_uri(rospy.get_master(), node_name)
# 	code, msg, pid = client.ServerProxy(node_api[2]).getPid(ID)
# 	p = psutil.Process(pid)
# 	#print(p.name())
# 	return p.name()

def get_total_distance(total_time):
	# rosservice call /rosplan_knowledge_base/state/functions "predicate_name: 'velocity'"
	instance = KnowledgeItem()
	rospy.wait_for_service('/rosplan_knowledge_base/state/functions')
	try:
		print ("Calling Service get_function")
		query_proxy = rospy.ServiceProxy('rosplan_knowledge_base/state/functions', GetAttributeService)
		instance = query_proxy('velocity')
		#instance = instance.attributes[0]
		velocity = instance.attributes[0].function_value
	except rospy.ServiceException as e:
		print ("Service call failed: %s"%e)
		velocity = float(Inf)
	return velocity*total_time



#rosservice call /rosplan_knowledge_base/domain/name

'''
This function should be a subscriver in a topic provide by another node, 
but at the moment I'm to deep in POG (programação orientada a gambiarra), 
so, I'm gonna do the wrong thing and reopen the file
'''
def goals(id_mission):
	mission_filename = PATH + "missao.json"
	mision = {}
	with open(mission_filename, "r") as mission_file:
			mission_file = json.load(mission_file)
			mission = mission_file[mission_id]

	obj = []
	total_goals = 0
	for step in mission["mission_execution"]:
		total_goals += 1
		if step["command"] not in obj: #should exclude goal 'end'?
			obj.append(step["command"])
	return total_goals, len(obj)


#Observação: Os códigos retirados da Verônica não estão comentados pois não quis me arriscar.
if __name__ == '__main__':
	FLAG, ArrayActions = main()
	PATH = '~/drone_arch/Data/' #set 
	PATH = os.path.expanduser(PATH)
	args = sys.argv
	mission_id = int(args[1])
	total_goals, type_qtd = goals(mission_id)
	#Caminho do CSV
	PATH = PATH + 'Noel/out.csv'
	mission_total = ''

	domain = get_domain()

	sucsses, cpu_time, total_time = parse_file_plan()
	total_distance = get_total_distance(total_time)

	#Nota: como nao eh possivel escrever em uma linha especifica com csv, copiamos tudo dele para um array, escrevemos esse array e soh
	# ai eh feita a escrita do novo log. Por isso, deixar ele simulando para muitos casos nao eh muito proveitos (no caso onde ele vai ter que ler milhares de 
	# linhas e escreve-las antes de reescrever uma nova).
	logs_list = []

	with open(PATH, mode='r') as log:
		#Cria um reader
		logs = csv.reader(log, delimiter=',')
		#Guarda tudo em um array
		for row in logs:
			logs_list.append(row)

	with open(PATH, mode='w', newline='') as log_pt:
		#aqui eh feita a escrita repetitiva
		writer = csv.writer(log_pt, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL, lineterminator='\n')

		#e aqui embaixo eh feita a escrita do novo log
		for log in logs_list:
			writer.writerow(log)

		for obj in ArrayActions:
			mission_unit  = str(obj.typeA) + " " + str(obj.region) + " " + str(obj.time) + "\n"
			mission_total = mission_total + mission_unit

		if(FLAG == 1):
			writer.writerow([mission_id, mission_total, 0,domain, total_goals, type_qtd, sucsses, cpu_time, total_time, total_distance])
		else:
			writer.writerow([mission_id, mission_total, 1	,domain, total_goals, type_qtd, sucsses, cpu_time, total_time, total_distance])
