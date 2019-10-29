
import argparse

import matplotlib.pyplot as plt 

from data_definitions import Mapa, CartesianPoint, Conversor, GeoPoint



## obstacle (ICMC)
#-22.006045,-47.896007
#-22.006045,-47.895561
#-22.007557, -47.895561
#-22.007557,,-47.896007

icmc = [GeoPoint(-22.006045, -47.896673, 10), GeoPoint(-22.005787, -47.896007,10), GeoPoint(-22.007557, -47.893121,10), GeoPoint(-22.007861, -47.895561,10)]

icmc1 = [GeoPoint(-22.006045,-47.896007, 10), GeoPoint(-22.006045,-47.895561,10), GeoPoint(-22.007557, -47.895561,10), GeoPoint(-22.007557,-47.896007,10)]


point_in_obstacle = GeoPoint(-22.007252, -47.894711, 10)

point_out_obstacle = GeoPoint(-22.006484, -47.896854, 10)

geo_home = GeoPoint(-22.006218, -47.898374,0)
cart_icmc = []
for point in icmc:
    cart_icmc.append(Conversor.geo_to_cart(point, geo_home))

print(cart_icmc)

cart_in = Conversor.geo_to_cart(point_in_obstacle, geo_home)
cart_out = Conversor.geo_to_cart(point_out_obstacle, geo_home)


# x axis values 
x = [cart_icmc[0].x, cart_icmc[1].x, cart_icmc[2].x, cart_icmc[3].x,cart_icmc[0].x] 
# corresponding y axis values 
y = [cart_icmc[0].y, cart_icmc[1].y, cart_icmc[2].y, cart_icmc[3].y, cart_icmc[0].y] 
  
print(x)
print(y)
# plotting the points  
plt.plot(x,y) 

plt.scatter(cart_in.x, cart_in.y, label= "stars", color= "blue",  
            marker= "*", s=30) 
plt.scatter(cart_out.x, cart_out.y, label= "stars", color= "green",  
            marker= "*", s=30) 
  
# naming the x axis 
plt.xlabel('x - axis') 
# naming the y axis 
plt.ylabel('y - axis') 
  
# giving a title to my graph 
plt.title('My first graph!') 
  
# function to show the plot 
plt.show()

