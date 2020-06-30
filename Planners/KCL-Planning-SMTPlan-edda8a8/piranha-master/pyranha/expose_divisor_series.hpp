/* Copyright 2009-2017 Francesco Biscani (bluescarni@gmail.com)

This file is part of the Piranha library.

The Piranha library is free software; you can redistribute it and/or modify
it under the terms of either:

  * the GNU Lesser General Public License as published by the Free
    Software Foundation; either version 3 of the License, or (at your
    option) any later version.

or

  * the GNU General Public License as published by the Free Software
    Foundation; either version 3 of the License, or (at your option) any
    later version.

or both in parallel, as here.

The Piranha library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received copies of the GNU General Public License and the
GNU Lesser General Public License along with the Piranha library.  If not,
see https://www.gnu.org/licenses/. */

#ifndef PYRANHA_EXPOSE_DIVISOR_SERIES_HPP
#define PYRANHA_EXPOSE_DIVISOR_SERIES_HPP

#include <piranha/divisor_series.hpp>

#include "type_system.hpp"

namespace pyranha
{

PYRANHA_DECLARE_T_NAME(piranha::divisor_series)

void expose_divisor_series_0();
void expose_divisor_series_1();
void expose_divisor_series_2();
void expose_divisor_series_3();
void expose_divisor_series_4();
void expose_divisor_series_5();
}

#endif
