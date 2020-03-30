#include <ros/ros.h>
#include <vector>

#include <rosplan_action_interface/RPActionInterface.h>

#ifndef KCL_tutorial_10
#define KCL_tutorial_10

/**
 * This file defines an action interface created in tutorial 10.
 */
namespace KCL_rosplan {

	class RPTutorialInterface: public RPActionInterface
	{

	private:

	public:

		/* constructor */
		RPTutorialInterface(ros::NodeHandle &nh);

		/* listen to and process action_dispatch topic */
		bool concreteCallback(const rosplan_dispatch_msgs::ActionDispatch::ConstPtr& msg);
	};
}
#endif