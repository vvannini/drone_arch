import collections
import re
from pathlib import Path

GeoPoint = collections.namedtuple("GeoPoint", "latitude, longitude, altitude")

def write_mavros(filename, geo_points):  # throws FileNotFoundException
    print("Writing mavros file...\nMake sure the filename given has the .wp extension")
    with open(filename, "w+") as file:
        current_waypoint = 1

        file.write("QGC WPL 120\n")  # Determines the file version

        i = 0

        for geo_point in geo_points:
            file.write(
                str(i)
                + "\t"
                + str(current_waypoint)
                + "\t"
                + "3\t16\t3\t0\t0\t0\t"
                + "{:10.8f}".format(geo_point.longitude)
                + "\t"
                + "{:10.8f}".format(geo_point.latitude)
                + "\t"
                + "{:10.8f}".format(geo_point.altitude)
                + "\t"
                + "1"
                + "\n"
            )

            current_waypoint = 0
            i += 1

    print("Output file generated: {}".format(filename))



def read_txt(filename):
    
    with open(filename, 'r') as file:
        file = file.readlines()
        
        geo_points = []
        
        iterfile = iter(file)
        next(iterfile)
        
        for f in iterfile:
            f = re.sub('\n', '', f)
            f = f.split(' ')
            f = list(filter(None, f))
            
            geo_point = GeoPoint(latitude=float(f[0]), longitude=float(f[1]), altitude=float(f[2]))
            geo_points.append(geo_point)
    
    return geo_points


BASEPATH = '/home/vannini/drone_arch/Data/Rotas/txt/' 
SAVEPATH = '/home/vannini/drone_arch/Data/Rotas/wp/' 

pathlist = Path(BASEPATH).glob("*.txt")

for i, path in enumerate(pathlist):
    
    geo_points = read_txt(str(path))
    
    output_filename = SAVEPATH + path.stem + '.wp'
    write_mavros(output_filename, geo_points)