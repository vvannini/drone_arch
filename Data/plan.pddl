Number of literals: 61
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
54% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 9.000
b (8.000 | 8.000)b (7.000 | 8.000)b (5.000 | 8.000)b (4.000 | 408.000)b (3.000 | 772.145)b (2.000 | 865.146)b (1.000 | 1129.288);;;; Solution Found
; States evaluated: 15
; Cost: 1129.288
; Time 0.00
0.000: (recharge-input rover1 input1 base1)  [8.000]
0.000: (clean-camera rover1 base1 camera1)  [8.000]
0.000: (recharge-battery rover1 base1)  [5.000]
8.000: (go-to rover1 base1 base2)  [400.000]
408.001: (go-to rover1 base2 region1)  [357.143]
765.145: (take_image rover1 region1_photo region1 camera1)  [7.000]
765.146: (pulverize-region rover1 input1 region1 orange_objective1 camera1)  [100.000]
772.145: (go-to rover1 region1 base2)  [357.143]
