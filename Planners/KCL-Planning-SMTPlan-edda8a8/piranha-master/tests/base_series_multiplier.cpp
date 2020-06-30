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

#include <piranha/base_series_multiplier.hpp>

#define BOOST_TEST_MODULE base_series_multiplier_test
#include <boost/test/included/unit_test.hpp>

#include <algorithm>
#include <array>
#include <cstddef>
#include <iterator>
#include <limits>
#include <set>
#include <stdexcept>
#include <tuple>
#include <type_traits>
#include <unordered_set>
#include <utility>
#include <vector>

#include <piranha/integer.hpp>
#include <piranha/kronecker_monomial.hpp>
#include <piranha/monomial.hpp>
#include <piranha/polynomial.hpp>
#include <piranha/rational.hpp>
#include <piranha/settings.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/tuning.hpp>
#include <piranha/type_traits.hpp>

using namespace piranha;

template <typename Cf>
using p_type = polynomial<Cf, monomial<int>>;

template <typename Series>
struct m_checker : public base_series_multiplier<Series> {
    using base = base_series_multiplier<Series>;
    using size_type = typename base::size_type;
    explicit m_checker(const Series &s1, const Series &s2) : base(s1, s2)
    {
        BOOST_CHECK(!std::is_constructible<base>::value);
        BOOST_CHECK(!std::is_copy_constructible<base>::value);
        BOOST_CHECK(!std::is_move_constructible<base>::value);
        BOOST_CHECK(!std::is_copy_assignable<base>::value);
        BOOST_CHECK(!std::is_move_assignable<base>::value);
        term_pointers_checker(s1, s2);
        null_absorber_checker(s1, s2);
    }
    template <typename T, enable_if_t<zero_is_absorbing<typename T::term_type::cf_type>::value, int> = 0>
    void null_absorber_checker(const T &s1_, const T &s2_) const
    {
        const T &s1 = s1_.size() < s2_.size() ? s2_ : s1_;
        const T &s2 = s1_.size() < s2_.size() ? s1_ : s2_;
        BOOST_CHECK(s1.size() == this->m_v1.size());
        BOOST_CHECK(s2.size() == this->m_v2.size());
    }
    template <typename T, enable_if_t<!zero_is_absorbing<typename T::term_type::cf_type>::value, int> = 0>
    void null_absorber_checker(const T &s1_, const T &s2_) const
    {
        const T &s1 = s1_.size() < s2_.size() ? s2_ : s1_;
        const T &s2 = s1_.size() < s2_.size() ? s1_ : s2_;
        BOOST_CHECK((s1.size() && s1.size() == this->m_v1.size())
                    || (!s1.size() && this->m_v1.size() == 1u && this->m_v1[0]->m_cf == 0));
        BOOST_CHECK((s2.size() && s2.size() == this->m_v2.size())
                    || (!s2.size() && this->m_v2.size() == 1u && this->m_v2[0]->m_cf == 0));
    }
    template <typename T, enable_if_t<std::is_same<typename T::term_type::cf_type, double>::value, int> = 0>
    void term_pointers_checker(const T &, const T &) const
    {
        // Don't do anything for double, as we use it only to check the null absorber.
    }
    template <typename T,
              typename std::enable_if<std::is_same<typename T::term_type::cf_type, integer>::value, int>::type = 0>
    void term_pointers_checker(const T &s1_, const T &s2_) const
    {
        // Swap the operands if needed.
        const T &s1 = s1_.size() < s2_.size() ? s2_ : s1_;
        const T &s2 = s1_.size() < s2_.size() ? s1_ : s2_;
        BOOST_CHECK(s1.size() == this->m_v1.size());
        BOOST_CHECK(s2.size() == this->m_v2.size());
        // Create hash sets with the term pointers from the vectors.
        std::unordered_set<const typename T::term_type *> h1, h2;
        std::copy(this->m_v1.begin(), this->m_v1.end(), std::inserter(h1, h1.begin()));
        std::copy(this->m_v2.begin(), this->m_v2.end(), std::inserter(h2, h2.begin()));
        for (const auto &t : s1._container()) {
            BOOST_CHECK(h1.find(&t) != h1.end());
        }
        for (const auto &t : s2._container()) {
            BOOST_CHECK(h2.find(&t) != h2.end());
        }
    }
    template <typename T,
              typename std::enable_if<std::is_same<typename T::term_type::cf_type, rational>::value, int>::type = 0>
    void term_pointers_checker(const T &s1_, const T &s2_) const
    {
        const T &s1 = s1_.size() < s2_.size() ? s2_ : s1_;
        const T &s2 = s1_.size() < s2_.size() ? s1_ : s2_;
        BOOST_CHECK(s1.size() == this->m_v1.size());
        BOOST_CHECK(s2.size() == this->m_v2.size());
        std::unordered_set<const typename T::term_type *> h1, h2;
        std::transform(s1._container().begin(), s1._container().end(), std::inserter(h1, h1.begin()),
                       [](const typename T::term_type &t) { return &t; });
        std::transform(s2._container().begin(), s2._container().end(), std::inserter(h2, h2.begin()),
                       [](const typename T::term_type &t) { return &t; });
        for (size_type i = 0u; i != s1.size(); ++i) {
            BOOST_CHECK(h1.find(this->m_v1[i]) == h1.end());
            BOOST_CHECK(this->m_v1[i]->m_cf.get_den() == 1);
            auto it = s1._container().find(*this->m_v1[i]);
            BOOST_CHECK(it != s1._container().end());
            BOOST_CHECK(this->m_v1[i]->m_cf.get_num() % it->m_cf.get_num() == 0);
        }
        for (size_type i = 0u; i != s2.size(); ++i) {
            BOOST_CHECK(h2.find(this->m_v2[i]) == h2.end());
            BOOST_CHECK(this->m_v2[i]->m_cf.get_den() == 1);
            auto it = s2._container().find(*this->m_v2[i]);
            BOOST_CHECK(it != s2._container().end());
            BOOST_CHECK(this->m_v2[i]->m_cf.get_num() % it->m_cf.get_num() == 0);
        }
    }
    // Perfect forwarding of protected members, to make them accessible.
    template <typename... Args>
    void blocked_multiplication(Args &&... args) const
    {
        base::blocked_multiplication(std::forward<Args>(args)...);
    }
    template <std::size_t N, typename MultFunctor, typename... Args>
    typename Series::size_type estimate_final_series_size(Args &&... args) const
    {
        return base::template estimate_final_series_size<N, MultFunctor>(std::forward<Args>(args)...);
    }
    template <typename... Args>
    static void sanitise_series(Args &&... args)
    {
        return base::sanitise_series(std::forward<Args>(args)...);
    }
    template <typename... Args>
    Series plain_multiplication(Args &&... args) const
    {
        return base::plain_multiplication(std::forward<Args>(args)...);
    }
    template <typename... Args>
    void finalise_series(Args &&... args) const
    {
        base::finalise_series(std::forward<Args>(args)...);
    }
    unsigned get_n_threads() const
    {
        return this->m_n_threads;
    }
};

