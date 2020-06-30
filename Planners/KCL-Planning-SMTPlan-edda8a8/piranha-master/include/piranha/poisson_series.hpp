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

#ifndef PIRANHA_POISSON_SERIES_HPP
#define PIRANHA_POISSON_SERIES_HPP

#include <algorithm>
#include <atomic>
#include <boost/container/container_fwd.hpp>
#include <boost/iterator/transform_iterator.hpp>
#include <iterator>
#include <stdexcept>
#include <string>
#include <type_traits>
#include <utility>
#include <vector>

#include <piranha/base_series_multiplier.hpp>
#include <piranha/config.hpp>
#include <piranha/detail/divisor_series_fwd.hpp>
#include <piranha/detail/init.hpp>
#include <piranha/detail/poisson_series_fwd.hpp>
#include <piranha/detail/polynomial_fwd.hpp>
#include <piranha/detail/sfinae_types.hpp>
#include <piranha/exceptions.hpp>
#include <piranha/forwarding.hpp>
#include <piranha/integer.hpp>
#include <piranha/ipow_substitutable_series.hpp>
#include <piranha/is_cf.hpp>
#include <piranha/key_is_multipliable.hpp>
#include <piranha/math.hpp>
#include <piranha/math/cos.hpp>
#include <piranha/math/degree.hpp>
#include <piranha/math/gcd3.hpp>
#include <piranha/math/is_zero.hpp>
#include <piranha/math/sin.hpp>
#include <piranha/power_series.hpp>
#include <piranha/real_trigonometric_kronecker_monomial.hpp>
#include <piranha/safe_cast.hpp>
#include <piranha/series.hpp>
#include <piranha/series_multiplier.hpp>
#include <piranha/substitutable_series.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/t_substitutable_series.hpp>
#include <piranha/term.hpp>
#include <piranha/thread_pool.hpp>
#include <piranha/trigonometric_series.hpp>
#include <piranha/type_traits.hpp>

