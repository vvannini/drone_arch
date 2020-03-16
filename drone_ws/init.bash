echo "Setting Rate"
rosservice call /mavros/set_stream_rate 0 10 1

echo "Update KB"
python3 src/data/scripts/create_pddl.py 

echo "Generating a Problem"
rosservice call /rosplan_problem_interface/problem_generation_server

echo "Planning"
rosservice call /rosplan_planner_interface/planning_server

echo "Executing the Plan"
rosservice call /rosplan_parsing_interface/parse_plan
rosservice call /rosplan_plan_dispatcher/dispatch_plan