BOOST_AUTO_TEST_CASE(base_series_multiplier_constructor_test)
{
    {
        // Check with empty series.
        using pt = p_type<rational>;
        pt e1, e2;
        BOOST_CHECK_NO_THROW((m_checker<pt>{e1, e2}));
        m_checker<pt> mc{e1, e2};
        BOOST_CHECK(mc.get_n_threads() != 0u);
    }
    {
        using pt = p_type<rational>;
        pt x{"x"}, y{"y"}, z{"z"};
        auto s1 = (x / 2 + y / 5).pow(5), s2 = (x / 3 + y / 22).pow(6);
        m_checker<pt> m0(s1, s2);
        std::swap(s1, s2);
        m_checker<pt> m1(s1, s2);
        s1 = 0;
        s2 = 0;
        m_checker<pt> m2(s1, s2);
        BOOST_CHECK_THROW(m_checker<pt>(x, z), std::invalid_argument);
    }
    {
        using pt = p_type<integer>;
        pt x{"x"}, y{"y"}, z{"z"};
        auto s1 = (x + y * 2).pow(5), s2 = (-x + y).pow(6);
        m_checker<pt> m0(s1, s2);
        std::swap(s1, s2);
        m_checker<pt> m1(s1, s2);
        s1 = 0;
        s2 = 0;
        m_checker<pt> m2(s1, s2);
        BOOST_CHECK_THROW(m_checker<pt>(x, z), std::invalid_argument);
    }
    {
        // Do a test with floating-point and null series.
        using pt = p_type<double>;
        pt x{"x"}, null{x - x};
        m_checker<pt> m1((x + 1).pow(5), null);
        m_checker<pt> m2(null, (x - 3).pow(5));
        m_checker<pt> m3(null, null);
    }
}

