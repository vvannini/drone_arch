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

#include <piranha/key_is_convertible.hpp>

#define BOOST_TEST_MODULE key_is_convertible_test
#include <boost/test/included/unit_test.hpp>

#include <cstddef>
#include <functional>
#include <iostream>
#include <vector>

#include <piranha/key/key_is_one.hpp>
#include <piranha/symbol_utils.hpp>

using namespace piranha;

// Mock key which is not convertible to other key types.
struct mock_key {
    mock_key() = default;
    mock_key(const mock_key &) = default;
    mock_key(mock_key &&) noexcept;
    mock_key &operator=(const mock_key &) = default;
    mock_key &operator=(mock_key &&) noexcept;
    mock_key(const symbol_fset &);
    bool operator==(const mock_key &) const;
    bool operator!=(const mock_key &) const;
    bool is_compatible(const symbol_fset &) const noexcept;
    mock_key merge_symbols(const symbol_idx_fmap<symbol_fset> &, const symbol_fset &) const;
    void print(std::ostream &, const symbol_fset &) const;
    void print_tex(std::ostream &, const symbol_fset &) const;
    void trim_identify(std::vector<char> &, const symbol_fset &) const;
    mock_key trim(const std::vector<char> &, const symbol_fset &) const;
};

// Good one, depending on the other key type.
struct mock_key_00 {
    mock_key_00() = default;
    mock_key_00(const mock_key_00 &) = default;
    mock_key_00(mock_key_00 &&) noexcept;
    mock_key_00 &operator=(const mock_key_00 &) = default;
    mock_key_00 &operator=(mock_key_00 &&) noexcept;
    mock_key_00(const symbol_fset &);
    mock_key_00(const mock_key &, const symbol_fset &);
    bool operator==(const mock_key_00 &) const;
    bool operator!=(const mock_key_00 &) const;
    bool is_compatible(const symbol_fset &) const noexcept;
    mock_key_00 merge_symbols(const symbol_idx_fmap<symbol_fset> &, const symbol_fset &) const;
    void print(std::ostream &, const symbol_fset &) const;
    void print_tex(std::ostream &, const symbol_fset &) const;
    void trim_identify(std::vector<char> &, const symbol_fset &) const;
    mock_key_00 trim(const std::vector<char> &, const symbol_fset &) const;
};

namespace std
{

template <>
struct hash<mock_key> {
    std::size_t operator()(const mock_key &) const;
};

template <>
struct hash<mock_key_00> {
    std::size_t operator()(const mock_key_00 &) const;
};
}

namespace piranha
{

template <>
class key_is_one_impl<mock_key>
{
public:
    bool operator()(const mock_key &, const symbol_fset &) const;
};

template <>
class key_is_one_impl<mock_key_00>
{
public:
    bool operator()(const mock_key_00 &, const symbol_fset &) const;
};
}

BOOST_AUTO_TEST_CASE(key_is_convertible_test_00)
{
    BOOST_CHECK(is_key<mock_key>::value);
    BOOST_CHECK((!key_is_convertible<mock_key, mock_key>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key, mock_key &>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key &, mock_key &&>::value));
    BOOST_CHECK(is_key<mock_key_00>::value);
    BOOST_CHECK((!key_is_convertible<mock_key_00, mock_key_00>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key_00, mock_key_00 &>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key_00 &, mock_key_00 &&>::value));
    BOOST_CHECK((!key_is_convertible<mock_key, mock_key_00>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key, mock_key_00 &>::value));
    BOOST_CHECK((!key_is_convertible<const mock_key &, mock_key_00 &&>::value));
    BOOST_CHECK((key_is_convertible<mock_key_00, mock_key>::value));
    BOOST_CHECK((key_is_convertible<const mock_key_00, mock_key &>::value));
    BOOST_CHECK((key_is_convertible<const mock_key_00 &, mock_key &&>::value));
}
