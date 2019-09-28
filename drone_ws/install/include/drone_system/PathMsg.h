// Generated by gencpp from file drone_system/PathMsg.msg
// DO NOT EDIT!


#ifndef DRONE_SYSTEM_MESSAGE_PATHMSG_H
#define DRONE_SYSTEM_MESSAGE_PATHMSG_H

#include <ros/service_traits.h>


#include <drone_system/PathMsgRequest.h>
#include <drone_system/PathMsgResponse.h>


namespace drone_system
{

struct PathMsg
{

typedef PathMsgRequest Request;
typedef PathMsgResponse Response;
Request request;
Response response;

typedef Request RequestType;
typedef Response ResponseType;

}; // struct PathMsg
} // namespace drone_system


namespace ros
{
namespace service_traits
{


template<>
struct MD5Sum< ::drone_system::PathMsg > {
  static const char* value()
  {
    return "f16097f93022db785b2cc9436c158893";
  }

  static const char* value(const ::drone_system::PathMsg&) { return value(); }
};

template<>
struct DataType< ::drone_system::PathMsg > {
  static const char* value()
  {
    return "drone_system/PathMsg";
  }

  static const char* value(const ::drone_system::PathMsg&) { return value(); }
};


// service_traits::MD5Sum< ::drone_system::PathMsgRequest> should match 
// service_traits::MD5Sum< ::drone_system::PathMsg > 
template<>
struct MD5Sum< ::drone_system::PathMsgRequest>
{
  static const char* value()
  {
    return MD5Sum< ::drone_system::PathMsg >::value();
  }
  static const char* value(const ::drone_system::PathMsgRequest&)
  {
    return value();
  }
};

// service_traits::DataType< ::drone_system::PathMsgRequest> should match 
// service_traits::DataType< ::drone_system::PathMsg > 
template<>
struct DataType< ::drone_system::PathMsgRequest>
{
  static const char* value()
  {
    return DataType< ::drone_system::PathMsg >::value();
  }
  static const char* value(const ::drone_system::PathMsgRequest&)
  {
    return value();
  }
};

// service_traits::MD5Sum< ::drone_system::PathMsgResponse> should match 
// service_traits::MD5Sum< ::drone_system::PathMsg > 
template<>
struct MD5Sum< ::drone_system::PathMsgResponse>
{
  static const char* value()
  {
    return MD5Sum< ::drone_system::PathMsg >::value();
  }
  static const char* value(const ::drone_system::PathMsgResponse&)
  {
    return value();
  }
};

// service_traits::DataType< ::drone_system::PathMsgResponse> should match 
// service_traits::DataType< ::drone_system::PathMsg > 
template<>
struct DataType< ::drone_system::PathMsgResponse>
{
  static const char* value()
  {
    return DataType< ::drone_system::PathMsg >::value();
  }
  static const char* value(const ::drone_system::PathMsgResponse&)
  {
    return value();
  }
};

} // namespace service_traits
} // namespace ros

#endif // DRONE_SYSTEM_MESSAGE_PATHMSG_H