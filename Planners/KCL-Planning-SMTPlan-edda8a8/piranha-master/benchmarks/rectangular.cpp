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

#include <piranha/polynomial.hpp>

#define BOOST_TEST_MODULE rectangular_test
#include <boost/test/included/unit_test.hpp>

#include <boost/lexical_cast.hpp>

#include <piranha/kronecker_monomial.hpp>
#include <piranha/settings.hpp>

#include "simple_timer.hpp"

using namespace piranha;

// Test taken from:
// http://groups.google.com/group/sage-devel/browse_thread/thread/f5b976c979a3b784/1263afcc6f9d09da
// Meant to test sparse multiplication where series have very different sizes.

BOOST_AUTO_TEST_CASE(rectangular_test)
{
    settings::set_thread_binding(true);
    if (boost::unit_test::framework::master_test_suite().argc > 1) {
        settings::set_n_threads(
            boost::lexical_cast<unsigned>(boost::unit_test::framework::master_test_suite().argv[1u]));
    }
    typedef polynomial<double, kronecker_monomial<>> p_type;

    auto func = []() -> p_type {
        p_type x("x"), y("y"), z("z");
        auto f = x * y * y * y * z * z + x * x * y * y * z + x * y * y * y * z + x * y * y * z * z + y * y * y * z * z
                 + y * y * y * z + 2 * y * y * z * z + 2 * x * y * z + y * y * z + y * z * z + y * y + 2 * y * z + z;
        p_type curr(1);
        for (auto i = 1; i <= 70; ++i) {
            curr *= f;
        }
        BOOST_CHECK_EQUAL(curr.size(), 1284816u);
        return curr;
    };
    {
        simple_timer t;
        auto tmp = func();
    }
}
