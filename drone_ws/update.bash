#echo "Cancel atual plan"
#rosservice call /rosplan_plan_dispatcher/cancel_dispatch

echo "Cancel atual plan & Update Goal"
#python3 add_1_goal.py
python3 add_1_goal_at_move.py 1

echo "Generating a Problem"
rosservice call /rosplan_problem_interface/problem_generation_server

echo "Planning"
rosservice call /rosplan_planner_interface/planning_server

echo "Executing the Plan"
rosservice call /rosplan_parsing_interface/parse_plan
rosservice call /rosplan_plan_dispatcher/dispatch_plan
