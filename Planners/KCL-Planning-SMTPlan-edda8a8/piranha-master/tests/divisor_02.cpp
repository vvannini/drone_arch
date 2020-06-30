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

#include <piranha/divisor.hpp>

#define BOOST_TEST_MODULE divisor_02_test
#include <boost/test/included/unit_test.hpp>

#include <algorithm>
#include <boost/algorithm/string/predicate.hpp>
#include <initializer_list>
#include <random>
#include <sstream>
#include <stdexcept>
#include <string>
#include <tuple>
#include <vector>

#include <piranha/config.hpp>
#include <piranha/integer.hpp>
#include <piranha/math.hpp>
#include <piranha/math/gcd3.hpp>
#include <piranha/s11n.hpp>
#include <piranha/symbol_utils.hpp>
#include <piranha/type_traits.hpp>

using namespace piranha;

using value_types = std::tuple<signed char, short, int, long, long long, integer>;

static std::mt19937 rng;

#if defined(PIRANHA_WITH_BOOST_S11N) || defined(PIRANHA_WITH_MSGPACK)
static const int ntries = 1000;
#endif

BOOST_AUTO_TEST_CASE(divisor_empty_test) {}

#if defined(PIRANHA_WITH_BOOST_S11N)

template <typename OArchive, typename IArchive, typename T>
static inline void boost_round_trip(const T &d, const symbol_fset &s)
{
    using w_type = boost_s11n_key_wrapper<T>;
    {
        std::stringstream ss;
        {
            OArchive oa(ss);
            boost_save(oa, w_type{d, s});
        }
        T retval;
        {
            IArchive ia(ss);
            w_type w{retval, s};
            boost_load(ia, w);
        }
        BOOST_CHECK(retval == d);
    }
    {
        std::stringstream ss;
        {
            OArchive oa(ss);
            w_type w{d, s};
            oa << w;
        }
        T retval;
        {
            IArchive ia(ss);
            w_type w{retval, s};
            ia >> w;
        }
        BOOST_CHECK(retval == d);
    }
}

struct boost_s11n_tester {
    template <typename T>
    void operator()(const T &) const
    {
        using d_type = divisor<T>;
        using w_type = boost_s11n_key_wrapper<d_type>;
        BOOST_CHECK((has_boost_save<boost::archive::binary_oarchive, w_type>::value));
        BOOST_CHECK((has_boost_save<boost::archive::binary_oarchive &, w_type>::value));
        BOOST_CHECK((has_boost_save<boost::archive::binary_oarchive &, const w_type>::value));
        BOOST_CHECK((has_boost_save<boost::archive::binary_oarchive &, const w_type &>::value));
        BOOST_CHECK((!has_boost_save<const boost::archive::binary_oarchive &, const w_type &>::value));
        BOOST_CHECK((!has_boost_save<void, const w_type &>::value));
        BOOST_CHECK((!has_boost_save<boost::archive::binary_iarchive, w_type>::value));
        BOOST_CHECK((has_boost_load<boost::archive::binary_iarchive, w_type>::value));
        BOOST_CHECK((has_boost_load<boost::archive::binary_iarchive &, w_type>::value));
        BOOST_CHECK((!has_boost_load<boost::archive::binary_iarchive &, const w_type>::value));
        BOOST_CHECK((!has_boost_load<boost::archive::binary_iarchive &, const w_type &>::value));
        BOOST_CHECK((!has_boost_load<const boost::archive::binary_iarchive &, const w_type &>::value));
        BOOST_CHECK((!has_boost_load<void, const w_type &>::value));
        BOOST_CHECK((!has_boost_load<boost::archive::binary_oarchive, w_type>::value));
        std::uniform_int_distribution<int> sdist(0, 10), ddist(-10, 10), edist(1, 10);
        const std::vector<std::string> vs = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        for (int i = 0; i < ntries; ++i) {
            d_type d;
            auto ssize = sdist(rng);
            auto dsize = sdist(rng);
            symbol_fset ss(vs.data(), vs.data() + ssize);
            std::vector<T> divs;
            divs.resize(unsigned(ssize));
            for (int j = 0; j < dsize; ++j) {
                std::generate(divs.begin(), divs.end(), [&ddist]() { return ddist(rng); });
                T exp(static_cast<T>(edist(rng)));
                bool nonzero_found = false;
                for (auto &dv : divs) {
                    if (dv < 0) {
                        dv = T(-dv);
                        nonzero_found = true;
                        break;
                    } else if (dv > 0) {
                        nonzero_found = true;
                    }
                }
                if (!nonzero_found) {
                    continue;
                }
                T g(0);
                for (const auto &dv : divs) {
                    piranha::gcd3(g, g, dv);
                }
                for (auto &dv : divs) {
                    dv = T(dv / g);
                }
                d.insert(divs.begin(), divs.end(), exp);
            }
            boost_round_trip<boost::archive::binary_oarchive, boost::archive::binary_iarchive>(d, ss);
            boost_round_trip<boost::archive::text_oarchive, boost::archive::text_iarchive>(d, ss);
            if (d.size() != 0u) {
                // Error handling with invalid symbol sets.
                std::stringstream sst;
                {
                    boost::archive::binary_oarchive oa(sst);
                    BOOST_CHECK_EXCEPTION(boost_save(oa, w_type{d, symbol_fset{}}), std::invalid_argument,
                                          [](const std::invalid_argument &iae) {
                                              return boost::contains(
                                                  iae.what(),
                                                  "an invalid symbol_set was passed as an argument during the "
                                                  "Boost serialization of a divisor");
                                          });
                }
                sst.str("");
                sst.clear();
                {
                    boost::archive::binary_oarchive oa(sst);
                    boost_save(oa, w_type{d, ss});
                }
                ss.emplace_hint(ss.end(), "z");
                {
                    boost::archive::binary_iarchive ia(sst);
                    w_type w{d, ss};
                    BOOST_CHECK_EXCEPTION(
                        boost_load(ia, w), std::invalid_argument, [](const std::invalid_argument &iae) {
                            return boost::contains(iae.what(),
                                                   "the divisor loaded from a Boost archive is not compatible "
                                                   "with the supplied symbol set");
                        });
                    BOOST_CHECK_EQUAL(d.size(), 0u);
                }
            }
        }
    }
};

