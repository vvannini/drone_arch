// Auto-generated. Do not edit!

// (in-package planners.srv)


"use strict";

const _serializer = _ros_msg_utils.Serialize;
const _arraySerializer = _serializer.Array;
const _deserializer = _ros_msg_utils.Deserialize;
const _arrayDeserializer = _deserializer.Array;
const _finder = _ros_msg_utils.Find;
const _getByteLength = _ros_msg_utils.getByteLength;

//-----------------------------------------------------------


//-----------------------------------------------------------

class GA_PlannerRequest {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.origin_lat = null;
      this.origin_long = null;
      this.origin_alt = null;
      this.destination_lat = null;
      this.destination_long = null;
      this.destination_alt = null;
      this.map_id = null;
    }
    else {
      if (initObj.hasOwnProperty('origin_lat')) {
        this.origin_lat = initObj.origin_lat
      }
      else {
        this.origin_lat = 0.0;
      }
      if (initObj.hasOwnProperty('origin_long')) {
        this.origin_long = initObj.origin_long
      }
      else {
        this.origin_long = 0.0;
      }
      if (initObj.hasOwnProperty('origin_alt')) {
        this.origin_alt = initObj.origin_alt
      }
      else {
        this.origin_alt = 0.0;
      }
      if (initObj.hasOwnProperty('destination_lat')) {
        this.destination_lat = initObj.destination_lat
      }
      else {
        this.destination_lat = 0.0;
      }
      if (initObj.hasOwnProperty('destination_long')) {
        this.destination_long = initObj.destination_long
      }
      else {
        this.destination_long = 0.0;
      }
      if (initObj.hasOwnProperty('destination_alt')) {
        this.destination_alt = initObj.destination_alt
      }
      else {
        this.destination_alt = 0.0;
      }
      if (initObj.hasOwnProperty('map_id')) {
        this.map_id = initObj.map_id
      }
      else {
        this.map_id = 0;
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type GA_PlannerRequest
    // Serialize message field [origin_lat]
    bufferOffset = _serializer.float32(obj.origin_lat, buffer, bufferOffset);
    // Serialize message field [origin_long]
    bufferOffset = _serializer.float32(obj.origin_long, buffer, bufferOffset);
    // Serialize message field [origin_alt]
    bufferOffset = _serializer.float32(obj.origin_alt, buffer, bufferOffset);
    // Serialize message field [destination_lat]
    bufferOffset = _serializer.float32(obj.destination_lat, buffer, bufferOffset);
    // Serialize message field [destination_long]
    bufferOffset = _serializer.float32(obj.destination_long, buffer, bufferOffset);
    // Serialize message field [destination_alt]
    bufferOffset = _serializer.float32(obj.destination_alt, buffer, bufferOffset);
    // Serialize message field [map_id]
    bufferOffset = _serializer.int32(obj.map_id, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type GA_PlannerRequest
    let len;
    let data = new GA_PlannerRequest(null);
    // Deserialize message field [origin_lat]
    data.origin_lat = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [origin_long]
    data.origin_long = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [origin_alt]
    data.origin_alt = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [destination_lat]
    data.destination_lat = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [destination_long]
    data.destination_long = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [destination_alt]
    data.destination_alt = _deserializer.float32(buffer, bufferOffset);
    // Deserialize message field [map_id]
    data.map_id = _deserializer.int32(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    return 28;
  }

  static datatype() {
    // Returns string type for a service object
    return 'planners/GA_PlannerRequest';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return '26ab780b1ffed608535599842d9b65b1';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    float32 origin_lat
    float32 origin_long
    float32 origin_alt
    float32 destination_lat
    float32 destination_long
    float32 destination_alt
    int32 map_id
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new GA_PlannerRequest(null);
    if (msg.origin_lat !== undefined) {
      resolved.origin_lat = msg.origin_lat;
    }
    else {
      resolved.origin_lat = 0.0
    }

    if (msg.origin_long !== undefined) {
      resolved.origin_long = msg.origin_long;
    }
    else {
      resolved.origin_long = 0.0
    }

    if (msg.origin_alt !== undefined) {
      resolved.origin_alt = msg.origin_alt;
    }
    else {
      resolved.origin_alt = 0.0
    }

    if (msg.destination_lat !== undefined) {
      resolved.destination_lat = msg.destination_lat;
    }
    else {
      resolved.destination_lat = 0.0
    }

    if (msg.destination_long !== undefined) {
      resolved.destination_long = msg.destination_long;
    }
    else {
      resolved.destination_long = 0.0
    }

    if (msg.destination_alt !== undefined) {
      resolved.destination_alt = msg.destination_alt;
    }
    else {
      resolved.destination_alt = 0.0
    }

    if (msg.map_id !== undefined) {
      resolved.map_id = msg.map_id;
    }
    else {
      resolved.map_id = 0
    }

    return resolved;
    }
};

class GA_PlannerResponse {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.wp_path = null;
    }
    else {
      if (initObj.hasOwnProperty('wp_path')) {
        this.wp_path = initObj.wp_path
      }
      else {
        this.wp_path = '';
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type GA_PlannerResponse
    // Serialize message field [wp_path]
    bufferOffset = _serializer.string(obj.wp_path, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type GA_PlannerResponse
    let len;
    let data = new GA_PlannerResponse(null);
    // Deserialize message field [wp_path]
    data.wp_path = _deserializer.string(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    let length = 0;
    length += object.wp_path.length;
    return length + 4;
  }

  static datatype() {
    // Returns string type for a service object
    return 'planners/GA_PlannerResponse';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return '1ada448ae45e59df26b471029dda5c76';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    string wp_path
    
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new GA_PlannerResponse(null);
    if (msg.wp_path !== undefined) {
      resolved.wp_path = msg.wp_path;
    }
    else {
      resolved.wp_path = ''
    }

    return resolved;
    }
};

module.exports = {
  Request: GA_PlannerRequest,
  Response: GA_PlannerResponse,
  md5sum() { return '4d22e4fe344ad31e2831d2deed99337f'; },
  datatype() { return 'planners/GA_Planner'; }
};
