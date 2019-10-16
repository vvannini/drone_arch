// Generated by gencpp from file planners/JarPlannersRequest.msg
// DO NOT EDIT!


#ifndef PLANNERS_MESSAGE_JARPLANNERSREQUEST_H
#define PLANNERS_MESSAGE_JARPLANNERSREQUEST_H


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
struct JarPlannersRequest_
{
  typedef JarPlannersRequest_<ContainerAllocator> Type;

  JarPlannersRequest_()
    : pedido(0)  {
    }
  JarPlannersRequest_(const ContainerAllocator& _alloc)
    : pedido(0)  {
  (void)_alloc;
    }



   typedef int64_t _pedido_type;
  _pedido_type pedido;





  typedef boost::shared_ptr< ::planners::JarPlannersRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::planners::JarPlannersRequest_<ContainerAllocator> const> ConstPtr;

}; // struct JarPlannersRequest_

typedef ::planners::JarPlannersRequest_<std::allocator<void> > JarPlannersRequest;

typedef boost::shared_ptr< ::planners::JarPlannersRequest > JarPlannersRequestPtr;
typedef boost::shared_ptr< ::planners::JarPlannersRequest const> JarPlannersRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::planners::JarPlannersRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::planners::JarPlannersRequest_<ContainerAllocator> >::stream(s, "", v);
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
struct IsFixedSize< ::planners::JarPlannersRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::planners::JarPlannersRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::planners::JarPlannersRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::planners::JarPlannersRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::planners::JarPlannersRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::planners::JarPlannersRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::planners::JarPlannersRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "7aec8105d110d548b7a1ac63cf67345b";
  }

  static const char* value(const ::planners::JarPlannersRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x7aec8105d110d548ULL;
  static const uint64_t static_value2 = 0xb7a1ac63cf67345bULL;
};

template<class ContainerAllocator>
struct DataType< ::planners::JarPlannersRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "planners/JarPlannersRequest";
  }

  static const char* value(const ::planners::JarPlannersRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::planners::JarPlannersRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "int64 pedido\n"
;
  }

  static const char* value(const ::planners::JarPlannersRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::planners::JarPlannersRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.pedido);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER
  }; // struct JarPlannersRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::planners::JarPlannersRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::planners::JarPlannersRequest_<ContainerAllocator>& v)
  {
    s << indent << "pedido: ";
    Printer<int64_t>::stream(s, indent + "  ", v.pedido);
  }
};

} // namespace message_operations
} // namespace ros

#endif // PLANNERS_MESSAGE_JARPLANNERSREQUEST_H
