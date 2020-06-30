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

#include <piranha/memory.hpp>

#define BOOST_TEST_MODULE memory_test
#include <boost/test/included/unit_test.hpp>

#include <algorithm>
#include <atomic>
#include <cstddef>
#include <cstdlib>
#include <iterator>
#include <limits>
#include <new>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

#include <piranha/config.hpp>
#include <piranha/settings.hpp>

// Helper to def-init as an array of T the raw storage returned by the memory alloc function.
template <typename T>
static inline void default_init(T *begin, T *end)
{
    for (; begin != end; ++begin) {
        ::new (static_cast<void *>(begin)) T;
    }
}

using namespace piranha;

BOOST_AUTO_TEST_CASE(memory_aligned_malloc_test)
{
    // Test the common codepath.
    auto ptr = aligned_palloc(0, 0);
    BOOST_CHECK(ptr == nullptr);
    BOOST_CHECK_NO_THROW(aligned_pfree(0, ptr));
    ptr = aligned_palloc(123, 0);
    BOOST_CHECK(ptr == nullptr);
    BOOST_CHECK_NO_THROW(aligned_pfree(123, ptr));
    ptr = aligned_palloc(0, 1);
    BOOST_CHECK(ptr != nullptr);
    BOOST_CHECK_NO_THROW(aligned_pfree(0, ptr));
#if defined(PIRANHA_HAVE_POSIX_MEMALIGN)
    // posix_memalign requires power of two and multiple of sizeof(void *) for alignment value.
    BOOST_CHECK_THROW(aligned_palloc(3, 1), std::bad_alloc);
    BOOST_CHECK_THROW(aligned_palloc(7, 1), std::bad_alloc);
    // For these tests, require that the alignment is valid for int and that it is a power of 2.
    if (sizeof(void *) % alignof(int) == 0 && !(sizeof(void *) & (sizeof(void *) - 1u))) {
        ptr = aligned_palloc(sizeof(void *), sizeof(int) * 10000u);
        default_init(static_cast<int *>(ptr), static_cast<int *>(ptr) + 10000u);
        std::vector<int> v;
        std::generate_n(std::back_inserter(v), 10000u, std::rand);
        std::copy(v.begin(), v.end(), static_cast<int *>(ptr));
        BOOST_CHECK(std::equal(v.begin(), v.end(), static_cast<int *>(ptr)));
        // NOTE: no need to call dtor, int has trivial destruction.
        BOOST_CHECK_NO_THROW(aligned_pfree(sizeof(void *), ptr));
    }
#elif defined(_WIN32)
    // _aligned_malloc requires power of two.
    BOOST_CHECK_THROW(aligned_palloc(3, 1), std::bad_alloc);
    BOOST_CHECK_THROW(aligned_palloc(7, 1), std::bad_alloc);
    // Check that the alignment is valid for int.
    if (16 % alignof(int) == 0) {
        ptr = aligned_palloc(16, sizeof(int) * 10000u);
        default_init(static_cast<int *>(ptr), static_cast<int *>(ptr) + 10000u);
        std::vector<int> v;
        std::generate_n(std::back_inserter(v), 10000u, std::rand);
        std::copy(v.begin(), v.end(), static_cast<int *>(ptr));
        BOOST_CHECK(std::equal(v.begin(), v.end(), static_cast<int *>(ptr)));
        BOOST_CHECK_NO_THROW(aligned_pfree(16, ptr));
    }
#endif
}

// NOTE: here we are assuming we can do some basic arithmetics on the alignment and size values.
BOOST_AUTO_TEST_CASE(memory_alignment_check_test)
{
    BOOST_CHECK(piranha::alignment_check<int>(0));
    BOOST_CHECK(piranha::alignment_check<long long>(0));
    BOOST_CHECK(piranha::alignment_check<std::string>(0));
#if defined(PIRANHA_HAVE_POSIX_MEMALIGN)
    // posix_memalign has additional requirements.
    if (sizeof(void *) >= alignof(int) && !(sizeof(void *) & ((sizeof(void *) - 1u)))) {
        // sizeof(void *) is a power of two greater than the alignment of int.
        BOOST_CHECK(piranha::alignment_check<int>(sizeof(void *) * 2u));
        BOOST_CHECK(piranha::alignment_check<int>(sizeof(void *) * 4u));
        BOOST_CHECK(piranha::alignment_check<int>(sizeof(void *) * 8u));
    }
    if (sizeof(void *) >= alignof(long) && !(sizeof(void *) & ((sizeof(void *) - 1u)))) {
        BOOST_CHECK(piranha::alignment_check<long>(sizeof(void *) * 2u));
        BOOST_CHECK(piranha::alignment_check<long>(sizeof(void *) * 4u));
        BOOST_CHECK(piranha::alignment_check<long>(sizeof(void *) * 8u));
    }
#else
    BOOST_CHECK(piranha::alignment_check<int>(alignof(int)));
    BOOST_CHECK(piranha::alignment_check<std::string>(alignof(std::string)));
    BOOST_CHECK(piranha::alignment_check<int>(alignof(int) * 2u));
    BOOST_CHECK(piranha::alignment_check<std::string>(alignof(std::string) * 4u));
    BOOST_CHECK(piranha::alignment_check<std::string>(alignof(std::string) * 8u));
    if (alignof(int) > 1u) {
        BOOST_CHECK(!piranha::alignment_check<int>(alignof(int) / 2u));
    }
    if (alignof(long) > 1u) {
        BOOST_CHECK(!piranha::alignment_check<long>(alignof(long) / 2u));
    }
    if (alignof(std::string) > 1u) {
        BOOST_CHECK(!piranha::alignment_check<std::string>(alignof(std::string) / 2u));
    }
#endif
}

