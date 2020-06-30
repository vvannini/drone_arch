if(YACMAPythonSetupIncluded)
    return()
endif()

# NOTE: this is a heuristic to determine whether we need to link to the Python library.
# In theory, Python extensions don't need to, as they are dlopened() by the Python process
# and thus they don't need to be linked to the Python library at compile time. However,
# the dependency on Boost.Python muddies the waters, as BP itself does link to the Python
# library, at least on some platforms. The following configuration seems to be working fine
# on various CI setups.
# NOTE: apparently homebrew requires NOT to link to the Python library. We might want
# to add a config option to accommodate that eventually.
if(WIN32 OR ${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  message(STATUS "Python modules require linking to the Python library.")
  set(_YACMA_PYTHON_MODULE_NEED_LINK TRUE)
else()
  message(STATUS "Python modules do NOT require linking to the Python library.")
  set(_YACMA_PYTHON_MODULE_NEED_LINK FALSE)
endif()

# Find Python interpreter.
find_package(PythonInterp REQUIRED)

if(_YACMA_PYTHON_MODULE_NEED_LINK)
  # NOTE: this will give us both the Python lib and the Python include dir.
  find_package(PythonLibs REQUIRED)
  if(NOT YACMA_PYTHON_INCLUDE_DIR)
    set(YACMA_PYTHON_INCLUDE_DIR "${PYTHON_INCLUDE_DIRS}" CACHE PATH "Path to the Python include dir.")
  endif()
else()
  # NOTE: we need to determine the include dir on our own.
  if(NOT YACMA_PYTHON_INCLUDE_DIR)
    execute_process(COMMAND ${PYTHON_EXECUTABLE} -c "from __future__ import print_function\nfrom distutils import sysconfig\nprint(sysconfig.get_python_inc())"
      OUTPUT_VARIABLE _YACMA_PYTHON_INCLUDE_DIR OUTPUT_STRIP_TRAILING_WHITESPACE)
    if(_YACMA_PYTHON_INCLUDE_DIR)
      set(YACMA_PYTHON_INCLUDE_DIR "${_YACMA_PYTHON_INCLUDE_DIR}" CACHE PATH "Path to the Python include dir.")
    endif()
  endif()
  if(NOT YACMA_PYTHON_INCLUDE_DIR)
      message(FATAL_ERROR "Could not determine the Python include dir.")
  endif()
endif()
mark_as_advanced(YACMA_PYTHON_INCLUDE_DIR)

message(STATUS "Python interpreter: ${PYTHON_EXECUTABLE}")
message(STATUS "Python interpreter version: ${PYTHON_VERSION_STRING}")
if(_YACMA_PYTHON_MODULE_NEED_LINK)
  message(STATUS "Python libraries: ${PYTHON_LIBRARIES}")
endif()
message(STATUS "Python include dir: ${YACMA_PYTHON_INCLUDE_DIR}")

# An imported target to be used when building extension modules.
if(_YACMA_PYTHON_MODULE_NEED_LINK)
  add_library(YACMA::PythonModule UNKNOWN IMPORTED)
  set_target_properties(YACMA::PythonModule PROPERTIES INTERFACE_INCLUDE_DIRECTORIES "${YACMA_PYTHON_INCLUDE_DIR}"
    IMPORTED_LOCATION "${PYTHON_LIBRARIES}" IMPORTED_LINK_INTERFACE_LANGUAGES "C")
else()
  add_library(YACMA::PythonModule INTERFACE IMPORTED)
  set_target_properties(YACMA::PythonModule PROPERTIES INTERFACE_INCLUDE_DIRECTORIES "${YACMA_PYTHON_INCLUDE_DIR}")
endif()

# This flag is used to signal the need to override the default extension of the Python modules
# depending on the architecture. Under Windows, for instance, CMake produces shared objects as
# .dll files, but Python from 2.5 onwards requires .pyd files (hence the need to override).
set(_YACMA_PY_MODULE_EXTENSION "")

# Platform-specific setup.
if(UNIX)
  if(${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
    message(STATUS "OS X platform detected.")
    # Apparently on OS X Python expects the .so extension for compiled modules.
	  message(STATUS "Output extension for compiled modules will be '.so'.")
    set(_YACMA_PY_MODULE_EXTENSION "so")
  else()
    message(STATUS "Generic UNIX platform detected.")
  endif()
  if(NOT YACMA_PYTHON_MODULES_INSTALL_PATH)
    # NOTE: here we use this contraption (instead of the simple method below for Win32) because like this we can
    # support installation into the CMake prefix (e.g., in the user's home dir).
    execute_process(COMMAND ${PYTHON_EXECUTABLE} -c "from __future__ import print_function\nimport distutils.sysconfig\nimport os\nprint(os.path.split(distutils.sysconfig.get_python_lib())[-1])"
      OUTPUT_VARIABLE _YACMA_PY_PACKAGES_DIR OUTPUT_STRIP_TRAILING_WHITESPACE)
    message(STATUS "Python packages dir is: ${_YACMA_PY_PACKAGES_DIR}")
    set(YACMA_PYTHON_MODULES_INSTALL_PATH "lib/python${PYTHON_VERSION_MAJOR}.${PYTHON_VERSION_MINOR}/${_YACMA_PY_PACKAGES_DIR}" CACHE PATH "Install path for Python modules.")
    mark_as_advanced(YACMA_PYTHON_MODULES_INSTALL_PATH)
  endif()
elseif(WIN32)
  message(STATUS "Windows platform detected.")
  message(STATUS "Output extension for compiled modules will be '.pyd'.")
  set(_YACMA_PY_MODULE_EXTENSION "pyd")
  if(NOT YACMA_PYTHON_MODULES_INSTALL_PATH)
    # On Windows, we will install directly into the install path of the Python interpreter.
    execute_process(COMMAND ${PYTHON_EXECUTABLE} -c "from distutils.sysconfig import get_python_lib; print(get_python_lib())"
      OUTPUT_VARIABLE _YACMA_PYTHON_MODULES_INSTALL_PATH OUTPUT_STRIP_TRAILING_WHITESPACE)
    set(YACMA_PYTHON_MODULES_INSTALL_PATH "${_YACMA_PYTHON_MODULES_INSTALL_PATH}" CACHE PATH "Install path for Python modules.")
    mark_as_advanced(YACMA_PYTHON_MODULES_INSTALL_PATH)
  endif()
else()
  message(FATAL_ERROR "Platform not supported.")
endif()

# Check the install path was actually detected.
if("${YACMA_PYTHON_MODULES_INSTALL_PATH}" STREQUAL "")
  message(FATAL_ERROR "Python module install path not detected correctly.")
endif()

message(STATUS "Python modules install path: ${YACMA_PYTHON_MODULES_INSTALL_PATH}")

function(YACMA_PYTHON_MODULE name)
    message(STATUS "Setting up the compilation of the Python module '${name}'.")
    # If we need an explicit link to the Python library, we compile it as a normal shared library.
    # Otherwise, we compile it as a module.
    if(_YACMA_PYTHON_MODULE_NEED_LINK)
      add_library("${name}" SHARED ${ARGN})
    else()
      add_library("${name}" MODULE ${ARGN})
    endif()
    # Any "lib" prefix normally added by CMake must be removed.
    set_target_properties("${name}" PROPERTIES PREFIX "")
    if(NOT ${_YACMA_PY_MODULE_EXTENSION} STREQUAL "")
        # If needed, set a custom extension for the module.
        message(STATUS "Setting up custom extension '${_YACMA_PY_MODULE_EXTENSION}' for the Python module '${name}'.")
        set_target_properties("${name}" PROPERTIES SUFFIX ".${_YACMA_PY_MODULE_EXTENSION}")
    endif()
    # We need extra flags to be set when compiling Python modules, at least
    # with clang and gcc. See:
    # https://bugs.python.org/issue11149
    # http://www.python.org/dev/peps/pep-3123/
    # NOTE: not sure here how we should set flags up for MSVC or clang on windows, need
    # to check in the future.
    # NOTE: do not use the yacma compiler linker settings bits, so this module
    # can be used stand-alone.
    if(CMAKE_COMPILER_IS_GNUCXX OR ${CMAKE_CXX_COMPILER_ID} MATCHES "Clang")
        message(STATUS "Setting up extra compiler flag '-fwrapv' for the Python module '${name}'.")
        target_compile_options(${name} PRIVATE "-fwrapv")
        if(${PYTHON_VERSION_MAJOR} LESS 3)
            message(STATUS "Python < 3 detected, setting up extra compiler flag '-fno-strict-aliasing' for the Python module '${name}'.")
            target_compile_options(${name} PRIVATE "-fno-strict-aliasing")
        endif()
    endif()
    target_link_libraries("${name}" PRIVATE YACMA::PythonModule)
endfunction()

# Mark as included.
set(YACMAPythonSetupIncluded YES)
