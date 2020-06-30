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

#ifndef PIRANHA_SERIES_HPP
#define PIRANHA_SERIES_HPP

#include <algorithm>
#include <array>
#include <boost/iostreams/copy.hpp>
#include <boost/iostreams/filter/bzip2.hpp>
#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iterator/indirect_iterator.hpp>
#include <boost/iterator/transform_iterator.hpp>
#include <boost/numeric/conversion/cast.hpp>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <fstream>
#include <functional>
#include <ios>
#include <iostream>
#include <iterator>
#include <limits>
#include <memory>
#include <mutex>
#include <sstream>
#include <stdexcept>
#include <string>
#include <tuple>
#include <type_traits>
#include <unordered_map>
#include <utility>
#include <vector>

#include <piranha/config.hpp>
#include <piranha/convert_to.hpp>
#include <piranha/detail/debug_access.hpp>
#include <piranha/detail/init.hpp>
#include <piranha/detail/series_fwd.hpp>
#include <piranha/detail/sfinae_types.hpp>
#include <piranha/exceptions.hpp>
#include <piranha/hash_set.hpp>
#include <piranha/integer.hpp>
#include <piranha/invert.hpp>
#include <piranha/is_cf.hpp>
#include <piranha/key/key_is_one.hpp>
#include <piranha/key_is_convertible.hpp>
#include <piranha/math.hpp>
#include <piranha/math/cos.hpp>
#include <piranha/math/is_zero.hpp>
#include <piranha/math/pow.hpp>
#include <piranha/math/sin.hpp>
#include <piranha/print_coefficient.hpp>
#include <piranha/print_tex_coefficient.hpp>
#include <piranha/s11n.hpp>
#include <piranha/safe_cast.hpp>
#include <piranha/series_multiplier.hpp>
#include <piranha/settings.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/term.hpp>
#include <piranha/type_traits.hpp>

