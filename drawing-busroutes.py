import sys
import matplotlib.pyplot as plt
import networkx as nx
from networkx import *
import subprocess

"""
@author William Wallace
@date 09/06/2020
"""

G=nx.Graph()
if len(sys.argv) != 2:
    print("Error: must have two arguments - program and file")
    exit()

filename = (sys.argv[1])
p1 = subprocess.run("java BusRoutes " + filename, shell = True, capture_output = True, text = True)

if p1.returncode == 1:
    print(p1.stdout)
    exit(1)

output = p1.stdout.strip()
shortest = output.split("-")

file = open(filename)
with file as f:
    next(f)
    lines = [line.lower() for line in file]
    for line in lines:
        parts = line.split(",")  
        parts = list(map(str.strip, parts))
        # print(parts)    
        if not G.has_node(parts[0]):
            G.add_node(parts[0])
        if not G.has_node(parts[1]):
            G.add_node(parts[1])
        G.add_edge(parts[0], parts[1], weight=parts[2], alpha=0.5)                    
   

color_map = []
# print(shortest)
for node in G.nodes():
    # print("Node name:" +node)
    if node in shortest:
        # print("Shortest:" + node)
        color_map.append('orange')                                                                  
    else: 
        color_map.append('blue')


pos = nx.fruchterman_reingold_layout(G)

# source: https://stackoverflow.com/questions/34120957/python-networkx-mark-edges-by-coloring-for-graph-drawing                                            
for e in G.edges():
    G[e[0]][e[1]]['color'] = 'blue'
    G[e[0]][e[1]]['style'] = 'dashed'
    # nx.draw_networkx_edges(G, pos, alpha = 0.5, width = 2.0,)
for i in range(len(shortest)-1):
    G[shortest[i]][shortest[i+1]]['color'] = 'green'     
    G[shortest[i]][shortest[i+1]]['style'] = 'solid'           
    # nx.draw_networkx_edges(G, pos, alpha = 0.5, width = 2.0,)                                                            
edge_color_list = [ G[e[0]][e[1]]['color'] for e in G.edges() ]
edge_style_list = [ G[e[0]][e[1]]['style'] for e in G.edges() ]

# pos = nx.get_node_attributes(G, "pos")
#labels=labeldict
# nx.draw_networkx(H, node_color = "blue", node_size = 500, alpha = 0.5, style = "dashed", edge_color = "blue", width = 2.0)


# print(nx.info(G))
labels = nx.get_edge_attributes(G,'weight')
nx.draw_networkx_edge_labels(G,pos, edge_labels=labels)
nx.draw_networkx_nodes(G, pos, node_color=color_map, node_size = 500, alpha = 0.8, edge_labels=labels)
nx.draw_networkx_labels(G, pos)
nx.draw_networkx_edges(G, pos, style = edge_style_list, edge_color = edge_color_list, width = 2.0,  edge_labels=labels)


plt.title("Possible bus routes")
plt.savefig("Graph.png", format="PNG")
plt.show()



