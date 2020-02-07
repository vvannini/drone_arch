import json
import re
from data_definitions import Mission

# READ
# ______________________________________________

regions = {
    "region1": [11,11],
    "region2" : [22,11],
    "region3" : [33,11],
    "region4" : [44,11],
    "base1" : [55,11],
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


def goto_AG(command):
    print("goto "+str(regions[command[3]])+ " "+str(regions[command[4]]))

def discharge(command):
    print("discharge")

def recharge(command):
    print("recharge")

def clean_camera(command):
    print("clean_camera")

def do_pattern(command):

    print("do_pattern (pulverize-region)"+str(objectives[command[5]]))

def call_fill(command):
    print("call_fill"+str(regions[command[4]]))

def recharge_battery(command):
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


def create_init(problem_file_name):
    string = ""
    with open(problem_file_name) as json_file:
        problem_file = json.load(json_file)

        #print(problem_file)
        for p in problem_file['init']:
            string = string + " " + ["(rover "+ rover +") " for rover in p['rover']]
        string = string + " " + ["(payload "+ payload +") " for payload in problem_file['init']['payload']]
        string = string + " " + ["(region "+ region +") " for region in problem_file['init']['region']]
        string = string + " " + ["(base "+ base +") " for base in problem_file['init']['base']]
        string = string + " " + ["(objective "+ objective +") " for objective in problem_file['init']['objective']]
        string = string + " " + ["(is-recharging-dock "+ recharging + ") " for recharging in problem_file['init']['is-recharging-dock']]

        print(string)

    return string

    
def read_mapa(mapa_filename, mapa_id):
    
    with open(mapa_filename, 'r') as mapa_file:
        mapa_file = json.load(mapa_file)
        mapa = mapa_file[mapa_id]

        geo_home = GeoPoint(mapa['geo_home'][1], mapa['geo_home'][0], mapa['geo_home'][2])
  
        areas_bonificadoras  = [ Conversor.list_geo_to_cart(area['geo_points'], geo_home) for area in mapa['areas_bonificadoras'] ]
        areas_penalizadoras  = [ Conversor.list_geo_to_cart(area['geo_points'], geo_home) for area in mapa['areas_penalizadoras'] ]
        
        areas_nao_navegaveis = []
        for area in mapa['areas_nao_navegaveis']:
            geo_points=[]
            for geo_point in area['geo_points']:
                geo_points.append(Conversor.geo_to_cart(
                    GeoPoint(geo_point[1], geo_point[0], geo_point[2])
                    , geo_home))
            geo_points.append(geo_points[0])
            areas_nao_navegaveis.append(geo_points)



    return geo_home, areas_bonificadoras, areas_penalizadoras, areas_nao_navegaveis


# WRITE
# ______________________________________________

def write_problem(filename, objects, init, goal): #throws FileNotFoundException
    print('Writing problem file...\nMake sure the filename given has the .pddl extension')
    count = 1

    with open(filename, 'w+') as file:
        current_waypoint = 1

        file.write('(define (problem P'+str(problem_number)+') \n') # Determines the file version
        file.write('(:domain rover-domain)\n')
        
        file.write('(:objects \n')
        for obj in objects:
            file.write(obj+' ')
        file.write(' \n )')


        file.write('(:init \n')
        for i in init:
            file.write(i +' \n')
        file.write(' \n )')



        file.write('(:goal \n')
        file.write('\t (and \n')
        for g in goal:
            file.write(g+' \n')
        file.write(' \n ))')

        file.write('(:metric minimize (total-time) ) \n )')

            
    print('Output file generated: {}'.format(filename))

#string = create_init('/home/vannini/drone_arch/Data/problema-pddl.json')

#print('OLAR')
#print(string)

if __name__ == '__main__':
    main()
