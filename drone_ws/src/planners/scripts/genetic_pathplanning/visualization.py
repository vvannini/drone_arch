import pandas as pd
import numpy as np

import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.path import Path

import plotly.graph_objects as go

from sklearn import preprocessing


cor_area_nn    = '#25283d' # Yankees Blue
cor_area_p     = '#8f3985' # Plum
cor_area_b     = '#07beb8' # Tiffany Blue
cor_waypoints  = '#aa1155' # Jazzberry Jam
cor_ori_dest   = '#880044' # Pink Raspberry


def new_shape(vertices, color='navajowhite', lw=.25):

    assert len(vertices) >= 3, 'At least 3 vertices to form a shape'

    colors = {
        'n': cor_area_nn,
        'p': cor_area_p,
        'b': cor_area_b
    }
    
    color = colors[color] if color in colors else color
    
    codes = [Path.MOVETO]
    for _ in range(1, len(vertices)-1):
        codes.append(Path.LINETO)
    codes.append(Path.CLOSEPOLY)
    
    path = Path(vertices, codes)

    patch = patches.PathPatch(path, facecolor=color, lw=lw, alpha=0.6)

    return patch


def plot_map(wp_style='-x', **kwargs):
    # Optional arguments:
    # areas, labels, origem, destino, waypoints, texts

    fig, ax = plt.subplots(figsize=(8,8)) 
    
    # Plot areas
    if 'areas' in kwargs and 'labels' in kwargs:
        areas=kwargs['areas']
        labels=kwargs['labels']
        patches = [ new_shape(vertice, color=label) for vertice, label in zip(areas, labels) ]
        for patch in patches:
            ax.add_patch(patch)
        
    # Plot origin and destination
    if 'origem' in kwargs and 'destino' in kwargs:
        origem=kwargs['origem']
        destino=kwargs['destino']
        ax.plot([origem[0], destino[0]], [origem[1], destino[1]], 'o', color=cor_ori_dest)
    
    # Plot waypoints and route
    if 'waypoints' in kwargs:
        waypoints=kwargs['waypoints']
        waypoints = list(map(list, zip(*waypoints)))
        ax.plot(waypoints[0], waypoints[1], wp_style, color=cor_waypoints, linewidth=2) #marker='x', linestyle='solid'
        if 'texts' in kwargs:
            for i, text in enumerate(kwargs['texts']):
                ax.annotate(text, (waypoints[0][i], waypoints[1][i]))
    
    if 'stress' in kwargs:
        # Plot waypoints
        if 'points' in kwargs:
            for wp, text in zip(kwargs['points'], kwargs['texts']):
                if text=='T':
                    in_color=cor_area_b
                else:
                    in_color=cor_waypoints

                ax.plot(wp[0], wp[1], wp_style, color=in_color, linewidth=2)
                ax.annotate(text, (wp[0], wp[1]))

        # Plot segments
        if 'segments' in kwargs and 'texts' in kwargs:
            i=0
            for segment, text in zip(kwargs['segments'], kwargs['texts']):
                X = [segment[0].x, segment[1].x]
                Y = [segment[0].y, segment[1].y]

                if text=='T':
                    in_color=cor_area_b
                else:
                    in_color=cor_waypoints
                ax.plot(X, Y, wp_style, color=in_color, linewidth=2)
                ax.annotate(text+str(i), (X[0], Y[0]))
                i+=1
    
    # Set size
    automin, automax = ax.get_xlim()
    plt.xlim(automin-0.5, automax+0.5)
    automin, automax = ax.get_ylim()
    plt.ylim(automin-0.5, automax+0.5)
    plt.gca().set_aspect('equal', adjustable='box')
    
    if 'title' in kwargs:
        plt.title(kwargs['title'])
    
    #plt.show()
    plt.savefig('out.png')

def plot_stats(ag_trace, normalize=True):
    dft = pd.DataFrame.from_dict(ag_trace)
    
    if normalize:
        x = dft.values #returns a numpy array
        min_max_scaler = preprocessing.MinMaxScaler()
        x_scaled = min_max_scaler.fit_transform(x)
        dft = pd.DataFrame(x_scaled, columns=dft.columns)

    fig = go.Figure()

    for column in dft.columns:
        fig.add_trace(go.Scatter(x=dft.index, y=dft[column], name=column))
    fig.show()