// The sorting function for the definition of the set in the mult functor below.
struct p_sorter {
    bool operator()(const std::pair<unsigned, unsigned> &p1, const std::pair<unsigned, unsigned> &p2) const
    {
        if (p1.first < p2.first) {
            return true;
        }
        if (p1.first == p2.first) {
            return p1.second < p2.second;
        }
        return false;
    }
};

struct m_functor_0 {
    m_functor_0() = default;
    template <typename Series>
    explicit m_functor_0(const base_series_multiplier<Series> &, Series &)
    {
    }
    template <typename T>
    void operator()(const T &i, const T &j) const
    {
        m_set.emplace(unsigned(i), unsigned(j));
    }
    mutable std::set<std::pair<unsigned, unsigned>, p_sorter> m_set;
};

// A limit functor that will always return the construction parameter.
struct l_functor_0 {
    l_functor_0(unsigned n) : m_n(n) {}
    template <typename T>
    T operator()(const T &) const
    {
        return T(m_n);
    }
    const unsigned m_n;
};

BOOST_AUTO_TEST_CASE(base_series_multiplier_blocked_multiplication_test)
{
    using pt = p_type<rational>;
    pt x{"x"}, y{"y"};
    auto s1 = (x + y).pow(100);
    // Take out one term in order to make it exactly 100 terms.
    s1 -= 1345860629046814650_z * x.pow(16) * y.pow(84);
    m_checker<pt> m0(s1, s1);
    tuning::set_multiplication_block_size(16u);
    m_functor_0 mf0;
    m0.blocked_multiplication(mf0, 0u, 100u);
    BOOST_CHECK(mf0.m_set.size() == 100u * 100u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 100u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Try with commensurable block size.
    tuning::set_multiplication_block_size(25u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u);
    BOOST_CHECK(mf0.m_set.size() == 100u * 100u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 100u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Block size same as series size.
    tuning::set_multiplication_block_size(100u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u);
    BOOST_CHECK(mf0.m_set.size() == 100u * 100u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 100u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Larger size than series size.
    tuning::set_multiplication_block_size(200u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u);
    BOOST_CHECK(mf0.m_set.size() == 100u * 100u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 100u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Only parts of the series.
    tuning::set_multiplication_block_size(23u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 20u, 87u);
    BOOST_CHECK(mf0.m_set.size() == (87u - 20u) * 100u);
    for (unsigned i = 20u; i < 87u; ++i) {
        for (unsigned j = 0u; j < 100u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Now with limits.
    tuning::set_multiplication_block_size(16u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u, l_functor_0{1u});
    BOOST_CHECK(mf0.m_set.size() == 100u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 1u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // No mults done.
    tuning::set_multiplication_block_size(16u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u, l_functor_0{0u});
    BOOST_CHECK(mf0.m_set.size() == 0u);
    // Try with commensurable block size.
    tuning::set_multiplication_block_size(25u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u, l_functor_0{2u});
    BOOST_CHECK(mf0.m_set.size() == 100u * 2u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 2u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Block size same as series size.
    tuning::set_multiplication_block_size(100u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u, l_functor_0{2u});
    BOOST_CHECK(mf0.m_set.size() == 100u * 2u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 2u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Larger size than series size.
    tuning::set_multiplication_block_size(200u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 0u, 100u, l_functor_0{2u});
    BOOST_CHECK(mf0.m_set.size() == 100u * 2u);
    for (unsigned i = 0u; i < 100u; ++i) {
        for (unsigned j = 0u; j < 2u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Only parts of the series.
    tuning::set_multiplication_block_size(23u);
    mf0.m_set.clear();
    m0.blocked_multiplication(mf0, 20u, 87u, l_functor_0{2u});
    BOOST_CHECK(mf0.m_set.size() == (87u - 20u) * 2u);
    for (unsigned i = 20u; i < 87u; ++i) {
        for (unsigned j = 1u; j < 2u; ++j) {
            BOOST_CHECK(mf0.m_set.count(std::make_pair(i, j)) == 1u);
        }
    }
    // Test error throwing.
    BOOST_CHECK_THROW(m0.blocked_multiplication(mf0, 3u, 2u), std::invalid_argument);
    BOOST_CHECK_THROW(m0.blocked_multiplication(mf0, 101u, 102u), std::invalid_argument);
    BOOST_CHECK_THROW(m0.blocked_multiplication(mf0, 1u, 102u), std::invalid_argument);
    // Try also with empty series, just to make sure.
    pt e1, e2;
    m_checker<pt> m1(e1, e2);
    m_functor_0 mf1;
    BOOST_CHECK_NO_THROW(m1.blocked_multiplication(mf1, 0u, 0u));
    // Final reset of the mult block size.
    tuning::reset_multiplication_block_size();
}

BOOST_AUTO_TEST_CASE(base_series_multiplier_estimate_final_series_size_test)
{
    settings::set_min_work_per_thread(1u);
    for (auto nt = 1u; nt < 4u; ++nt) {
        using pt = p_type<integer>;
        settings::set_n_threads(nt);
        // Start with empty series.
        pt e1, e2;
        {
            m_checker<pt> m0(e1, e2);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<1u, m_functor_0>()), 1u);
        }
        {
            // Check with series with only one term.
            e1 = 1;
            e2 = 2;
            m_checker<pt> m0(e1, e2);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<1u, m_functor_0>()), 1u);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<2u, m_functor_0>()), 2u);
        }
        {
            // 1 by n terms.
            e1 = 1 + pt{"x"} - pt{"x"};
            e2 = 2;
            e2 += pt{"x"};
            m_checker<pt> m0(e1, e2);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<1u, m_functor_0>()), 2u);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<2u, m_functor_0>()), 4u);
        }
        {
            // Check with total truncation.
            e1 += pt{"x"};
            m_checker<pt> m0(e1, e2);
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<1u, m_functor_0>(l_functor_0{0u})), 1u);
        }
        // Just a couple of simple tests using polynomials, we can't really know what to expect as the method
        // works in a statistical fashion.
        {
            pt x{"x"}, y{"y"};
            auto a = (x + 2 * y + 4), b = (x * x - 2 * y * x - 3 - 4 * y);
            m_checker<pt> m0(a, b);
            // Here the multiplier functor does nothing, the estimation will exit immediately yielding 1.
            BOOST_CHECK_EQUAL((m0.estimate_final_series_size<1u, m_functor_0>()), 1u);
        }
        // A reduced fateman1 benchmark, just to test a bit more.
        {
            pt x("x"), y("y"), z("z"), t("t");
            auto f = x + y + z + t + 1;
            auto tmp2(f);
            for (auto i = 1; i < 10; ++i) {
                f *= tmp2;
            }
            auto b = f + 1;
            auto retval = f * b;
            std::cout << "Bucket count vs actual size: " << retval.table_bucket_count() << ',' << retval.size() << '\n';
        }
    }
    settings::reset_min_work_per_thread();
    settings::reset_n_threads();
}

