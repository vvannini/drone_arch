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

#include <piranha/series.hpp>

#define BOOST_TEST_MODULE series_03_test
#include <boost/test/included/unit_test.hpp>

#include <iostream>
#include <stdexcept>
#include <string>

#include <piranha/forwarding.hpp>
#include <piranha/invert.hpp>
#include <piranha/monomial.hpp>
#include <piranha/polynomial.hpp>
#include <piranha/symbol_utils.hpp>

using namespace piranha;

// Mock coefficient.
struct mock_cf {
    mock_cf() = default;
    mock_cf(const int &) {}
    mock_cf(const mock_cf &) = default;
    mock_cf(mock_cf &&) = default;
    mock_cf &operator=(const mock_cf &) = default;
    mock_cf &operator=(mock_cf &&) noexcept
    {
        return *this;
    }
    friend std::ostream &operator<<(std::ostream &, const mock_cf &);
    mock_cf operator-() const;
    bool operator==(const mock_cf &) const
    {
        return true;
    }
    bool operator!=(const mock_cf &) const
    {
        return false;
    }
    mock_cf &operator+=(const mock_cf &);
    mock_cf &operator-=(const mock_cf &);
    mock_cf operator+(const mock_cf &) const;
    mock_cf operator-(const mock_cf &) const;
    mock_cf &operator*=(const mock_cf &);
    mock_cf operator*(const mock_cf &)const;
};

// Normal usage via forwarding.
template <typename Cf, typename Expo>
class g_series_type : public series<Cf, monomial<Expo>, g_series_type<Cf, Expo>>
{
    using base = series<Cf, monomial<Expo>, g_series_type<Cf, Expo>>;

public:
    g_series_type() = default;
    g_series_type(const g_series_type &) = default;
    g_series_type(g_series_type &&) = default;
    g_series_type &operator=(const g_series_type &) = default;
    g_series_type &operator=(g_series_type &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type, base)
    explicit g_series_type(const char *name) : base()
    {
        typedef typename base::term_type term_type;
        // Insert the symbol.
        this->m_symbol_set = symbol_fset{name};
        // Construct and insert the term.
        this->insert(term_type(Cf(1), typename term_type::key_type{Expo(1)}));
    }
};

// Copy ctor of base called explicitly: if the generic ctor is invoked, this should give a linker error
// as it will need the +/- operators from mock_cf which are declared but not defined.
template <typename Cf, typename Expo>
class g_series_type2 : public series<Cf, monomial<Expo>, g_series_type2<Cf, Expo>>
{
    using base = series<Cf, monomial<Expo>, g_series_type2<Cf, Expo>>;

public:
    g_series_type2() = default;
    g_series_type2(const g_series_type2 &other) : base(other) {}
    g_series_type2(g_series_type2 &&) = default;
    g_series_type2 &operator=(const g_series_type2 &) = default;
    g_series_type2 &operator=(g_series_type2 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type2, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type2, base)
};

// A null toolbox, which will call explicitly the copy ctor of T.
template <typename T>
class null_toolbox : public T
{
public:
    null_toolbox() = default;
    null_toolbox(const null_toolbox &other) : T(other) {}
    null_toolbox(null_toolbox &&) = default;
    null_toolbox &operator=(const null_toolbox &) = default;
    null_toolbox &operator=(null_toolbox &&) = default;
    PIRANHA_FORWARDING_CTOR(null_toolbox, T)
    PIRANHA_FORWARDING_ASSIGNMENT(null_toolbox, T)
};

template <typename T>
class null_toolbox2 : public T
{
public:
    null_toolbox2() = default;
    null_toolbox2(const null_toolbox2 &) = default;
    null_toolbox2(null_toolbox2 &&) = default;
    null_toolbox2 &operator=(const null_toolbox2 &) = default;
    null_toolbox2 &operator=(null_toolbox2 &&) = default;
    PIRANHA_FORWARDING_CTOR(null_toolbox2, T)
    PIRANHA_FORWARDING_ASSIGNMENT(null_toolbox2, T)
};

// Another series type, using the toolbox this time.
template <typename Cf, typename Expo>
class g_series_type3 : public null_toolbox<series<Cf, monomial<Expo>, g_series_type3<Cf, Expo>>>
{
    using base = null_toolbox<series<Cf, monomial<Expo>, g_series_type3<Cf, Expo>>>;

public:
    g_series_type3() = default;
    g_series_type3(const g_series_type3 &) = default;
    g_series_type3(g_series_type3 &&) = default;
    g_series_type3 &operator=(const g_series_type3 &) = default;
    g_series_type3 &operator=(g_series_type3 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type3, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type3, base)
};

