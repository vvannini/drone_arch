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

#include <piranha/term.hpp>

#define BOOST_TEST_MODULE term_test
#include <boost/test/included/unit_test.hpp>

#include <functional>
#include <limits>
#include <sstream>
#include <string>
#include <tuple>
#include <type_traits>

#include <piranha/integer.hpp>
#include <piranha/key/key_is_zero.hpp>
#include <piranha/kronecker_monomial.hpp>
#include <piranha/math.hpp>
#include <piranha/math/is_zero.hpp>
#include <piranha/monomial.hpp>
#include <piranha/rational.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/type_traits.hpp>

using namespace piranha;

// NOTE: maybe once series coefficient is available we can add it here.
using cf_types = std::tuple<double, integer, rational>;
using key_types = std::tuple<monomial<int>, monomial<integer>>;

struct constructor_tester {
    template <typename Cf>
    struct runner {
        template <typename Key>
        void operator()(const Key &) const
        {
            typedef term<Cf, Key> term_type;
            typedef typename Key::value_type value_type;
            symbol_fset args{"x"};
            // Default constructor.
            BOOST_CHECK_EQUAL(term_type().m_cf, Cf());
            BOOST_CHECK(term_type().m_key == Key());
            // Generic constructor.
            BOOST_CHECK_EQUAL(term_type(Cf(1), Key{value_type(1)}).m_cf, Cf(1));
            BOOST_CHECK(term_type(Cf(1), Key{value_type(1)}).m_key == Key{value_type(1)});
            // Constructor from term of different type.
            typedef long Cf2;
            typedef term<Cf2, Key> other_term_type;
            other_term_type other(Cf2(1), Key{value_type(1)});
            BOOST_CHECK_EQUAL(term_type(Cf(other.m_cf), Key(other.m_key, args)).m_cf, Cf(Cf2(1)));
            BOOST_CHECK(term_type(Cf(other.m_cf), Key(other.m_key, args)).m_key[0] == Key{value_type(1)}[0]);
            // Move assignment.
            term_type term(Cf(1), Key{value_type(2)});
            term = term_type(Cf(2), Key{value_type(1)});
            BOOST_CHECK_EQUAL(term.m_cf, Cf(2));
            // Type trait test.
            BOOST_CHECK((std::is_constructible<term_type, Cf, Key>::value));
            BOOST_CHECK((!std::is_constructible<term_type, Cf, std::string>::value));
        }
    };
    template <typename Cf>
    void operator()(const Cf &) const
    {
        tuple_for_each(key_types{}, runner<Cf>{});
    }
};

BOOST_AUTO_TEST_CASE(term_constructor_test)
{
    tuple_for_each(cf_types{}, constructor_tester{});
}

struct equality_tester {
    template <typename Cf>
    struct runner {
        template <typename Key>
        void operator()(const Key &) const
        {
            typedef term<Cf, Key> term_type;
            typedef typename Key::value_type value_type;
            BOOST_CHECK(term_type() == term_type());
            BOOST_CHECK(term_type(Cf(1), Key{value_type(2)}) == term_type(Cf(2), Key{value_type(2)}));
            BOOST_CHECK(!(term_type(Cf(2), Key{value_type(1)}) == term_type(Cf(2), Key{value_type(2)})));
        }
    };
    template <typename Cf>
    void operator()(const Cf &) const
    {
        tuple_for_each(key_types{}, runner<Cf>{});
    }
};

BOOST_AUTO_TEST_CASE(term_equality_test)
{
    tuple_for_each(cf_types{}, equality_tester{});
}