// Custom string class, will create a string with some content and
// mark move operations as noexcept.
class custom_string : public std::string
{
public:
    custom_string() : std::string("hello") {}
    custom_string(const custom_string &) = default;
    custom_string(custom_string &&other) noexcept : std::string(std::move(other)) {}
    template <typename... Args>
    custom_string(Args &&... params) : std::string(std::forward<Args>(params)...)
    {
    }
    custom_string &operator=(const custom_string &) = default;
    custom_string &operator=(custom_string &&other) noexcept
    {
        std::string::operator=(std::move(other));
        return *this;
    }
    ~custom_string() noexcept {}
};

struct faulty_string : public custom_string {
    faulty_string() : custom_string()
    {
        if (++s_counter == 500u) {
            s_counter.store(0u);
            throw std::runtime_error("oh noes!");
        }
    }
    static std::atomic<unsigned> s_counter;
};

std::atomic<unsigned> faulty_string::s_counter(0u);

static const std::size_t small_alloc_size = 100000u;

BOOST_AUTO_TEST_CASE(memory_parallel_init_destroy_test)
{
    for (unsigned i = 0u; i <= settings::get_n_threads(); ++i) {
        BOOST_CHECK_NO_THROW(parallel_value_init(static_cast<int *>(nullptr), 0u, i));
        BOOST_CHECK_NO_THROW(parallel_destroy(static_cast<int *>(nullptr), 0u, i));
        auto ptr1 = make_parallel_array<int>(small_alloc_size, i);
        BOOST_CHECK(std::all_of(ptr1.get(), ptr1.get() + small_alloc_size, [](int n) { return n == 0; }));
        auto ptr2 = make_parallel_array<custom_string>(small_alloc_size, i);
        BOOST_CHECK(std::all_of(ptr2.get(), ptr2.get() + small_alloc_size,
                                [](const std::string &str) { return str == "hello"; }));
        BOOST_CHECK_THROW(make_parallel_array<faulty_string>(small_alloc_size, i), std::runtime_error);
        // Check zero sizes.
        BOOST_CHECK(make_parallel_array<int>(0u, i).get() == nullptr);
        BOOST_CHECK(make_parallel_array<custom_string>(0u, i).get() == nullptr);
        BOOST_CHECK(make_parallel_array<faulty_string>(0u, i).get() == nullptr);
        if (sizeof(int) > 1u) {
            BOOST_CHECK_THROW(make_parallel_array<int>(std::numeric_limits<std::size_t>::max(), i), std::bad_alloc);
        }
        // Check operator[] and release.
        auto ptr3 = make_parallel_array<int>(small_alloc_size, i);
        ptr3[10u] = 100;
        BOOST_CHECK_EQUAL(ptr3[10u], 100);
        auto ptr4 = ptr3.release();
        BOOST_CHECK(ptr3.get() == nullptr);
        // Just deallocate, no need to destroy ints.
        aligned_pfree(0u, static_cast<void *>(ptr4));
        // Check move semantics on the smart pointer.
        auto ptr_cmp = make_parallel_array<custom_string>(small_alloc_size, i);
        auto ptr5 = make_parallel_array<custom_string>(small_alloc_size, i);
        auto ptr6(std::move(ptr5));
        BOOST_CHECK(std::equal(ptr6.get(), ptr6.get() + small_alloc_size, ptr_cmp.get()));
        BOOST_CHECK(ptr5.get() == nullptr);
        auto ptr7 = make_parallel_array<custom_string>(small_alloc_size, i);
        ptr7 = std::move(ptr6);
        BOOST_CHECK(ptr6.get() == nullptr);
        BOOST_CHECK(std::equal(ptr7.get(), ptr7.get() + small_alloc_size, ptr_cmp.get()));
    }
}
