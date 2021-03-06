import json

import genetic_v2_3 as genetic

from data_definitions import Conversor, Mapa, GeoPoint

# READ
# ______________________________________________

def read_mission(missao_filename, missao_id, mapa_filename):
    
    with open(missao_filename, 'r') as missao_file, open(mapa_filename, 'r') as mapa_file:
        #missao_file = json.load(missao_file)[[mapa_id]mapa_id]
        mapa_file   = json.load(mapa_file)

        mapa_id = missao_file[missao_id]['map']

        geo_home = mapa[mapa_id]['geo_home']

        origin      = Conversor.geo_to_cart(missao_file[missao_id]['mission_execution'][0]['instructions']['geo_origin'], geo_home)
        destination = Conversor.geo_to_cart(missao_file[missao_id]['mission_execution'][0]['instructions']['geo_destination'], geo_home)
    
        areas_bonificadoras  = [ Conversor.list_geo_to_cart(mapa_file[mapa_id]['areas_bonificadoras']['geo_points'], geo_home)  ]
        areas_penalizadoras  = [ Conversor.list_geo_to_cart(mapa_file[mapa_id]['areas_penalizadoras']['geo_points'], geo_home)  ]
        areas_nao_navegaveis = [ Conversor.list_geo_to_cart(mapa_file[mapa_id]['areas_nao_navegaveis']['geo_points'], geo_home) ]


    mapa = Mapa(origin, destination, areas_nao_navegaveis)

    return mapa


def upload_mapa(mapa_file, mapa_id):
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

def write_mavros(filename, geo_points): #throws FileNotFoundException
    print('Writing mavros file...\nMake sure the filename given has the .wp extension')
    count = 1

    with open(filename, 'w+') as file:
        current_waypoint = 1

        file.write('QGC WPL 120\n') # Determines the file version

        i = 0

        for geo_point in geo_points:
            file.write(
                str(i) + '\t'
                + str(current_waypoint) + '\t' 
                + '3\t16\t3\t0\t0\t0\t'
                + '{:10.8f}'.format(geo_point.longitude) + '\t' 
                + '{:10.8f}'.format(geo_point.latitude) + '\t'
                + '{:10.8f}'.format(geo_point.altitude) + '\t'
                + '1'
                + '\n'
            )

            current_waypoint = 0
            i+=1

    print('Output file generated: {}'.format(filename))