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

#define BOOST_TEST_MODULE power_series_test
#include <boost/test/included/unit_test.hpp>

#include <iostream>

#include <mp++/integer.hpp>

#include <piranha/integer.hpp>
#include <piranha/kronecker_monomial.hpp>

#include "pearce1.hpp"
#include "simple_timer.hpp"

using namespace piranha;

BOOST_AUTO_TEST_CASE(pearce1_test)
{
    settings::set_thread_binding(true);
    std::cout << "Timing multiplication:\n";
    auto ret1 = pearce1<mppp::integer<2>, kronecker_monomial<>>();
    decltype(ret1) ret2;
    {
        std::cout << "Timing degree computation: ";
        simple_timer t;
        std::cout << ret1.degree() << '\n';
    }
    {
        std::cout << "Timing degree truncation:\n";
        simple_timer t;
        ret2 = ret1.truncate_degree(30);
    }
    {
        std::cout << "Timing new degree computation: ";
        simple_timer t;
        std::cout << ret2.degree() << '\n';
    }
    {
        std::cout << "Timing partial degree truncation:\n";
        simple_timer t;
        ret2 = ret1.truncate_degree(30, {"u", "z"});
    }
}
