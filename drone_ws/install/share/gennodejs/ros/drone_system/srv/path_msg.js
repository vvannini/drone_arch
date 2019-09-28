// Auto-generated. Do not edit!

// (in-package drone_system.srv)


"use strict";

const _serializer = _ros_msg_utils.Serialize;
const _arraySerializer = _serializer.Array;
const _deserializer = _ros_msg_utils.Deserialize;
const _arrayDeserializer = _deserializer.Array;
const _finder = _ros_msg_utils.Find;
const _getByteLength = _ros_msg_utils.getByteLength;

//-----------------------------------------------------------


//-----------------------------------------------------------

class path_msgRequest {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.option = null;
    }
    else {
      if (initObj.hasOwnProperty('option')) {
        this.option = initObj.option
      }
      else {
        this.option = '';
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type path_msgRequest
    // Serialize message field [option]
    bufferOffset = _serializer.string(obj.option, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type path_msgRequest
    let len;
    let data = new path_msgRequest(null);
    // Deserialize message field [option]
    data.option = _deserializer.string(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    let length = 0;
    length += object.option.length;
    return length + 4;
  }

  static datatype() {
    // Returns string type for a service object
    return 'drone_system/path_msgRequest';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return '7be597108526c007715ebf9a51b492e9';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    string option
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new path_msgRequest(null);
    if (msg.option !== undefined) {
      resolved.option = msg.option;
    }
    else {
      resolved.option = ''
    }

    return resolved;
    }
};

class path_msgResponse {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.path = null;
    }
    else {
      if (initObj.hasOwnProperty('path')) {
        this.path = initObj.path
      }
      else {
        this.path = '';
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type path_msgResponse
    // Serialize message field [path]
    bufferOffset = _serializer.string(obj.path, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type path_msgResponse
    let len;
    let data = new path_msgResponse(null);
    // Deserialize message field [path]
    data.path = _deserializer.string(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    let length = 0;
    length += object.path.length;
    return length + 4;
  }

  static datatype() {
    // Returns string type for a service object
    return 'drone_system/path_msgResponse';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return '1d00cd540af97efeb6b1589112fab63e';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    string path
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new path_msgResponse(null);
    if (msg.path !== undefined) {
      resolved.path = msg.path;
    }
    else {
      resolved.path = ''
    }

    return resolved;
    }
};

module.exports = {
  Request: path_msgRequest,
  Response: path_msgResponse,
  md5sum() { return 'c48136865ebee6f8ec8dec3519522f22'; },
  datatype() { return 'drone_system/path_msg'; }
};
