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

#include "pearce2.hpp"

#define BOOST_TEST_MODULE pearce2_test
#include <boost/test/included/unit_test.hpp>

#include <boost/lexical_cast.hpp>

#include <mp++/integer.hpp>

#include <piranha/integer.hpp>
#include <piranha/monomial.hpp>
#include <piranha/settings.hpp>

using namespace piranha;

// Pearce's polynomial multiplication test number 2. Calculate:
// f * g
// where
// f = (1 + x + y + 2*z**2 + 3*t**3 + 5*u**5)**16
// g = (1 + u + t + 2*z**2 + 3*y**3 + 5*x**5)**16

BOOST_AUTO_TEST_CASE(pearce2_test)
{
    settings::set_thread_binding(true);
    if (boost::unit_test::framework::master_test_suite().argc > 1) {
        settings::set_n_threads(
            boost::lexical_cast<unsigned>(boost::unit_test::framework::master_test_suite().argv[1u]));
    }
    BOOST_CHECK_EQUAL((pearce2<mppp::integer<2>, monomial<char>>().size()), 28398035u);
}