BOOST_AUTO_TEST_CASE(divisor_boost_s11n_test)
{
    tuple_for_each(value_types{}, boost_s11n_tester{});
}

#endif

#if defined(PIRANHA_WITH_MSGPACK)

template <typename T>
static inline void msgpack_round_trip(const T &d, const symbol_fset &s, msgpack_format f)
{
    msgpack::sbuffer sbuf;
    msgpack::packer<msgpack::sbuffer> p(sbuf);
    d.msgpack_pack(p, f, s);
    auto oh = msgpack::unpack(sbuf.data(), sbuf.size());
    T retval;
    retval.msgpack_convert(oh.get(), f, s);
    BOOST_CHECK(retval == d);
}

struct msgpack_s11n_tester {
    template <typename T>
    void operator()(const T &) const
    {
        using d_type = divisor<T>;
        BOOST_CHECK((key_has_msgpack_pack<msgpack::sbuffer, d_type>::value));
        BOOST_CHECK((key_has_msgpack_pack<msgpack::sbuffer, d_type &>::value));
        BOOST_CHECK((key_has_msgpack_pack<msgpack::sbuffer, const d_type &>::value));
        BOOST_CHECK((!key_has_msgpack_pack<msgpack::sbuffer &, const d_type &>::value));
        BOOST_CHECK((!key_has_msgpack_pack<void, const d_type &>::value));
        BOOST_CHECK((key_has_msgpack_convert<d_type>::value));
        BOOST_CHECK((key_has_msgpack_convert<d_type &>::value));
        BOOST_CHECK((!key_has_msgpack_convert<const d_type &>::value));
        std::uniform_int_distribution<int> sdist(0, 10), ddist(-10, 10), edist(1, 10);
        const std::vector<std::string> vs = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        for (auto f : {msgpack_format::portable, msgpack_format::binary}) {
            for (int i = 0; i < ntries; ++i) {
                d_type d;
                auto ssize = sdist(rng);
                auto dsize = sdist(rng);
                symbol_fset ss(vs.data(), vs.data() + ssize);
                std::vector<T> divs;
                divs.resize(unsigned(ssize));
                for (int j = 0; j < dsize; ++j) {
                    std::generate(divs.begin(), divs.end(), [&ddist]() { return ddist(rng); });
                    T exp(static_cast<T>(edist(rng)));
                    bool nonzero_found = false;
                    for (auto &dv : divs) {
                        if (dv < 0) {
                            dv = T(-dv);
                            nonzero_found = true;
                            break;
                        } else if (dv > 0) {
                            nonzero_found = true;
                        }
                    }
                    if (!nonzero_found) {
                        continue;
                    }
                    T g(0);
                    for (const auto &dv : divs) {
                        piranha::gcd3(g, g, dv);
                    }
                    for (auto &dv : divs) {
                        dv = T(dv / g);
                    }
                    d.insert(divs.begin(), divs.end(), exp);
                }
                msgpack_round_trip(d, ss, f);
                if (d.size() != 0u) {
                    {
                        // Error handling with invalid symbol sets.
                        msgpack::sbuffer sbuf;
                        msgpack::packer<msgpack::sbuffer> p(sbuf);
                        BOOST_CHECK_EXCEPTION(d.msgpack_pack(p, f, symbol_fset{}), std::invalid_argument,
                                              [](const std::invalid_argument &iae) {
                                                  return boost::contains(
                                                      iae.what(),
                                                      "an invalid symbol_set was passed as an argument for the "
                                                      "msgpack_pack() method of a divisor");
                                              });
                    }
                    {
                        msgpack::sbuffer sbuf;
                        msgpack::packer<msgpack::sbuffer> p(sbuf);
                        d.msgpack_pack(p, f, ss);
                        ss.emplace_hint(ss.end(), "z");
                        auto oh = msgpack::unpack(sbuf.data(), sbuf.size());
                        BOOST_CHECK_EXCEPTION(d.msgpack_convert(oh.get(), f, ss), std::invalid_argument,
                                              [](const std::invalid_argument &iae) {
                                                  return boost::contains(
                                                      iae.what(),
                                                      "the divisor loaded from a msgpack object is not compatible "
                                                      "with the supplied symbol set");
                                              });
                    }
                }
            }
        }
        // Check malformed data.
        d_type dv;
        msgpack::sbuffer sbuf;
        msgpack::packer<msgpack::sbuffer> p(sbuf);
        p.pack_array(1);
        p.pack_array(2);
        p.pack_array(0);
        msgpack_pack(p, T(0), msgpack_format::binary);
        auto oh = msgpack::unpack(sbuf.data(), sbuf.size());
        BOOST_CHECK_EXCEPTION(dv.msgpack_convert(oh.get(), msgpack_format::binary, symbol_fset{}),
                              std::invalid_argument, [](const std::invalid_argument &iae) {
                                  return boost::contains(iae.what(),
                                                         "the divisor loaded from a msgpack object failed internal "
                                                         "consistency checks");
                              });
        BOOST_CHECK(dv == d_type{});
    }
};

BOOST_AUTO_TEST_CASE(divisor_msgpack_s11n_test)
{
    tuple_for_each(value_types{}, msgpack_s11n_tester{});
}

#endif
