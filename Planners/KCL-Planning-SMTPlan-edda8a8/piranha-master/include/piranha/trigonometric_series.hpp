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

#ifndef PIRANHA_TRIGONOMETRIC_SERIES_HPP
#define PIRANHA_TRIGONOMETRIC_SERIES_HPP

#include <algorithm>
#include <type_traits>
#include <utility>

#include <piranha/detail/init.hpp>
#include <piranha/forwarding.hpp>
#include <piranha/math.hpp>
#include <piranha/series.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/type_traits.hpp>

namespace piranha
{

namespace detail
{

// Tag for trigonometric series.
struct trigonometric_series_tag {
};
}

/// Trigonometric series toolbox.
/**
 * This toolbox extends a series class with properties of trigonometric series. Specifically, this class will
 * conditionally add methods
 * to query the trigonometric (partial,low) degree and order of a series. This augmentation takes places if either the
 * coefficient or the
 * key satisfy the relevant type traits: piranha::has_t_degree and related for the coefficient type,
 * piranha::key_has_t_degree and related
 * for the key type.
 *
 * Note that in order for the trigonometric methods to be enabled, coefficient and key type cannot satisfy these type
 * traits at the same time,
 * and all degree/order type traits need to be satisfied for the coefficient/key type. As an additional requirement, the
 * types returned when
 * querying the degree and order must be constructible from \p int, copy or move constructible, and less-than
 * comparable.
 *
 * If the above requirements are not satisfied, this class will not add any new functionality to the \p Series class and
 * will just provide generic constructors and assignment operators that will forward their arguments to \p Series.
 *
 * This class satisfies the piranha::is_series type trait.
 *
 * ## Exception safety guarantee ##
 *
 * This class provides the same guarantee as \p Series.
 *
 * ## Type requirements ##
 *
 * \p Series must be an instance of piranha::series.
 *
 * ## Move semantics ##
 *
 * Move semantics is equivalent to the move semantics of \p Series.
 */
template <typename Series>
class trigonometric_series : public Series, detail::trigonometric_series_tag
{
    PIRANHA_TT_CHECK(is_series, Series);
    typedef Series base;
    // Utilities to detect trigonometric terms.
    template <typename Cf>
    struct cf_trig_score {
        static const int value
            = has_t_degree<Cf>::value + has_t_ldegree<Cf>::value + has_t_order<Cf>::value + has_t_lorder<Cf>::value;
    };
    template <typename Key>
    struct key_trig_score {
        static const int value = key_has_t_degree<Key>::value + key_has_t_ldegree<Key>::value
                                 + key_has_t_order<Key>::value + key_has_t_lorder<Key>::value;
    };
    // Type checks for the degree/order type.
    template <typename T>
    using common_type_checks = conjunction<is_less_than_comparable<T>, is_returnable<T>>;
// Total versions.
#define PIRANHA_DEFINE_TRIG_PROPERTY_GETTER(property)                                                                  \
    template <typename Term, enable_if_t<cf_trig_score<typename Term::cf_type>::value == 4u                            \
                                             && key_trig_score<typename Term::key_type>::value == 0u,                  \
                                         int> = 0>                                                                     \
    static auto get_t_##property(const Term &t, const symbol_fset &)->decltype(math::t_##property(t.m_cf))             \
    {                                                                                                                  \
        return math::t_##property(t.m_cf);                                                                             \
    }                                                                                                                  \
    template <typename Term, enable_if_t<cf_trig_score<typename Term::cf_type>::value == 0u                            \
                                             && key_trig_score<typename Term::key_type>::value == 4u,                  \
                                         int> = 0>                                                                     \
    static auto get_t_##property(const Term &t, const symbol_fset &s)->decltype(t.m_key.t_##property(s))               \
    {                                                                                                                  \
        return t.m_key.t_##property(s);                                                                                \
    }                                                                                                                  \
    template <typename T>                                                                                              \
    using t_##property##_type_ = decltype(                                                                             \
        get_t_##property(std::declval<const typename T::term_type &>(), std::declval<const symbol_fset &>()));         \
    template <typename T>                                                                                              \
    using t_##property##_type                                                                                          \
        = enable_if_t<common_type_checks<t_##property##_type_<T>>::value, t_##property##_type_<T>>;
    PIRANHA_DEFINE_TRIG_PROPERTY_GETTER(degree)
    PIRANHA_DEFINE_TRIG_PROPERTY_GETTER(ldegree)
    PIRANHA_DEFINE_TRIG_PROPERTY_GETTER(order)
    PIRANHA_DEFINE_TRIG_PROPERTY_GETTER(lorder)
#undef PIRANHA_DEFINE_TRIG_PROPERTY_GETTER
// Partial versions.
#define PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER(property)                                                          \
    template <typename Term, enable_if_t<cf_trig_score<typename Term::cf_type>::value == 4u                            \
                                             && key_trig_score<typename Term::key_type>::value == 0u,                  \
                                         int> = 0>                                                                     \
    static auto get_t_##property(const Term &t, const symbol_idx_fset &, const symbol_fset &names)                     \
        ->decltype(math::t_##property(t.m_cf, names))                                                                  \
    {                                                                                                                  \
        return math::t_##property(t.m_cf, names);                                                                      \
    }                                                                                                                  \
    template <typename Term, enable_if_t<cf_trig_score<typename Term::cf_type>::value == 0u                            \
                                             && key_trig_score<typename Term::key_type>::value == 4u,                  \
                                         int> = 0>                                                                     \
    static auto get_t_##property(const Term &t, const symbol_idx_fset &idx, const symbol_fset &names)                  \
        ->decltype(t.m_key.t_##property(idx, names))                                                                   \
    {                                                                                                                  \
        return t.m_key.t_##property(idx, names);                                                                       \
    }                                                                                                                  \
    template <typename T>                                                                                              \
    using pt_##property##_type_                                                                                        \
        = decltype(get_t_##property(std::declval<const typename T::term_type &>(),                                     \
                                    std::declval<const symbol_idx_fset &>(), std::declval<const symbol_fset &>()));    \
    template <typename T>                                                                                              \
    using pt_##property##_type                                                                                         \
        = enable_if_t<common_type_checks<pt_##property##_type_<T>>::value, pt_##property##_type_<T>>;
    PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER(degree)
    PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER(ldegree)
    PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER(order)
    PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER(lorder)
#undef PIRANHA_DEFINE_PARTIAL_TRIG_PROPERTY_GETTER
public:
    /// Defaulted default constructor.
    trigonometric_series() = default;
    /// Defaulted copy constructor.
    trigonometric_series(const trigonometric_series &) = default;
    /// Defaulted move constructor.
    trigonometric_series(trigonometric_series &&) = default;
    PIRANHA_FORWARDING_CTOR(trigonometric_series, base)
    /// Copy assignment operator.
    /**
     * @param other the assignment argument.
     *
     * @return a reference to \p this.
     *
     * @throws unspecified any exception thrown by the assignment operator of the base class.
     */
    trigonometric_series &operator=(const trigonometric_series &other) = default;
    /// Move assignment operator.
    /**
     * @param other the assignment argument.
     *
     * @return a reference to \p this.
     */
    trigonometric_series &operator=(trigonometric_series &&other) = default;
    /// Trivial destructor.
    ~trigonometric_series()
    {
        PIRANHA_TT_CHECK(is_series, trigonometric_series);
    }
    PIRANHA_FORWARDING_ASSIGNMENT(trigonometric_series, base)
    /// Trigonometric degree.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @return the total trigonometric degree of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the degree of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    t_degree_type<T> t_degree() const
    {
        using term_type = typename T::term_type;
        auto it = std::max_element(
            this->m_container.begin(), this->m_container.end(), [this](const term_type &t1, const term_type &t2) {
                return this->get_t_degree(t1, this->m_symbol_set) < this->get_t_degree(t2, this->m_symbol_set);
            });
        return (it == this->m_container.end()) ? t_degree_type<T>(0) : get_t_degree(*it, this->m_symbol_set);
    }
    /// Partial trigonometric degree.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @param names the names of the variables to be considered in the computation.
     *
     * @return the partial trigonometric degree of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the degree of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    pt_degree_type<T> t_degree(const symbol_fset &names) const
    {
        using term_type = typename T::term_type;
        const auto idx = ss_intersect_idx(this->m_symbol_set, names);
        auto it = std::max_element(this->m_container.begin(), this->m_container.end(),
                                   [this, &idx](const term_type &t1, const term_type &t2) {
                                       return this->get_t_degree(t1, idx, this->m_symbol_set)
                                              < this->get_t_degree(t2, idx, this->m_symbol_set);
                                   });
        return (it == this->m_container.end()) ? pt_degree_type<T>(0) : get_t_degree(*it, idx, this->m_symbol_set);
    }
    /// Trigonometric low degree.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @return the total trigonometric low degree of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the degree of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    t_ldegree_type<T> t_ldegree() const
    {
        using term_type = typename T::term_type;
        auto it = std::min_element(
            this->m_container.begin(), this->m_container.end(), [this](const term_type &t1, const term_type &t2) {
                return this->get_t_ldegree(t1, this->m_symbol_set) < this->get_t_ldegree(t2, this->m_symbol_set);
            });
        return (it == this->m_container.end()) ? t_ldegree_type<T>(0) : get_t_ldegree(*it, this->m_symbol_set);
    }
    /// Partial trigonometric low degree.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @param names the names of the variables to be considered in the computation.
     *
     * @return the partial trigonometric low degree of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the degree of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    pt_ldegree_type<T> t_ldegree(const symbol_fset &names) const
    {
        using term_type = typename T::term_type;
        const auto idx = ss_intersect_idx(this->m_symbol_set, names);
        auto it = std::min_element(this->m_container.begin(), this->m_container.end(),
                                   [this, &idx](const term_type &t1, const term_type &t2) {
                                       return this->get_t_ldegree(t1, idx, this->m_symbol_set)
                                              < this->get_t_ldegree(t2, idx, this->m_symbol_set);
                                   });
        return (it == this->m_container.end()) ? pt_ldegree_type<T>(0) : get_t_ldegree(*it, idx, this->m_symbol_set);
    }
    /// Trigonometric order.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @return the total trigonometric order of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the order of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    t_order_type<T> t_order() const
    {
        using term_type = typename T::term_type;
        auto it = std::max_element(
            this->m_container.begin(), this->m_container.end(), [this](const term_type &t1, const term_type &t2) {
                return this->get_t_order(t1, this->m_symbol_set) < this->get_t_order(t2, this->m_symbol_set);
            });
        return (it == this->m_container.end()) ? t_order_type<T>(0) : get_t_order(*it, this->m_symbol_set);
    }
    /// Partial trigonometric order.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @param names the names of the variables to be considered in the computation.
     *
     * @return the partial trigonometric order of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the order of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    pt_order_type<T> t_order(const symbol_fset &names) const
    {
        using term_type = typename T::term_type;
        const auto idx = ss_intersect_idx(this->m_symbol_set, names);
        auto it = std::max_element(
            this->m_container.begin(), this->m_container.end(), [this, &idx](const term_type &t1, const term_type &t2) {
                return this->get_t_order(t1, idx, this->m_symbol_set) < this->get_t_order(t2, idx, this->m_symbol_set);
            });
        return (it == this->m_container.end()) ? pt_order_type<T>(0) : get_t_order(*it, idx, this->m_symbol_set);
    }
    /// Trigonometric low order.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @return the total trigonometric low order of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the order of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    t_lorder_type<T> t_lorder() const
    {
        using term_type = typename T::term_type;
        auto it = std::min_element(
            this->m_container.begin(), this->m_container.end(), [this](const term_type &t1, const term_type &t2) {
                return this->get_t_lorder(t1, this->m_symbol_set) < this->get_t_lorder(t2, this->m_symbol_set);
            });
        return (it == this->m_container.end()) ? t_lorder_type<T>(0) : get_t_lorder(*it, this->m_symbol_set);
    }
    /// Partial trigonometric low order.
    /**
     * \note
     * This method is enabled only if the requirements outlined in piranha::trigonometric_series are satisfied.
     *
     * @param names the names of the variables to be considered in the computation.
     *
     * @return the partial trigonometric low order of the series.
     *
     * @throws unspecified any exception resulting from the computation and comparison of the order of the individual
     * terms.
     */
    template <typename T = trigonometric_series>
    pt_lorder_type<T> t_lorder(const symbol_fset &names) const
    {
        using term_type = typename T::term_type;
        const auto idx = ss_intersect_idx(this->m_symbol_set, names);
        auto it = std::min_element(this->m_container.begin(), this->m_container.end(),
                                   [this, &idx](const term_type &t1, const term_type &t2) {
                                       return this->get_t_lorder(t1, idx, this->m_symbol_set)
                                              < this->get_t_lorder(t2, idx, this->m_symbol_set);
                                   });
        return (it == this->m_container.end()) ? pt_lorder_type<T>(0) : get_t_lorder(*it, idx, this->m_symbol_set);
    }
};

namespace math
{

/// Specialisation of the piranha::math::t_degree() functor for instances of piranha::trigonometric_series.
/**
 * This specialisation is activated if \p Series is an instance of piranha::trigonometric_series.
 * The corrsponding method in piranha::trigonometric_series will be used for the computation, if available.
 */
template <typename Series>
struct t_degree_impl<Series,
                     typename std::enable_if<std::is_base_of<detail::trigonometric_series_tag, Series>::value>::type> {
    /// Trigonometric degree operator.
    /**
     * \note
     * This operator is enabled only if the invoked method in piranha::trigonometric_series is enabled.
     *
     * @param ts input series.
     * @param args variadic argument pack.
     *
     * @return trigonometric degree of \p ts.
     *
     * @throws unspecified any exception thrown by piranha::trigonometric_series::t_degree().
     */
    template <typename... Args>
    auto operator()(const Series &ts, const Args &... args) const -> decltype(ts.t_degree(args...))
    {
        return ts.t_degree(args...);
    }
};

/// Specialisation of the piranha::math::t_ldegree() functor for instances of piranha::trigonometric_series.
/**
 * This specialisation is activated if \p Series is an instance of piranha::trigonometric_series.
 * The corrsponding method in piranha::trigonometric_series will be used for the computation, if available.
 */
template <typename Series>
struct t_ldegree_impl<Series,
                      typename std::enable_if<std::is_base_of<detail::trigonometric_series_tag, Series>::value>::type> {
    /// Trigonometric low degree operator.
    /**
     * \note
     * This operator is enabled only if the invoked method in piranha::trigonometric_series is enabled.
     *
     * @param ts input series.
     * @param args variadic argument pack.
     *
     * @return trigonometric low degree of \p ts.
     *
     * @throws unspecified any exception thrown by piranha::trigonometric_series::t_ldegree().
     */
    template <typename... Args>
    auto operator()(const Series &ts, const Args &... args) const -> decltype(ts.t_ldegree(args...))
    {
        return ts.t_ldegree(args...);
    }
};

/// Specialisation of the piranha::math::t_order() functor for instances of piranha::trigonometric_series.
/**
 * This specialisation is activated if \p Series is an instance of piranha::trigonometric_series.
 * The corrsponding method in piranha::trigonometric_series will be used for the computation, if available.
 */
template <typename Series>
struct t_order_impl<Series,
                    typename std::enable_if<std::is_base_of<detail::trigonometric_series_tag, Series>::value>::type> {
    /// Trigonometric order operator.
    /**
     * \note
     * This operator is enabled only if the invoked method in piranha::trigonometric_series is enabled.
     *
     * @param ts input series.
     * @param args variadic argument pack.
     *
     * @return trigonometric order of \p ts.
     *
     * @throws unspecified any exception thrown by piranha::trigonometric_series::t_order().
     */
    template <typename... Args>
    auto operator()(const Series &ts, const Args &... args) const -> decltype(ts.t_order(args...))
    {
        return ts.t_order(args...);
    }
};

/// Specialisation of the piranha::math::t_lorder() functor for instances of piranha::trigonometric_series.
/**
 * This specialisation is activated if \p Series is an instance of piranha::trigonometric_series.
 * The corrsponding method in piranha::trigonometric_series will be used for the computation, if available.
 */
template <typename Series>
struct t_lorder_impl<Series,
                     typename std::enable_if<std::is_base_of<detail::trigonometric_series_tag, Series>::value>::type> {
    /// Trigonometric low order operator.
    /**
     * \note
     * This operator is enabled only if the invoked method in piranha::trigonometric_series is enabled.
     *
     * @param ts input series.
     * @param args variadic argument pack.
     *
     * @return trigonometric low order of \p ts.
     *
     * @throws unspecified any exception thrown by piranha::trigonometric_series::t_lorder().
     */
    template <typename... Args>
    auto operator()(const Series &ts, const Args &... args) const -> decltype(ts.t_lorder(args...))
    {
        return ts.t_lorder(args...);
    }
};
}
}

#endif
