START_TIME=$(date +%s)
echo "Setting Rate"
rosservice call /mavros/set_stream_rate 0 10 1

echo "Update KB"
python3 src/data/scripts/create_pddl_global.py 19530

echo "Generating a Problem"
rosservice call /rosplan_problem_interface/problem_generation_server

echo "Planning"
rosservice call /rosplan_planner_interface/planning_server

echo "Creating log"
python3 src/data/scripts/create_log.py 19530 0

echo "Executing the Plan"
rosservice call /rosplan_parsing_interface/parse_plan
rosservice call /rosplan_plan_dispatcher/dispatch_plan

# echo "Cancel atual plan & Update Goal"
# python3 replanning/update.py 1 20787

# echo "Generating a Problem"
# rosservice call /rosplan_problem_interface/problem_generation_server

# echo "Planning"
# rosservice call /rosplan_planner_interface/planning_server

# echo "Creating log"
# python3 src/data/scripts/create_log.py 20787 1

# echo "Executing the Plan"
# rosservice call /rosplan_parsing_interface/parse_plan
# timeout 800s rosservice call /rosplan_plan_dispatcher/dispatch_plan

# echo "Cancel atual plan & Update Goal"
# python3 replanning/update.py 1 124

# echo "Generating a Problem"
# rosservice call /rosplan_problem_interface/problem_generation_server

# echo "Planning"
# rosservice call /rosplan_planner_interface/planning_server

# echo "Creating log"
# python3 src/data/scripts/create_log.py 124 1

# echo "Executing the Plan"
# rosservice call /rosplan_parsing_interface/parse_plan
# timeout 800s rosservice call /rosplan_plan_dispatcher/dispatch_plan

START_TIME=$(date +%s)
python3 src/data/scripts/create_log.py $(($END_TIME - $START_TIME)) 2