import os
import json
import matplotlib
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter


PATH = '~/drone_arch/Data/' #set 
PATH = os.path.expanduser(PATH)

def read_log(fn):
	try:
		with open(fn, "r") as log_file:
			log_file = json.load(log_file)

		domains = []
		missions = []
		qtd_types = []
		planners = []
		qtd_goals = []

		for step in log_file:
			if step['domain'] not in domains: domains.append(step['domain'])
			if step['planner'] not in planners: planners.append(step['planner'])
			if step['id_mission'] not in missions: missions.append(step['id_mission'])
			if step['total_goals'] not in qtd_goals: qtd_goals.append(step['total_goals'])
			if step['qtd_types_goals'] not in qtd_types: qtd_types.append(step['qtd_types_goals'])


		return domains, missions, qtd_types, planners, qtd_goals, log_file

	except IOError:
	  
	  print("Log file does not appear to exist.")
	  
	  return []

def plot_test():
	x = np.arange(4)
	money = [1.5e5, 2.5e6, 5.5e6, 2.0e7]


	def millions(x, pos):
	    'The two args are the value and tick position'
	    return '$%1.1fM' % (x * 1e-6)


	formatter = FuncFormatter(millions)

	fig, ax = plt.subplots()
	ax.yaxis.set_major_formatter(formatter)
	plt.bar(x, money)
	plt.xticks(x, ('Bill', 'Fred', 'Mary', 'Sue'))
	plt.show()


	# n_groups = 4
	# means_frank = [90, 55, 40, 65]
	# means_guido = [85, 62, 54, 20]

	# # create plot
	# fig, ax = plt.subplots()
	# index = np.arange(n_groups)
	# bar_width = 0.35
	# opacity = 0.8

	# rects1 = plt.bar(index, means_frank, bar_width,
	# alpha=opacity,
	# color=np.random.rand(3,),
	# label='Frank')

	# rects2 = plt.bar(index + bar_width, means_guido, bar_width,
	# alpha=opacity,
	# color=np.random.rand(3,),
	# label='Guido')

	# plt.xlabel('Person')
	# plt.ylabel('Scores')
	# plt.title('Scores by person')
	# plt.xticks(index + bar_width, ('A', 'B', 'C', 'D'))
	# plt.legend()

	# plt.tight_layout()
	# plt.show()

'''

'''
def plot(log, domains, qtd_goals):
	n_groups = len(qtd_goals)

	listoflist = []

	for i in range(0, len(domains)):
		values = []
		for step in log:
			if step['domain'] == domains[i]:
				# values.append((step['id_mission'],step['cpu_time']))
				values.append(step['cpu_time'])
		listoflist.append(values)

	str_list = []
	for i in qtd_goals:
		str_list.append(str(i))

	# create plot
	fig, ax = plt.subplots()
	index = np.arange(n_groups)
	bar_width = 0.35
	opacity = 0.8

	for i in range(0, len(domains)):
		plt.bar(index + (i*bar_width), listoflist[i], bar_width,
		alpha=opacity,
		color=np.random.rand(4,),
		label=domains[i])

	plt.xlabel('total_goals')
	plt.ylabel('cpu_time')
	plt.title('CPU time per quantity of goals')
	plt.xticks(index + bar_width, qtd_goals)
	plt.legend()

	plt.tight_layout()
	plt.show()