template <typename Cf, typename Expo>
class g_series_type4 : public null_toolbox2<series<Cf, monomial<Expo>, g_series_type4<Cf, Expo>>>
{
    using base = null_toolbox2<series<Cf, monomial<Expo>, g_series_type4<Cf, Expo>>>;

public:
    g_series_type4() = default;
    g_series_type4(const g_series_type4 &other) : base(other) {}
    g_series_type4(g_series_type4 &&) = default;
    g_series_type4 &operator=(const g_series_type4 &) = default;
    g_series_type4 &operator=(g_series_type4 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type4, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type4, base)
};

BOOST_AUTO_TEST_CASE(series_generic_ctor_forwarding_test)
{
    using st0 = g_series_type<mock_cf, int>;
    BOOST_CHECK(is_series<st0>::value);
    // Test we are using copy ctor and copy assignment.
    st0 s0, s1(s0);
    s1 = s0;
    using st1 = g_series_type2<mock_cf, int>;
    BOOST_CHECK(is_series<st1>::value);
    st1 s2, s3(s2);
    s3 = s2;
    using st2 = g_series_type3<mock_cf, int>;
    BOOST_CHECK(is_series<st2>::value);
    st2 s4, s5(s4);
    s5 = s4;
    using st3 = g_series_type4<mock_cf, int>;
    BOOST_CHECK(is_series<st3>::value);
    st3 s6, s7(s6);
    s7 = s6;
}

BOOST_AUTO_TEST_CASE(series_symbol_set_test)
{
    using st0 = g_series_type<double, int>;
    symbol_fset ss{"x", "y"};
    st0 s;
    s.set_symbol_set(ss);
    BOOST_CHECK(ss == s.get_symbol_set());
    s += 1;
    BOOST_CHECK_THROW(s.set_symbol_set(ss), std::invalid_argument);
}

// Series type with valid invert() override.
template <typename Cf, typename Expo>
class g_series_type5 : public series<Cf, monomial<Expo>, g_series_type5<Cf, Expo>>
{
    using base = series<Cf, monomial<Expo>, g_series_type5<Cf, Expo>>;

public:
    g_series_type5() = default;
    g_series_type5(const g_series_type5 &) = default;
    g_series_type5(g_series_type5 &&) = default;
    g_series_type5 &operator=(const g_series_type5 &) = default;
    g_series_type5 &operator=(g_series_type5 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type5, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type5, base)
    int invert() const
    {
        return 42;
    }
};

// Series type with invalid invert() override.
template <typename Cf, typename Expo>
class g_series_type6 : public series<Cf, monomial<Expo>, g_series_type6<Cf, Expo>>
{
    using base = series<Cf, monomial<Expo>, g_series_type6<Cf, Expo>>;

public:
    g_series_type6() = default;
    g_series_type6(const g_series_type6 &) = default;
    g_series_type6(g_series_type6 &&) = default;
    g_series_type6 &operator=(const g_series_type6 &) = default;
    g_series_type6 &operator=(g_series_type6 &&) = default;
    PIRANHA_FORWARDING_CTOR(g_series_type6, base)
    PIRANHA_FORWARDING_ASSIGNMENT(g_series_type6, base)
    int invert();
};

BOOST_AUTO_TEST_CASE(series_invert_test)
{
    using st0 = g_series_type<double, int>;
    BOOST_CHECK(is_invertible<st0>::value);
    BOOST_CHECK((std::is_same<decltype(math::invert(st0{1.23})), st0>::value));
    BOOST_CHECK_EQUAL(math::invert(st0{1.23}), math::invert(1.23));
    BOOST_CHECK_EQUAL(math::invert(st0{0.}), math::invert(0.));
    using st1 = g_series_type5<double, int>;
    BOOST_CHECK(is_invertible<st1>::value);
    BOOST_CHECK((std::is_same<decltype(math::invert(st1{1})), int>::value));
    BOOST_CHECK_EQUAL(math::invert(st1{1.23}), 42);
    using st2 = g_series_type6<double, int>;
    BOOST_CHECK(is_invertible<st2>::value);
    BOOST_CHECK((std::is_same<decltype(math::invert(st2{1})), st2>::value));
    BOOST_CHECK_EQUAL(math::invert(st2{1.23}), math::invert(1.23));
}
