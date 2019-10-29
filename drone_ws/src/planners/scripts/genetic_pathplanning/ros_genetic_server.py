import argparse

from visualization import plot_map
from genetic_v2_3 import Subject, Genetic 
from data_definitions import Mapa, CartesianPoint, Conversor, GeoPoint

from file_manipulation import read_mapa, write_mavros, upload_mapa

## ENTRADA

# Parâmetros recebidos (arquivo .srv)
origin_lat       = -22.002237
origin_long      = -47.932546
origin_alt       = 13
destination_lat  = -22.002674 
destination_long = -47.932608
destination_alt  = 15

# P0
# destination_lat  = -22.002278 
# destination_long = -47.932099
# destination_alt  = 20

# P1
# destination_lat  = -22.002396 
# destination_long = -47.932063
# destination_alt  = 20


mapa_id          = 0

# Leitura do arquvio em DATA
PATH = 'XXX/mapa.json' #Ubuntu Veronica
#PATH = '/home/gustavosouza/Documents/Per/path-planning/data/mapa.json' #Ubuntu Gustavo
#PATH = r'../../data' #Windows
geo_home, _, _, areas_n = read_mapa(PATH, mapa_id)
#geo_home, _, _, areas_n = upload_mapa(mapa_file, mapa_id)

cart_origin      = Conversor.geo_to_cart(GeoPoint(origin_lat, origin_long, origin_alt), geo_home)
cart_destination = Conversor.geo_to_cart(GeoPoint(destination_lat, destination_long, destination_alt), geo_home)


mapa = Mapa(cart_origin, cart_destination, areas_n, inflation_rate=1)



## EXECUÇÃO DO AG
ag_teste = Genetic(Subject, mapa,
        # Genetic
        taxa_cross=0.5,
        population_size=100,
        C_d=1000,
        C_obs=10000,
        C_con=1000,
        C_cur=1000,
        C_t=10,
        max_exec_time=300,
        #Subject,
        T_min=10,
        T_max=40,
        mutation_prob=0.7,
        px0=cart_origin.x,
        py0=cart_origin.y
)


best = ag_teste.run(info=True)

# Melhor rota encontrada : WPs em cartesiano
cart_points = best.get_route()

# Melhor rota encontrada : WPs em geográfico
geo_points = [ Conversor.cart_to_geo(CartesianPoint(cart_point[0], cart_point[1]), geo_home) for cart_point in cart_points ]


# Visualização do Mapa usado, com a rota do melhor de todos
areas = [ area for area in itertools.chain(mapa.areas_n, mapa.areas_n_inf) ]
tipos = [ 'n' for _ in range(len(areas))]
plot_map(
    areas=areas,            # Mapa usado
    labels=tipos,            # Tipo do mapa {'n','p','b'} <- Não afeta o genético, só muda a visualização
    origem=mapa.origin,      # waypoint de origem
    destino=mapa.destination, # waypoint de destino
    waypoints=best.get_route(), # rota do melhor de todos
)

## SAÍDA

output_filename = '/mnt/c/Projetos/path-planning/algorithms/ros_genetic/path_from_ga_output.wp'
write_mavros(output_filename, geo_points)


























# Versão do Algoritmo Genético: v2_3

import rospy
from planners.srv import *

import argparse


from genetic_v2_3 import Subject, Genetic
from data_definitions import Mapa, CartesianPoint, Conversor, GeoPoint

from file_manipulation import read_mapa, write_mavros

## Interface Gráfica
from visualization import plot_map
## /Interface Gráfica

def run_genetic(req):
    ## ENTRADA

    ## Parâmetros recebidos (arquivo .srv)
    origin_lat       = req.origin_lat
    origin_long      = req.origin_long
    origin_alt       = req.origin_alt
    destination_lat  = req.destination_lat
    destination_long = req.destination_long
    destination_alt  = req.destination_alt
    map_id           = req.map_id

    print(origin_lat)


    # Leitura do arquvio em DATA
    PATH = '/home/vannini/Data/mapa.json' #Ubuntu Veronica
    #PATH = '/home/gustavosouza/Documents/Per/path-planning/data/mapa.json' #Ubuntu Gustavo
    #PATH = r'../../data' #Windows
    geo_home, _, _, areas_n = read_mapa(PATH, mapa_id)
    #geo_home, _, _, areas_n = upload_mapa(mapa_file, mapa_id)

    cart_origin      = Conversor.geo_to_cart(GeoPoint(origin_lat, origin_long, origin_alt), geo_home)
    cart_destination = Conversor.geo_to_cart(GeoPoint(destination_lat, destination_long, destination_alt), geo_home)


    mapa = Mapa(cart_origin, cart_destination, areas_n, inflation_rate=1)



    ## EXECUÇÃO DO AG
    ag_teste = Genetic(Subject, mapa,
            # Genetic
            taxa_cross=0.5,
            population_size=100,
            C_d=1000,
            C_obs=10000,
            C_con=1000,
            C_cur=1000,
            C_t=10,
            max_exec_time=300,
            #Subject,
            T_min=10,
            T_max=40,
            mutation_prob=0.7,
            px0=cart_origin.x,
            py0=cart_origin.y
    )


    best = ag_teste.run(info=True)

    # Melhor rota encontrada : WPs em cartesiano
    cart_points = best.get_route()

    # Melhor rota encontrada : WPs em geográfico
    geo_points = [ Conversor.cart_to_geo(CartesianPoint(cart_point[0], cart_point[1]), geo_home) for cart_point in cart_points ]


    # Visualização do Mapa usado, com a rota do melhor de todos
    areas = [ area for area in itertools.chain(mapa.areas_n, mapa.areas_n_inf) ]
    tipos = [ 'n' for _ in range(len(areas))]
    plot_map(
        areas=areas,            # Mapa usado
        labels=tipos,            # Tipo do mapa {'n','p','b'} <- Não afeta o genético, só muda a visualização
        origem=mapa.origin,      # waypoint de origem
        destino=mapa.destination, # waypoint de destino
        waypoints=best.get_route(), # rota do melhor de todos
    )

    ## SAÍDA

    ## /Interface Gráfica

    # output_filename = '/mnt/c/Projetos/path-planning/algorithms/ros_genetic/path_from_ga_output.wp' # Ubuntu Gustavo    
    output_filename = '/home/vannini/Missions/path_from_ga_output.wp' # Ubuntu Veronica
    write_mavros(output_filename, geo_points)


    return GA_PlannerResponse(output_filename)


def genetic_server():
    rospy.init_node('genetic_server')
    s = rospy.Service('genetic', GA_Planner, run_genetic)
    print ("Running Genetic Algorithm to Path-Planning")
    rospy.spin()


if __name__ == "__main__":

    # parser = argparse.ArgumentParser(description='Execute Genetic Algorithm to Path-Planning')
    # parser.add_argument('integers', metavar='N', type=int, nargs='+',
    #                     help='an integer for the accumulator')
    # parser.add_argument('--sum', dest='accumulate', action='store_const',
    #                     const=sum, default=max,
    #                     help='sum the integers (default: find the max)')

    # args = parser.parse_args()

    genetic_server()