namespace piranha
{

namespace detail
{

struct poisson_series_tag {
};
}

/// Poisson series class.
/**
 * This class represents multivariate Poisson series as collections of multivariate Poisson series terms,
 * in which the trigonometric monomials are represented by piranha::rtk_monomial.
 * \p Cf represents the ring over which the Poisson series is defined.
 *
 * This class satisfies the piranha::is_series type trait.
 *
 * ## Type requirements ##
 *
 * \p Cf must be suitable for use in piranha::series as first template argument.
 *
 * ## Exception safety guarantee ##
 *
 * This class provides the same guarantee as the base series type it derives from.
 *
 * ## Move semantics ##
 *
 * Move semantics is equivalent to the move semantics of the base series type it derives from.
 */
// TODO:
// - make this more general, make the key type selectable;
// - once the above is done, remeber to fix the rebind alias.
// - once we have a selectable key type, we must take care that in a few places we assume that the value type
//   of the key is a C++ integral, but this might not be the case any more (e.g., in the sin/cos implementation
//   we will need a safe cast) -> also in integrate(), there are a few occurrences of this (e.g., == 0 should
//   become piranha::is_zero() etc.). Will also need the is_integrable check on the key type.
template <typename Cf>
class poisson_series
    : public power_series<
          ipow_substitutable_series<
              substitutable_series<
                  t_substitutable_series<trigonometric_series<series<Cf, rtk_monomial, poisson_series<Cf>>>,
                                         poisson_series<Cf>>,
                  poisson_series<Cf>>,
              poisson_series<Cf>>,
          poisson_series<Cf>>,
      detail::poisson_series_tag
{
    using base
        = power_series<ipow_substitutable_series<
                           substitutable_series<t_substitutable_series<
                                                    trigonometric_series<series<Cf, rtk_monomial, poisson_series<Cf>>>,
                                                    poisson_series<Cf>>,
                                                poisson_series<Cf>>,
                           poisson_series<Cf>>,
                       poisson_series<Cf>>;
    // Sin/cos utils.
    // Types coming out of sin()/cos() for the base type. These will also be the final types.
    template <typename T>
    using sin_type = sin_t<const typename T::base &>;
    template <typename T>
    using cos_type = cos_t<const typename T::base &>;
    // Case 0: Poisson series is not suitable for special sin() implementation. Just forward to the base one, via
    // casting.
    template <typename T = poisson_series, typename std::enable_if<!detail::poly_in_cf<T>::value, int>::type = 0>
    sin_type<T> sin_impl() const
    {
        return piranha::sin(*static_cast<const base *>(this));
    }
    // Case 1: Poisson series is suitable for special sin() implementation via poly. This can fail at runtime depending
    // on what is contained in the coefficients. The return type is the same as the base one, as in this routine we only
    // need operations which are supported by all coefficient types, no need for rebinding or anything like that.
    template <typename T = poisson_series, typename std::enable_if<detail::poly_in_cf<T>::value, int>::type = 0>
    sin_type<T> sin_impl() const
    {
        return special_sin_cos<false, sin_type<T>>(*this);
    }
    // Same as above, for cos().
    template <typename T = poisson_series, typename std::enable_if<!detail::poly_in_cf<T>::value, int>::type = 0>
    cos_type<T> cos_impl() const
    {
        return piranha::cos(*static_cast<const base *>(this));
    }
    template <typename T = poisson_series, typename std::enable_if<detail::poly_in_cf<T>::value, int>::type = 0>
    cos_type<T> cos_impl() const
    {
        return special_sin_cos<true, cos_type<T>>(*this);
    }
    // Functions to handle the case in which special sin/cos implementation fails. They will just call the base sin/cos,
    // we need them with tag dispatching as the implementation below is generic and needs to cope with potentially
    // different sin/cos return types.
    template <typename T>
    static cos_type<T> special_sin_cos_failure(const T &s, const std::true_type &)
    {
        return piranha::cos(static_cast<const typename T::base &>(s));
    }
    template <typename T>
    static sin_type<T> special_sin_cos_failure(const T &s, const std::false_type &)
    {
        return piranha::sin(static_cast<const typename T::base &>(s));
    }
    // Convert an input polynomial to a Poisson series of type RetT. The conversion
    // will be successful if the polynomial can be reduced to an integral linear combination of
    // symbols.
    template <bool IsCos, typename RetT, typename Poly>
    static RetT poly_to_ps(const Poly &poly)
    {
        // Shortcuts.
        typedef typename RetT::term_type term_type;
        typedef typename term_type::cf_type cf_type;
        typedef typename term_type::key_type key_type;
        typedef typename key_type::value_type value_type;
        // Try to get the integral combination from the poly coefficient.
        auto lc = poly.integral_combination();
        // Change sign if needed.
        bool sign_change = false;
        if (!lc.empty() && lc.begin()->second.sgn() < 0) {
            std::for_each(lc.begin(), lc.end(), [](std::pair<const std::string, integer> &p) { p.second.neg(); });
            sign_change = true;
        }
        // Return value.
        RetT retval;
        // Build vector of integral multipliers and the symbol set.
        // NOTE: integral_combination returns a string map, which is guaranteed to be ordered.
        struct t_iter {
            const std::string &operator()(const typename decltype(lc)::value_type &p) const
            {
                return p.first;
            }
        };
        retval.set_symbol_set(symbol_fset(boost::container::ordered_unique_range_t{},
                                          boost::make_transform_iterator(lc.begin(), t_iter{}),
                                          boost::make_transform_iterator(lc.end(), t_iter{})));
        piranha_assert(retval.get_symbol_set().size() == lc.size());
        std::vector<value_type> v;
        std::transform(lc.begin(), lc.end(), std::back_inserter(v), [](const typename decltype(lc)::value_type &p) {
            // NOTE: this should probably be a safe_cast.
            // The value type here could be anything, and not guaranteed to be castable,
            // even if in the current implementation this is guaranteed to be a signed
            // int of some kind.
            return static_cast<value_type>(p.second);
        });
        // Build term, fix signs and flavour and move-insert it.
        term_type term(cf_type(1), key_type(v.begin(), v.end()));
        if (!IsCos) {
            term.m_key.set_flavour(false);
            if (sign_change) {
                // NOTE: negate is supported by any coefficient type.
                math::negate(term.m_cf);
            }
        }
        retval.insert(std::move(term));
        return retval;
    }
    // Special sin/cos implementation when we have reached the first polynomial coefficient in the hierarchy.
    template <bool IsCos, typename RetT, typename T,
              typename std::enable_if<std::is_base_of<detail::polynomial_tag, typename T::term_type::cf_type>::value,
                                      int>::type
              = 0>
    RetT special_sin_cos(const T &s) const
    {
        // Do something only if the series is equivalent to a polynomial.
        if (s.is_single_coefficient() && !s.empty()) {
            try {
                return poly_to_ps<IsCos, RetT>(s._container().begin()->m_cf);
            } catch (const std::invalid_argument &) {
                // Interpret invalid_argument as a failure in extracting integral combination,
                // and move on.
            }
        }
        return special_sin_cos_failure(*this, std::integral_constant<bool, IsCos>{});
    }
    // The coefficient is not a polynomial: recurse to the inner coefficient type, if the current coefficient type
    // is suitable.
    template <bool IsCos, typename RetT, typename T,
              typename std::enable_if<!std::is_base_of<detail::polynomial_tag, typename T::term_type::cf_type>::value,
                                      int>::type
              = 0>
    RetT special_sin_cos(const T &s) const
    {
        if (s.is_single_coefficient() && !s.empty()) {
            return special_sin_cos<IsCos, RetT>(s._container().begin()->m_cf);
        }
        return special_sin_cos_failure(*this, std::integral_constant<bool, IsCos>{});
    }
    // Integration utils.
    // The type resulting from the integration of the key of series T.
    template <typename T>
    using key_integrate_type
        = decltype(std::declval<const typename T::term_type::key_type &>()
                       .integrate(std::declval<const std::string &>(), std::declval<const symbol_fset &>())
                       .first);
    // Basic integration requirements for series T, to be satisfied both when the coefficient is a polynomial
    // and when it is not. ResT is the type of the result of the integration.
    // NOTE: this used to be a template alias to be used with true_tt in the usual fashion, but there is a pesky bug
    // in GCC < 5 that results in a segfault of the compiler. This is a workaround.
    template <typename T, typename ResT, typename = void>
    struct basic_integrate_requirements {
        static const bool value = false;
    };
    template <typename T, typename ResT>
    struct basic_integrate_requirements<
        T, ResT,
        typename std::enable_if<
            // Coefficient differentiable, and can call is_zero on the result.
            // NOTE: use addlref_t to avoid forming a cv-qualified reference. See this defect:
            // http://www.open-std.org/jtc1/sc22/wg21/docs/cwg_defects.html#1510
            // This should not be a concern in C++14 and later.
            is_is_zero_type<addlref_t<const decltype(math::partial(
                std::declval<const typename T::term_type::cf_type &>(), std::declval<const std::string &>()))>>::value
            &&
            // The result needs to be addable in-place.
            is_addable_in_place<ResT>::value &&
            // It also needs to be ctible from zero.
            std::is_constructible<ResT, int>::value>::type> {
        static const bool value = true;
    };
    // Machinery for the integration of the coefficient only.
    // Type resulting from the integration of the coefficient only.
    template <typename ResT, typename T>
    using i_cf_only_type = decltype(
        math::integrate(std::declval<const typename T::term_type::cf_type &>(), std::declval<const std::string &>())
        * std::declval<const T &>());
    // Integration of coefficient only is enabled only if the type above is well defined
    // and it is the same as the result type.
    template <typename ResT, typename T, typename = void>
    struct i_cf_only_enabler {
        static const bool value = false;
    };
    template <typename ResT, typename T>
    struct i_cf_only_enabler<ResT, T,
                             typename std::enable_if<std::is_same<ResT, i_cf_only_type<ResT, T>>::value>::type> {
        static const bool value = true;
    };
    template <typename ResT, typename It, typename T = poisson_series>
    void integrate_coefficient_only(ResT &retval, It it, const std::string &name,
                                    typename std::enable_if<i_cf_only_enabler<ResT, T>::value>::type * = nullptr) const
    {
        using term_type = typename base::term_type;
        using cf_type = typename term_type::cf_type;
        poisson_series tmp;
        tmp.set_symbol_set(this->m_symbol_set);
        tmp.insert(term_type(cf_type(1), it->m_key));
        retval += math::integrate(it->m_cf, name) * tmp;
    }
    template <typename ResT, typename It, typename T = poisson_series>
    void integrate_coefficient_only(ResT &, It, const std::string &,
                                    typename std::enable_if<!i_cf_only_enabler<ResT, T>::value>::type * = nullptr) const
    {
        piranha_throw(std::invalid_argument,
                      "unable to perform Poisson series integration: coefficient type is not integrable");
    }
    // Empty for SFINAE.
    template <typename T, typename = void>
    struct integrate_type_ {
    };
    // Non-polynomial coefficient.
    template <typename T>
    using npc_res_type = decltype((std::declval<const T &>() * std::declval<const typename T::term_type::cf_type &>())
                                  / std::declval<const key_integrate_type<T> &>());
    template <typename T>
    struct integrate_type_<
        T, typename std::enable_if<!std::is_base_of<detail::polynomial_tag, typename T::term_type::cf_type>::value
                                   && basic_integrate_requirements<T, npc_res_type<T>>::value>::type> {
        using type = npc_res_type<T>;
    };
    // Polynomial coefficient.
    // Coefficient type divided by the value coming from the integration of the key.
    template <typename T>
    using i_cf_type = decltype(std::declval<const typename T::term_type::cf_type &>()
                               / std::declval<const key_integrate_type<T> &>());
    // Derivative of the type above.
    template <typename T>
    using i_cf_type_p
        = decltype(math::partial(std::declval<const i_cf_type<T> &>(), std::declval<const std::string &>()));
    // The final return type.
    template <typename T>
    using pc_res_type = decltype(std::declval<const i_cf_type_p<T> &>() * std::declval<const T &>());
    template <typename T>
    struct integrate_type_<
        T, typename std::enable_if<std::is_base_of<detail::polynomial_tag, typename T::term_type::cf_type>::value
                                   && basic_integrate_requirements<T, pc_res_type<T>>::value &&
                                   // We need to be able to add in the npc type.
                                   is_addable_in_place<pc_res_type<T>, npc_res_type<T>>::value &&
                                   // We need to be able to compute the degree of the polynomials and
                                   // convert it safely to integer.
                                   is_safely_castable<addlref_t<const decltype(piranha::degree(
                                                          std::declval<const typename T::term_type::cf_type &>(),
                                                          std::declval<const symbol_fset &>()))>,
                                                      integer>::value
                                   &&
                                   // We need this conversion in the algorithm below.
                                   std::is_constructible<i_cf_type_p<T>, i_cf_type<T>>::value &&
                                   // This type needs also to be negated.
                                   has_negate<i_cf_type_p<T>>::value>::type> {
        using type = pc_res_type<T>;
    };
    // The final typedef.
    template <typename T>
    using integrate_type = typename std::enable_if<is_returnable<typename integrate_type_<T>::type>::value,
                                                   typename integrate_type_<T>::type>::type;
    template <typename T = poisson_series>
    integrate_type<T> integrate_impl(const std::string &s, const typename base::term_type &term,
                                     const std::true_type &) const
    {
        typedef typename base::term_type term_type;
        typedef typename term_type::cf_type cf_type;
        integer degree;
        try {
            degree = piranha::safe_cast<integer>(piranha::degree(term.m_cf, {s}));
        } catch (const safe_cast_failure &) {
            piranha_throw(
                std::invalid_argument,
                "unable to perform Poisson series integration: cannot convert polynomial degree to an integer");
        }
        // If the variable is in both cf and key, and the cf degree is negative, we cannot integrate.
        if (degree.sgn() < 0) {
            piranha_throw(
                std::invalid_argument,
                "unable to perform Poisson series integration: polynomial coefficient has negative integral degree");
        }
        // Init retval and auxiliary quantities for the iteration.
        auto key_int = term.m_key.integrate(s, this->m_symbol_set);
        // NOTE: here we are sure that the variable is contained in the monomial.
        piranha_assert(key_int.first != 0);
        poisson_series tmp;
        tmp.set_symbol_set(this->m_symbol_set);
        // NOTE: not move for .second, as it is needed in the loop below.
        tmp.insert(term_type(cf_type(1), key_int.second));
        i_cf_type_p<T> p_cf(term.m_cf / std::move(key_int.first));
        integrate_type<T> retval(p_cf * tmp);
        for (integer i(1); i <= degree; ++i) {
            key_int = key_int.second.integrate(s, this->m_symbol_set);
            piranha_assert(key_int.first != 0);
            p_cf = math::partial(p_cf / std::move(key_int.first), s);
            // Sign change due to the second portion of integration by part.
            math::negate(p_cf);
            tmp = poisson_series{};
            tmp.set_symbol_set(this->m_symbol_set);
            // NOTE: don't move second.
            tmp.insert(term_type(cf_type(1), key_int.second));
            retval += p_cf * tmp;
        }
        return retval;
    }
    template <typename T = poisson_series>
    integrate_type<T> integrate_impl(const std::string &, const typename base::term_type &,
                                     const std::false_type &) const
    {
        piranha_throw(std::invalid_argument,
                      "unable to perform Poisson series integration: coefficient type is not a polynomial");
    }
    // Time integration.
    // First the implementation for divisor series.
    template <typename T>
    using ti_type_divisor_
        = decltype((std::declval<const T &>() * std::declval<const typename T::term_type::cf_type &>())
                   / std::declval<const integer &>());
    template <typename T>
    using ti_type_divisor = typename std::enable_if<
        std::is_constructible<ti_type_divisor_<T>, int>::value && is_addable_in_place<ti_type_divisor_<T>>::value
            && std::is_base_of<detail::divisor_series_tag, typename T::term_type::cf_type>::value,
        ti_type_divisor_<T>>::type;
    template <typename T = poisson_series>
    ti_type_divisor<T> t_integrate_impl(const std::vector<std::string> &names) const
    {
        using return_type = ti_type<T>;
        // Calling series types.
        using term_type = typename base::term_type;
        // The value type of the trigonometric key.
        using k_value_type = typename term_type::key_type::value_type;
        // Divisor series types.
        using d_series_type = typename base::term_type::cf_type;
        using d_term_type = typename d_series_type::term_type;
        using d_cf_type = typename d_term_type::cf_type;
        using d_key_type = typename d_term_type::key_type;
        // Initialise the return value.
        return_type retval(0);
        // Setup of the symbol set.
        piranha_assert(names.size() == this->m_symbol_set.size());
        const symbol_fset div_symbols(names.begin(), names.end());
        piranha_assert(div_symbols.size() == names.size());
        // A temp vector of integers used to normalise the divisors coming
        // out of the integration operation from the trig keys.
        std::vector<integer> tmp_int;
        // Build the return value.
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            // Clear the tmp integer vector.
            tmp_int.clear();
            // Get the vector of trigonometric multipliers.
            const auto trig_vector = it->m_key.unpack(this->m_symbol_set);
            // Copy it over to the tmp_int as integer values.
            std::transform(trig_vector.begin(), trig_vector.end(), std::back_inserter(tmp_int),
                           [](const k_value_type &n) { return integer(n); });
            // Determine the common divisor.
            // NOTE: both the divisor and the trigonometric key share the canonical form in which the
            // first nonzero multiplier is positive, so we don't need to account for sign flips when
            // constructing a divisor from the trigonometric part. We just need to take care
            // of the common divisor.
            integer cd;
            bool first_nonzero_found = false;
            for (const auto &n_int : tmp_int) {
                // NOTE: gcd is safe, operating on integers.
                piranha::gcd3(cd, cd, n_int);
                if (!first_nonzero_found && !piranha::is_zero(n_int)) {
                    piranha_assert(n_int > 0);
                    first_nonzero_found = true;
                }
            }
            if (unlikely(piranha::is_zero(cd))) {
                piranha_throw(std::invalid_argument, "an invalid trigonometric term was encountered while "
                                                     "attempting a time integration");
            }
            // NOTE: gcd is always non-negative for integers.
            piranha_assert(cd.sgn() == 1);
            // Divide the vector by the common divisor.
            for (auto &n_int : tmp_int) {
                n_int /= cd;
            }
            // Build the temporary divisor series from the trigonometric arguments.
            d_series_type div_series;
            div_series.set_symbol_set(div_symbols);
            // Build the divisor key.
            d_key_type div_key;
            div_key.insert(tmp_int.begin(), tmp_int.end(), typename d_key_type::value_type(1));
            // Finish building the temporary divisor series.
            div_series.insert(d_term_type(d_cf_type(1), std::move(div_key)));
            // Temporary Poisson series from the current term, with the trig flavour flipped.
            poisson_series tmp_ps;
            tmp_ps.set_symbol_set(this->m_symbol_set);
            auto tmp_key = it->m_key;
            tmp_key.set_flavour(!tmp_key.get_flavour());
            tmp_ps.insert(term_type(it->m_cf, std::move(tmp_key)));
            // Update the return value.
            auto tmp = (std::move(tmp_ps) * std::move(div_series)) / cd;
            // It also needs a negation, if the original trig key is a sine.
            if (!it->m_key.get_flavour()) {
                math::negate(tmp);
            }
            retval += std::move(tmp);
        }
        return retval;
    }
    // The final typedef.
    template <typename T>
    using ti_type
        = decltype(std::declval<const T &>().t_integrate_impl(std::declval<const std::vector<std::string> &>()));

public:
    /// Series rebind alias.
    template <typename Cf2>
    using rebind = poisson_series<Cf2>;
    /// Defaulted default constructor.
    /**
     * Will construct a Poisson series with zero terms.
     */
    poisson_series() = default;
    /// Defaulted copy constructor.
    poisson_series(const poisson_series &) = default;
    /// Defaulted move constructor.
    poisson_series(poisson_series &&) = default;
    PIRANHA_FORWARDING_CTOR(poisson_series, base)
    /// Trivial destructor.
    ~poisson_series()
    {
        PIRANHA_TT_CHECK(is_series, poisson_series);
    }
    /// Copy assignment operator.
    /**
     * @param other the assignment argument.
     *
     * @return a reference to \p this.
     *
     * @throws unspecified any exception thrown by the assignment operator of the base class.
     */
    poisson_series &operator=(const poisson_series &other) = default;
    /// Move assignment operator.
    /**
     * @param other the assignment argument.
     *
     * @return a reference to \p this.
     */
    poisson_series &operator=(poisson_series &&other) = default;
    PIRANHA_FORWARDING_ASSIGNMENT(poisson_series, base)
    /// Sine.
    /**
     * \note
     * This template method is enabled only if piranha::sin() can be called on the class
     * from which piranha::poisson_series derives (i.e., only if the default piranha::sin()
     * implementation for series is appropriate).
     *
     * In general, this method behaves exactly like the default implementation of piranha::sin() for series
     * types. If, however, a polynomial appears in the hierarchy of coefficients, then this method
     * will attempt to extract an integral linear combination of symbolic arguments and use it to construct
     * a Poisson series with a single term, unitary coefficient and the trigonometric key built from the linear
     * combination.
     *
     * For instance, if the calling Poisson series is
     * \f[
     * -2x + y,
     * \f]
     * then calling this method will produce the Poisson series
     * \f[
     * -\sin \left( 2x - y \right).
     * \f]
     *
     * If for any reason it is not possible to extract the linear integral combination,
     * then this method will forward the call to the default implementation of piranha::sin() for series
     * types.
     *
     * @return the sine of \p this.
     *
     * @throws unspecified any exception thrown by:
     * - piranha::series::is_single_coefficient(), piranha::series::insert(),
     * - memory allocation errors in standard containers,
     * - the constructors of coefficient, key and term types,
     * - the cast operator of piranha::integer,
     * - piranha::math::negate(),
     * - piranha::sin(),
     * - the extraction of a linear combination of integral arguments from the polynomial coefficient.
     */
    template <typename T = poisson_series>
    sin_type<T> sin() const
    {
        return sin_impl();
    }
    /// Cosine.
    /**
     * \note
     * This method is enabled only if piranha::poisson_series::sin() is enabled.
     *
     * This method works in the same way as piranha::poisson_series::sin().
     *
     * @return the cosine of \p this.
     *
     * @throws unspecified any exception thrown by piranha::poisson_series::sin().
     */
    template <typename T = poisson_series>
    cos_type<T> cos() const
    {
        return cos_impl();
    }
    /// Integration.
    /**
     * \note
     * This method is enabled only if the algorithm described below is supported by all the involved types.
     *
     * This method will attempt to compute the antiderivative of the Poisson series term by term using the
     * following procedure:
     * - if the term's monomial does not depend on the integration variable, the integration will be deferred to the
     *   coefficient;
     * - otherwise:
     *   - if the coefficient does not depend on the integration variable, the monomial is integrated;
     *   - if the coefficient is a polynomial, a strategy of integration by parts is attempted, its success depending on
     *     whether the degree of the polynomial is a non-negative integral value;
     *   - otherwise, an error will be produced.
     *
     * @param name integration variable.
     *
     * @return the antiderivative of \p this with respect to \p name.
     *
     * @throws std::invalid_argument if the integration procedure fails.
     * @throws unspecified any exception thrown by:
     * - piranha::math::partial(), piranha::is_zero(), piranha::math::integrate(), piranha::safe_cast() and
     *   piranha::math::negate(),
     * - the assignment operator of piranha::symbol_fset,
     * - term construction,
     * - coefficient construction, assignment and arithmetics,
     * - integration, construction and assignment of the key type,
     * - insert(),
     * - piranha::polynomial::degree(),
     * - series arithmetics.
     */
    template <typename T = poisson_series>
    integrate_type<T> integrate(const std::string &name) const
    {
        typedef typename base::term_type term_type;
        typedef typename term_type::cf_type cf_type;
        // Init the return value.
        integrate_type<T> retval(0);
        const auto it_f = this->m_container.end();
        for (auto it = this->m_container.begin(); it != it_f; ++it) {
            // Integrate the key first.
            auto key_int = it->m_key.integrate(name, this->m_symbol_set);
            // If the variable does not appear in the monomial, try deferring the integration
            // to the coefficient.
            if (key_int.first == 0) {
                integrate_coefficient_only(retval, it, name);
                continue;
            }
            // The variable is in the monomial, let's check if the variable is also in the coefficient.
            if (piranha::is_zero(math::partial(it->m_cf, name))) {
                // No variable in the coefficient, proceed with the integrated key and divide by multiplier.
                poisson_series tmp;
                tmp.set_symbol_set(this->m_symbol_set);
                tmp.insert(term_type(cf_type(1), std::move(key_int.second)));
                retval += (std::move(tmp) * it->m_cf) / key_int.first;
            } else {
                // With the variable both in the coefficient and the key, we only know how to proceed with polynomials.
                retval += integrate_impl(
                    name, *it, std::integral_constant<bool, std::is_base_of<detail::polynomial_tag, Cf>::value>());
            }
        }
        return retval;
    }
    /// Time integration.
    /**
     * \note
     * This method is enabled only if:
     * - the coefficient type is an instance of piranha::divisor_series, and
     * - the operations required by the computation of the time integration are supported by all
     *   the involved types.
     *
     * This is a special type of integration in which the trigonometric arguments are considered as linear functions
     * of time, and in which the integration variable is time itself. For instance, if the input series is
     * \f[
     * \frac{1}{5}z\cos\left( x - y \right),
     * \f]
     * the result of the time integration is
     * \f[
     * \frac{1}{5}{z}\frac{1}{\left(\nu_{x}-\nu_{y}\right)}\sin{\left({x}-{y}\right)},
     * \f]
     * where \f$ \nu_{x} \f$ and \f$ \nu_{y} \f$ are the frequencies associated to \f$ x \f$ and \f$ y \f$ (that is,
     * it is understood that \f$ x = \nu_{x}t \f$ and \f$ x = \nu_{y}t \f$).
     *
     * This method will throw an error if any term of the calling series has a unitary key (e.g., in the Poisson series
     * \f$ \frac{1}{5}z \f$ the only trigonometric key is \f$ \cos\left( 0 \right) \f$ and would thus result in a
     * division by zero
     * during a time integration).
     *
     * @return the result of the time integration.
     *
     * @throws std::invalid_argument if the calling series has a unitary key.
     * @throws unspecified any exception thrown by:
     * - memory errors in standard containers,
     * - the public interfaces of piranha::symbol_fset, piranha::integer and piranha::series,
     * - piranha::is_zero(), piranha::math::negate(),
     * - the mathematical operations needed to compute the result,
     * - piranha::divisor::insert(),
     * - construction of the involved types.
     */
    template <typename T = poisson_series>
    ti_type<T> t_integrate() const
    {
        std::vector<std::string> names;
        std::transform(this->m_symbol_set.begin(), this->m_symbol_set.end(), std::back_inserter(names),
                       [](const std::string &s) { return "\\nu_{" + s + "}"; });
        return t_integrate_impl(names);
    }
    /// Time integration (alternative overload).
    /**
     * \note
     * This method is enabled only if the other overload of piranha::poisson_series::t_integrate() is enabled.
     *
     * This method operates exactly like the other overload of piranha::poisson_series::t_integrate(), with the
     * difference
     * that the names of the symbols used to represent the frequencies are passed as the \p names argument, rather
     * than automatically deduced. The \p names argument must be sorted lexicographically, otherwise an error
     * will be produced. Duplicate entries in \p names will be removed.
     *
     * @param names the names of the symbols used to represent the frequencies.
     *
     * @return the result of the time integration.
     *
     * @throws std::invalid_argument if the size of \p names is not equal to the size of the symbol set of \p this
     * (after the removal of duplicate entries), or if \p names is not sorted lexicographically.
     * @throws unspecified any exception thrown by the other overload.
     */
    template <typename T = poisson_series>
    ti_type<T> t_integrate(std::vector<std::string> names) const
    {
        if (unlikely(!std::is_sorted(names.begin(), names.end()))) {
            piranha_throw(std::invalid_argument, "the list of symbol names must be ordered lexicographically");
        }
        // Remove duplicates.
        names.erase(std::unique(names.begin(), names.end()), names.end());
        if (unlikely(names.size() != this->m_symbol_set.size())) {
            piranha_throw(std::invalid_argument, "the number of symbols passed in input must be equal to the "
                                                 "number of symbols of the Poisson series");
        }
        return t_integrate_impl(names);
    }
};

