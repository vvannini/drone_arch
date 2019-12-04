// Generated by gencpp from file planners/GA_PlannerRequest.msg
// DO NOT EDIT!


#ifndef PLANNERS_MESSAGE_GA_PLANNERREQUEST_H
#define PLANNERS_MESSAGE_GA_PLANNERREQUEST_H


#include <string>
#include <vector>
#include <map>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>


namespace planners
{
template <class ContainerAllocator>
struct GA_PlannerRequest_
{
  typedef GA_PlannerRequest_<ContainerAllocator> Type;

  GA_PlannerRequest_()
    : origin_lat(0.0)
    , origin_long(0.0)
    , origin_alt(0.0)
    , destination_lat(0.0)
    , destination_long(0.0)
    , destination_alt(0.0)
    , map_id(0)  {
    }
  GA_PlannerRequest_(const ContainerAllocator& _alloc)
    : origin_lat(0.0)
    , origin_long(0.0)
    , origin_alt(0.0)
    , destination_lat(0.0)
    , destination_long(0.0)
    , destination_alt(0.0)
    , map_id(0)  {
  (void)_alloc;
    }



   typedef float _origin_lat_type;
  _origin_lat_type origin_lat;

   typedef float _origin_long_type;
  _origin_long_type origin_long;

   typedef float _origin_alt_type;
  _origin_alt_type origin_alt;

   typedef float _destination_lat_type;
  _destination_lat_type destination_lat;

   typedef float _destination_long_type;
  _destination_long_type destination_long;

   typedef float _destination_alt_type;
  _destination_alt_type destination_alt;

   typedef int32_t _map_id_type;
  _map_id_type map_id;





  typedef boost::shared_ptr< ::planners::GA_PlannerRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::planners::GA_PlannerRequest_<ContainerAllocator> const> ConstPtr;

}; // struct GA_PlannerRequest_

typedef ::planners::GA_PlannerRequest_<std::allocator<void> > GA_PlannerRequest;

typedef boost::shared_ptr< ::planners::GA_PlannerRequest > GA_PlannerRequestPtr;
typedef boost::shared_ptr< ::planners::GA_PlannerRequest const> GA_PlannerRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::planners::GA_PlannerRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::planners::GA_PlannerRequest_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace planners

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': True, 'IsMessage': True, 'HasHeader': False}
// {'geographic_msgs': ['/opt/ros/melodic/share/geographic_msgs/cmake/../msg'], 'sensor_msgs': ['/opt/ros/melodic/share/sensor_msgs/cmake/../msg'], 'std_msgs': ['/opt/ros/melodic/share/std_msgs/cmake/../msg'], 'mavros_msgs': ['/opt/ros/melodic/share/mavros_msgs/cmake/../msg'], 'geometry_msgs': ['/opt/ros/melodic/share/geometry_msgs/cmake/../msg'], 'uuid_msgs': ['/opt/ros/melodic/share/uuid_msgs/cmake/../msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::planners::GA_PlannerRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::planners::GA_PlannerRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::planners::GA_PlannerRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::planners::GA_PlannerRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::planners::GA_PlannerRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::planners::GA_PlannerRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::planners::GA_PlannerRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "26ab780b1ffed608535599842d9b65b1";
  }

  static const char* value(const ::planners::GA_PlannerRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x26ab780b1ffed608ULL;
  static const uint64_t static_value2 = 0x535599842d9b65b1ULL;
};

template<class ContainerAllocator>
struct DataType< ::planners::GA_PlannerRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "planners/GA_PlannerRequest";
  }

  static const char* value(const ::planners::GA_PlannerRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::planners::GA_PlannerRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "float32 origin_lat\n"
"float32 origin_long\n"
"float32 origin_alt\n"
"float32 destination_lat\n"
"float32 destination_long\n"
"float32 destination_alt\n"
"int32 map_id\n"
;
  }

  static const char* value(const ::planners::GA_PlannerRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::planners::GA_PlannerRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.origin_lat);
      stream.next(m.origin_long);
      stream.next(m.origin_alt);
      stream.next(m.destination_lat);
      stream.next(m.destination_long);
      stream.next(m.destination_alt);
      stream.next(m.map_id);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER
  }; // struct GA_PlannerRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::planners::GA_PlannerRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::planners::GA_PlannerRequest_<ContainerAllocator>& v)
  {
    s << indent << "origin_lat: ";
    Printer<float>::stream(s, indent + "  ", v.origin_lat);
    s << indent << "origin_long: ";
    Printer<float>::stream(s, indent + "  ", v.origin_long);
    s << indent << "origin_alt: ";
    Printer<float>::stream(s, indent + "  ", v.origin_alt);
    s << indent << "destination_lat: ";
    Printer<float>::stream(s, indent + "  ", v.destination_lat);
    s << indent << "destination_long: ";
    Printer<float>::stream(s, indent + "  ", v.destination_long);
    s << indent << "destination_alt: ";
    Printer<float>::stream(s, indent + "  ", v.destination_alt);
    s << indent << "map_id: ";
    Printer<int32_t>::stream(s, indent + "  ", v.map_id);
  }
};

} // namespace message_operations
} // namespace ros

#endif // PLANNERS_MESSAGE_GA_PLANNERREQUEST_H
