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

#ifndef PIRANHA_IS_CF_HPP
#define PIRANHA_IS_CF_HPP

#include <type_traits>

#include <piranha/config.hpp>
#include <piranha/detail/init.hpp>
#include <piranha/math.hpp>
#include <piranha/math/is_zero.hpp>
#include <piranha/print_coefficient.hpp>
#include <piranha/print_tex_coefficient.hpp>
#include <piranha/type_traits.hpp>

namespace piranha
{

/// Type trait to detect coefficient types.
/**
 * This type trait will be \p true if \p T can be used as a coefficient type, \p false otherwise.
 * The requisites for \p T are the following:
 *
 * - it must satisfy piranha::is_container_element,
 * - it must satisfy piranha::has_print_coefficient and piranha::has_print_tex_coefficient,
 * - it must satisfy piranha::is_is_zero_type and piranha::has_negate,
 * - it must be equality comparable,
 * - it must be addable and subtractable (both binary and in-place forms),
 * - it must be constructible from integer numerals.
 */
template <typename T>
class is_cf
{
    // NOTE: we have to use addlref_t here as we cannot rely on sfinae in case
    // "const T &" turns out to be a malformed expression (e.g., for T void).
    static const bool implementation_defined
        = conjunction<is_container_element<T>, has_print_coefficient<T>, has_print_tex_coefficient<T>,
                      is_is_zero_type<addlref_t<const T>>, has_negate<T>, is_equality_comparable<addlref_t<const T>>,
                      is_addable<addlref_t<const T>>, is_addable_in_place<T>, is_subtractable_in_place<T>,
                      is_subtractable<T>, std::is_constructible<T, const int &>>::value;

public:
    /// Value of the type trait.
    static constexpr bool value = implementation_defined;
};

#if PIRANHA_CPLUSPLUS < 201703L

template <typename T>
constexpr bool is_cf<T>::value;

#endif
} // namespace piranha

#endif