namespace detail
{

template <typename Series>
using ps_series_multiplier_enabler = typename std::enable_if<std::is_base_of<poisson_series_tag, Series>::value>::type;
}

/// Specialisation of piranha::series_multiplier for piranha::poisson_series.
template <typename Series>
class series_multiplier<Series, detail::ps_series_multiplier_enabler<Series>> : public base_series_multiplier<Series>
{
    using base = base_series_multiplier<Series>;
    template <typename T>
    using call_enabler = typename std::enable_if<
        key_is_multipliable<typename T::term_type::cf_type, typename T::term_type::key_type>::value, int>::type;
    void divide_by_two(Series &s) const
    {
        // NOTE: if we ever implement multi-threaded series division we most likely need
        // to revisit this.
        piranha_assert(this->m_n_threads > 0u);
        if (this->m_n_threads == 1u) {
            // This is possible, as the requirements of series divisibility and trig key
            // multipliability overlap.
            s /= 2;
        } else {
            using bucket_size_type = typename base::bucket_size_type;
            using term_type = typename Series::term_type;
            auto &container = s._container();
            std::atomic<bucket_size_type> total_erase_count(0u);
            auto divider
                = [&container, &total_erase_count, this](bucket_size_type start_idx, bucket_size_type end_idx) {
                      // A vector of terms to be erased at each bucket iteration.
                      std::vector<term_type> term_list;
                      // Total number of terms erased by this thread.
                      bucket_size_type erase_count = 0u;
                      for (; start_idx != end_idx; ++start_idx) {
                          // Reset the list of terms to be erased.
                          term_list.clear();
                          const auto &list = container._get_bucket_list(start_idx);
                          for (const auto &t : list) {
                              t.m_cf /= 2;
                              if (unlikely(t.is_zero(this->m_ss))) {
                                  term_list.push_back(t);
                              }
                          }
                          for (const auto &t : term_list) {
                              container._erase(container._find(t, start_idx));
                              erase_count = static_cast<bucket_size_type>(erase_count + 1u);
                          }
                      }
                      // Update the global counter of erased terms.
                      total_erase_count += erase_count;
                  };
            // Buckets per thread.
            const auto bpt = static_cast<bucket_size_type>(container.bucket_count() / this->m_n_threads);
            // Go with the threads.
            future_list<decltype(divider(0u, 0u))> ff_list;
            try {
                for (unsigned i = 0u; i < this->m_n_threads; ++i) {
                    const auto start_idx = static_cast<bucket_size_type>(bpt * i);
                    // Special casing for the last thread.
                    const auto end_idx = (i == this->m_n_threads - 1u) ? container.bucket_count()
                                                                       : static_cast<bucket_size_type>(bpt * (i + 1u));
                    ff_list.push_back(thread_pool::enqueue(i, divider, start_idx, end_idx));
                }
                // First let's wait for everything to finish.
                ff_list.wait_all();
                // Then, let's handle the exceptions.
                ff_list.get_all();
            } catch (...) {
                ff_list.wait_all();
                // Clear out the container as it might be in an inconsistent state.
                container.clear();
                throw;
            }
            // Final size update - all of this is noexcept.
            const auto tot = total_erase_count.load();
            piranha_assert(tot <= s.size());
            container._update_size(static_cast<bucket_size_type>(s.size() - tot));
        }
    }

public:
    /// Inherit base constructors.
    using base::base;
    /// Call operator.
    /**
     * \note
     * This operator is enabled only if the coefficient and key types of \p Series satisfy
     * piranha::key_is_multipliable.
     *
     * The call operator will use base_series_multiplier::plain_multiplication().
     *
     * @return the result of the multiplication.
     *
     * @throws unspecified any exception thrown by base_series_multiplier::plain_multiplication(),
     * or by piranha::term::is_zero().
     */
    template <typename T = Series, call_enabler<T> = 0>
    Series operator()() const
    {
        auto retval(this->plain_multiplication());
        divide_by_two(retval);
        return retval;
    }
};
}

#endif
