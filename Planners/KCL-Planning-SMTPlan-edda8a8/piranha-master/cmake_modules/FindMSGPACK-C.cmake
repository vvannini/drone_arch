# Copyright (c) 2016-2017 Francesco Biscani, <bluescarni@gmail.com>

# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 3. The name of the author may not be used to endorse or promote products
#    derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
# IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
# THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
# ------------------------------------------------------------------------------------------

# We look only for the header-only version of msgpack-c for C++.

if(MSGPACK-C_INCLUDE_DIR)
	# Already in cache, be silent
	set(MSGPACK-C_FIND_QUIETLY TRUE)
endif()

find_path(MSGPACK-C_INCLUDE_DIR NAMES msgpack.hpp)

include(FindPackageHandleStandardArgs)

find_package_handle_standard_args(MSGPACK-C DEFAULT_MSG MSGPACK-C_INCLUDE_DIR)

mark_as_advanced(MSGPACK-C_INCLUDE_DIR)

# NOTE: this has been adapted from CMake's FindPNG.cmake.
if(MSGPACK-C_FOUND AND NOT TARGET MSGPACK-C::MSGPACK-C)
	add_library(MSGPACK-C::MSGPACK-C INTERFACE IMPORTED)
	set_target_properties(MSGPACK-C::MSGPACK-C PROPERTIES INTERFACE_INCLUDE_DIRECTORIES "${MSGPACK-C_INCLUDE_DIR}")
endif()
