echo "Setting Rate"
# rosservice call /mavros/set_stream_rate 0 10 1

# echo "Cleaning Route and out files"
# rm ../Data/out.txt
# rm ../Data/route.txt

echo "Update KB"
python3.6 src/data/scripts/create_pddl_global.py $1
# python3 src/data/scripts/create_pddl.py $1

echo "Generating a Problem"
rosservice call /rosplan_problem_interface/problem_generation_server

echo "Planning"
rosservice call /rosplan_planner_interface/planning_server

echo "Predicting"
python3.6 src/data/scripts/pred.py $1



#sudo apt-get install libc6-dev-i386 g++-multilib