BOOST_AUTO_TEST_CASE(base_series_multiplier_sanitise_series_test)
{
    using pt = p_type<integer>;
    using mt = m_checker<pt>;
    using term_type = typename pt::term_type;
    std::array<unsigned, 4u> nt = {{1u, 2u, 3u, 4u}};
    // Make sure the thread pool has 4 slots for testing
    // below with various numbers of threads.
    settings::set_n_threads(4u);
    for (const auto &n : nt) {
        // First test with an empty series.
        pt e;
        term_type tmp;
        BOOST_CHECK_THROW(mt::sanitise_series(e, 0u), std::invalid_argument);
        BOOST_CHECK_NO_THROW(mt::sanitise_series(e, n));
        // Insert a term without updating the count.
        tmp = term_type{1_z, term_type::key_type{}};
        e._container().rehash(1u);
        e._container()._unique_insert(tmp, 0u);
        mt::sanitise_series(e, n);
        BOOST_CHECK_EQUAL(e.size(), 1u);
        // Try with a term with zero coefficient.
        e._container().clear();
        e._container().rehash(1u);
        tmp = term_type{0_z, term_type::key_type{}};
        e._container()._unique_insert(tmp, 0u);
        mt::sanitise_series(e, n);
        BOOST_CHECK_EQUAL(e.size(), 0u);
        // Try with an incompatible term.
        e._container().clear();
        e._container().rehash(1u);
        // NOTE: this is also ignorable, but the compatibility check is done first.
        tmp = term_type{0_z, term_type::key_type{1}};
        e._container()._unique_insert(tmp, 0u);
        BOOST_CHECK_THROW(mt::sanitise_series(e, n), std::invalid_argument);
        e._container().clear();
        // Wrong size.
        e._container().rehash(1u);
        e._container()._update_size(3u);
        tmp = term_type{2_z, term_type::key_type{}};
        e._container()._unique_insert(tmp, 0u);
        mt::sanitise_series(e, n);
        BOOST_CHECK_EQUAL(e.size(), 1u);
        // A test with multiple buckets.
        e = pt{"x"} - pt{"x"}; // Just make sure we set the symbol set correctly.
        e._container().clear();
        e._container().rehash(16u);
        e._container()._update_size(3u);
        for (unsigned i = 0u; i < 10u; ++i) {
            tmp = term_type{i, term_type::key_type{int(i)}};
            e._container()._unique_insert(tmp, e._container()._bucket(tmp));
        }
        mt::sanitise_series(e, n);
        BOOST_CHECK_EQUAL(e.size(), 9u);
        // Also with incompatible term.
        e._container().clear();
        e._container().rehash(16u);
        e._container()._update_size(3u);
        for (unsigned i = 0u; i < 10u; ++i) {
            tmp = term_type{i, term_type::key_type{int(i), int(i)}};
            e._container()._unique_insert(tmp, e._container()._bucket(tmp));
        }
        BOOST_CHECK_THROW(mt::sanitise_series(e, n), std::invalid_argument);
        e._container().clear();
    }
    // Reset to the default setup.
    settings::reset_n_threads();
}

