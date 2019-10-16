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

class JarPlannersRequest {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.pedido = null;
    }
    else {
      if (initObj.hasOwnProperty('pedido')) {
        this.pedido = initObj.pedido
      }
      else {
        this.pedido = 0;
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type JarPlannersRequest
    // Serialize message field [pedido]
    bufferOffset = _serializer.int64(obj.pedido, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type JarPlannersRequest
    let len;
    let data = new JarPlannersRequest(null);
    // Deserialize message field [pedido]
    data.pedido = _deserializer.int64(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    return 8;
  }

  static datatype() {
    // Returns string type for a service object
    return 'planners/JarPlannersRequest';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return '7aec8105d110d548b7a1ac63cf67345b';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    int64 pedido
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new JarPlannersRequest(null);
    if (msg.pedido !== undefined) {
      resolved.pedido = msg.pedido;
    }
    else {
      resolved.pedido = 0
    }

    return resolved;
    }
};

class JarPlannersResponse {
  constructor(initObj={}) {
    if (initObj === null) {
      // initObj === null is a special case for deserialization where we don't initialize fields
      this.result = null;
    }
    else {
      if (initObj.hasOwnProperty('result')) {
        this.result = initObj.result
      }
      else {
        this.result = '';
      }
    }
  }

  static serialize(obj, buffer, bufferOffset) {
    // Serializes a message object of type JarPlannersResponse
    // Serialize message field [result]
    bufferOffset = _serializer.string(obj.result, buffer, bufferOffset);
    return bufferOffset;
  }

  static deserialize(buffer, bufferOffset=[0]) {
    //deserializes a message object of type JarPlannersResponse
    let len;
    let data = new JarPlannersResponse(null);
    // Deserialize message field [result]
    data.result = _deserializer.string(buffer, bufferOffset);
    return data;
  }

  static getMessageSize(object) {
    let length = 0;
    length += object.result.length;
    return length + 4;
  }

  static datatype() {
    // Returns string type for a service object
    return 'planners/JarPlannersResponse';
  }

  static md5sum() {
    //Returns md5sum for a message object
    return 'c22f2a1ed8654a0b365f1bb3f7ff2c0f';
  }

  static messageDefinition() {
    // Returns full string definition for message
    return `
    string result
    
    `;
  }

  static Resolve(msg) {
    // deep-construct a valid message object instance of whatever was passed in
    if (typeof msg !== 'object' || msg === null) {
      msg = {};
    }
    const resolved = new JarPlannersResponse(null);
    if (msg.result !== undefined) {
      resolved.result = msg.result;
    }
    else {
      resolved.result = ''
    }

    return resolved;
    }
};

module.exports = {
  Request: JarPlannersRequest,
  Response: JarPlannersResponse,
  md5sum() { return '4227327d0bce5bc51aba1221199a837b'; },
  datatype() { return 'planners/JarPlanners'; }
};
