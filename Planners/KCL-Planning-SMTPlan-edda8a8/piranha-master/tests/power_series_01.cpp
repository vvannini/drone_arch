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

#include <piranha/power_series.hpp>

#define BOOST_TEST_MODULE power_series_01_test
#include <boost/test/included/unit_test.hpp>

#include <boost/mpl/for_each.hpp>
#include <boost/mpl/vector.hpp>
#include <type_traits>

#include <piranha/integer.hpp>
#include <piranha/math.hpp>
#include <piranha/math/degree.hpp>
#include <piranha/math/ldegree.hpp>
#include <piranha/monomial.hpp>
#include <piranha/poisson_series.hpp>
#include <piranha/polynomial.hpp>
#include <piranha/rational.hpp>
#include <piranha/real_trigonometric_kronecker_monomial.hpp>
#include <piranha/series.hpp>
#include <piranha/symbol_utils.hpp>

using namespace piranha;

typedef boost::mpl::vector<double, integer> cf_types;
typedef boost::mpl::vector<int, integer> expo_types;

template <typename Cf, typename Expo>
class g_series_type : public power_series<series<Cf, monomial<Expo>, g_series_type<Cf, Expo>>, g_series_type<Cf, Expo>>
{
    using base = power_series<series<Cf, monomial<Expo>, g_series_type<Cf, Expo>>, g_series_type<Cf, Expo>>;

public:
    g_series_type() = default;
    g_series_type(const g_series_type &) = default;
    g_series_type(g_series_type &&) = default;
    g_series_type &operator=(const g_series_type &) = default;
    g_series_type &operator=(g_series_type &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type, base)
};

template <typename Cf>
class g_series_type2 : public power_series<series<Cf, rtk_monomial, g_series_type2<Cf>>, g_series_type2<Cf>>
{
    typedef power_series<series<Cf, rtk_monomial, g_series_type2<Cf>>, g_series_type2<Cf>> base;

public:
    g_series_type2() = default;
    g_series_type2(const g_series_type2 &) = default;
    g_series_type2(g_series_type2 &&) = default;
    g_series_type2 &operator=(const g_series_type2 &) = default;
    g_series_type2 &operator=(g_series_type2 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type2, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type2, base)
};