struct multiplication_tester {
    template <typename T>
    void operator()(const T &)
    {
        // NOTE: this test is going to be exact in case of coefficients cancellations with double
        // precision coefficients only if the platform has ieee 754 format (integer exactly representable
        // as doubles up to 2 ** 53).
        if (std::is_same<typename T::term_type::cf_type, double>::value
            && (!std::numeric_limits<double>::is_iec559 || std::numeric_limits<double>::digits < 53)) {
            return;
        }
        T x("x"), y("y"), z("z"), t("t"), u("u");
        // Dense case, default setup.
        auto f = 1 + x + y + z + t;
        auto tmp(f);
        for (int i = 1; i < 10; ++i) {
            f *= tmp;
        }
        auto g = f + 1;
        auto retval = f * g;
        BOOST_CHECK_EQUAL(retval.size(), 10626u);
        // Test swapping.
        BOOST_CHECK(x * (1 + x) == (1 + x) * x);
        BOOST_CHECK(T(1) * retval == retval);
        // Dense case, force number of threads.
        for (auto i = 1u; i <= 4u; ++i) {
            settings::set_n_threads(i);
            auto tmp2 = f * g;
            BOOST_CHECK_EQUAL(tmp2.size(), 10626u);
            BOOST_CHECK(tmp2 == retval);
        }
        // Dense case, same input series.
        settings::set_n_threads(4u);
        {
            auto tmp2 = f * f;
            BOOST_CHECK_EQUAL(tmp2.size(), 10626u);
        }
        settings::reset_n_threads();
        // Dense case with cancellations, default setup.
        auto h = 1 - x + y + z + t;
        tmp = h;
        for (int i = 1; i < 10; ++i) {
            h *= tmp;
        }
        retval = f * h;
        BOOST_CHECK_EQUAL(retval.size(), 5786u);
        // Dense case with cancellations, force number of threads.
        for (auto i = 1u; i <= 4u; ++i) {
            settings::set_n_threads(i);
            auto tmp2 = f * h;
            BOOST_CHECK_EQUAL(tmp2.size(), 5786u);
            BOOST_CHECK(retval == tmp2);
        }
        settings::reset_n_threads();
        // Sparse case, default.
        f = (x + y + z * z * 2 + t * t * t * 3 + u * u * u * u * u * 5 + 1);
        auto tmp_f(f);
        g = (u + t + z * z * 2 + y * y * y * 3 + x * x * x * x * x * 5 + 1);
        auto tmp_g(g);
        h = (-u + t + z * z * 2 + y * y * y * 3 + x * x * x * x * x * 5 + 1);
        auto tmp_h(h);
        for (int i = 1; i < 8; ++i) {
            f *= tmp_f;
            g *= tmp_g;
            h *= tmp_h;
        }
        retval = f * g;
        BOOST_CHECK_EQUAL(retval.size(), 591235u);
        // Sparse case, force n threads.
        for (auto i = 1u; i <= 4u; ++i) {
            settings::set_n_threads(i);
            auto tmp2 = f * g;
            BOOST_CHECK_EQUAL(tmp2.size(), 591235u);
            BOOST_CHECK(retval == tmp2);
        }
        settings::reset_n_threads();
        // Sparse case with cancellations, default.
        retval = f * h;
        BOOST_CHECK_EQUAL(retval.size(), 591184u);
        // Sparse case with cancellations, force number of threads.
        for (auto i = 1u; i <= 4u; ++i) {
            settings::set_n_threads(i);
            auto tmp2 = f * h;
            BOOST_CHECK_EQUAL(tmp2.size(), 591184u);
            BOOST_CHECK(tmp2 == retval);
        }
        settings::reset_n_threads();
    }
};