'''
parameters:
	log 		-> json file 
	series 		-> list what you going to compare
	serie 		-> name of the series
	x_values 	-> list of values
	y_group		-> name of what you want to grup (ex by misson or by type of missions)
	y_name 		-> name of y values

	x_label
	y_label
	title

'''
def plot_general(log, series, serie, x_values, y_group, y_name, x_label, y_label, title, exclude_name, exclude_value):
	n_groups = len(x_values)

	listoflist = []

	for i in range(0, len(series)):
		# using dic to make a sparce list (You can judge me, I don't care, it works)
		values = {}
		count = {}
		for step in log:
			if step[serie] == series[i] and exclude_name != '':
				if step[exclude_name] != exclude_value:
					try:
						values[step[y_group]] +=  step[y_name]
						count[step[y_group]] += 1
					except Exception as e:
						values[step[y_group]] =  step[y_name]
						count[step[y_group]] = 1
					
		tuple_list = []
		list_value = []

		# making a list of average
		for i in values:
			tuple_list.append((i, (values[i]/count[i])))

		# getting just value sorted
		for i in sorted(tuple_list, key = lambda x: x[0]): list_value.append(i[1])

		# adding to list
		listoflist.append(list_value)


	str_list = []
	for i in x_values:
		str_list.append(str(i))

	# create plot
	fig, ax = plt.subplots()
	index = np.arange(n_groups)
	bar_width = 1/len(series)
	opacity = 1

	for i in range(0, len(series)):
		rects = plt.bar(index + (i*bar_width), listoflist[i], bar_width,
		alpha=opacity,
		color=np.random.rand(4,),
		label=series[i])
		for rect in rects:
			# print(rect)
			height = rect.get_height()
			ax.annotate('{:.0f}'.format(height),
						xy=(rect.get_x() + rect.get_width() / 2, height),
						xytext=(0, 3),  # 3 points vertical offset
						textcoords="offset points",
						ha='center', va='bottom')
		

	plt.xlabel(x_label)
	plt.ylabel(y_label)
	plt.title(title)
	plt.xticks(index + bar_width, x_values)
	plt.legend()
	

	plt.tight_layout()
	plt.show()
	
	


def plot_fails(log, domains):
	x = np.arange(len(domains))
	errors = {}

	for d in domains:
		errors[d] = 0

	for step in log:
		if step['sucsses'] == 0:
			errors[step['domain']] += 1
	errors_list = []

	# print(errors)

	for step in errors:
		# print(errors[step])
		errors_list.append(errors[step])

	# print(errors_list)

	fig, ax = plt.subplots()
	# ax.yaxis.set_major_formatter(formatter)
	plt.bar(domains, errors_list)
	plt.title('Quantity of plan failure ')
	# plt.ylabel(errors_list)
	# plt.xticks(x, domains)
	plt.show()

log_path = PATH + "mission_log.json"
domains, missions, qtd_types, planners, qtd_goals, log_file = read_log(log_path)


#			log 	 , series , serie   , x_values, y_group     , y_name    , x_label    ,  y_label   , title          , exclude name,     exclude value
# plot_general(log_file, domains, 'domain', missions, 'id_mission', 'cpu_time', 'id_mission', 'cpu_time', 'CPU time per Mission')
# plot_general(log_file, domains, 'domain', missions, 'id_mission', 'total_time', 'id_mission', 'total_time', 'Total Time per Mission')
# plot_general(log_file, domains, 'domain', missions, 'id_mission', 'total_distance', 'id_mission', 'total_distance', 'Total Distance per Mission')


# plot_general(log_file, domains, 'domain', qtd_goals, 'total_goals', 'total_distance', 'total_goals', 'total_distance', 'Total Distance per quantity of goals')


# plot_general(log_file, domains, 'domain', qtd_types, 'qtd_types_goals', 'total_distance', 'qtd_goals_types', 'total_distance', 'Total Distance per quantity of goals types')



# plot_general(log_file, domains, 'domain', qtd_goals, 'total_goals', 'cpu_time', 'total_goals', 'cpu_time', 'CPU time per quantity of goals', 'sucsses', 0)
plot_general(log_file, domains, 'domain', qtd_goals, 'total_goals', 'total_time', 'total_goals', 'total_time', 'Total mission time per quantity of goals', 'sucsses', 0)
# plot_general(log_file, domains, 'domain', qtd_types, 'qtd_types_goals', 'cpu_time', 'qtd_goals_types', 'cpu_time', 'CPU time per quantity of goals types', 'sucsses', 0)
# plot_general(log_file, domains, 'domain', qtd_types, 'qtd_types_goals', 'total_time', 'qtd_goals_types', 'total_time', 'Total mission time per quantity of goals types', 'sucsses', 0)


# plot_fails(log_file, domains)
# print(domains, missions, qtd_types, planners, qtd_goals)

# data to plot