struct hash_tester {
    template <typename Cf>
    struct runner {
        template <typename Key>
        void operator()(const Key &) const
        {
            typedef term<Cf, Key> term_type;
            typedef typename Key::value_type value_type;
            BOOST_CHECK_EQUAL(term_type().hash(), std::hash<Key>()(Key()));
            BOOST_CHECK_EQUAL(term_type().hash(), std::hash<term_type>()(term_type()));
            BOOST_CHECK_EQUAL(term_type(Cf(2), Key{value_type(1)}).hash(), std::hash<Key>()(Key{value_type(1)}));
            BOOST_CHECK_EQUAL(term_type(Cf(2), Key{value_type(1)}).hash(),
                              std::hash<term_type>()(term_type{Cf(), Key{value_type(1)}}));
        }
    };
    template <typename Cf>
    void operator()(const Cf &) const
    {
        tuple_for_each(key_types{}, runner<Cf>{});
    }
};

BOOST_AUTO_TEST_CASE(term_hash_test)
{
    tuple_for_each(cf_types{}, hash_tester());
}

struct compatibility_tester {
    template <typename Cf>
    struct runner {
        template <typename Key>
        void operator()(const Key &) const
        {
            using term_type = term<Cf, Key>;
            typedef typename Key::value_type value_type;
            symbol_fset args;
            term_type t1;
            BOOST_CHECK_EQUAL(t1.is_compatible(args), t1.m_key.is_compatible(args));
            term_type t2;
            t2.m_cf = 1;
            t2.m_key = Key{value_type(1)};
            BOOST_CHECK_EQUAL(t2.is_compatible(args), t2.m_key.is_compatible(args));
        }
    };
    template <typename Cf>
    void operator()(const Cf &) const
    {
        tuple_for_each(key_types{}, runner<Cf>{});
    }
};

BOOST_AUTO_TEST_CASE(term_compatibility_test)
{
    tuple_for_each(cf_types{}, compatibility_tester{});
}

struct zero_tester {
    template <typename Cf>
    struct runner {
        template <typename Key>
        void operator()(const Key &) const
        {
            using term_type = term<Cf, Key>;
            symbol_fset args;
            term_type t1;
            BOOST_CHECK_EQUAL(t1.is_zero(args), (piranha::key_is_zero(t1.m_key, args) || piranha::is_zero(t1.m_cf)));
            BOOST_CHECK(t1.is_zero(args));
            term_type t2;
            t2.m_cf = 1;
            BOOST_CHECK_EQUAL(t2.is_zero(args), (piranha::key_is_zero(t2.m_key, args) || piranha::is_zero(t2.m_cf)));
            BOOST_CHECK(!t2.is_zero(args));
        }
    };
    template <typename Cf>
    void operator()(const Cf &) const
    {
        tuple_for_each(key_types{}, runner<Cf>{});
    }
};

BOOST_AUTO_TEST_CASE(term_zero_test)
{
    tuple_for_each(cf_types{}, zero_tester{});
}

namespace piranha
{

// Disable the noexcept checks for a few types.
template <>
struct enable_noexcept_checks<double, void> : std::false_type {
};

template <>
struct enable_noexcept_checks<float, void> : std::false_type {
};

template <>
struct enable_noexcept_checks<k_monomial, void> : std::false_type {
};
}

BOOST_AUTO_TEST_CASE(term_noexcept_spec_test)
{
    BOOST_CHECK((!enable_noexcept_checks<term<double, monomial<int>>>::value));
    BOOST_CHECK((!enable_noexcept_checks<term<float, monomial<int>>>::value));
    BOOST_CHECK((enable_noexcept_checks<term<long double, monomial<int>>>::value));
    BOOST_CHECK((!enable_noexcept_checks<term<long double, k_monomial>>::value));
    BOOST_CHECK((!enable_noexcept_checks<term<float, k_monomial>>::value));
    BOOST_CHECK((is_container_element<term<long double, monomial<int>>>::value));
    BOOST_CHECK((is_container_element<term<double, monomial<int>>>::value));
    BOOST_CHECK((is_container_element<term<float, monomial<int>>>::value));
    BOOST_CHECK((is_container_element<term<long double, k_monomial>>::value));
    BOOST_CHECK((is_container_element<term<float, k_monomial>>::value));
}