BOOST_AUTO_TEST_CASE(base_series_multiplier_plain_multiplication_test)
{
    {
        // Simple test with empty series.
        using pt = p_type<integer>;
        using mt = m_checker<pt>;
        pt e1, e2;
        mt m0{e1, e2};
        BOOST_CHECK_EQUAL(m0.plain_multiplication(), 0);
        BOOST_CHECK(m0.get_n_threads() != 0u);
        // Testing ported over from the previous series_multiplier tests. Just use polynomial directly.
        using pt1 = p_type<double>;
        using pt2 = p_type<integer>;
        pt1 p1{"x"}, p2{"x"};
        // Check that the merged symbol set is returned when one of the series is empty.
        BOOST_CHECK(e1 * p1 == 0);
        BOOST_CHECK((e1 * p1).get_symbol_set() == symbol_fset{"x"});
        BOOST_CHECK((p1 * e1).get_symbol_set() == symbol_fset{"x"});
        p1._container().begin()->m_cf *= 2;
        p2._container().begin()->m_cf *= 3;
        auto retval = p1 * p2;
        BOOST_CHECK(retval.size() == 1u);
        BOOST_CHECK(retval._container().begin()->m_key.size() == 1u);
        BOOST_CHECK(retval._container().begin()->m_key[0] == 2);
        BOOST_CHECK(retval._container().begin()->m_cf == (double(3) * double(1)) * (double(2) * double(1)));
        pt2 p3{"x"};
        p3._container().begin()->m_cf *= 4;
        pt2 p4{"x"};
        p4._container().begin()->m_cf *= 2;
        retval = p4 * p3;
        BOOST_CHECK(retval.size() == 1u);
        BOOST_CHECK(retval._container().begin()->m_key.size() == 1u);
        BOOST_CHECK(retval._container().begin()->m_key[0] == 2);
        BOOST_CHECK(retval._container().begin()->m_cf == double((double(2) * double(1)) * (integer(1) * 4)));
        using p_types = boost::mpl::vector<pt1, pt2, p_type<rational>>;
        boost::mpl::for_each<p_types>(multiplication_tester());
    }
    {
        // Some testing with double for the zero absorber modifications.
        using pt = p_type<double>;
        pt x{"x"}, y{"y"};
        BOOST_CHECK_EQUAL((x + y + 1).pow(5) * pt(0), 0);
        BOOST_CHECK_EQUAL(pt(0) * (x + y + 1).pow(5), 0);
        BOOST_CHECK_EQUAL(pt(0) * pt(0), 0);
        BOOST_CHECK_EQUAL((x - x + y - y) * (x - x + y - y), 0);
    }
}

