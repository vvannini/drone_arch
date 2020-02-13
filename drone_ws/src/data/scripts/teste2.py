#!/usr/bin/env python

import rospy

import rosplan_pytools
import rosplan_pytools
import rosplan_pytools.controller.knowledge_base as kb
import rosplan_pytools.controller.scene_database as sdb
import rosplan_pytools.controller.planning_system as ps
  
rospy.init_node("teste")

#rosplan_pytools.init()
kb.initialize("/rosplan_knowledge_base")

print("OLAR")

kb.reset()
  
# Using the KB
kb.add_instance("rover1", "rover")
kb.list_instances()
  
# You can store stuff into the scene database with a third arg
# kb.add_instance("msg1", "msg_type")
# sdb.add_element("msg1", sdb.Element(std_msgs.msg.String("Be sure to drink your ovaltine"), "msg_type"))

# kb.add_goal("robot-at", loc="loc1")
# kb.add_goal("has-received-message", msg="msg1", loc="loc1")
  

# # Then, plan and execute! using PS
# ps.plan()
  
# # Now, let's try stopping it
# time.sleep(2)
# ps.cancel()