namespace piranha
{

inline namespace impl
{

// Fwd declaration.
template <typename S1, typename S2, typename F>
auto series_merge_f(S1 &&s1, S2 &&s2, const F &f) -> decltype(f(std::forward<S1>(s1), std::forward<S2>(s2)));
} // namespace impl

namespace detail
{

// NOTE: this needs to go here, instead of in the series class as private method,
// because of a bug in GCC 4.7/4.8:
// http://gcc.gnu.org/bugzilla/show_bug.cgi?id=53137
template <typename Term, typename Derived>
inline std::pair<typename Term::cf_type, Derived> pair_from_term(const symbol_fset &s, const Term &t)
{
    typedef typename Term::cf_type cf_type;
    Derived retval;
    retval.set_symbol_set(s);
    retval.insert(Term(cf_type(1), t.m_key));
    return std::make_pair(t.m_cf, std::move(retval));
}

// Apply a functor to a single-coefficient series. This is in the spirit of the old apply_cf_functor method,
// and it is used in some of the math:: overrides below.
template <typename Functor, typename RetT, typename T>
inline RetT apply_cf_functor(const T &s)
{
    using ret_term_type = typename RetT::term_type;
    using orig_cf_type = typename T::term_type::cf_type;
    using ret_key_type = typename ret_term_type::key_type;
    if (!s.is_single_coefficient()) {
        piranha_throw(std::invalid_argument,
                      std::string("cannot compute ") + Functor::name + ", series is not single-coefficient");
    }
    RetT retval;
    Functor f;
    if (s.empty()) {
        retval.insert(ret_term_type(f(orig_cf_type(0)), ret_key_type(symbol_fset{})));
    } else {
        retval.insert(ret_term_type(f(s._container().begin()->m_cf), ret_key_type(symbol_fset{})));
    }
    return retval;
}
} // namespace detail

/// Type trait to detect series types.
/**
 * This type trait will be true if \p T is an instance of piranha::series which satisfies piranha::is_container_element.
 */
// NOTE: runtime/other requirements:
// - a def cted series is empty (this is used, e.g., in the series multiplier);
// - typedefs in series should not be overridden.
template <typename T>
class is_series
{
    static const bool implementation_defined
        = conjunction<std::is_base_of<detail::series_tag, T>, is_container_element<T>>::value;

public:
    /// Value of the type trait.
    static const bool value = implementation_defined;
};

template <typename T>
const bool is_series<T>::value;

// Implementation of the series_is_rebindable tt.
inline namespace impl
{

template <typename T, typename Cf>
using series_rebind_t = typename uncvref_t<T>::template rebind<uncvref_t<Cf>>;

template <typename T, typename Cf>
using series_rebind_cf_t = typename series_rebind_t<T, Cf>::term_type::cf_type;

template <typename T, typename Cf, typename = void>
struct series_is_rebindable_impl {
    static const bool value = conjunction<is_series<detected_t<series_rebind_t, T, Cf>>,
                                          std::is_same<detected_t<series_rebind_cf_t, T, Cf>, uncvref_t<Cf>>>::value;
};

template <typename T, typename Cf>
struct series_is_rebindable_impl<
    T, Cf, enable_if_t<disjunction<negation<is_cf<uncvref_t<Cf>>>, negation<is_series<uncvref_t<T>>>>::value>> {
    static const bool value = false;
};
} // namespace impl

/// Check if a series can be rebound.
/**
 * Rebinding a series type \p T means to change its coefficient type to \p Cf. The ability of a series
 * to be rebound is signalled by the presence of a <tt>rebind</tt> template alias defined within the series class.
 *
 * A series is considered rebindable if all the following conditions hold:
 * - there exists a template alias <tt>rebind</tt> in the series type \p T,
 * - <tt>T::rebind<Cf></tt> is a series types,
 * - the coefficient type of <tt>T::rebind<Cf></tt> is \p Cf.
 *
 * \p T and \p Cf are considered after the removal of cv/reference qualifiers in this type trait.
 * If \p T is not an instance of piranha::series or \p Cf does not satisfy piranha::is_cf, then the value of the type
 * trait will be \p false.
 */
// NOTE: the behaviour when T and Cf do not satisfy the requirements is to allow this type trait
// to be used an all types. The trait is used in the metaprogramming of generic series arithmetics,
// and if we ever move the operators outside the series_operators class it is convenient to have this
// class not fire static asserts when non-series arguments are substituted.
template <typename T, typename Cf>
struct series_is_rebindable {
private:
    static const bool implementation_defined = series_is_rebindable_impl<T, Cf>::value;

public:
    /// Value of the type trait.
    static const bool value = implementation_defined;
};

template <typename T, typename Cf>
const bool series_is_rebindable<T, Cf>::value;

inline namespace impl
{

// Implementation of the series_rebind alias, with SFINAE to soft-disable it in case
// the series is not rebindable.
template <typename T, typename Cf>
using series_rebind_implementation = enable_if_t<series_is_rebindable<T, Cf>::value, series_rebind_t<T, Cf>>;
} // namespace impl

/// Rebind series.
/**
 * Utility alias to rebind series \p T to coefficient \p Cf. See piranha::series_is_rebindable for an explanation.
 * In addition to being a shortcut to the \p rebind alias in \p T (if present), the implementation will
 * also check that \p T and \p Cf satisfy the piranha::series_is_rebindable type traits.
 */
template <typename T, typename Cf>
using series_rebind = series_rebind_implementation<T, Cf>;

/// Series recursion index.
/**
 * The recursion index measures how many times the coefficient of a series types is itself a series type.
 * The algorithm works as follows:
 * - if \p T is not a series type, the recursion index is 0,
 * - else, the recursion index of \p T is the recursion index of its coefficient type plus one.
 *
 * The decay type of \p T is considered in this type trait.
 */
template <typename T, typename = void>
class series_recursion_index
{
    static const std::size_t implementation_defined = 0u;

public:
    /// Value of the recursion index.
    static const std::size_t value = implementation_defined;
};

template <typename T, typename Enable>
const std::size_t series_recursion_index<T, Enable>::value;

template <typename T>
class series_recursion_index<
    T, typename std::enable_if<std::is_base_of<detail::series_tag, typename std::decay<T>::type>::value>::type>
{
    using cf_type = typename std::decay<T>::type::term_type::cf_type;
    static_assert(series_recursion_index<cf_type>::value < std::numeric_limits<std::size_t>::max(), "Overflow error.");

public:
    static const std::size_t value = static_cast<std::size_t>(series_recursion_index<cf_type>::value + 1u);
};

template <typename T>
const std::size_t series_recursion_index<
    T, typename std::enable_if<std::is_base_of<detail::series_tag, typename std::decay<T>::type>::value>::type>::value;

/// Type trait to detect the availability of a series multiplier.
/**
 * This type trait will be \p true if a piranha::series_multiplier of \p Series can be constructed and used
 * (as described in the piranha::series_multiplier documentation), \p false otherwise. The decay type of \p Series,
 * which must satisfy piranha::is_series, is considered by this type trait.
 */
template <typename Series>
class series_has_multiplier : detail::sfinae_types
{
    using Sd = typename std::decay<Series>::type;
    PIRANHA_TT_CHECK(is_series, Sd);
    template <typename T>
    static auto test(const T &s) -> decltype(series_multiplier<T>(s, s)());
    static no test(...);
    static const bool implementation_defined = std::is_same<decltype(test(std::declval<Sd>())), Sd>::value;

public:
    /// Value of the type trait.
    static const bool value = implementation_defined;
};

template <typename Series>
const bool series_has_multiplier<Series>::value;

namespace detail
{

// Some notes on this machinery:
// - this is only for determining the type of the result, but it does not guarantee that we can actually compute it.
//   In general we should separate the algorithmic requirements from the determination of the type. Note that we still
//   use the operators on the coefficients to determine the return type, but that's inevitable.

// Alias for getting the cf type from a series. Will generate a type error if S is not a series.
// NOTE: the is_series check is an extra safe guard to really assert we are
// operating on series. Without the checks, in theory classes with internal term_type and similar typedefs
// could trigger no errors.
template <typename S>
using bso_cf_t = typename std::enable_if<is_series<S>::value, typename S::term_type::cf_type>::type;

// Result of a generic arithmetic binary operation:
// - 0 for addition,
// - 1 for subtraction,
// - 2 for multiplication,
// - 3 for division.
// No type is defined if the operation is not supported by the involved types.
template <typename, typename, int, typename = void>
struct op_result {
};

template <typename T, typename U>
struct op_result<T, U, 0, typename std::enable_if<is_addable<addlref_t<const T>, addlref_t<const U>>::value>::type> {
    using type = decltype(std::declval<const T &>() + std::declval<const U &>());
};

template <typename T, typename U>
struct op_result<T, U, 1, typename std::enable_if<is_subtractable<T, U>::value>::type> {
    using type = decltype(std::declval<const T &>() - std::declval<const U &>());
};

template <typename T, typename U>
struct op_result<T, U, 2, typename std::enable_if<is_multipliable<T, U>::value>::type> {
    using type = decltype(std::declval<const T &>() * std::declval<const U &>());
};

template <typename T, typename U>
struct op_result<T, U, 3, typename std::enable_if<is_divisible<T, U>::value>::type> {
    using type = decltype(std::declval<const T &>() / std::declval<const U &>());
};

// Coefficient type in a binary arithmetic operation between two series with the same recursion index.
// Will generate a type error if S1 or S2 are not series with same recursion index or if their coefficients
// do not support the operation.
template <typename S1, typename S2, int N>
using bso_cf_op_t = typename std::enable_if<series_recursion_index<S1>::value == series_recursion_index<S2>::value
                                                && series_recursion_index<S1>::value != 0u,
                                            typename op_result<bso_cf_t<S1>, bso_cf_t<S2>, N>::type>::type;

// Coefficient type in a mixed binary arithmetic operation in which the first operand has recursion index
// greater than the second. Will generate a type error if S does not have a rec. index > T, or if the coefficient
// of S cannot be opped to T.
template <typename S, typename T, int N>
using bsom_cf_op_t = typename std::enable_if<(series_recursion_index<S>::value > series_recursion_index<T>::value),
                                             typename op_result<bso_cf_t<S>, T, N>::type>::type;

// Default case is empty for SFINAE.
template <typename, typename, int, typename = void>
struct binary_series_op_return_type {
};

// Case 0:
// a. both are series with same recursion index,
// b. the coefficients are the same and the op results in the same coefficient,
// c. the two series types are equal.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        // NOTE: in all these checks, every access to S1 and S2 should not incur in hard
                                        // errors:
                                        // - the series recursion index works on all types safely,
                                        // - the various _t aliases are SFINAE friendly - if S1 or S2 are not series
                                        // with the appropriate characteristics,
                                        //   or the coefficients are not oppable, etc., there should be a soft error.
                                        /*a*/ series_recursion_index<S1>::value == series_recursion_index<S2>::value
                                        && series_recursion_index<S1>::value != 0u &&
                                        /*b*/ std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S1>>::value
                                        && std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S2>>::value &&
                                        /*c*/ std::is_same<S1, S2>::value>::type> {
    using type = S1;
    static const unsigned value = 0u;
};

// Case 1:
// a. both are series with same recursion index,
// b. the coefficients are not the same and the op results in the first coefficient.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ series_recursion_index<S1>::value == series_recursion_index<S2>::value
                                        && series_recursion_index<S1>::value != 0u &&
                                        /*b*/ std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S1>>::value
                                        && !std::is_same<bso_cf_t<S1>, bso_cf_t<S2>>::value>::type> {
    using type = S1;
    static const unsigned value = 1u;
};

// Case 2:
// a. both are series with same recursion index,
// b. the coefficients are not the same and the op results in the second coefficient.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ series_recursion_index<S1>::value == series_recursion_index<S2>::value
                                        && series_recursion_index<S1>::value != 0u &&
                                        /*b*/ std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S2>>::value
                                        && !std::is_same<bso_cf_t<S1>, bso_cf_t<S2>>::value>::type> {
    using type = S2;
    static const unsigned value = 2u;
};

// Case 3:
// a. both are series with same recursion index,
// b. the coefficient op results in something else than first or second cf,
// c. both series are rebindable to the new coefficient, yielding the same series.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ series_recursion_index<S1>::value == series_recursion_index<S2>::value
                                        && series_recursion_index<S1>::value != 0u &&
                                        /*b*/ !std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S1>>::value
                                        && !std::is_same<bso_cf_op_t<S1, S2, N>, bso_cf_t<S2>>::value
                                        &&
                                        /*c*/ std::is_same<series_rebind<S1, bso_cf_op_t<S1, S2, N>>,
                                                           series_rebind<S2, bso_cf_op_t<S1, S2, N>>>::value>::type> {
    using type = series_rebind<S1, bso_cf_op_t<S1, S2, N>>;
    static const unsigned value = 3u;
};

// Case 4:
// a. the first operand has recursion index greater than the second operand,
// b. the op of the coefficient of S1 by S2 results in the coefficient of S1.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ (series_recursion_index<S1>::value > series_recursion_index<S2>::value) &&
                                        /*b*/ std::is_same<bsom_cf_op_t<S1, S2, N>, bso_cf_t<S1>>::value>::type> {
    using type = S1;
    static const unsigned value = 4u;
};

// Case 5:
// a. the first operand has recursion index greater than the second operand,
// b. the op of the coefficient of S1 by S2 results in a type T different from the coefficient of S1,
// c. S1 is rebindable to T.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ (series_recursion_index<S1>::value > series_recursion_index<S2>::value) &&
                                        /*b*/ !std::is_same<bsom_cf_op_t<S1, S2, N>, bso_cf_t<S1>>::value &&
                                        /*c*/ series_is_rebindable<S1, bsom_cf_op_t<S1, S2, N>>::value>::type> {
    using type = series_rebind<S1, bsom_cf_op_t<S1, S2, N>>;
    static const unsigned value = 5u;
};

// Case 6 (symmetric of 4):
// a. the second operand has recursion index greater than the first operand,
// b. the op of the coefficient of S2 by S1 results in the coefficient of S2.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ (series_recursion_index<S2>::value > series_recursion_index<S1>::value) &&
                                        /*b*/ std::is_same<bsom_cf_op_t<S2, S1, N>, bso_cf_t<S2>>::value>::type> {
    using type = S2;
    static const unsigned value = 6u;
};

// Case 7 (symmetric of 5):
// a. the second operand has recursion index greater than the first operand,
// b. the op of the coefficient of S2 by S1 results in a type T different from the coefficient of S2,
// c. S2 is rebindable to T.
template <typename S1, typename S2, int N>
struct binary_series_op_return_type<S1, S2, N,
                                    typename std::enable_if<
                                        /*a*/ (series_recursion_index<S2>::value > series_recursion_index<S1>::value) &&
                                        /*b*/ !std::is_same<bsom_cf_op_t<S2, S1, N>, bso_cf_t<S2>>::value &&
                                        /*c*/ series_is_rebindable<S2, bsom_cf_op_t<S2, S1, N>>::value>::type> {
    using type = series_rebind<S2, bsom_cf_op_t<S2, S1, N>>;
    static const unsigned value = 7u;
};
} // namespace detail

/// Series operators.
/**
 * This class contains the arithmetic and comparison operator overloads for piranha::series instances. The operators
 * are implemented as inline friend functions and they will be found via argument-dependent name lookup when at least
 * one of the two operands is an instance of piranha::series.
 *
 * The operators defined here, similarly to the builtin operators in C++, promote one or both operands to a common
 * type, if necessary, before actually performing the operation. The promotion rules are dependent on
 * the recursion indices and coefficient types of the series, and they rely on the series rebinding mechanism to promote
 * a series as needed (see piranha::series_is_rebindable and piranha::series_recursion_index).
 *
 * These are the scenarios handled by the type promotion mechanism:
 * - the two arguments are of the same series type and the operator on the coefficient type of the series results in the
 * same coefficient type.
 *   In this case there is no type promotion;
 * - both series arguments have the same recursion index, different coefficients, and the operator on the coefficient
 * types results in the first
 *   (resp. second) coefficient type. In this case the result of the operations is the first (resp. second) series type;
 * - both series arguments have the same recursion index, different coefficients, and the operator on the coefficient
 * types results in something
 *   other than the first or second coefficient type. In this case both series are promoted to a type resulting from the
 * rebinding of the two
 *   series to the resulting coefficient type;
 * - the first (resp. second) argument has recursion index greater than the second (resp. first) one, and the result
 * type of the operator on the coefficient
 *   type of the first (resp. second) argument and the second (resp. first) argument is the coefficient type of the
 * first (resp. second) argument. In this case,
 *   the second (resp. first) argument is promoted to the first (resp. second);
 * - the first (resp. second) argument has recursion index greater than the second (resp. first) one, and the result
 * type of the operator on the coefficient
 *   type of the first (resp. second) argument and the second (resp. first) argument is something other than the
 * coefficient type of the first (resp. second) argument.
 *   In this case, both arguments are promoted to a type resulting from the rebinding of the first (resp. second)
 * argument to the resulting coefficient type.
 *
 * If any necessary conversion is not possible, either because the series are not rebindable or they do not support the
 * needed constructors, the operators
 * are disabled. The operators are also disabled if any operation needed by the implementation is not supported, or if
 * an ambiguity arises in the type
 * promotion algorithm (e.g., two series with same recursion index, same coefficient type which does not trigger any
 * promotion, and different key types).
 *
 * A few things to note about the operators implemented within this class:
 * - in case two series arguments have different symbol sets, either one or both series will be copied in a new series
 * in which the symbols
 *   have been merged, and the operation will be performed on those series instead;
 * - in-place arithmetic operators are implemented as binary operators plus move-assignment (and they are thus disabled
 * if either the corresponding
 *   binary operation or the assignment are invalid);
 * - series multiplication requires the coefficient types to be multipliable and a valid specialisation of
 * piranha::series_multiplier for the
 *   promoted series type;
 * - division is implemented only when it reduces to coefficient division (true series division may be implemented in
 * specific series types,
 *   e.g., piranha::polynomial);
 * - the comparison operators will use <tt>operator+()</tt> on the coefficient types to determine if any type promotion
 * is necessary
 *   before performing the comparison.
 *
 * The operators are implemented using variadic templates in order to allow overriding them via non-variadic overloads.
 */
class series_operators
{
    // A couple of handy aliases.
    // NOTE: we need both bso_type and common_type because bso gives access to the ::value
    // member to decide on the implementation, common_type is handy because it's shorter
    // than using ::type on the bso.
    template <typename T, typename U, int N>
    using bso_type
        = detail::binary_series_op_return_type<typename std::decay<T>::type, typename std::decay<U>::type, N>;
    template <typename T, typename U, int N>
    using series_common_type = typename bso_type<T, U, N>::type;
    // Main implementation function for add/sub: all the possible cases end up here eventually, after
    // any necessary conversion.
    template <bool Sign, typename T, typename U>
    static series_common_type<T, U, 0> binary_add_impl(T &&x, U &&y)
    {
        // This is the same as T and U.
        using ret_type = series_common_type<T, U, 0>;
        static_assert(std::is_same<typename std::decay<T>::type, ret_type>::value, "Invalid type.");
        static_assert(std::is_same<typename std::decay<U>::type, ret_type>::value, "Invalid type.");
        // Init the return value from the first operand.
        // NOTE: this is always possible to do, a series is always copy/move-constructible.
        ret_type retval(std::forward<T>(x));
        // NOTE: here we cover the corner case in which x and y are the same object.
        // This could be problematic in the algorithm below if retval was move-cted from
        // x, since in such a case y will also be erased. This will happen if one does
        // std::move(a) + std::move(a).
        if (unlikely(&x == &y)) {
            retval.template merge_terms<Sign>(retval);
            return retval;
        }
        // NOTE: we do not use x any more, as it might have been moved.
        if (likely(retval.m_symbol_set == y.m_symbol_set)) {
            retval.template merge_terms<Sign>(std::forward<U>(y));
        } else {
            // Let's fix the args of the first series, if needed.
            const auto merge = ss_merge(retval.m_symbol_set, y.m_symbol_set);
            if (std::get<0>(merge) != retval.m_symbol_set) {
                // This is a move assignment, always possible.
                retval = retval.merge_arguments(std::get<0>(merge), std::get<1>(merge));
            }
            // Fix the args of the second series.
            if (std::get<0>(merge) != y.m_symbol_set) {
                retval.template merge_terms<Sign>(y.merge_arguments(std::get<0>(merge), std::get<2>(merge)));
            } else {
                retval.template merge_terms<Sign>(std::forward<U>(y));
            }
        }
        return retval;
    }
    // NOTE: this case has no special algorithmic requirements, the base requirements for cf and series types
    // already cover everything (including the needeed series constructors and cf arithmetic operators).
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 0>::value == 0u, int>::type = 0>
    static series_common_type<T, U, 0> dispatch_binary_add(T &&x, U &&y)
    {
        return binary_add_impl<true>(std::forward<T>(x), std::forward<U>(y));
    }
    // NOTE: this covers two cases:
    // - equal recursion, first series wins,
    // - first higher recursion, coefficient of first series wins,
    // In both cases we need to construct a T from y.
    template <typename T, typename U,
              typename std::enable_if<
                  (bso_type<T, U, 0>::value == 1u || bso_type<T, U, 0>::value == 4u) &&
                      // In order for the implementation to work, we need to be able to build T from U.
                      std::is_constructible<typename std::decay<T>::type, const typename std::decay<U>::type &>::value,
                  int>::type
              = 0>
    static series_common_type<T, U, 0> dispatch_binary_add(T &&x, U &&y)
    {
        typename std::decay<T>::type y1(std::forward<U>(y));
        return dispatch_binary_add(std::forward<T>(x), std::move(y1));
    }
    // Symmetric of 1.
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 0>::value == 2u, int>::type = 0>
    static auto dispatch_binary_add(T &&x, U &&y)
        -> decltype(dispatch_binary_add(std::forward<U>(y), std::forward<T>(x)))
    {
        return dispatch_binary_add(std::forward<U>(y), std::forward<T>(x));
    }
    // Two cases:
    // - equal series recursion, 3rd coefficient type generated,
    // - first higher recursion, coefficient result is different from first series.
    // In both cases we need to promote both operands to a 3rd type.
    template <
        typename T, typename U,
        typename std::enable_if<
            (bso_type<T, U, 0>::value == 3u || bso_type<T, U, 0>::value == 5u) &&
                // In order for the implementation to work, we need to be able to build the
                // common type from T and U.
                std::is_constructible<series_common_type<T, U, 0>, const typename std::decay<T>::type &>::value
                && std::is_constructible<series_common_type<T, U, 0>, const typename std::decay<U>::type &>::value,
            int>::type
        = 0>
    static series_common_type<T, U, 0> dispatch_binary_add(T &&x, U &&y)
    {
        series_common_type<T, U, 0> x1(std::forward<T>(x));
        series_common_type<T, U, 0> y1(std::forward<U>(y));
        return dispatch_binary_add(std::move(x1), std::move(y1));
    }
    // 6 and 7 are symmetric cases of 4 and 5. Just reverse the operands.
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 0>::value == 6u || bso_type<T, U, 0>::value == 7u, int>::type = 0>
    static auto dispatch_binary_add(T &&x, U &&y)
        -> decltype(dispatch_binary_add(std::forward<U>(y), std::forward<T>(x)))
    {
        return dispatch_binary_add(std::forward<U>(y), std::forward<T>(x));
    }
    // NOTE: here we use the version of binary_add with const references. The idea
    // here is that any overload other than the const references one is an optimisation detail
    // and that for the operator to be enabled the "canonical" form of addition operator must be available.
    // The consequence is that if, for instance, only a move constructor is available, the operator
    // will anyway be disabled even if technically we could still perform the computation.
    template <typename T, typename... U>
    using binary_add_type = decltype(dispatch_binary_add(std::declval<const typename std::decay<T>::type &>(),
                                                         std::declval<const typename std::decay<U>::type &>()...));
    // In-place add. Default implementation is to do simply x = x + y, if possible.
    // NOTE: this should also be able to handle int += series, if we ever implement it.
    template <typename T, typename U,
              typename std::enable_if<std::is_assignable<T &, binary_add_type<T, U>>::value, int>::type = 0>
    static T &dispatch_in_place_add(T &x, U &&y)
    {
        // NOTE: we can move x here, as it is going to be re-assigned anyway.
        x = dispatch_binary_add(std::move(x), std::forward<U>(y));
        return x;
    }
    // NOTE: as explained above, here the enabling mechanism essentially recasts U as a const reference,
    // because we require in-place addition to work with the second argument as a const reference (as that is
    // the canonical form of in-place addition). The actual call will however do perfect forwarding, so optimisations
    // are still possible.
    template <typename T, typename... U>
    using in_place_add_type
        = decltype(dispatch_in_place_add(std::declval<T &>(), std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using in_place_add_enabler = typename std::enable_if<true_tt<in_place_add_type<T, U...>>::value, int>::type;
    // Subtraction.
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 1>::value == 0u, int>::type = 0>
    static series_common_type<T, U, 1> dispatch_binary_sub(T &&x, U &&y)
    {
        return binary_add_impl<false>(std::forward<T>(x), std::forward<U>(y));
    }
    template <typename T, typename U,
              typename std::enable_if<(bso_type<T, U, 1>::value == 1u || bso_type<T, U, 1>::value == 4u)
                                          && std::is_constructible<typename std::decay<T>::type,
                                                                   const typename std::decay<U>::type &>::value,
                                      int>::type
              = 0>
    static series_common_type<T, U, 1> dispatch_binary_sub(T &&x, U &&y)
    {
        typename std::decay<T>::type y1(std::forward<U>(y));
        return dispatch_binary_sub(std::forward<T>(x), std::move(y1));
    }
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 1>::value == 2u, int>::type = 0>
    static auto dispatch_binary_sub(T &&x, U &&y)
        -> decltype(dispatch_binary_sub(std::forward<U>(y), std::forward<T>(x)))
    {
        auto retval = dispatch_binary_sub(std::forward<U>(y), std::forward<T>(x));
        retval.negate();
        return retval;
    }
    template <
        typename T, typename U,
        typename std::enable_if<
            (bso_type<T, U, 1>::value == 3u || bso_type<T, U, 1>::value == 5u)
                && std::is_constructible<series_common_type<T, U, 1>, const typename std::decay<T>::type &>::value
                && std::is_constructible<series_common_type<T, U, 1>, const typename std::decay<U>::type &>::value,
            int>::type
        = 0>
    static series_common_type<T, U, 1> dispatch_binary_sub(T &&x, U &&y)
    {
        series_common_type<T, U, 1> x1(std::forward<T>(x));
        series_common_type<T, U, 1> y1(std::forward<U>(y));
        return dispatch_binary_sub(std::move(x1), std::move(y1));
    }
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 1>::value == 6u || bso_type<T, U, 1>::value == 7u, int>::type = 0>
    static auto dispatch_binary_sub(T &&x, U &&y)
        -> decltype(dispatch_binary_sub(std::forward<U>(y), std::forward<T>(x)))
    {
        auto retval = dispatch_binary_sub(std::forward<U>(y), std::forward<T>(x));
        retval.negate();
        return retval;
    }
    template <typename T, typename... U>
    using binary_sub_type = decltype(dispatch_binary_sub(std::declval<const typename std::decay<T>::type &>(),
                                                         std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename U,
              typename std::enable_if<std::is_assignable<T &, binary_sub_type<T, U>>::value, int>::type = 0>
    static T &dispatch_in_place_sub(T &x, U &&y)
    {
        x = dispatch_binary_sub(std::move(x), std::forward<U>(y));
        return x;
    }
    template <typename T, typename... U>
    using in_place_sub_type
        = decltype(dispatch_in_place_sub(std::declval<T &>(), std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using in_place_sub_enabler = typename std::enable_if<true_tt<in_place_sub_type<T, U...>>::value, int>::type;
    // Multiplication.
    struct binary_mul_impl {
        template <typename T, typename U>
        series_common_type<T, U, 2> operator()(T &&x, U &&y) const
        {
            return series_multiplier<series_common_type<T, U, 2>>(std::forward<T>(x), std::forward<U>(y))();
        }
    };
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 2>::value == 0u, int>::type = 0>
    static series_common_type<T, U, 2> dispatch_binary_mul(T &&x, U &&y)
    {
        using ret_type = series_common_type<T, U, 2>;
        static_assert(std::is_same<typename std::decay<T>::type, ret_type>::value, "Invalid type.");
        static_assert(std::is_same<typename std::decay<U>::type, ret_type>::value, "Invalid type.");
        return series_merge_f(std::forward<T>(x), std::forward<U>(y), binary_mul_impl{});
    }
    template <typename T, typename U,
              typename std::enable_if<(bso_type<T, U, 2>::value == 1u || bso_type<T, U, 2>::value == 4u)
                                          && std::is_constructible<typename std::decay<T>::type,
                                                                   const typename std::decay<U>::type &>::value,
                                      int>::type
              = 0>
    static series_common_type<T, U, 2> dispatch_binary_mul(T &&x, U &&y)
    {
        typename std::decay<T>::type y1(std::forward<U>(y));
        return dispatch_binary_mul(std::forward<T>(x), std::move(y1));
    }
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 2>::value == 2u, int>::type = 0>
    static auto dispatch_binary_mul(T &&x, U &&y)
        -> decltype(dispatch_binary_mul(std::forward<U>(y), std::forward<T>(x)))
    {
        return dispatch_binary_mul(std::forward<U>(y), std::forward<T>(x));
    }
    template <
        typename T, typename U,
        typename std::enable_if<
            (bso_type<T, U, 2>::value == 3u || bso_type<T, U, 2>::value == 5u)
                && std::is_constructible<series_common_type<T, U, 2>, const typename std::decay<T>::type &>::value
                && std::is_constructible<series_common_type<T, U, 2>, const typename std::decay<U>::type &>::value,
            int>::type
        = 0>
    static series_common_type<T, U, 2> dispatch_binary_mul(T &&x, U &&y)
    {
        series_common_type<T, U, 2> x1(std::forward<T>(x));
        series_common_type<T, U, 2> y1(std::forward<U>(y));
        return dispatch_binary_mul(std::move(x1), std::move(y1));
    }
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 2>::value == 6u || bso_type<T, U, 2>::value == 7u, int>::type = 0>
    static auto dispatch_binary_mul(T &&x, U &&y)
        -> decltype(dispatch_binary_mul(std::forward<U>(y), std::forward<T>(x)))
    {
        return dispatch_binary_mul(std::forward<U>(y), std::forward<T>(x));
    }
    // NOTE: this is the real type from the multiplication, below we put another enable_if to make it conditional
    // on the presence of a series multiplier.
    template <typename T, typename... U>
    using binary_mul_type_ = decltype(dispatch_binary_mul(std::declval<const typename std::decay<T>::type &>(),
                                                          std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using binary_mul_type = typename std::enable_if<series_has_multiplier<binary_mul_type_<T, U...>>::value,
                                                    binary_mul_type_<T, U...>>::type;
    template <typename T, typename U,
              typename std::enable_if<std::is_assignable<T &, binary_mul_type<T, U>>::value, int>::type = 0>
    static T &dispatch_in_place_mul(T &x, U &&y)
    {
        x = dispatch_binary_mul(std::move(x), std::forward<U>(y));
        return x;
    }
    template <typename T, typename... U>
    using in_place_mul_type
        = decltype(dispatch_in_place_mul(std::declval<T &>(), std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using in_place_mul_enabler = typename std::enable_if<true_tt<in_place_mul_type<T, U...>>::value, int>::type;
    // Division.
    // NOTE: the divisibility requirement here is already satisfied in the determination of the return type.
    // The base case: same recursion, no promotion.
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 3>::value == 0u, int>::type = 0>
    static series_common_type<T, U, 3> dispatch_binary_div(T &&x, U &&y)
    {
        using ret_type = series_common_type<T, U, 3>;
        using term_type = typename ret_type::term_type;
        using cf_type = typename term_type::cf_type;
        using key_type = typename term_type::key_type;
        if (!y.is_single_coefficient()) {
            piranha_throw(std::invalid_argument, "divisor in series division does not consist of a single coefficient");
        }
        const auto y_cf = y.empty() ? cf_type(0) : y._container().begin()->m_cf;
        if (x.empty()) {
            ret_type retval;
            retval.insert(term_type{cf_type{0} / y_cf, key_type{retval.get_symbol_set()}});
            return retval;
        }
        return x / y_cf;
    }
    // Same recursion, first coefficient wins.
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 3>::value == 1u
                                          && std::is_constructible<typename std::decay<T>::type,
                                                                   const typename std::decay<U>::type &>::value,
                                      int>::type
              = 0>
    static series_common_type<T, U, 3> dispatch_binary_div(T &&x, U &&y)
    {
        typename std::decay<T>::type y1(std::forward<U>(y));
        return dispatch_binary_div(std::forward<T>(x), std::move(y1));
    }
    // Two cases:
    // - same recursion, second coefficient wins,
    // - second operand has higher recursion, first operand promotes to second.
    template <typename T, typename U,
              typename std::enable_if<(bso_type<T, U, 3>::value == 2u || bso_type<T, U, 3>::value == 6u)
                                          && std::is_constructible<typename std::decay<U>::type,
                                                                   const typename std::decay<T>::type &>::value,
                                      int>::type
              = 0>
    static series_common_type<T, U, 3> dispatch_binary_div(T &&x, U &&y)
    {
        typename std::decay<U>::type x1(std::forward<T>(x));
        return dispatch_binary_div(std::move(x1), std::forward<U>(y));
    }
    // Two cases:
    // - same recursion, need promoted coefficient,
    // - second operand has higher recursion, both promote to a third type.
    template <
        typename T, typename U,
        typename std::enable_if<
            (bso_type<T, U, 3>::value == 3u || bso_type<T, U, 3>::value == 7u)
                && std::is_constructible<series_common_type<T, U, 3>, const typename std::decay<T>::type &>::value
                && std::is_constructible<series_common_type<T, U, 3>, const typename std::decay<U>::type &>::value,
            int>::type
        = 0>
    static series_common_type<T, U, 3> dispatch_binary_div(T &&x, U &&y)
    {
        series_common_type<T, U, 0> x1(std::forward<T>(x));
        series_common_type<T, U, 0> y1(std::forward<U>(y));
        return dispatch_binary_div(std::move(x1), std::move(y1));
    }
    // The implementation of these two cases 4 and 5 is different from the other operations, as we cannot promote to a
    // common type (true series division is not implemented).
    // NOTE: the use of the old syntax for the enable_if with nullptr is because of a likely GCC bug:
    // https://gcc.gnu.org/bugzilla/show_bug.cgi?id=59366
    // In real.hpp, there are a few uses of operator/= on reals (e.g., in binary_div) *before* the is_zero_impl
    // specialisation for real is declared. These operators are immediately instantiated when parsed because they are
    // not templated. During the overload resolution of operator/=, the operator defined in this class is considered -
    // which is wrong, as the operators here should be found only via ADL and this class is in no way associated to
    // real. What happens then is that is_zero_impl is instantiated for a real type before the real specialisation is
    // seen, and GCC errors out. I *think* the nullptr syntax works because the bso_type enable_if disables the function
    // before is_is_zero_type is encountered, or maybe because it does not participate in template deduction.
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 3>::value == 4u, int>::type = 0>
    static series_common_type<T, U, 3> dispatch_binary_div(
        T &&x, U &&y,
        typename std::enable_if<is_is_zero_type<const typename std::decay<U>::type &>::value>::type * = nullptr)
    {
        using ret_type = series_common_type<T, U, 3>;
        if (x.empty()) {
            // Special case: if x is empty, then we will just insert a single term consisting of 0 / y.
            using term_type = typename ret_type::term_type;
            using cf_type = typename term_type::cf_type;
            using key_type = typename term_type::key_type;
            ret_type retval;
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif
            retval.insert(term_type{cf_type{0} / y, key_type{retval.get_symbol_set()}});
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic pop
#endif
            return retval;
        }
        static_assert(std::is_same<typename std::decay<T>::type, ret_type>::value, "Invalid type.");
        // Create a copy of x and work on it. This is always possible.
        ret_type retval(std::forward<T>(x));
        // NOTE: x is not used any more.
        const auto it_f = retval.m_container.end();
        try {
            for (auto it = retval.m_container.begin(); it != it_f;) {
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif
                // NOTE: here the original requirement is that cf / y is defined, but we know
                // that cf / y results in another cf, and we assume always that cf /= y is exactly equivalent
                // to cf = cf / y. And cf must be move-assignable. So this should be possible.
                it->m_cf /= y;
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic pop
#endif
                // NOTE: no need to check for compatibility, as it depends only on the key type and here
                // we are only acting on the coefficient.
                if (unlikely(it->is_zero(retval.m_symbol_set))) {
                    // Erase will return the next iterator.
                    it = retval.m_container.erase(it);
                } else {
                    ++it;
                }
            }
        } catch (...) {
            // In case of errors clear out the series.
            retval.m_container.clear();
            throw;
        }
        return retval;
    }
    // NOTE: the trailing decltype() syntax is used here to make sure we can actually call the other overload of the
    // function
    // in the body.
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 3>::value == 5u
                                          && std::is_constructible<series_common_type<T, U, 3>,
                                                                   const typename std::decay<T>::type &>::value,
                                      int>::type
              = 0>
    static auto dispatch_binary_div(T &&x, U &&y)
        -> decltype(dispatch_binary_div(std::declval<const series_common_type<T, U, 3> &>(), std::forward<U>(y)))
    {
        series_common_type<T, U, 3> x1(std::forward<T>(x));
        return dispatch_binary_div(std::move(x1), std::forward<U>(y));
    }
    template <typename T, typename... U>
    using binary_div_type = decltype(dispatch_binary_div(std::declval<const typename std::decay<T>::type &>(),
                                                         std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename U,
              typename std::enable_if<std::is_assignable<T &, binary_div_type<T, U>>::value, int>::type = 0>
    static T &dispatch_in_place_div(T &x, U &&y)
    {
        x = dispatch_binary_div(std::move(x), std::forward<U>(y));
        return x;
    }
    template <typename T, typename... U>
    using in_place_div_type
        = decltype(dispatch_in_place_div(std::declval<T &>(), std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using in_place_div_enabler = typename std::enable_if<true_tt<in_place_div_type<T, U...>>::value, int>::type;
    // Equality.
    // Low-level implementation of equality.
    template <typename T>
    static bool equality_impl(const T &x, const T &y)
    {
        if (x.size() != y.size()) {
            return false;
        }
        // NOTE: maybe it's possible to write an optimised algorithm
        // in case the sizes coincide: compare bucket by bucket (e.g.,
        // if the buckets at a certain index in the two operands have
        // different sizes, then the series are different). Maybe this could
        // even be moved into hash_set to be its equality operator.
        piranha_assert(x.m_symbol_set == y.m_symbol_set);
        const auto it_f_x = x.m_container.end(), it_f_y = y.m_container.end();
        for (auto it = x.m_container.begin(); it != it_f_x; ++it) {
            const auto tmp_it = y.m_container.find(*it);
            if (tmp_it == it_f_y || tmp_it->m_cf != it->m_cf) {
                return false;
            }
        }
        return true;
    }
    // NOTE: we are using operator+() on the coefficients to determine type conversions.
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 0>::value == 0u, int>::type = 0>
    static bool dispatch_equality(const T &x, const U &y)
    {
        static_assert(std::is_same<T, U>::value, "Invalid types for the equality operator.");
        return series_merge_f(x, y, [](const T &a, const T &b) { return series_operators::equality_impl(a, b); });
    }
    template <typename T, typename U,
              typename std::enable_if<(bso_type<T, U, 0>::value == 1u || bso_type<T, U, 0>::value == 4u)
                                          && std::is_constructible<T, const U &>::value,
                                      int>::type
              = 0>
    static bool dispatch_equality(const T &x, const U &y)
    {
        return dispatch_equality(x, T(y));
    }
    template <typename T, typename U, typename std::enable_if<bso_type<T, U, 0>::value == 2u, int>::type = 0>
    static auto dispatch_equality(const T &x, const U &y) -> decltype(dispatch_equality(y, x))
    {
        return dispatch_equality(y, x);
    }
    template <typename T, typename U,
              typename std::enable_if<(bso_type<T, U, 0>::value == 3u || bso_type<T, U, 0>::value == 5u)
                                          && std::is_constructible<series_common_type<T, U, 0>, const T &>::value
                                          && std::is_constructible<series_common_type<T, U, 0>, const U &>::value,
                                      int>::type
              = 0>
    static bool dispatch_equality(const T &x, const U &y)
    {
        series_common_type<T, U, 0> x1(x);
        series_common_type<T, U, 0> y1(y);
        return dispatch_equality(x1, y1);
    }
    template <typename T, typename U,
              typename std::enable_if<bso_type<T, U, 0>::value == 6u || bso_type<T, U, 0>::value == 7u, int>::type = 0>
    static auto dispatch_equality(const T &x, const U &y) -> decltype(dispatch_equality(y, x))
    {
        return dispatch_equality(y, x);
    }
    template <typename T, typename... U>
    using eq_type = decltype(dispatch_equality(std::declval<const typename std::decay<T>::type &>(),
                                               std::declval<const typename std::decay<U>::type &>()...));
    template <typename T, typename... U>
    using eq_enabler = typename std::enable_if<true_tt<eq_type<T, U...>>::value, int>::type;

public:
    /// Binary addition involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return <tt>x + y</tt>.
     *
     * @throws unspecified any exception thrown by:
     * - any invoked series, coefficient or key constructor,
     * - construction, assignment and other operations on piranha::symbol_fset,
     * - piranha::series::insert(),
     * - piranha::term::is_zero().
     */
    template <typename T, typename... U>
    friend binary_add_type<T, U...> operator+(T &&x, U &&... y)
    {
        return dispatch_binary_add(std::forward<T>(x), std::forward<U>(y)...);
    }
    /// In-place addition involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by operator+().
     */
    template <typename T, typename... U, in_place_add_enabler<T, U...> = 0>
    friend T &operator+=(T &x, U &&... y)
    {
        return dispatch_in_place_add(x, std::forward<U>(y)...);
    }
    /// Binary subtraction involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return <tt>x - y</tt>.
     *
     * @throws unspecified any exception thrown by:
     * - any invoked series, coefficient or key constructor,
     * - construction, assignment and other operations on piranha::symbol_fset,
     * - piranha::series::insert(),
     * - piranha::series::negate().
     */
    template <typename T, typename... U>
    friend binary_sub_type<T, U...> operator-(T &&x, U &&... y)
    {
        return dispatch_binary_sub(std::forward<T>(x), std::forward<U>(y)...);
    }
    /// In-place subtraction involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by operator-().
     */
    template <typename T, typename... U, in_place_sub_enabler<T, U...> = 0>
    friend T &operator-=(T &x, U &&... y)
    {
        return dispatch_in_place_sub(x, std::forward<U>(y)...);
    }
    /// Binary multiplication involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return <tt>x * y</tt>.
     *
     * @throws unspecified any exception thrown by:
     * - any invoked series, coefficient or key constructor,
     * - construction, assignment and other operations on piranha::symbol_fset,
     * - piranha::series::insert(),
     * - the call operator of piranha::series_multiplier.
     */
    template <typename T, typename... U>
    friend binary_mul_type<T, U...> operator*(T &&x, U &&... y)
    {
        return dispatch_binary_mul(std::forward<T>(x), std::forward<U>(y)...);
    }
    /// In-place multiplication involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by operator*().
     */
    template <typename T, typename... U, in_place_mul_enabler<T, U...> = 0>
    friend T &operator*=(T &x, U &&... y)
    {
        return dispatch_in_place_mul(x, std::forward<U>(y)...);
    }
    /// Binary division involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return <tt>x / y</tt>.
     *
     * @throws unspecified any exception thrown by:
     * - any invoked series, coefficient, term and key constructor,
     * - piranha::series::insert(),
     * - piranha::hash_set::erase(),
     * - piranha::term::is_zero(),
     * - the division operator on the coefficient type of the result.
     */
    template <typename T, typename... U>
    friend binary_div_type<T, U...> operator/(T &&x, U &&... y)
    {
        return dispatch_binary_div(std::forward<T>(x), std::forward<U>(y)...);
    }
    /// In-place division involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by operator/().
     */
    template <typename T, typename... U, in_place_div_enabler<T, U...> = 0>
    friend T &operator/=(T &x, U &&... y)
    {
        return dispatch_in_place_div(x, std::forward<U>(y)...);
    }
    /// Equality operator involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * Two series are considered equal if they have the same number of terms and all terms in one series
     * appear in the other.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return \p true if <tt>x == y</tt>, \p false otherwise.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::hash_set::find(),
     * - the comparison operator of the coefficient type,
     * - any invoked series, coefficient or key constructor,
     * - construction, assignment and other operations on piranha::symbol_fset.
     */
    template <typename T, typename... U, eq_enabler<T, U...> = 0>
    friend bool operator==(const T &x, const U &... y)
    {
        return dispatch_equality(x, y...);
    }
    /// Inequality operator involving piranha::series.
    /**
     * \note
     * This operator is enabled only if the algorithm outlined in piranha::series_operators
     * is supported by the arguments.
     *
     * @param x first argument.
     * @param y second argument.
     *
     * @return \p true if <tt>x != y</tt>, \p false otherwise.
     *
     * @throws unspecified any exception thrown by operator==().
     */
    template <typename T, typename... U, eq_enabler<T, U...> = 0>
    friend bool operator!=(const T &x, const U &... y)
    {
        return !dispatch_equality(x, y...);
    }
};

/// Series class.
/**
 * This class contains the arithmetic and comparison operator overloads for piranha::series instances
 * via the parent class piranha::series_operators.
 *
 * ## Type requirements ##
 *
 * - \p Cf and \p Key must be suitable for use in piranha::term.
 * - \p Derived must derive from piranha::series of \p Cf, \p Key and \p Derived.
 * - \p Derived must satisfy piranha::is_series.
 * - \p Derived must satisfy piranha::is_container_element.
 *
 * ## Exception safety guarantee ##
 *
 * Unless otherwise specified, this class provides the strong exception safety guarantee for all operations.
 *
 * ## Move semantics ##
 *
 * Moved-from series are left in a state equivalent to an empty series.
 */
/* TODO:
 * \todo cast operator, to series and non-series types.
 * \todo cast operator would allow to define in-place operators with fundamental types as first operand.
 * \todo filter and transform can probably take arbitrary functors as input, instead of std::function. Just assert the
 * function object's signature.
 * \todo transform needs sfinaeing.
 * TODO new operators:
 * - test with mock_cfs that are not addable to scalars.
 * - the unary + and - operators should probably follow the type promotion rules for consistency.
 */
template <typename Cf, typename Key, typename Derived>
class series : detail::series_tag, series_operators
{
public:
    /// Alias for term type.
    using term_type = term<Cf, Key>;

private:
    // Make friend with all series.
    template <typename, typename, typename>
    friend class series;
    // Make friend with debugging class.
    template <typename>
    friend class debug_access;
    // Make friend with the operators class.
    friend class series_operators;
    // Partial need access to the custom derivatives.
    template <typename, typename>
    friend struct math::partial_impl;
    // Friendship with the series_merge_f helper.
    template <typename S1, typename S2, typename F>
    friend auto impl::series_merge_f(S1 &&s1, S2 &&s2, const F &f)
        -> decltype(f(std::forward<S1>(s1), std::forward<S2>(s2)));

protected:
    /// Container type for terms.
    using container_type = hash_set<term_type>;

private:
    typedef decltype(std::declval<container_type>().evaluate_sparsity()) sparsity_info_type;
    // Insertion.
    template <bool Sign, typename T>
    void dispatch_insertion(T &&term)
    {
        // Debug checks.
        piranha_assert(empty() || m_container.begin()->is_compatible(m_symbol_set));
        // Generate error if term is not compatible.
        if (unlikely(!term.is_compatible(m_symbol_set))) {
            piranha_throw(std::invalid_argument, "cannot insert incompatible term");
        }
        // Discard ignorable term.
        if (unlikely(term.is_zero(m_symbol_set))) {
            return;
        }
        insertion_impl<Sign>(std::forward<T>(term));
    }
    // Cf arithmetics when inserting, normal and move variants.
    template <bool Sign, typename Iterator>
    static void insertion_cf_arithmetics(Iterator &it, const term_type &term)
    {
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif
        if (Sign) {
            it->m_cf += term.m_cf;
        } else {
            it->m_cf -= term.m_cf;
        }
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic pop
#endif
    }
    template <bool Sign, typename Iterator>
    static void insertion_cf_arithmetics(Iterator &it, term_type &&term)
    {
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif
        if (Sign) {
            it->m_cf += std::move(term.m_cf);
        } else {
            it->m_cf -= std::move(term.m_cf);
        }
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic pop
#endif
    }
    // Insert compatible, non-ignorable term.
    template <bool Sign, typename T>
    void insertion_impl(T &&term)
    {
        // NOTE: here we are basically going to reconstruct hash_set::insert() with the goal
        // of optimising things by avoiding one branch.
        // Handle the case of a table with no buckets.
        if (unlikely(!m_container.bucket_count())) {
            m_container._increase_size();
        }
        // Try to locate the element.
        auto bucket_idx = m_container._bucket(term);
        const auto it = m_container._find(term, bucket_idx);
        if (it == m_container.end()) {
            if (unlikely(m_container.size() == std::numeric_limits<size_type>::max())) {
                piranha_throw(std::overflow_error, "maximum number of elements reached");
            }
            // Term is new. Handle the case in which we need to rehash because of load factor.
            if (unlikely(static_cast<double>(m_container.size() + size_type(1u))
                             / static_cast<double>(m_container.bucket_count())
                         > m_container.max_load_factor())) {
                m_container._increase_size();
                // We need a new bucket index in case of a rehash.
                bucket_idx = m_container._bucket(term);
            }
            const auto new_it = m_container._unique_insert(std::forward<T>(term), bucket_idx);
            m_container._update_size(m_container.size() + size_type(1u));
            // Insertion was successful, change sign if requested.
            if (!Sign) {
                try {
                    math::negate(new_it->m_cf);
                    // Check if the term has become ignorable after the negation.
                    if (unlikely(new_it->is_zero(m_symbol_set))) {
                        m_container.erase(new_it);
                    }
                } catch (...) {
                    // Clear up the whole container in case of errors, in order
                    // to avoid having an inconsistent state.
                    m_container.clear();
                    throw;
                }
            }
        } else {
            // Assert the existing term is not ignorable and it is compatible.
            piranha_assert(!it->is_zero(m_symbol_set) && it->is_compatible(m_symbol_set));
            try {
                // The term exists already, update it.
                insertion_cf_arithmetics<Sign>(it, std::forward<T>(term));
                // Check if the term has become ignorable after the modification.
                if (unlikely(it->is_zero(m_symbol_set))) {
                    m_container.erase(it);
                }
            } catch (...) {
                // Clear up the whole container in case of errors, in order
                // to avoid having an inconsistent state.
                m_container.clear();
                throw;
            }
        }
    }
    template <typename T>
    using insert_enabler = enable_if_t<std::is_same<term_type, uncvref_t<T>>::value, int>;
    // Terms merging
    // =============
    // NOTE: ideas to improve the algorithm:
    // - optimization when merging with self: add each coefficient to itself, instead of copying and merging.
    // - optimization when merging with series with same bucket size: avoid computing the destination bucket,
    //   as it will be the same as the original.
    template <bool Sign, typename T>
    void merge_terms_impl0(T &&s)
    {
        // NOTE: here we can take the pointer to series and compare it to this because we know that
        // series derives from the type of this.
        if (unlikely(&s == this)) {
            // If the two series are the same object, we need to make a copy.
            // NOTE: here we are making sure we are doing a real deep copy (as opposed, say, to a move,
            // which could happen if we used std::forward.).
            merge_terms_impl1<Sign>(series<Cf, Key, Derived>(static_cast<const series<Cf, Key, Derived> &>(s)));
        } else {
            merge_terms_impl1<Sign>(std::forward<T>(s));
        }
    }
    // Overload if we cannot move objects from series.
    template <bool Sign, typename T>
    void merge_terms_impl1(T &&s, typename std::enable_if<!is_nonconst_rvalue_ref<T &&>::value>::type * = nullptr)
    {
        const auto it_f = s.m_container.end();
        try {
            for (auto it = s.m_container.begin(); it != it_f; ++it) {
                insert<Sign>(*it);
            }
        } catch (...) {
            // In case of any insertion error, zero out this series.
            m_container.clear();
            throw;
        }
    }
    static void swap_for_merge(container_type &&c1, container_type &&c2, bool &swap)
    {
        piranha_assert(!swap);
        // Do not do anything in case of overflows.
        if (unlikely(c1.size() > std::numeric_limits<size_type>::max() - c2.size())) {
            return;
        }
        // This is the maximum number of terms in the return value.
        const auto max_size = c1.size() + c2.size();
        // This will be the maximum required number of buckets.
        size_type max_n_buckets;
        try {
            piranha_assert(c1.max_load_factor() > 0);
            max_n_buckets
                = boost::numeric_cast<size_type>(std::trunc(static_cast<double>(max_size) / c1.max_load_factor()));
        } catch (...) {
            // Ignore any error on conversions.
            return;
        }
        // Now we swap the containers, if the first one is not enough to contain the expected number of terms
        // and if the second one is larger.
        if (c1.bucket_count() < max_n_buckets && c2.bucket_count() > c1.bucket_count()) {
            container_type tmp(std::move(c1));
            c1 = std::move(c2);
            c2 = std::move(tmp);
            swap = true;
        }
    }
    // This overloads the above in the case we are dealing with two different container types:
    // in such a condition, we won't do any swapping.
    template <typename OtherContainerType>
    static void swap_for_merge(container_type &&, OtherContainerType &&, bool &)
    {
    }
    // Overload if we can move objects from series.
    template <bool Sign, typename T>
    void merge_terms_impl1(T &&s, typename std::enable_if<is_nonconst_rvalue_ref<T &&>::value>::type * = nullptr)
    {
        bool swap = false;
        // Try to steal memory from other.
        swap_for_merge(std::move(m_container), std::move(s.m_container), swap);
        try {
            const auto it_f = s.m_container._m_end();
            for (auto it = s.m_container._m_begin(); it != it_f; ++it) {
                insert<Sign>(std::move(*it));
            }
            // If we swapped the operands and a negative merge was performed, we need to change
            // the signs of all coefficients.
            if (swap && !Sign) {
                const auto it_f2 = m_container.end();
                for (auto it = m_container.begin(); it != it_f2;) {
                    math::negate(it->m_cf);
                    if (unlikely(it->is_zero(m_symbol_set))) {
                        // Erase the invalid term.
                        it = m_container.erase(it);
                    } else {
                        ++it;
                    }
                }
            }
        } catch (...) {
            // In case of any insertion error, zero out both series.
            m_container.clear();
            s.m_container.clear();
            throw;
        }
        // The other series must alway be cleared, since we moved out the terms.
        s.m_container.clear();
    }
    // Merge all terms from another series. Works if s is this (in which case a copy is made). Basic exception safety
    // guarantee.
    template <bool Sign, typename T>
    void merge_terms(T &&s, typename std::enable_if<is_series<typename std::decay<T>::type>::value>::type * = nullptr)
    {
        static_assert(std::is_base_of<series<Cf, Key, Derived>, typename std::decay<T>::type>::value, "Type error.");
        merge_terms_impl0<Sign>(std::forward<T>(s));
    }
    // Generic construction
    // ====================
    template <typename T, typename U = series,
              typename std::enable_if<(series_recursion_index<T>::value < series_recursion_index<U>::value)
                                          && has_convert_to<typename U::term_type::cf_type, T>::value,
                                      int>::type
              = 0>
    void dispatch_generic_constructor(const T &x)
    {
        typedef typename term_type::cf_type cf_type;
        typedef typename term_type::key_type key_type;
        cf_type cf(convert_to<cf_type>(x));
        key_type key(m_symbol_set);
        insert(term_type(std::move(cf), std::move(key)));
    }
    // NOTE: here in principle we could have a type U which is not a series but has a term_type typedef defined,
    // which could incur into a hard error (e.g., via a static_assert in U). Maybe for the future we could think of
    // defining
    // a term_type_t template alias in the spirit of series_rebind, that goes into SFINAE if the argument is not a
    // series.
    // Alternatively, the key_is_convertible and other similar typedefs with internal static asserts could be made
    // sfinae-friendly
    // in a similar way (i.e., don't define the ::value member if the input types are not keys).
    template <typename T, typename U = series,
              typename std::enable_if<
                  series_recursion_index<T>::value != 0u
                      && (series_recursion_index<T>::value == series_recursion_index<U>::value)
                      && has_convert_to<typename U::term_type::cf_type, typename T::term_type::cf_type>::value
                      && key_is_convertible<typename U::term_type::key_type, typename T::term_type::key_type>::value,
                  int>::type
              = 0>
    void dispatch_generic_constructor(const T &s)
    {
        typedef typename term_type::cf_type cf_type;
        typedef typename term_type::key_type key_type;
        m_symbol_set = s.m_symbol_set;
        const auto it_f = s.m_container.end();
        try {
            for (auto it = s.m_container.begin(); it != it_f; ++it) {
                insert<true>(term_type(convert_to<cf_type>(it->m_cf), key_type(it->m_key, m_symbol_set)));
            }
        } catch (...) {
            // In case of any insertion error, zero out this series.
            m_container.clear();
            throw;
        }
    }
    // NOTE: here we need to make sure that the generic ctor cannot be preferred over the copy constructor,
    // otherwise we pay a performance penalty. We need to make sure of the following things:
    // - this must *not* be a copy constructor for series - this will prevent automatically-generated copy constructors
    //   lower in the class hierarchy to call it;
    // - if we call explicitly a base() copy constructor lower in the hierarchy, the generic constructor must be
    // excluded
    //   from the overload set.
    // Here, U is the calling series object, T the generic object. The is_base_of check makes sure of the above
    // conditions:
    // - if T is U exactly, then clearly std::is_base_of<U,T>::value is true;
    // - if T comes from lower in the hierarchy, then clearly std::is_base_of<U,T>::value is true again.
    // These aspects are tested in series_03.
    template <typename T, typename U>
    using generic_ctor_enabler = typename std::enable_if<
        true_tt<decltype(std::declval<U &>().dispatch_generic_constructor(std::declval<const T &>()))>::value
            && !std::is_base_of<U, T>::value,
        int>::type;
    // Enabler for is_identical.
    template <typename T>
    using is_identical_enabler = typename std::enable_if<is_equality_comparable<const T &>::value, int>::type;
    // Iterator utilities.
    typedef boost::transform_iterator<std::function<std::pair<typename term_type::cf_type, Derived>(const term_type &)>,
                                      typename container_type::const_iterator>
        const_iterator_impl;
    // Print utilities.
    template <bool TexMode, typename Iterator>
    static std::ostream &print_helper(std::ostream &os, Iterator start, Iterator end, const symbol_fset &args)
    {
        piranha_assert(start != end);
        const auto limit = settings::get_max_term_output();
        size_type count = 0u;
        std::ostringstream oss;
        auto it = start;
        for (; it != end;) {
            if (limit && count == limit) {
                break;
            }
            std::ostringstream oss_cf;
            if (TexMode) {
                print_tex_coefficient(oss_cf, it->m_cf);
            } else {
                print_coefficient(oss_cf, it->m_cf);
            }
            auto str_cf = oss_cf.str();
            std::ostringstream oss_key;
            if (TexMode) {
                it->m_key.print_tex(oss_key, args);
            } else {
                it->m_key.print(oss_key, args);
            }
            auto str_key = oss_key.str();
            if (str_cf == "1" && !str_key.empty()) {
                str_cf = "";
            } else if (str_cf == "-1" && !str_key.empty()) {
                str_cf = "-";
            }
            oss << str_cf;
            if (str_cf != "" && str_cf != "-" && !str_key.empty() && !TexMode) {
                oss << "*";
            }
            oss << str_key;
            ++it;
            if (it != end) {
                oss << "+";
            }
            ++count;
        }
        auto str = oss.str();
        // If we reached the limit without printing all terms in the series, print the ellipsis.
        if (limit && count == limit && it != end) {
            if (TexMode) {
                str += "\\ldots";
            } else {
                str += "...";
            }
        }
        std::string::size_type index = 0u;
        while (true) {
            index = str.find("+-", index);
            if (index == std::string::npos) {
                break;
            }
            str.replace(index, 2u, "-");
            ++index;
        }
        os << str;
        return os;
    }
    // Merge arguments using a map m computed by ss_merge(). new_s is the new
    // merged symbol set.
    Derived merge_arguments(const symbol_fset &new_s, const symbol_idx_fmap<symbol_fset> &m) const
    {
        // We should never invoke this with an empty insertion map.
        piranha_assert(m.size());
        // The last index of the insertion map must be at most the size
        // of the current symbol set (which means that symbols need to be
        // appended).
        piranha_assert(m.rbegin()->first <= m_symbol_set.size());
#if !defined(NDEBUG)
        // In debug mode, verify that the concatenation of the values
        // in m forms a strictly monotonic sequence.
        auto verify_ins_map = [&m]() -> bool {
            std::vector<std::string> v_str;
            for (const auto &p : m) {
                v_str.insert(v_str.end(), p.second.begin(), p.second.end());
            }
            return std::is_sorted(v_str.begin(), v_str.end())
                   && std::adjacent_find(v_str.begin(), v_str.end()) == v_str.end();
        };
        // Verify also that new_s contains all the symbols in m.
        auto verify_ss = [&m, &new_s]() -> bool {
            for (const auto &p : m) {
                for (const auto &s : p.second) {
                    if (new_s.find(s) == new_s.end()) {
                        return false;
                    }
                }
            }
            return true;
        };
#endif
        piranha_assert(verify_ins_map());
        piranha_assert(verify_ss());
        // Create the output.
        Derived retval;
        // Assign the new symbol set.
        retval.m_symbol_set = new_s;
        // Prepare a number of buckets equal to the current one.
        retval.m_container.rehash(this->m_container.bucket_count());
        // Do the symbol merge.
        const auto it_f = m_container.end();
        for (auto it = m_container.begin(); it != it_f; ++it) {
            retval.insert(term_type{it->m_cf, it->m_key.merge_symbols(m, this->m_symbol_set)});
        }
        return retval;
    }
    // Set of checks to be run on destruction in debug mode.
    bool destruction_checks() const
    {
        for (auto it = m_container.begin(); it != m_container.end(); ++it) {
            if (!it->is_compatible(m_symbol_set)) {
                std::cout << "Term not compatible.\n";
                return false;
            }
            if (it->is_zero(m_symbol_set)) {
                std::cout << "Term not ignorable.\n";
                return false;
            }
        }
        return true;
    }
    template <typename T>
    static T trim_cf_impl(const T &s, typename std::enable_if<is_series<T>::value>::type * = nullptr)
    {
        return s.trim();
    }
    template <typename T>
    static const T &trim_cf_impl(const T &s, typename std::enable_if<!is_series<T>::value>::type * = nullptr)
    {
        return s;
    }
    // Metaprogramming bits for partial derivative.
    template <typename Cf2>
    using cf_diff_type = decltype(math::partial(std::declval<const Cf2 &>(), std::string()));
    // NOTE: decltype on a member is the type of that member:
    // http://thbecker.net/articles/auto_and_decltype/section_06.html
    template <typename Key2>
    using key_diff_type
        = decltype(std::declval<const Key2 &>().partial(symbol_idx{}, std::declval<const symbol_fset &>()).first);
    // Shortcuts to get cf/key from series.
    template <typename Series>
    using cf_t = typename Series::term_type::cf_type;
    template <typename Series>
    using key_t = typename Series::term_type::key_type;
    // We have different implementations, depending on whether the computed derivative type is the same as the original
    // one (in which case we will use faster term insertions) or not (in which case we resort to series arithmetics).
    // Adopt the usual scheme of providing an unspecialised value that sfinaes out if the enabler condition is
    // malformed, and two specialisations that implement the logic above.
    template <typename Series, typename = void>
    struct partial_type_ {
    };
// NOTE: the enabler conditions below are one the negation of the other, not sure if it is possible to avoid repetition
// without macro as we need
// them explicitly for sfinae.
#define PIRANHA_SERIES_PARTIAL_ENABLER                                                                                 \
    (std::is_same<cf_diff_type<cf_t<Series>>, cf_t<Series>>::value                                                     \
     && std::is_same<decltype(std::declval<const cf_t<Series> &>()                                                     \
                              * std::declval<const key_diff_type<key_t<Series>> &>()),                                 \
                     cf_t<Series>>::value)
    template <typename Series>
    struct partial_type_<Series, typename std::enable_if<PIRANHA_SERIES_PARTIAL_ENABLER>::type> {
        using type = Series;
        // Record the algorithm to be adopted for later use.
        static const int algo = 0;
    };
    // This is the type resulting from the second algorithm.
    template <typename Series>
    using partial_type_1
        = decltype(std::declval<const cf_diff_type<cf_t<Series>> &>() * std::declval<const Series &>()
                   + std::declval<const cf_t<Series> &>() * std::declval<const key_diff_type<key_t<Series>> &>()
                         * std::declval<const Series &>());
    // NOTE: in addition to check that we cannot use the optimised algorithm, we must also check that the resulting type
    // is constructible from int (for the accumulation of retval) and that it is addable in-place.
    template <typename Series>
    struct partial_type_<
        Series, typename std::enable_if<
                    !PIRANHA_SERIES_PARTIAL_ENABLER && std::is_constructible<partial_type_1<Series>, int>::value
                    && is_addable_in_place<decltype(std::declval<const partial_type_1<Series> &>()
                                                    + std::declval<const partial_type_1<Series> &>())>::value>::type> {
        using type = partial_type_1<Series>;
        static const int algo = 1;
    };
#undef PIRANHA_SERIES_PARTIAL_ENABLER
    // The final typedef.
    template <typename Series>
    using partial_type = typename std::enable_if<is_returnable<typename partial_type_<Series>::type>::value,
                                                 typename partial_type_<Series>::type>::type;
    // The implementation of the two partial() algorithms.
    template <typename Series = Derived, typename std::enable_if<partial_type_<Series>::algo == 0, int>::type = 0>
    partial_type<Series> partial_impl(const std::string &name) const
    {
        static_assert(std::is_same<Derived, partial_type<Series>>::value, "Invalid type.");
        // This is the faster algorithm.
        Derived retval;
        retval.m_symbol_set = this->m_symbol_set;
        // Prepare a number of buckets equal to the current one.
        retval.m_container.rehash(this->m_container.bucket_count());
        const auto pos = ss_index_of(retval.m_symbol_set, name);
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            // NOTE: here the term being inserted cannot be incompatible as it->m_key is coming from
            // a series with the same symbol set. The worst that can happen is something going awry in the derivative of
            // the coefficient. If the derivative becomes zero, the insertion routine will not insert anything.
            retval.insert(term_type{math::partial(it->m_cf, name), it->m_key});
            // NOTE: if the partial of the key returns an incompatible key, an error will be raised.
            auto p_key = it->m_key.partial(pos, retval.m_symbol_set);
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif
            retval.insert(term_type{it->m_cf * p_key.first, std::move(p_key.second)});
#if defined(PIRANHA_COMPILER_IS_GCC)
#pragma GCC diagnostic pop
#endif
        }
        return retval;
    }
    template <typename Series = Derived, typename std::enable_if<partial_type_<Series>::algo == 1, int>::type = 0>
    partial_type<Series> partial_impl(const std::string &name) const
    {
        const auto pos = ss_index_of(this->m_symbol_set, name);
        partial_type<Series> retval(0);
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            // Construct the two pieces of the derivative relating to the key
            // in the original term.
            Derived tmp0;
            tmp0.m_symbol_set = this->m_symbol_set;
            tmp0.insert(term_type{1, it->m_key});
            auto p_key = it->m_key.partial(pos, this->m_symbol_set);
            Derived tmp1;
            tmp1.m_symbol_set = this->m_symbol_set;
            tmp1.insert(term_type{1, std::move(p_key.second)});
            // Assemble everything into the return value.
            retval += math::partial(it->m_cf, name) * tmp0 + it->m_cf * p_key.first * tmp1;
        }
        return retval;
    }
    // Custom derivatives boilerplate.
    // Partial derivative of a series type, as defined by math::partial(). Note that here we cannot use
    // partial_type as Derived series might override the partial() method with their own implementation.
    template <typename Series>
    using series_p_type = decltype(math::partial(std::declval<const Series &>(), std::declval<const std::string &>()));
    template <typename Series>
    using cp_map_type = std::unordered_map<std::string, std::function<series_p_type<Series>(const Derived &)>>;
    // NOTE: here the initialisation of the static variable inside the body of the function
    // is guaranteed to be thread-safe. It should not matter too much as we always protect the access to it.
    template <typename Series = Derived>
    static cp_map_type<Series> &get_cp_map()
    {
        static cp_map_type<Series> cp_map;
        return cp_map;
    }
    template <typename F, typename Series>
    using custom_partial_enabler =
        typename std::enable_if<std::is_constructible<std::function<series_p_type<Series>(const Derived &)>, F>::value,
                                int>::type;
#if defined(PIRANHA_WITH_BOOST_S11N)
    // Boost serialization support.
    friend class boost::serialization::access;
    template <class Archive>
    void save(Archive &ar, unsigned) const
    {
        // Serialize the symbol set.
        boost_save(ar, get_symbol_set().size());
        for (const auto &sym : get_symbol_set()) {
            boost_save(ar, sym);
        }
        // Serialize the series.
        boost_save(ar, size());
        for (const auto &t : _container()) {
            boost_save(ar, t.m_cf);
            boost_save(ar, boost_s11n_key_wrapper<typename term_type::key_type>{t.m_key, get_symbol_set()});
        }
    }
    template <class Archive>
    void load(Archive &ar, unsigned)
    {
        using ss_size_t = decltype(get_symbol_set().size());
        using s_size_t = decltype(size());
        // Erase s.
        *this = series{};
        // Recover the symbol set.
        // First the size.
        ss_size_t ss_size;
        boost_load(ar, ss_size);
        // NOTE: not sure if it's worth it to make this thread_local, as when resizing up new memory
        // allocs for each string are necessary anyway. Still, with the small string optimisation
        // it may be worth it.
        std::vector<std::string> vs;
        vs.resize(piranha::safe_cast<std::vector<std::string>::size_type>(ss_size));
        // Load the symbol names in a vector of strings.
        for (auto &str : vs) {
            boost_load(ar, str);
        }
        // Construct the symbol set and set it to the series.
        symbol_fset ss(vs.begin(), vs.end());
        set_symbol_set(ss);
        // Now recover the series.
        // Size first.
        s_size_t s_size;
        boost_load(ar, s_size);
        // Preallocate buckets.
        _container().rehash(
            boost::numeric_cast<s_size_t>(std::ceil(static_cast<double>(s_size) / _container().max_load_factor())));
        // Insert all the terms.
        for (s_size_t i = 0u; i < s_size; ++i) {
            // NOTE: the rationale for creating a new term each time is that if we move it, we have
            // no guarantees on the moved-from state (in particular, we cannot be certain that a moved-from
            // term can be deserialized into).
            term_type t;
            boost_load(ar, t.m_cf);
            boost_s11n_key_wrapper<typename term_type::key_type> w{t.m_key, ss};
            boost_load(ar, w);
            insert(std::move(t));
        }
    }
    BOOST_SERIALIZATION_SPLIT_MEMBER()
#endif
    // Exponentiation machinery.
    // The type resulting from the exponentiation of the coefficient of a series U to the power of T.
    template <typename T, typename U>
    using pow_cf_type = pow_t<const typename U::term_type::cf_type &, const T &>;
    // Type resulting from exponentiation via multiplication.
    template <typename U>
    using pow_m_type = decltype(std::declval<const U &>() * std::declval<const U &>());
    // Common checks on the exponent.
    template <typename T>
    using pow_expo_checks = conjunction<is_is_zero_type<const T &>, is_safely_castable<const T &, integer>>;
    // Hashing utils for series.
    struct series_hasher {
        template <typename T>
        std::size_t operator()(const T &s) const
        {
            return s.hash();
        }
    };
    struct series_equal_to {
        template <typename T>
        bool operator()(const T &a, const T &b) const
        {
            return a.is_identical(b);
        }
    };
    template <typename Series>
    using pow_map_type = std::unordered_map<Series, std::vector<pow_m_type<Series>>, series_hasher, series_equal_to>;
    // NOTE: here, as in the custom derivative machinery, we need to pass through a static function
    // to get the cache because Derived is an incomplete type and we cannot thus use a static data member
    // involving Derived in series. Also, we need the Series template argument to inhibit the instantiation
    // of the function for series types that do not support exponentiation.
    template <typename Series = Derived>
    static pow_map_type<Series> &get_pow_cache()
    {
        static pow_map_type<Series> s_pow_cache;
        return s_pow_cache;
    }
    // Empty for sfinae.
    template <typename T, typename U, typename = void>
    struct pow_ret_type_ {
    };
    // Case 0: the exponentiation of the coefficient does not change its type.
    template <typename T, typename U>
    struct pow_ret_type_<
        T, U,
        enable_if_t<conjunction<
            std::is_same<pow_cf_type<T, U>, typename U::term_type::cf_type>, pow_expo_checks<T>,
            // Check we can construct the return value from the type stored
            // in the cache.
            std::is_constructible<U, pow_m_type<U>>,
            // Check that when we multiply the type stored in the cache by
            // *this, we get again the type stored in the cache.
            std::is_same<pow_m_type<U>, decltype(std::declval<const pow_m_type<U> &>() * std::declval<const U &>())>,
            // Check we can use is_identical.
            std::is_same<is_identical_enabler<U>, int>>::value>> {
        using type = U;
    };
    // Case 1: the exponentiation of the coefficient does change its type.
    template <typename T, typename U>
    struct pow_ret_type_<
        T, U,
        enable_if_t<conjunction<
            negation<std::is_same<pow_cf_type<T, U>, typename U::term_type::cf_type>>, pow_expo_checks<T>,
            // Check we can construct the return value from the type stored
            // in the cache.
            std::is_constructible<series_rebind<U, pow_cf_type<T, U>>, pow_m_type<U>>,
            // Check that when we multiply the type stored in the cache by
            // *this, we get again the type stored in the cache.
            std::is_same<pow_m_type<U>, decltype(std::declval<const pow_m_type<U> &>() * std::declval<const U &>())>,
            // Check we can use is_identical.
            std::is_same<is_identical_enabler<U>, int>>::value>> {
        using type = series_rebind<U, pow_cf_type<T, U>>;
    };
    // Final typedef.
    template <typename T, typename U>
    using pow_ret_type = typename pow_ret_type_<T, U>::type;

public:
    /// Size type.
    /**
     * Used to represent the number of terms in the series. Equivalent to piranha::hash_set::size_type.
     */
    using size_type = typename container_type::size_type;
    /// Const iterator.
    /**
     * Iterator type that can be used to iterate over the terms of the series.
     * The object returned upon dereferentiation is an \p std::pair in which the first element
     * is a copy of the coefficient of the term, the second element a single-term instance of \p Derived constructed
     * from
     * the term's key and a unitary coefficient.
     *
     * This iterator is an input iterator which additionally offers the multi-pass guarantee.
     *
     * @see piranha::series::begin() and piranha::series::end().
     */
    using const_iterator = const_iterator_impl;
    /// Defaulted default constructor.
    series() = default;
    /// Defaulted copy constructor.
    /**
     * @throws unspecified any exception thrown by the copy constructor of piranha::hash_set.
     */
    series(const series &) = default;
    /// Defaulted move constructor.
    series(series &&) = default;
    /// Generic constructor.
    /**
     * \note
     * This constructor is enabled only if \p T does not derive from the calling piranha::series instance and
     * the algorithm outlined below is supported by the involved types.
     *
     * The generic construction algorithm works as follows:
     * - if \p T is a series with the same series recursion index as \p this, then
     *   the symbol set of \p x is copied into \p this and all terms from \p x are inserted into \p this.
     *   The terms of \p x are converted to \p term_type via the binary constructor of piranha::term,
     *   and the keys of \p x are converted to the key type of \p term_type via a converting constructor,
     *   if available (see piranha::key_is_convertible);
     * - else, if the recursion index of \p T is less than the recursion index of \p this:
     *   - \p x is used to construct a new term as follows:
     *     - \p x is used to construct a coefficient via piranha::convert_to;
     *     - an empty arguments set will be used to construct a key;
     *     - coefficient and key are used to construct the new term instance;
     *   - the new term is inserted into \p this;
     * - otherwise, the constructor is disabled.
     *
     * @param x object to construct from.
     *
     * @throws unspecified any exception thrown by:
     * - the copy assignment operator of piranha::symbol_fset,
     * - the construction of a coefficient from \p x or of a key from piranha::symbol_fset,
     * - the construction of a term from a coefficient-key pair,
     * - insert().
     */
    template <typename T, typename U = series, generic_ctor_enabler<T, U> = 0>
    explicit series(const T &x)
    {
        dispatch_generic_constructor(x);
    }
    /// Trivial destructor.
    ~series()
    {
        PIRANHA_TT_CHECK(std::is_base_of, series, Derived);
        PIRANHA_TT_CHECK(is_series, Derived);
        PIRANHA_TT_CHECK(is_container_element, Derived);
        // Static checks on the iterator types.
        PIRANHA_TT_CHECK(is_input_iterator, const_iterator);
        piranha_assert(destruction_checks());
    }
    /// Copy-assignment operator.
    /**
     * @param other assignment argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by the copy constructor.
     */
    series &operator=(const series &other)
    {
        if (likely(this != &other)) {
            series tmp(other);
            *this = std::move(tmp);
        }
        return *this;
    }
    /// Move assignment operator.
    /**
     * @param other the assignment argument.
     *
     * @return a reference to \p this.
     */
    series &operator=(series &&other) = default;
    /// Generic assignment operator.
    /**
     * \note
     * This operator is enabled only if the corresponding generic constructor from \p x is enabled.
     *
     * Generic assignment is equivalent to assignment to a piranha::series constructed
     * via the generic constructor.
     *
     * @param x assignment argument.
     *
     * @return reference to \p this.
     *
     * @throws unspecified any exception thrown by the generic constructor.
     */
    template <typename T, typename U = series, generic_ctor_enabler<T, U> = 0>
    series &operator=(const T &x)
    {
        static_assert(!std::is_base_of<series, T>::value, "Generic assignment should not be enabled with "
                                                          "a type deriving from the calling series.");
        return operator=(series(x));
    }
    /// Series size.
    /**
     * @return the number of terms in the series.
     */
    size_type size() const
    {
        return m_container.size();
    }
    /// Empty test.
    /**
     * @return \p true if size() is nonzero, \p false otherwise.
     */
    bool empty() const
    {
        return !size();
    }
    /// Test for single-coefficient series.
    /**
     * A series is considered to be <em>single-coefficient</em> when it is symbolically equivalent to a coefficient.
     * That is, the series is either empty (in which case it is considered to be equivalent to a coefficient constructed
     * from zero) or consisting of a single term with unitary key (in which case the series is considered equivalent to
     * its only coefficient).
     *
     * @return \p true in case of single-coefficient series, \p false otherwise.
     *
     * @throws unspecified any exception thrown by piranha::key_is_one().
     */
    bool is_single_coefficient() const
    {
        return (empty() || (size() == 1u && piranha::key_is_one(m_container.begin()->m_key, m_symbol_set)));
    }
    /// Insert term.
    /**
     * \note
     * This method is enabled only if the decay type of \p T is piranha::series::term_type.
     *
     * This method will insert \p term into the series using internally piranha::hash_set::insert.
     *
     * The insertion algorithm proceeds as follows:
     * - if the term is not compatible for insertion, an \p std::invalid_argument exception is thrown;
     * - if the term is ignorable, the method will return without performing any insertion;
     * - if the term is already in the series, then:
     *   - its coefficient is added (if \p Sign is \p true) or subtracted (if \p Sign is \p false)
     *     to the existing term's coefficient;
     *   - if, after the addition/subtraction the existing term is ignorable, it will be erased;
     * - else:
     *   - the term is inserted into the term container and, if \p Sign is \p false, its coefficient is negated.
     *
     * After any modification to an existing term in the series (e.g., via insertion with negative \p Sign or via
     * in-place addition
     * or subtraction of existing coefficients), the term will be checked again for compatibility and ignorability, and,
     * in case
     * the term has become incompatible or ignorable, it will be erased from the series.
     *
     * The exception safety guarantee upon insertion is that the series will be left in an undefined but valid state.
     * Such a guarantee
     * relies on the fact that the addition/subtraction and negation methods of the coefficient type will leave the
     * coefficient in a valid
     * (possibly undefined) state in face of exceptions.
     *
     * @param term term to be inserted.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::hash_set::insert(),
     * - piranha::hash_set::find(),
     * - piranha::hash_set::erase(),
     * - piranha::math::negate(), in-place addition/subtraction on coefficient types,
     * - piranha::term::is_zero(),
     * - piranha::term::is_compatible().
     * @throws std::invalid_argument if \p term is incompatible.
     */
    template <bool Sign, typename T, insert_enabler<T> = 0>
    void insert(T &&term)
    {
        dispatch_insertion<Sign>(std::forward<T>(term));
    }
    /// Insert generic term with <tt>Sign = true</tt>.
    /**
     * \note
     * This method is enabled only if the decay type of \p T is piranha::series::term_type.
     *
     * Convenience wrapper for the generic insert() method, with \p Sign set to \p true.
     *
     * @param term term to be inserted.
     *
     * @throws unspecified any exception thrown by generic insert().
     */
    template <typename T, insert_enabler<T> = 0>
    void insert(T &&term)
    {
        insert<true>(std::forward<T>(term));
    }
    /// Identity operator.
    /**
     * @return copy of \p this, cast to \p Derived.
     *
     * @throws unspecified any exception thrown by the copy constructor.
     */
    Derived operator+() const
    {
        return Derived(*static_cast<Derived const *>(this));
    }
    /// Negation operator.
    /**
     * @return a copy of \p this on which negate() has been called.
     *
     * @throws unspecified any exception thrown by:
     * - negate(),
     * - the copy constructor of \p Derived.
     */
    Derived operator-() const
    {
        Derived retval(*static_cast<Derived const *>(this));
        retval.negate();
        return retval;
    }
    /// Negate series in-place.
    /**
     * This method will call math::negate() on the coefficients of all terms. In case of exceptions,
     * the basic exception safety guarantee is provided.
     *
     * If any term becomes ignorable or incompatible after negation, it will be erased from the series.
     *
     * @throws unspecified any exception thrown by math::negate() or by piranha::term::is_zero().
     */
    void negate()
    {
        try {
            const auto it_f = m_container.end();
            for (auto it = m_container.begin(); it != it_f;) {
                math::negate(it->m_cf);
                if (unlikely(it->is_zero(m_symbol_set))) {
                    it = m_container.erase(it);
                } else {
                    ++it;
                }
            }
        } catch (...) {
            m_container.clear();
            throw;
        }
    }
    /** @name Table-querying methods
     * Methods to query the properties of the internal container used to store the terms.
     */
    //@{
    /// Table sparsity.
    /**
     * Will call piranha::hash_set::evaluate_sparsity() on the internal terms container
     * and return the result.
     *
     * @return the output of piranha::hash_set::evaluate_sparsity().
     *
     * @throws unspecified any exception thrown by piranha::hash_set::evaluate_sparsity().
     */
    sparsity_info_type table_sparsity() const
    {
        return m_container.evaluate_sparsity();
    }
    /// Table load factor.
    /**
     * Will call piranha::hash_set::load_factor() on the internal terms container
     * and return the result.
     *
     * @return the load factor of the internal container.
     */
    double table_load_factor() const
    {
        return m_container.load_factor();
    }
    /// Table bucket count.
    /**
     * @return the bucket count of the internal container.
     */
    size_type table_bucket_count() const
    {
        return m_container.bucket_count();
    }
    //@}
    /// Exponentiation.
    /**
     * \note
     * This method is enabled only if the algorithm outlined here is supported by the involved types.
     *
     * Return \p this raised to the <tt>x</tt>-th power. The type of the result is either the calling series type,
     * or the calling series type rebound to the type resulting from the exponentiation of the coefficient of the
     * calling type to the power of \p x. The exponentiation algorithm proceeds as follows:
     * - if the series is single-coefficient, the result is a single-coefficient series in which the coefficient
     *   is the original coefficient (or zero, if the calling series is empty) raised to the power of \p x;
     * - if \p x is zero (as established by piranha::is_zero()), a series with a single term
     *   with unitary key and coefficient constructed from the integer numeral "1" is returned (i.e., any series raised
     *   to the power of zero is 1 - including empty series);
     * - if \p x represents a non-negative integral value, the return value is constructed via repeated multiplications;
     * - otherwise, an exception will be raised.
     *
     * An internal thread-safe cache of natural powers of series is maintained in order to improve performance during,
     * e.g., substitution operations. This cache can be cleared with clear_pow_cache().
     *
     * @param x exponent.
     *
     * @return \p this raised to the power of \p x.
     *
     * @throws std::invalid_argument if exponentiation is computed via repeated series multiplications and
     * \p x does not represent a non-negative integer.
     * @throws unspecified any exception thrown by:
     * - series, term, coefficient and key construction,
     * - insert(),
     * - is_single_coefficient(),
     * - piranha::pow(), piranha::is_zero() and piranha::safe_cast(),
     * - series multiplication,
     * - memory errors in standard containers,
     * - threading primitives,
     * - hash() or is_identical().
     */
    template <typename T, typename U = Derived>
    pow_ret_type<T, U> pow(const T &x) const
    {
        // NOTE: there are 3 types involved here:
        // - Derived,
        // - the return type (which is Derived or a rebound type from Derived),
        // - the type of Derived * Derived.
        using ret_type = pow_ret_type<T, U>;
        using r_term_type = typename ret_type::term_type;
        using r_cf_type = typename r_term_type::cf_type;
        using cf_type = typename term_type::cf_type;
        using key_type = typename term_type::key_type;
        using m_type = pow_m_type<Derived>;
        using m_term_type = typename m_type::term_type;
        using m_cf_type = typename m_term_type::cf_type;
        using m_key_type = typename m_term_type::key_type;
        // Handle the case of single coefficient series.
        if (is_single_coefficient()) {
            ret_type retval;
            if (empty()) {
                // An empty series is equal to zero.
                retval.insert(r_term_type(piranha::pow(cf_type(0), x), key_type(symbol_fset{})));
            } else {
                retval.insert(r_term_type(piranha::pow(m_container.begin()->m_cf, x), key_type(symbol_fset{})));
            }
            return retval;
        }
        // Handle the case of zero exponent.
        if (piranha::is_zero(x)) {
            ret_type retval;
            retval.insert(r_term_type(r_cf_type(1), key_type(symbol_fset{})));
            return retval;
        }
        // Exponentiation by repeated multiplications.
        integer n;
        try {
            n = piranha::safe_cast<integer>(x);
        } catch (const safe_cast_failure &) {
            piranha_throw(std::invalid_argument, "invalid argument for series exponentiation: non-integral value");
        }
        if (n.sgn() < 0) {
            piranha_throw(std::invalid_argument, "invalid argument for series exponentiation: negative integral value");
        }
        // Lock the cache for the rest of the method.
        std::lock_guard<std::mutex> lock(s_pow_mutex);
        auto &v = get_pow_cache()[*static_cast<Derived const *>(this)];
        using s_type = decltype(v.size());
        // Init the vector, if needed.
        if (!v.size()) {
            m_type tmp;
            tmp.insert(m_term_type(m_cf_type(1), m_key_type(symbol_fset{})));
            v.push_back(std::move(tmp));
        }
        // Fill in the missing powers.
        while (v.size() <= n) {
            // NOTE: for series it seems like it is better to run the dumb algorithm instead of, e.g.,
            // exponentiation by squaring - the growth in number of terms seems to be slower.
            v.push_back(v.back() * (*static_cast<Derived const *>(this)));
        }
        return ret_type(v[static_cast<s_type>(n)]);
    }
    /// Clear the internal cache of natural powers.
    /**
     * This method can be used to clear the cache of natural powers of series maintained by piranha::series::pow().
     *
     * @throws unspecified any exception thrown by threading primitives.
     */
    template <typename T = Derived, is_identical_enabler<T> = 0>
    static void clear_pow_cache()
    {
        std::lock_guard<std::mutex> lock(s_pow_mutex);
        get_pow_cache().clear();
    }
    /// Partial derivative.
    /**
     * \note
     * This method is enabled only if the coefficient and key are differentiable (i.e., they satisfy the
     * piranha::is_differentiable
     * and piranha::key_is_differentiable type traits), and if the arithmetic operations
     * needed to compute the partial derivative are supported by all the involved types.
     *
     * This method will return the partial derivative of \p this with respect to the variable called \p name. The method
     * will construct
     * the return value from the output of the differentiation methods of coefficient and key, and via arithmetic and/or
     * term insertion
     * operations.
     *
     * Note that, contrary to the specialisation of piranha::math::partial() for series types, this method will not take
     * into account
     * custom derivatives registered via piranha::series::register_custom_derivative().
     *
     * @param name name of the argument with respect to which the derivative will be calculated.
     *
     * @return partial derivative of \p this with respect to the symbol \p name.
     *
     * @throws unspecified any exception thrown by:
     * - the differentiation methods of coefficient and key,
     * - term construction and insertion,
     * - arithmetic operations on the involved types,
     * - construction of the return type.
     */
    template <typename Series = Derived>
    partial_type<Series> partial(const std::string &name) const
    {
        return partial_impl(name);
    }
    /// Register custom partial derivative.
    /**
     * \note
     * This method is enabled only if piranha::series::partial() is enabled for \p Derived, and if \p F
     * is a type that can be used to construct <tt>std::function<partial_type(const Derived &)</tt>, where
     * \p partial_type is the type resulting from the partial derivative of \p Derived.
     *
     * Register a copy of a callable \p func associated to the symbol \p name for use by
     * piranha::math::partial().
     * \p func will be used to compute the partial derivative of instances of type \p Derived with respect to
     * \p name in place of the default partial differentiation algorithm.
     *
     * It is safe to call this method from multiple threads.
     *
     * @param name symbol for which the custom partial derivative function will be registered.
     * @param func custom partial derivative function.
     *
     * @throws unspecified any exception thrown by:
     * - failure(s) in threading primitives,
     * - lookup and insertion operations on \p std::unordered_map,
     * - construction and move-assignment of \p std::function.
     */
    template <typename F, typename Series = Derived, custom_partial_enabler<F, Series> = 0>
    static void register_custom_derivative(const std::string &name, F func)
    {
        using p_type = series_p_type<Derived>;
        using f_type = std::function<p_type(const Derived &)>;
        std::lock_guard<std::mutex> lock(s_cp_mutex);
        get_cp_map()[name] = f_type(func);
    }
    /// Unregister custom partial derivative.
    /**
     * \note
     * This method is enabled only if piranha::series::partial() is enabled for \p Derived.
     *
     * Unregister the custom partial derivative function associated to the symbol \p name. If no custom
     * partial derivative was previously registered using register_custom_derivative(), calling this function will be a
     * no-op.
     *
     * It is safe to call this method from multiple threads.
     *
     * @param name symbol for which the custom partial derivative function will be unregistered.
     *
     * @throws unspecified any exception thrown by:
     * - failure(s) in threading primitives,
     * - lookup and erase operations on \p std::unordered_map.
     */
    // NOTE: the additional template parameter is to disable the method in case the partial
    // type is malformed.
    template <typename Series = Derived, typename Partial = partial_type<Series>>
    static void unregister_custom_derivative(const std::string &name)
    {
        std::lock_guard<std::mutex> lock(s_cp_mutex);
        auto it = get_cp_map().find(name);
        if (it != get_cp_map().end()) {
            get_cp_map().erase(it);
        }
    }
    /// Unregister all custom partial derivatives.
    /**
     * \note
     * This method is enabled only if piranha::series::partial() is enabled for \p Derived.
     *
     * Will unregister all custom derivatives currently registered via register_custom_derivative().
     * It is safe to call this method from multiple threads.
     *
     * @throws unspecified any exception thrown by failure(s) in threading primitives.
     */
    template <typename Series = Derived, typename Partial = partial_type<Series>>
    static void unregister_all_custom_derivatives()
    {
        std::lock_guard<std::mutex> lock(s_cp_mutex);
        get_cp_map().clear();
    }
    /// Begin iterator.
    /**
     * Return an iterator to the first term of the series. The returned iterator will
     * provide, when dereferenced, an \p std::pair in which the first element is a copy of the coefficient of
     * the term, whereas the second element is a single-term instance of \p Derived built from the term's key
     * and a unitary coefficient.
     *
     * Note that terms are stored unordered in the series, hence it is not defined which particular
     * term will be returned by calling this method. The only guarantee is that the iterator can be used to transverse
     * all the series' terms until piranha::series::end() is eventually reached.
     *
     * Calling any non-const method on the series will invalidate the iterators obtained via piranha::series::begin()
     * and piranha::series::end().
     *
     * @return an iterator to the first term of the series.
     *
     * @throws unspecified any exception thrown by:
     * - construction and assignment of piranha::symbol_fset,
     * - insert(),
     * - construction of term, coefficient and key instances.
     */
    const_iterator begin() const
    {
        typedef std::function<std::pair<typename term_type::cf_type, Derived>(const term_type &)> func_type;
        auto func
            = [this](const term_type &t) { return detail::pair_from_term<term_type, Derived>(this->m_symbol_set, t); };
        return boost::make_transform_iterator(m_container.begin(), func_type(std::move(func)));
    }
    /// End iterator.
    /**
     * Return an iterator one past the last term of the series. See the documentation of piranha::series::begin()
     * on how the returned iterator can be used.
     *
     * @return an iterator to the end of the series.
     *
     * @throws unspecified any exception thrown by:
     * - construction and assignment of piranha::symbol_fset,
     * - insert(),
     * - construction of term, coefficient and key instances.
     */
    const_iterator end() const
    {
        typedef std::function<std::pair<typename term_type::cf_type, Derived>(const term_type &)> func_type;
        auto func
            = [this](const term_type &t) { return detail::pair_from_term<term_type, Derived>(this->m_symbol_set, t); };
        return boost::make_transform_iterator(m_container.end(), func_type(std::move(func)));
    }
    /// Term filtering.
    /**
     * This method will apply the functor \p func to each term in the series, and produce a return series
     * containing all terms in \p this for which \p func returns \p true.
     * Terms are passed to \p func in the format resulting from dereferencing the iterators obtained
     * via piranha::series::begin().
     *
     * @param func filtering functor.
     *
     * @return filtered series.
     *
     * @throw unspecified any exception thrown by:
     * - the call operator of \p func,
     * - insert(),
     * - the assignment operator of piranha::symbol_fset,
     * - term, coefficient, key construction.
     */
    Derived filter(std::function<bool(const std::pair<typename term_type::cf_type, Derived> &)> func) const
    {
        Derived retval;
        retval.m_symbol_set = m_symbol_set;
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            if (func(detail::pair_from_term<term_type, Derived>(m_symbol_set, *it))) {
                retval.insert(*it);
            }
        }
        return retval;
    }
    /// Term transformation.
    /**
     * This method will apply the functor \p func to each term in the series, and will use the return
     * value of the functor to construct a new series. Terms are passed to \p func in the same format
     * resulting from dereferencing the iterators obtained via piranha::series::begin(), and \p func is expected to
     * produce
     * a return value of the same type.
     *
     * The return series is first initialised as an empty series. For each input term \p t, the return value
     * of \p func is used to construct a new temporary series from the multiplication of \p t.first and
     * \p t.second. Each temporary series is then added to the return value series.
     *
     * This method requires the coefficient type to be multipliable by \p Derived.
     *
     * @param func transforming functor.
     *
     * @return transformed series.
     *
     * @throw unspecified any exception thrown by:
     * - the call operator of \p func,
     * - insert(),
     * - the assignment operator of piranha::symbol_fset,
     * - term, coefficient, key construction,
     * - series multiplication and addition.
     */
    // TODO require multipliability of cf * Derived and addability of the result to Derived in place.
    Derived transform(std::function<std::pair<typename term_type::cf_type, Derived>(
                          const std::pair<typename term_type::cf_type, Derived> &)>
                          func) const
    {
        Derived retval;
        std::pair<typename term_type::cf_type, Derived> tmp;
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            tmp = func(detail::pair_from_term<term_type, Derived>(m_symbol_set, *it));
            // NOTE: here we could use multadd, but it seems like there's not
            // much benefit (plus, the types involved are different).
            retval += tmp.first * tmp.second;
        }
        return retval;
    }
    /// Trim.
    /**
     * This method will return a series mathematically equivalent to \p this in which discardable arguments
     * have been removed from the internal set of symbols. Which symbols are removed depends on the trimming
     * method \p trim_identify() of the key type (e.g., in a polynomial a symbol can be discarded if its exponent
     * is zero in all monomials).
     *
     * If the coefficient type is an instance of piranha::series, trim() will be called recursively on the coefficients
     * while building the return value.
     *
     * @return trimmed version of \p this.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::safe_cast(),
     * - operations on piranha::symbol_fset,
     * - the trimming methods of coefficient and/or key,
     * - insert(),
     * - term, coefficient and key type construction.
     */
    Derived trim() const
    {
        // Init the trimming mask.
        std::vector<char> trim_mask(piranha::safe_cast<std::vector<char>::size_type>(m_symbol_set.size()), char(1));
        // Determine the symbols to be trimmed.
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            it->m_key.trim_identify(trim_mask, m_symbol_set);
        }
        // Build the retval.
        Derived retval;
        retval.m_symbol_set = ss_trim(m_symbol_set, trim_mask);
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            retval.insert(term_type{trim_cf_impl(it->m_cf), it->m_key.trim(trim_mask, m_symbol_set)});
        }
        return retval;
    }
    /// Print in TeX mode.
    /**
     * Print series to stream \p os in TeX mode. The representation is constructed in the same way as explained in
     * piranha::series::operator<<(), but using piranha::print_tex_coefficient() and the key's TeX printing method
     * instead of the plain
     * printing functions.
     *
     * @param os target stream.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::print_tex_coefficient(),
     * - the TeX printing method of the key type,
     * - memory allocation errors in standard containers,
     * - piranha::settings::get_max_term_output(),
     * - streaming to \p os or to instances of \p std::ostringstream.
     *
     * @see operator<<().
     */
    void print_tex(std::ostream &os) const
    {
        // If the series is empty, print zero and exit.
        if (empty()) {
            os << "0";
            return;
        }
        print_helper<true>(os, m_container.begin(), m_container.end(), m_symbol_set);
    }
    /// Overloaded stream operator for piranha::series.
    /**
     * Will direct to stream a human-readable representation of the series.
     *
     * The human-readable representation of the series is built as follows:
     *
     * - the coefficient and key of each term are printed adjacent to each other separated by the character "*",
     *   the former via the piranha::print_coefficient() function, the latter via its <tt>print()</tt> method;
     * - terms are separated by a "+" sign.
     *
     * The following additional transformations take place on the printed output:
     *
     * - if the printed output of a coefficient is the string "1" and the printed output of its key
     *   is not empty, the coefficient and the "*" sign are not printed;
     * - if the printed output of a coefficient is the string "-1" and the printed output of its key
     *   is not empty, the printed output of the coefficient is transformed into "-" and the sign "*" is not printed;
     * - if the key output is empty, the sign "*" is not printed;
     * - the sequence of characters "+-" is transformed into "-";
     * - at most piranha::settings::get_max_term_output() terms are printed, and terms in excess are
     *   represented with ellipsis "..." at the end of the output; if piranha::settings::get_max_term_output()
     *   is zero, all the terms will be printed.
     *
     * Note that the print order of the terms will be undefined.
     *
     * @param os target stream.
     * @param s piranha::series argument.
     *
     * @return reference to \p os.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::print_coefficient(),
     * - the <tt>print()</tt> method of the key type,
     * - memory allocation errors in standard containers,
     * - piranha::settings::get_max_term_output(),
     * - streaming to \p os or to instances of \p std::ostringstream.
     */
    friend std::ostream &operator<<(std::ostream &os, const series &s)
    {
        // If the series is empty, print zero and exit.
        if (s.empty()) {
            os << "0";
            return os;
        }
        return print_helper<false>(os, s.m_container.begin(), s.m_container.end(), s.m_symbol_set);
    }
    /// Hash value.
    /**
     * The hash value for a series is zero if the series is empty, otherwise it is computed by adding the hash values
     * of all terms. This ensures that two identical series in which the terms are stored in different order still
     * produce
     * the same hash value.
     *
     * Note, however, that the arguments of the series are not considered in the hash value and that,
     * in general, two series that compare equal according to operator==() will **not** have the same hash value
     * (as the equality operator merges the arguments of two series before actually performing the comparison). Instead
     * of operator==(), is_identical() should be used for storing series as keys in associative containers.
     *
     * @return a hash value for the series.
     *
     * @throws unspecified any exception thrown by computing the hash of a term.
     */
    // NOTE: hash and is_identical must always be considered together. E.g., two series can be identical even in case
    // of coefficient series which are not identical - but this does not matter, as is_identical implies strict
    // equivalence
    // of the keys and hash considers only the keys. If we wanted to consider also the coefficients for hashing, then we
    // need
    // (probably) to call recursively is_identical on coefficient series.
    std::size_t hash() const
    {
        std::size_t retval = 0u;
        const auto it_f = m_container.end();
        for (auto it = m_container.begin(); it != it_f; ++it) {
            retval += it->hash();
        }
        return retval;
    }
    /// Check for identical series.
    /**
     * \note
     * This method is enabled only if \p Derived is equality-comparable.
     *
     * This method will return \p true if the symbol sets of \p this and \p other are the same,
     * and <tt>other == *this</tt>.
     *
     * @param other argument for the comparison.
     *
     * @return \p true if \p this and \p other are identical, \p false otherwise.
     *
     * @throws unspecified any exception thrown by the comparison operator of \p Derived.
     */
    template <typename T = Derived, is_identical_enabler<T> = 0>
    bool is_identical(const Derived &other) const
    {
        return m_symbol_set == other.m_symbol_set && other == *static_cast<Derived const *>(this);
    }
    /// Symbol set getter.
    /**
     * @return const reference to the piranha::symbol_fset associated to the series.
     */
    const symbol_fset &get_symbol_set() const
    {
        return m_symbol_set;
    }
    /// Symbol set setter.
    /**
     * @param args piranha::symbol_fset that will be associated to the series.
     *
     * @throws std::invalid_argument if the series is not empty.
     * @throws unspecified any exception thrown by the copy assignment operator of piranha::symbol_fset.
     */
    void set_symbol_set(const symbol_fset &args)
    {
        if (unlikely(!empty())) {
            piranha_throw(std::invalid_argument, "cannot set arguments on a non-empty series");
        }
        m_symbol_set = args;
    }
    /** @name Low-level interface
     * Low-level methods.
     */
    //@{
    /// Get a mutable reference to the container of terms.
    /**
     * @return a reference to the internal container of terms.
     */
    container_type &_container()
    {
        return m_container;
    }
    /// Get a const reference to the container of terms.
    /**
     * @return a const reference to the internal container of terms.
     */
    const container_type &_container() const
    {
        return m_container;
    }
    //@}
protected:
    /// Symbol set.
    symbol_fset m_symbol_set;
    /// Terms container.
    container_type m_container;

private:
    // Custom derivatives machinery.
    static std::mutex s_cp_mutex;
    // Pow cache machinery;
    static std::mutex s_pow_mutex;
};

template <typename Cf, typename Key, typename Derived>
std::mutex series<Cf, Key, Derived>::s_cp_mutex;

template <typename Cf, typename Key, typename Derived>
std::mutex series<Cf, Key, Derived>::s_pow_mutex;

inline namespace impl
{

// Implementation of the series merge helper.
// This function will call f(s1,s2), after having possibly merged the symbol sets
// of s1 and s2 if necessary (in which case, f will be called on copies of s1/s2).
template <typename S1, typename S2, typename F>
inline auto series_merge_f(S1 &&s1, S2 &&s2, const F &f) -> decltype(f(std::forward<S1>(s1), std::forward<S2>(s2)))
{
    if (s1.get_symbol_set() == s2.get_symbol_set()) {
        // If the symbol sets are identical, just call f(s1,s2) directly.
        return f(std::forward<S1>(s1), std::forward<S2>(s2));
    }
    // Otherwise, we need to merge the symbol sets.
    const auto merge = ss_merge(s1.get_symbol_set(), s2.get_symbol_set());
    // Check that all possible return types are the same. This is necessary
    // as potentially we end up calling different overloads of f.
    static_assert(std::is_same<decltype(f(std::forward<S1>(s1), std::forward<S2>(s2))),
                               decltype(f(s1.merge_arguments(std::get<0>(merge), std::get<1>(merge)),
                                          std::forward<S2>(s2)))>::value,
                  "Inconsistent return type.");
    static_assert(std::is_same<decltype(f(std::forward<S1>(s1), std::forward<S2>(s2))),
                               decltype(f(std::forward<S1>(s1),
                                          s2.merge_arguments(std::get<0>(merge), std::get<2>(merge))))>::value,
                  "Inconsistent return type.");
    static_assert(std::is_same<decltype(f(std::forward<S1>(s1), std::forward<S2>(s2))),
                               decltype(f(s1.merge_arguments(std::get<0>(merge), std::get<1>(merge)),
                                          s2.merge_arguments(std::get<0>(merge), std::get<2>(merge))))>::value,
                  "Inconsistent return type.");
    // Check who needs a copy.
    const bool s1_needs_copy = (std::get<0>(merge) != s1.get_symbol_set()),
               s2_needs_copy = (std::get<0>(merge) != s2.get_symbol_set());
    const unsigned mask = unsigned(s1_needs_copy) + (unsigned(s2_needs_copy) << 1u);
    piranha_assert(mask != 0u);
    // Handle the 3 possible cases.
    switch (mask) {
        case 1u:
            return f(s1.merge_arguments(std::get<0>(merge), std::get<1>(merge)), std::forward<S2>(s2));
        case 2u:
            return f(std::forward<S1>(s1), s2.merge_arguments(std::get<0>(merge), std::get<2>(merge)));
    }
    // Put the last case outside the switch, so we avoid compiler warnings.
    return f(s1.merge_arguments(std::get<0>(merge), std::get<1>(merge)),
             s2.merge_arguments(std::get<0>(merge), std::get<2>(merge)));
}
} // namespace impl

/// Specialisation of piranha::print_coefficient_impl for series.
/**
 * This specialisation is enabled if \p Series is an instance of piranha::series.
 */
template <typename Series>
struct print_coefficient_impl<Series, typename std::enable_if<is_series<Series>::value>::type> {
    /// Call operator.
    /**
     * Equivalent to the stream operator overload of piranha::series, apart from a couple
     * of parentheses '()' enclosing the coefficient series if its size is larger than 1.
     *
     * @param os target stream.
     * @param s coefficient series to be printed.
     *
     * @throws unspecified any exception thrown by the stream operator overload of piranha::series.
     */
    void operator()(std::ostream &os, const Series &s) const
    {
        if (s.size() > 1u) {
            os << '(';
        }
        os << s;
        if (s.size() > 1u) {
            os << ')';
        }
    }
};

/// Specialisation of piranha::print_tex_coefficient_impl for series.
/**
 * This specialisation is enabled if \p Series is an instance of piranha::series.
 */
template <typename Series>
struct print_tex_coefficient_impl<Series, typename std::enable_if<is_series<Series>::value>::type> {
    /// Call operator.
    /**
     * Equivalent to piranha::series::print_tex(), apart from a couple
     * of parentheses '()' enclosing the coefficient series if its size is larger than 1.
     *
     * @param os target stream.
     * @param s coefficient series to be printed.
     *
     * @throws unspecified any exception thrown by piranha::series::print_tex().
     */
    void operator()(std::ostream &os, const Series &s) const
    {
        if (s.size() > 1u) {
            os << "\\left(";
        }
        s.print_tex(os);
        if (s.size() > 1u) {
            os << "\\right)";
        }
    }
};

namespace math
{

/// Specialisation of the piranha::math::negate() functor for piranha::series.
/**
 * This specialisation is enabled if \p T is an instance of piranha::series.
 */
template <typename T>
struct negate_impl<T, typename std::enable_if<is_series<T>::value>::type> {
    /// Call operator.
    /**
     * @param s piranha::series to be negated.
     *
     * @throws unspecified any exception thrown by piranha::series::negate().
     */
    template <typename U>
    void operator()(U &s) const
    {
        s.negate();
    }
};
} // namespace math

/// Specialisation of the piranha::is_zero() functor for piranha::series.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series.
 * The result will be computed via the series' <tt>empty()</tt> method.
 */
template <typename Series>
class is_zero_impl<Series, typename std::enable_if<is_series<Series>::value>::type>
{
public:
    /// Call operator.
    /**
     * @param s piranha::series to be tested.
     *
     * @return \p true if \p s is empty, \p false otherwise.
     */
    bool operator()(const Series &s) const
    {
        return s.empty();
    }
};

inline namespace impl
{

// Enabler for the pow() specialisation for series.
template <typename Series, typename T>
using series_pow_member_t = decltype(std::declval<const Series &>().pow(std::declval<const T &>()));

template <typename Series, typename T>
using pow_series_enabler
    = enable_if_t<conjunction<is_series<Series>, is_detected<series_pow_member_t, Series, T>>::value>;
} // namespace impl

/// Specialisation of the piranha::pow() functor for piranha::series.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series with
 * a method with the same signature as piranha::series::pow().
 */
template <typename Series, typename T>
class pow_impl<Series, T, pow_series_enabler<Series, T>>
{
    using pow_type = series_pow_member_t<Series, T>;

public:
    /// Call operator.
    /**
     * The exponentiation will be computed via the series' <tt>pow()</tt> method. The body of this method
     * is equivalent to:
     * @code
     * return s.pow(x);
     * @endcode
     *
     * @param s base.
     * @param x exponent.
     *
     * @return \p s to the power of \p x.
     *
     * @throws unspecified any exception resulting from the series' <tt>pow()</tt> method.
     */
    pow_type operator()(const Series &s, const T &x) const
    {
        return s.pow(x);
    }
};

namespace detail
{

// Detect if series has a const invert() method.
template <typename T>
class series_has_invert : sfinae_types
{
    template <typename U>
    static auto test(const U &t) -> decltype(t.invert(), void(), yes());
    static no test(...);

public:
    static const bool value = std::is_same<decltype(test(std::declval<T>())), yes>::value;
};

// 1. Inversion via the custom method.
template <typename T, typename std::enable_if<is_series<T>::value && series_has_invert<T>::value, int>::type = 0>
inline auto series_invert_impl(const T &s) -> decltype(s.invert())
{
    return s.invert();
}

struct series_cf_invert_functor {
    template <typename T>
    auto operator()(const T &x) const -> decltype(math::invert(x))
    {
        return math::invert(x);
    }
    static constexpr const char *name = "inverse";
};

// 2. Coefficient type supports math::invert(), with return type equal to coefficient type.
// NOTE: here we are passing a series<> parameter: this will require a conversion from whatever series type (Derived) to
// its series<> base,
// and thus this overload will never conflict with the overload 1., even if the series that we pass in actually has the
// invert method.
// This is useful because we can now force the use of this "base" implementation by simply casting a series to its base
// type.
// This is used, e.g., in the sin/cos overrides for poisson_series, and it is similar to what it is done for the pow()
// overrides.
template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(math::invert(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline Derived series_invert_impl(const series<Cf, Key, Derived> &s)
{
    return apply_cf_functor<series_cf_invert_functor, Derived>(s);
}

// 3. coefficient type supports math::invert() with a result different from the original coefficient type and the series
// can be rebound to this new type.
template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && !std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(math::invert(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline series_rebind<Derived, decltype(math::invert(std::declval<const typename Derived::term_type::cf_type &>()))>
series_invert_impl(const series<Cf, Key, Derived> &s)
{
    using ret_type
        = series_rebind<Derived, decltype(math::invert(std::declval<const typename Derived::term_type::cf_type &>()))>;
    return apply_cf_functor<series_cf_invert_functor, ret_type>(s);
}

// Final enabler condition for the invert implementation.
template <typename T>
using series_invert_enabler =
    typename std::enable_if<true_tt<decltype(series_invert_impl(std::declval<const T &>()))>::value>::type;
} // namespace detail

namespace math
{

/// Specialisation of the piranha::math::invert() functor for piranha::series.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series and:
 * - either the series type provides a const <tt>%invert()</tt> method, or
 * - the series' coefficient type \p Cf supports math::invert() yielding a type \p T and either
 *   \p T is the same as \p Cf, or the series type can be rebound to the type \p T.
 */
template <typename Series>
struct invert_impl<Series, detail::series_invert_enabler<Series>> {
    /// Call operator.
    /**
     * @param s argument.
     *
     * @return inverse of \p s.
     *
     * @throws unspecified any exception thrown by:
     * - the <tt>Series::invert()</tt> method,
     * - piranha::math::invert(),
     * - term, coefficient, and key construction and/or insertion via piranha::series::insert().
     */
    template <typename T>
    auto operator()(const T &s) const -> decltype(detail::series_invert_impl(s))
    {
        return detail::series_invert_impl(s);
    }
};
} // namespace math

namespace detail
{

// Detect if series has a const sin() method.
template <typename T>
class series_has_sin : sfinae_types
{
    template <typename U>
    static auto test(const U &t) -> decltype(t.sin());
    static no test(...);

public:
    static const bool value = is_returnable<decltype(test(std::declval<T>()))>::value;
};

// Three cases for sin() implementation.
// 1. call the member function, if available.
template <typename T, typename std::enable_if<is_series<T>::value && series_has_sin<T>::value, int>::type = 0>
inline auto series_sin_impl(const T &s) -> decltype(s.sin())
{
    return s.sin();
}

struct series_cf_sin_functor {
    template <typename T>
    auto operator()(const T &x) const -> decltype(piranha::sin(x))
    {
        return piranha::sin(x);
    }
    static constexpr const char *name = "sine";
};

// 2. coefficient type supports piranha::sin() with a result equal to the original coefficient type.
// NOTE: this overload and the one below do not conflict with the one above because it takes a series
// as input argument: when used on a concrete series type, it will have to go through a to-base
// conversion in order to be selected.
template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(piranha::sin(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline Derived series_sin_impl(const series<Cf, Key, Derived> &s)
{
    return apply_cf_functor<series_cf_sin_functor, Derived>(s);
}

// 3. coefficient type supports piranha::sin() with a result different from the original coefficient type and the series
// can be rebound to this new type.
template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && !std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(piranha::sin(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline series_rebind<Derived, decltype(piranha::sin(std::declval<const typename Derived::term_type::cf_type &>()))>
series_sin_impl(const series<Cf, Key, Derived> &s)
{
    using ret_type
        = series_rebind<Derived, decltype(piranha::sin(std::declval<const typename Derived::term_type::cf_type &>()))>;
    return apply_cf_functor<series_cf_sin_functor, ret_type>(s);
}

// Final enabler condition for the sin implementation.
template <typename T>
using series_sin_enabler =
    typename std::enable_if<true_tt<decltype(series_sin_impl(std::declval<const T &>()))>::value>::type;

// All of the above, but for cos().
template <typename T>
class series_has_cos : sfinae_types
{
    template <typename U>
    static auto test(const U &t) -> decltype(t.cos());
    static no test(...);

public:
    static const bool value = is_returnable<decltype(test(std::declval<T>()))>::value;
};

template <typename T, typename std::enable_if<is_series<T>::value && series_has_cos<T>::value, int>::type = 0>
inline auto series_cos_impl(const T &s) -> decltype(s.cos())
{
    return s.cos();
}

struct series_cf_cos_functor {
    template <typename T>
    auto operator()(const T &x) const -> decltype(piranha::cos(x))
    {
        return piranha::cos(x);
    }
    static constexpr const char *name = "cosine";
};

template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(piranha::cos(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline Derived series_cos_impl(const series<Cf, Key, Derived> &s)
{
    return apply_cf_functor<series_cf_cos_functor, Derived>(s);
}

template <typename Cf, typename Key, typename Derived,
          typename std::enable_if<
              is_series<Derived>::value
                  && !std::is_same<
                         typename Derived::term_type::cf_type,
                         decltype(piranha::cos(std::declval<const typename Derived::term_type::cf_type &>()))>::value,
              int>::type
          = 0>
inline series_rebind<Derived, decltype(piranha::cos(std::declval<const typename Derived::term_type::cf_type &>()))>
series_cos_impl(const series<Cf, Key, Derived> &s)
{
    using ret_type
        = series_rebind<Derived, decltype(piranha::cos(std::declval<const typename Derived::term_type::cf_type &>()))>;
    return apply_cf_functor<series_cf_cos_functor, ret_type>(s);
}

template <typename T>
using series_cos_enabler =
    typename std::enable_if<true_tt<decltype(series_cos_impl(std::declval<const T &>()))>::value>::type;
} // namespace detail

/// Specialisation of the piranha::sin() functor for piranha::series.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series and:
 * - either the series type provides a const <tt>%sin()</tt> method returning a type which satisfies
 *   piranha::is_returnable, or, missing this method,
 * - the series' coefficient type \p Cf supports piranha::sin() yielding a type \p T and either
 *   \p T is the same as \p Cf, or the series type can be rebound to the type \p T.
 */
template <typename Series>
class sin_impl<Series, detail::series_sin_enabler<Series>>
{
public:
    /// Call operator.
    /**
     * @param s argument.
     *
     * @return sine of \p s.
     *
     * @throws unspecified any exception thrown by:
     * - the <tt>Series::sin()</tt> method,
     * - piranha::sin(),
     * - term, coefficient, and key construction and/or insertion via piranha::series::insert(),
     * - returning the result.
     */
    auto operator()(const Series &s) const -> decltype(detail::series_sin_impl(s))
    {
        return detail::series_sin_impl(s);
    }
};

/// Specialisation of the piranha::cos() functor for piranha::series.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series and:
 * - either the series type provides a const <tt>%cos()</tt> method returning a type which satisfies
 *   piranha::is_returnable, or, missing this method,
 * - the series' coefficient type \p Cf supports piranha::cos() yielding a type \p T and either
 *   \p T is the same as \p Cf, or the series type can be rebound to the type \p T.
 */
template <typename Series>
class cos_impl<Series, detail::series_cos_enabler<Series>>
{
public:
    /// Call operator.
    /**
     * @param s argument.
     *
     * @return cosine of \p s.
     *
     * @throws unspecified any exception thrown by:
     * - the <tt>Series::cos()</tt> method,
     * - piranha::cos(),
     * - term, coefficient, and key construction and/or insertion via piranha::series::insert(),
     * - returning the result.
     */
    auto operator()(const Series &s) const -> decltype(detail::series_cos_impl(s))
    {
        return detail::series_cos_impl(s);
    }
};

namespace detail
{

// Enabler for the partial() specialisation for series.
template <typename Series>
using series_partial_enabler = typename std::enable_if<is_series<Series>::value
                                                       && is_returnable<decltype(std::declval<const Series &>().partial(
                                                              std::declval<const std::string &>()))>::value>::type;

// Enabler for the integrate() specialisation: type needs to be a series which supports the integration method.
template <typename Series>
using series_integrate_enabler =
    typename std::enable_if<is_series<Series>::value
                            && is_returnable<decltype(std::declval<const Series &>().integrate(
                                   std::declval<const std::string &>()))>::value>::type;
} // namespace detail

namespace math
{

/// Specialisation of the piranha::math::partial() functor for series types.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series with a const \p partial() method
 * method taking a const <tt>std::string</tt> as parameter and returning a type which satisfies piranha::is_returnable.
 */
template <typename Series>
struct partial_impl<Series, detail::series_partial_enabler<Series>> {
    /// Call operator.
    /**
     * The call operator will first check whether a custom partial derivative for \p Series was registered
     * via piranha::series::register_custom_derivative(). In such a case, the custom derivative function will be used
     * to compute the return value. Otherwise, the output of piranha::series::partial() will be returned.
     *
     * @param s input series.
     * @param name name of the argument with respect to which the differentiation will be calculated.
     *
     * @return the partial derivative of \p s with respect to \p name.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::series::partial(),
     * - failure(s) in threading primitives,
     * - lookup operations on \p std::unordered_map,
     * - the copy assignment and call operators of the registered custom partial derivative function.
     */
    template <typename T>
    auto operator()(const T &s, const std::string &name) const -> decltype(s.partial(name))
    {
        using partial_type = decltype(s.partial(name));
        bool custom = false;
        std::function<partial_type(const T &)> func;
        // Try to locate a custom partial derivative and copy it into func, if found.
        {
            std::lock_guard<std::mutex> lock(T::s_cp_mutex);
            auto it = s.get_cp_map().find(name);
            if (it != s.get_cp_map().end()) {
                func = it->second;
                custom = true;
            }
        }
        if (custom) {
            return func(s);
        }
        return s.partial(name);
    }
};

/// Specialisation of the piranha::math::integrate() functor for series types.
/**
 * This specialisation is activated when \p Series is an instance of piranha::series with a const \p integrate()
 * method taking a const <tt>std::string</tt> as parameter and returning a type which satisfies piranha::is_returnable.
 */
template <typename Series>
struct integrate_impl<Series, detail::series_integrate_enabler<Series>> {
    /// Call operator.
    /**
     * @param s input series.
     * @param name name of the argument with respect to which the antiderivative will be calculated.
     *
     * @return the antiderivative of \p s with respect to \p name.
     *
     * @throws unspecified any exception thrown by the invoked series method.
     */
    template <typename T>
    auto operator()(const T &s, const std::string &name) const -> decltype(s.integrate(name))
    {
        return s.integrate(name);
    }
};

inline namespace impl
{

// This is the candidate evaluation type: the product of the evaluation of the coefficient by
// the evaluation of the key.
template <typename Series, typename T>
using series_eval_type = decltype(
    math::evaluate(std::declval<const typename Series::term_type::cf_type &>(), std::declval<const symbol_fmap<T> &>())
    * std::declval<const typename Series::term_type::key_type &>().evaluate(std::declval<const std::vector<T> &>(),
                                                                            std::declval<const symbol_fset &>()));

template <typename Series, typename T>
using math_series_evaluate_enabler = enable_if_t<
    conjunction<is_series<Series>, is_addable_in_place<series_eval_type<Series, T>>,
                std::is_constructible<series_eval_type<Series, T>, int>, is_returnable<series_eval_type<Series, T>>,
                std::is_destructible<T>, std::is_copy_constructible<T>, std::is_move_constructible<T>>::value>;
} // namespace impl

/// Specialisation of the implementation of piranha::math::evaluate() for series types.
/**
 * This specialisation is activated when all the following conditions hold:
 * - \p Series is an instance of piranha::series,
 * - the coefficient and key types of \p Series support evaluation,
 * - the types resulting from the evaluation of coefficients and keys can be multiplied,
 *   yielding a result of type \p E,
 * - \p E is addable in place, constructible from \p int and it satisfies piranha::is_returnable,
 * - \p T is destructible, copy-constructible and move-constructible.
 */
template <typename Series, typename T>
class evaluate_impl<Series, T, math_series_evaluate_enabler<Series, T>>
{
    using eval_type = series_eval_type<Series, T>;
    // Wrapper to do multadd either via math::multiply_accumulate(), if supported, or just
    // plain math operators.
    template <typename E, enable_if_t<has_multiply_accumulate<E>::value, int> = 0>
    static void multadd(E &retval, const E &a, const E &b)
    {
        math::multiply_accumulate(retval, a, b);
    }
    template <typename E1, typename E2, typename E3>
    static void multadd(E1 &retval, const E2 &a, const E3 &b)
    {
        retval += a * b;
    }

public:
    /// Call operator.
    /**
     * Series evaluation starts with a zero-initialised instance of the return type, which is determined
     * according to the evaluation types of coefficient and key. The return value accumulates the evaluation
     * of all terms in the series via the product of the evaluations of the coefficient-key pairs in each term.
     * The input dictionary \p dict specifies with which value each symbolic quantity will be evaluated.
     *
     * @param s the series to be evaluated.
     * @param dict the dictionary that will be used for evaluation.
     *
     * @return the result of evaluating the series according to the evaluation dictionary \p dict.
     *
     * @throws std::invalid_argument if a symbol of \p s does not appear in \p dict.
     * @throws unspecified any exception thrown by:
     * - coefficient and key evaluation,
     * - memory errors in standard containers,
     * - the copy constructor of \p T,
     * - arithmetic operations on the evaluation type,
     * - piranha::safe_cast().
     */
    eval_type operator()(const Series &s, const symbol_fmap<T> &dict) const
    {
        // NOTE: possible improvement: if the evaluation type is less-than comparable,
        // build a vector of evaluated terms, sort it and accumulate (to minimise accuracy loss
        // with fp types and maybe improve performance - e.g., for integers).

        // Cache a reference to the symbol set.
        const auto &ss = s.get_symbol_set();

        // Init the vector that will be used for key evaluation.
        std::vector<T> evec;
        evec.reserve(piranha::safe_cast<decltype(evec.size())>(ss.size()));

        auto it_dict = dict.begin();
        const auto it_dict_f = dict.end();
        for (const auto &sym : ss) {
            // Try to locate the current sym in the
            // [it_dict, it_dict_f) range. Store the result in it_dict.
            it_dict = std::lower_bound(
                it_dict, it_dict_f, sym,
                [](const std::pair<std::string, T> &p, const std::string &str) { return p.first < str; });
            // NOTE: if it_dict != it_dict_f, we found a value in the dict range which is >= sym,
            // but we still need to check it is really the same.
            if (unlikely(it_dict == it_dict_f || it_dict->first != sym)) {
                // The it_ss value was not found: we cannot evaluate.
                piranha_throw(std::invalid_argument, "cannot evaluate series: the symbol '" + sym
                                                         + "' is missing from the series evaluation dictionary'");
            }
            // Append the value mapped to the current ss symbol to the vector.
            evec.push_back(it_dict->second);
            // NOTE: we increase it_dict at the end of the iteration because, if we
            // get there, it means we identified a symbol of ss in dict. For the next
            // iteration of the for loop we want to start looking for the next ss symbol
            // right *after* the element in dict we just found.
            ++it_dict;
        }
        piranha_assert(evec.size() == ss.size());

        // Init the return value and accumulate it.
        eval_type retval(0);
        for (const auto &t : s._container()) {
            multadd(retval, math::evaluate(t.m_cf, dict), t.m_key.evaluate(evec, ss));
        }
        return retval;
    }
};
} // namespace math

#if defined(PIRANHA_WITH_BOOST_S11N)

inline namespace impl
{

// NOTE: we check first here if Series is a series, so that, if it is not, we do not instantiate other has_boost_save
// checks (which might result in infinite recursion due to this enabler being re-instantiated).
template <typename Archive, typename Series>
using series_boost_save_enabler = enable_if_t<
    conjunction<is_series<Series>, has_boost_save<Archive, decltype(symbol_fset{}.size())>,
                has_boost_save<Archive, const std::string &>,
                has_boost_save<Archive, decltype(std::declval<const Series &>().size())>,
                has_boost_save<Archive, typename Series::term_type::cf_type>,
                has_boost_save<Archive, boost_s11n_key_wrapper<typename Series::term_type::key_type>>>::value>;

// NOTE: the requirement that Series must not be const is in the is_series check.
template <typename Archive, typename Series>
using series_boost_load_enabler = enable_if_t<conjunction<
    is_series<Series>, has_boost_load<Archive, decltype(symbol_fset{}.size())>, has_boost_load<Archive, std::string>,
    has_boost_load<Archive, decltype(std::declval<const Series &>().size())>,
    has_boost_load<Archive, typename Series::term_type::cf_type>,
    has_boost_load<Archive, boost_s11n_key_wrapper<typename Series::term_type::key_type>>>::value>;
} // namespace impl

/// Specialisation of piranha::boost_save() for piranha::series.
/**
 * \note
 * This specialisation is enabled only if:
 * - \p Series satisfies piranha::is_series,
 * - the coefficient type, \p std::string and the integral types representing the size of the series
 *   and the size of piranha::symbol_fset satisfy piranha::has_boost_save,
 * - the key type satisfies piranha::has_boost_save (via piranha::boost_s11n_key_wrapper).
 *
 * @throws unspecified any exception thrown by piranha::boost_save().
 */
template <typename Archive, typename Series>
struct boost_save_impl<Archive, Series, series_boost_save_enabler<Archive, Series>>
    : boost_save_via_boost_api<Archive, Series> {
};

/// Specialisation of piranha::boost_load() for piranha::series.
/**
 * \note
 * This specialisation is enabled only if:
 * - \p Series satisfies piranha::is_series,
 * - the coefficient type, \p std::string and the integral types representing the size of the series and
 *   the size of piranha::symbol_fset satisfy piranha::has_boost_load,
 * - the key type satisfies piranha::has_boost_load (via piranha::boost_s11n_key_wrapper).
 *
 * The basic exception safety guarantee is provided.
 *
 * @throws unspecified any exception thrown by:
 * - piranha::boost_load(),
 * - memory errors in standard containers,
 * - piranha::safe_cast(),
 * - the public interface of piranha::symbol_fset and piranha::hash_set,
 * - <tt>boost::numeric_cast()</tt>,
 * - piranha::series::insert(), piranha::series::set_symbol_set().
 */
template <typename Archive, typename Series>
struct boost_load_impl<Archive, Series, series_boost_load_enabler<Archive, Series>>
    : boost_load_via_boost_api<Archive, Series> {
};

#endif

#if defined(PIRANHA_WITH_MSGPACK)

inline namespace impl
{

// Same scheme as above for Boost.
template <typename Stream, typename Series>
using series_msgpack_pack_enabler
    = enable_if_t<conjunction<is_series<Series>, is_msgpack_stream<Stream>, has_msgpack_pack<Stream, std::string>,
                              has_msgpack_pack<Stream, typename Series::term_type::cf_type>,
                              key_has_msgpack_pack<Stream, typename Series::term_type::key_type>>::value>;

template <typename Series>
using series_msgpack_convert_enabler
    = enable_if_t<conjunction<is_series<Series>, has_msgpack_convert<std::string>,
                              has_msgpack_convert<typename Series::term_type::cf_type>,
                              key_has_msgpack_convert<typename Series::term_type::key_type>>::value>;
} // namespace impl

/// Specialisation of piranha::msgpack_pack() for piranha::series.
/**
 * \note
 * This specialisation is enabled only if:
 * - \p Series satisfies piranha::is_series,
 * - \p Stream satisfies piranha::is_msgpack_stream,
 * - the coefficient type and \p std::string satisfy piranha::has_msgpack_pack,
 * - the key type satisfies piranha::key_has_msgpack_pack,
 * - the size types of piranha::series and piranha::symbol_fset are safely convertible to \p std::uint32_t.
 */
template <typename Stream, typename Series>
struct msgpack_pack_impl<Stream, Series, series_msgpack_pack_enabler<Stream, Series>> {
    /// Call operator.
    /**
     * The msgpack representation of a series consists of an array of 2 elements:
     * - the list of symbols, represented as an array of strings,
     * - the list of terms, represented as an array of coefficient-key pairs.
     *
     * @param packer the target <tt>msgpack::packer</tt>.
     * @param s the input series.
     * @param f the desired piranha::msgpack_format.
     *
     * @throws unspecified any exception thrown by:
     * - the public interface of <tt>msgpack::packer</tt>,
     * - piranha::msgpack_pack(),
     * - piranha::safe_cast(),
     * - the <tt>%msgpack_pack()</tt> method of the key.
     */
    void operator()(msgpack::packer<Stream> &packer, const Series &s, msgpack_format f) const
    {
        // A series is an array made of:
        // - the symbol set,
        // - the array of terms.
        packer.pack_array(2u);
        // Pack the symbol set.
        const auto &ss = s.get_symbol_set();
        packer.pack_array(piranha::safe_cast<std::uint32_t>(ss.size()));
        for (const auto &sym : ss) {
            msgpack_pack(packer, sym, f);
        }
        // Pack the terms.
        packer.pack_array(piranha::safe_cast<std::uint32_t>(s.size()));
        for (const auto &t : s._container()) {
            // Each term is an array made of two elements, coefficient and key.
            packer.pack_array(2u);
            msgpack_pack(packer, t.m_cf, f);
            t.m_key.msgpack_pack(packer, f, ss);
        }
    }
};

/// Specialisation of piranha::msgpack_convert() for piranha::series.
/**
 * \note
 * This specialisation is enabled only if:
 * - \p Series satisfies piranha::is_series,
 * - \p std::string and the coefficient type satisfy piranha::has_msgpack_convert,
 * - the key type satisfies piranha::key_has_msgpack_convert.
 */
template <typename Series>
struct msgpack_convert_impl<Series, series_msgpack_convert_enabler<Series>> {
    /// Call operator.
    /**
     * The call operator offers the basic exception safety guarantee: upon deserialization errors, \p s will
     * be left in an unspecified (but valid) state.
     *
     * @param s the output series.
     * @param o the <tt>msgpack::object</tt> that will be converted into \p s.
     * @param f the desired piranha::msgpack_format.
     *
     * @throws std::invalid_argument if \p o contains an array with an invalid number of
     * elements, or if \p f is piranha::msgpack_format::portable and the serialized object
     * has been created with a later version of Piranha.
     * @throws unspecified any exception thrown by:
     * - memory errors in standard containers,
     * - the public interface of <tt>msgpack::object</tt>,
     * - piranha::msgpack_convert(),
     * - the <tt>%msgpack_convert()</tt> method of the key,
     * - the public interfaces of piranha::symbol_fset, piranha::hash_set and piranha::series,
     * - the constructor of the term type of the series,
     * - <tt>boost::numeric_cast()</tt>.
     */
    void operator()(Series &s, const msgpack::object &o, msgpack_format f) const
    {
        using term_type = typename Series::term_type;
        using cf_type = typename term_type::cf_type;
        using key_type = typename term_type::key_type;
        using s_size_t = decltype(s.size());
        // Erase s.
        s = Series{};
        // Convert the object.
        std::array<std::vector<msgpack::object>, 2> tmp_v;
        o.convert(tmp_v);
        // Create the symbol set, passing through a vec of str.
        std::vector<std::string> v_str;
        std::transform(tmp_v[0].begin(), tmp_v[0].end(), std::back_inserter(v_str),
                       [f](const msgpack::object &obj) -> std::string {
                           std::string tmp_str;
                           msgpack_convert(tmp_str, obj, f);
                           return tmp_str;
                       });
        s.set_symbol_set(symbol_fset(v_str.begin(), v_str.end()));
        // Preallocate buckets.
        s._container().rehash(boost::numeric_cast<s_size_t>(
            std::ceil(static_cast<double>(tmp_v[1].size()) / s._container().max_load_factor())));
        // Insert all the terms.
        for (const auto &t : tmp_v[1]) {
            std::array<msgpack::object, 2> tmp_term;
            t.convert(tmp_term);
            cf_type tmp_cf;
            key_type tmp_key;
            msgpack_convert(tmp_cf, tmp_term[0], f);
            tmp_key.msgpack_convert(tmp_term[1], f, s.get_symbol_set());
            s.insert(term_type{std::move(tmp_cf), std::move(tmp_key)});
        }
    }
};

#endif

inline namespace impl
{

template <typename T>
using series_zero_is_absorbing_enabler = enable_if_t<is_series<uncvref_t<T>>::value>;
}

/// Specialisation of piranha::zero_is_absorbing for piranha::series.
/**
 * \note
 * This specialisation is enabled if \p T, after the removal of cv/reference qualifiers, satisfies piranha::is_series.
 *
 * The value of the type trait will be the value of piranha::zero_is_absorbing for the coefficient type of \p T. This
 * type trait requires \p T to satisfy piranha::is_multipliable, after the removal of cv/reference qualifiers.
 */
template <typename T>
struct zero_is_absorbing<T, series_zero_is_absorbing_enabler<T>> {
private:
    PIRANHA_TT_CHECK(is_multipliable, uncvref_t<T>);
    static const bool implementation_defined = zero_is_absorbing<typename uncvref_t<T>::term_type::cf_type>::value;

public:
    /// Value of the type trait.
    static const bool value = implementation_defined;
};

template <typename T>
const bool zero_is_absorbing<T, series_zero_is_absorbing_enabler<T>>::value;
} // namespace piranha

#endif