BOOST_AUTO_TEST_CASE(base_series_multiplier_finalise_test)
{
    {
        // Test proper handling of rational coefficients.
        using pt = p_type<rational>;
        pt x{"x"}, y{"y"};
        BOOST_CHECK_EQUAL(x * 4 / 3_q * y * 5 / 2_q, 10 / 3_q * x * y);
        BOOST_CHECK_EQUAL((x * 4 / 3_q + y * 5 / 2_q) * (x.pow(2) * 4 / 13_q - y * 5 / 17_q),
                          16 * x.pow(3) / 39 + 10 / 13_q * y * x * x - 20 * x * y / 51 - 25 * y * y / 34);
        // No finalisation happening with integral coefficients.
        using pt2 = p_type<integer>;
        pt2 x2{"x"}, y2{"y"};
        BOOST_CHECK_EQUAL(x2 * y2, y2 * x2);
    }
    {
        // Check with multiple threads.
        settings::set_min_work_per_thread(1u);
        using pt = p_type<rational>;
        using mt = m_checker<pt>;
        for (unsigned nt = 1u; nt <= 4u; ++nt) {
            settings::set_n_threads(nt);
            // Setup a multiplier for a polyomial with two variables and lcm 6.
            auto tmp1 = pt{"x"} / 3 + pt{"y"}, tmp2 = pt{"y"} / 2 + pt{"x"};
            mt m0{tmp1, tmp2};
            // First let's try with an empty retval.
            pt r;
            r.set_symbol_set(symbol_fset{"x", "y"});
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, 0);
            // Put in one term.
            r += pt{"x"};
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, pt{"x"} / 36);
            // Put in another term.
            r += 12 * pt{"y"};
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, pt{"x"} / 36 + pt{"y"} / 3);
        }
    }
    {
        // Same as above, but with k monomial.
        using pt = polynomial<rational, k_monomial>;
        using mt = m_checker<pt>;
        for (unsigned nt = 1u; nt <= 4u; ++nt) {
            settings::set_n_threads(nt);
            // Setup a multiplier for a polyomial with two variables and lcm 6.
            auto tmp1 = pt{"x"} / 3 + pt{"y"}, tmp2 = pt{"y"} / 2 + pt{"x"};
            mt m0{tmp1, tmp2};
            // First let's try with an empty retval.
            pt r;
            r.set_symbol_set(symbol_fset{"x", "y"});
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, 0);
            // Put in one term.
            r += pt{"x"};
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, pt{"x"} / 36);
            // Put in another term.
            r += 12 * pt{"y"};
            BOOST_CHECK_NO_THROW(m0.finalise_series(r));
            BOOST_CHECK_EQUAL(r, pt{"x"} / 36 + pt{"y"} / 3);
        }
    }
    // Reset.
    settings::reset_n_threads();
    settings::reset_min_work_per_thread();
}