struct degree_tester {
    template <typename Cf>
    struct runner {
        template <typename Expo>
        void operator()(const Expo &)
        {
            typedef polynomial<Cf, monomial<Expo>> p_type1;
            typedef polynomial<polynomial<Cf, monomial<Expo>>, monomial<Expo>> p_type11;
            using deg_type = typename std::conditional<std::is_same<Expo, int>::value, int, integer>::type;
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::degree(p_type1{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::degree(p_type1{}, symbol_fset{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::ldegree(p_type1{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::ldegree(p_type1{}, symbol_fset{}))>::value));
            BOOST_CHECK(piranha::degree(p_type1{}) == 0);
            BOOST_CHECK(piranha::degree(p_type1{}, symbol_fset{}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{}, symbol_fset{}) == 0);
            BOOST_CHECK(piranha::degree(p_type1{"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"}, {"y"}) == 0);
            BOOST_CHECK(piranha::degree(p_type1{"x"} * p_type1{"x"}) == 2);
            BOOST_CHECK(piranha::degree(p_type1{"x"} * p_type1{"x"}, {"x"}) == 2);
            BOOST_CHECK(piranha::degree(p_type1{"x"} * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"}) == 2);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"}, {"x"}) == 2);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::degree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} + p_type1{"y"} + p_type1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"} + p_type1{"y"} + p_type1{"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"} + p_type1{"y"} + p_type1{"x"}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"} + p_type1{"y"} + p_type1{"x"}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"x"} + 2 * p_type1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"y"} + 2 * p_type1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type1{"x"} * p_type1{"y"} + 2 * p_type1{"x"}, {"y"}) == 0);
            symbol_fset empty_set;
            BOOST_CHECK((std::is_same<decltype(piranha::degree(std::declval<const p_type11 &>())), deg_type>::value));
            BOOST_CHECK((
                std::is_same<decltype(piranha::degree(std::declval<const p_type11 &>(), empty_set)), deg_type>::value));
            BOOST_CHECK((std::is_same<decltype(piranha::ldegree(std::declval<const p_type11 &>())), deg_type>::value));
            BOOST_CHECK((std::is_same<decltype(piranha::ldegree(std::declval<const p_type11 &>(), empty_set)),
                                      deg_type>::value));
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}) == 2);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"} + 1) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"} + 1, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} + 2 * p_type1{"y"} + 1, {"y"}) == 0);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"} + 1) == 3);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"} + 1, {"x"})
                        == 1);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"} + 1, {"y"})
                        == 2);
            BOOST_CHECK(piranha::degree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"} + 1, {"y"})
                        == 2);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"} + 1) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(p_type11{"x"} * p_type1{"y"} * p_type1{"y"} + 2 * p_type1{"y"}, {"y"}) == 1);
            // Test the type traits.
            BOOST_CHECK(is_degree_type<p_type1>::value);
            BOOST_CHECK(is_degree_type<p_type11>::value);
            BOOST_CHECK(is_ldegree_type<p_type1>::value);
            BOOST_CHECK(is_ldegree_type<p_type11>::value);
            // Poisson series tests.
            typedef poisson_series<p_type1> pstype1;
            BOOST_CHECK(is_degree_type<pstype1>::value);
            BOOST_CHECK(is_ldegree_type<pstype1>::value);
            typedef poisson_series<Cf> pstype2;
            BOOST_CHECK(!is_degree_type<pstype2>::value);
            BOOST_CHECK(!is_ldegree_type<pstype2>::value);
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::degree(pstype1{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::degree(pstype1{}, symbol_fset{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::ldegree(pstype1{}))>::value));
            BOOST_CHECK((std::is_same<deg_type, decltype(piranha::ldegree(pstype1{}, symbol_fset{}))>::value));
            // As usual, operations on Poisson series with (polynomial) integer coefficients are not gonna give
            // meaningful mathematical results.
            if (std::is_same<Cf, integer>::value) {
                return;
            }
            BOOST_CHECK(piranha::degree(pstype1{}) == 0);
            BOOST_CHECK(piranha::degree(pstype1{}, symbol_fset{}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{}, symbol_fset{}) == 0);
            BOOST_CHECK(piranha::degree(pstype1{"x"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"}, {"y"}) == 0);
            BOOST_CHECK(piranha::degree(pstype1{"x"} * pstype1{"x"}) == 2);
            BOOST_CHECK(piranha::degree(pstype1{"x"} * pstype1{"x"}, {"x"}) == 2);
            BOOST_CHECK(piranha::degree(pstype1{"x"} * pstype1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"}) == 2);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"}, {"x"}) == 2);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"y"}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"x"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"y"}) == 1);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::degree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"y"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} + pstype1{"y"} + pstype1{1}, {"z"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"} + pstype1{"y"} + pstype1{"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"} + pstype1{"y"} + pstype1{"x"}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"} + pstype1{"y"} + pstype1{"x"}, {"x"}) == 0);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"x"} + 2 * pstype1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"y"} + 2 * pstype1{"x"}, {"x"}) == 1);
            BOOST_CHECK(piranha::ldegree(pstype1{"x"} * pstype1{"y"} + 2 * pstype1{"x"}, {"y"}) == 0);
        }
    };
    template <typename Cf>
    void operator()(const Cf &)
    {
        boost::mpl::for_each<expo_types>(runner<Cf>());
    }
};

BOOST_AUTO_TEST_CASE(power_series_test_01)
{
    boost::mpl::for_each<cf_types>(degree_tester());
}
