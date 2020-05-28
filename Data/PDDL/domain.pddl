(define (domain farm)
	(:requirements 
		 :typing :durative-actions :fluents :duration-inequalities :strips :disjunctive-preconditions :action-costs
	)
	(:types 
		rover - object
		region - object
		base - region
	)
	(:functions 
		(battery-amount ?rover - rover)
		(input-amount ?rover - rover)
		(recharge-rate-battery ?rover - rover)
		(recharge-rate-input ?rover - rover)
		(discharge-rate-battery ?rover - rover)
		(battery-capacity ?rover - rover)
		(input-capacity ?rover - rover)
		(distance ?from-region - region ?to-region - region)
		(velocity ?rover - rover)
		(total-goals)
		(goals-achived)
	)
	(:predicates 
		(been-at ?rover - rover ?region - region)
		(carry ?rover - rover)
		(at ?rover - rover ?region - region)
		(is-recharging-dock ?region - region)
		(can-spray ?rover - rover)
		(can-recharge ?rover - rover)
		(taken-image ?region - region)
		(pulverized ?region - region)
		(can-go ?rover - rover)
		(can-take-pic ?rover - rover)
		(its-not-base ?region - region)
		(can-go-to-base ?rover - rover)
		(has-pulverize-goal)
		(has-picture-goal)
		(at-move)
	)
	;actions 
	(:durative-action clean_camera
		:parameters
			(?rover - rover
			?region - base
			)
		:duration
			(= duration 8)
		:condition
			(and
				(over all (at rover region))
			)
		:effect
			(and
				(at start (can-take-pic rover))
			)
	)
	(:durative-action go_to
		:parameters
			(?rover - rover
			?from-region - region
			?to-region - region
			)
		:duration
			(= duration (/ (distance ?from-region ?to-region) (velocity ?rover)))
		:condition
			(and
				(over all (its-not-base to-region))
				(over all (can-go rover))
				(at start (at rover from-region))
				(at start (hw-ready rover))
			)
		:effect
			(and
				(not (at end (at-move rover)))
			)
	)


)