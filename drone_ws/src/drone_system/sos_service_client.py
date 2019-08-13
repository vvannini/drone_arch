#! /usr/bin/env python

import rospy
from std_srvs.srv import Trigger, TriggerRequest

# init a node as usual
rospy.init_node('sos_service_client')

# wait for this sevice to be running
rospy.wait_for_service('/fake_911')

# Create the connection to the service. Remember it's a Trigger service
sos_service = rospy.ServiceProxy('/fake_911', Trigger)

# Create an object of the type TriggerRequest. We nned a TriggerRequest for a Trigger service
sos = TriggerRequest('a')

# Now send the request through the connection
result = sos_service(sos)

# Done
print (result)