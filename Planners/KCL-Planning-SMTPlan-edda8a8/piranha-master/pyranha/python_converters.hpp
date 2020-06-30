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

#ifndef PYRANHA_PYTHON_CONVERTERS_HPP
#define PYRANHA_PYTHON_CONVERTERS_HPP

#include "python_includes.hpp"

#include <string>

#include <boost/lexical_cast.hpp>
#include <boost/python/converter/registry.hpp>
#include <boost/python/converter/rvalue_from_python_data.hpp>
#include <boost/python/extract.hpp>
#include <boost/python/handle.hpp>
#include <boost/python/import.hpp>
#include <boost/python/object.hpp>
#include <boost/python/type_id.hpp>

#include <mp++/config.hpp>
#if defined(MPPP_WITH_MPFR)
#include <mp++/detail/mpfr.hpp>
#endif

#include <piranha/config.hpp>
#include <piranha/integer.hpp>
#include <piranha/rational.hpp>
#if defined(MPPP_WITH_MPFR)
#include <piranha/real.hpp>
#endif
#include <piranha/safe_cast.hpp>

// NOTE: useful resources for python converters and C API:
// - http://misspent.wordpress.com/2009/09/27/how-to-write-boost-python-converters
// - http://svn.felspar.com/public/fost-py/trunk/fost-py/Cpp/fost-python/pystring.cpp
// - http://svn.felspar.com/public/fost-py/trunk/fost-py/Cpp/fost-python/pyjson.cpp
// - http://stackoverflow.com/questions/937884/how-do-i-import-modules-in-boostpython-embedded-python-code
// - http://docs.python.org/c-api/

namespace pyranha
{

namespace bp = boost::python;

template <typename T>
inline void construct_from_str(::PyObject *obj_ptr, bp::converter::rvalue_from_python_stage1_data *data,
                               const std::string &name)
{
    piranha_assert(obj_ptr);
    // NOTE: here we use str because for int and rational the string representations in Python
    // match the input format for the corresponding C++ objects. This is not true in general
    // for the repr() representations (e.g., in Python 2.x a trailing "L" is added to the repr() representation
    // for long integers).
    ::PyObject *str_obj = ::PyObject_Str(obj_ptr);
    if (!str_obj) {
        ::PyErr_SetString(PyExc_RuntimeError, ("unable to extract string representation of " + name).c_str());
        bp::throw_error_already_set();
    }
    bp::handle<> str_rep(str_obj);
#if PY_MAJOR_VERSION < 3
    const char *s = ::PyString_AsString(str_rep.get());
#else
    ::PyObject *unicode_str_obj = ::PyUnicode_AsEncodedString(str_rep.get(), "ascii", "strict");
    if (!unicode_str_obj) {
        ::PyErr_SetString(PyExc_RuntimeError, ("unable to extract string representation of " + name).c_str());
        bp::throw_error_already_set();
    }
    bp::handle<> unicode_str(unicode_str_obj);
    const char *s = ::PyBytes_AsString(unicode_str.get());
    if (!s) {
        ::PyErr_SetString(PyExc_RuntimeError, ("unable to extract string representation of " + name).c_str());
        bp::throw_error_already_set();
    }
#endif
    // NOTE: the use of reinterpret_cast here comes straight from the sources quoted at the beginning of the file.
    // reinterpret_cast
    // in general looks rather dangerous and unsafe, and it's not clear to me here if this is an idiomatic way of doing
    // things in Boost.Python
    // or instead this should be a static_cast to void * or something like that. Maybe a good question for the Boost
    // Python mailing list.
    void *storage = reinterpret_cast<bp::converter::rvalue_from_python_storage<T> *>(data)->storage.bytes;
    ::new (storage) T(s);
    data->convertible = storage;
}

struct integer_converter {
    integer_converter()
    {
        bp::to_python_converter<piranha::integer, to_python>();
        bp::converter::registry::push_back(&convertible, &construct, bp::type_id<piranha::integer>());
    }
    struct to_python {
        static ::PyObject *convert(const piranha::integer &n)
        {
            // NOTE: use PyLong_FromString here instead?
            const std::string str = boost::lexical_cast<std::string>(n);
#if PY_MAJOR_VERSION < 3
            bp::object bi_module = bp::import("__builtin__");
#else
            bp::object bi_module = bp::import("builtins");
#endif
            bp::object int_class = bi_module.attr("int");
            return bp::incref(int_class(str).ptr());
        }
    };
    static void *convertible(::PyObject *obj_ptr)
    {
        if (!obj_ptr
            || (
#if PY_MAJOR_VERSION < 3
                   !PyInt_CheckExact(obj_ptr) &&
#endif
                   !PyLong_CheckExact(obj_ptr))) {
            return nullptr;
        }
        return obj_ptr;
    }
    static void construct(::PyObject *obj_ptr, bp::converter::rvalue_from_python_stage1_data *data)
    {
        construct_from_str<piranha::integer>(obj_ptr, data, "integer");
    }
};

struct rational_converter {
    rational_converter()
    {
        bp::to_python_converter<piranha::rational, to_python>();
        bp::converter::registry::push_back(&convertible, &construct, bp::type_id<piranha::rational>());
    }
    struct to_python {
        static ::PyObject *convert(const piranha::rational &q)
        {
            bp::object frac_module = bp::import("fractions");
            bp::object frac_class = frac_module.attr("Fraction");
            return bp::incref(frac_class(q.get_num(), q.get_den()).ptr());
        }
    };
    static void *convertible(::PyObject *obj_ptr)
    {
        if (!obj_ptr) {
            return nullptr;
        }
        bp::object frac_module = bp::import("fractions");
        bp::object frac_class = frac_module.attr("Fraction");
        if (!::PyObject_IsInstance(obj_ptr, frac_class.ptr())) {
            return nullptr;
        }
        return obj_ptr;
    }
    static void construct(::PyObject *obj_ptr, bp::converter::rvalue_from_python_stage1_data *data)
    {
        construct_from_str<piranha::rational>(obj_ptr, data, "rational");
    }
};

#if defined(MPPP_WITH_MPFR)

struct real_converter {
    real_converter()
    {
        bp::to_python_converter<piranha::real, to_python>();
        bp::converter::registry::push_back(&convertible, &construct, bp::type_id<piranha::real>());
    }
    struct to_python {
        static ::PyObject *convert(const piranha::real &r)
        {
            bp::object str(boost::lexical_cast<std::string>(r));
            try {
                bp::object mpmath = bp::import("mpmath");
                bp::object mpf = mpmath.attr("mpf");
                return bp::incref(mpf(str).ptr());
            } catch (...) {
                ::PyErr_SetString(
                    PyExc_RuntimeError,
                    "could not convert real number to mpf object - please check the installation of mpmath");
                bp::throw_error_already_set();
                // A fake return value that will never be returned, to make a GCC warning go away.
                return nullptr;
            }
        }
    };
    // Check if obj_ptr is an instance of the class str_class in the module str_mod. In case
    // of import or attr errors, false will be returned.
    static bool is_instance_of(::PyObject *obj_ptr, const char *str_mod, const char *str_class)
    {
        try {
            bp::object c_obj = bp::import(str_mod).attr(str_class);
            if (::PyObject_IsInstance(obj_ptr, c_obj.ptr())) {
                return true;
            }
        } catch (...) {
            // Clear the Python global error status. We don't want some other function to check it later
            // and find it set by the failure in the block above.
            // NOTE: this can always be called, even if no error actually occurred.
            ::PyErr_Clear();
        }
        return false;
    }
    static void *convertible(::PyObject *obj_ptr)
    {
        // Not convertible if nullptr, or obj_ptr is not an instance of mpf.
        if (!obj_ptr || !is_instance_of(obj_ptr, "mpmath", "mpf")) {
            return nullptr;
        }
        return obj_ptr;
    }
    static void construct(::PyObject *obj_ptr, bp::converter::rvalue_from_python_stage1_data *data)
    {
        // NOTE: here we cannot construct directly from string, as we need to query the precision.
        piranha_assert(obj_ptr);
        // NOTE: here the handle is from borrowed because we are not responsible for the generation of obj_ptr:
        // borrowed will increase the refcount of obj_ptr, so that, when obj is destroyed, the refcount
        // for obj_ptr goes back to the original value instead of decreasing by 1.
        bp::handle<> obj_handle(bp::borrowed(obj_ptr));
        bp::object obj(obj_handle);
        const ::mpfr_prec_t prec
            = piranha::safe_cast<::mpfr_prec_t>(static_cast<long>(bp::extract<long>(obj.attr("context").attr("prec"))));
        // NOTE: here we use repr instead of str because the repr seems to give the most accurate representation
        // in base 10 for the object.
        ::PyObject *str_obj = ::PyObject_Repr(obj.ptr());
        if (!str_obj) {
            ::PyErr_SetString(PyExc_RuntimeError, "unable to extract string representation of real");
            bp::throw_error_already_set();
        }
        bp::handle<> str_rep(str_obj);
#if PY_MAJOR_VERSION < 3
        const char *s = ::PyString_AsString(str_rep.get());
#else
        ::PyObject *unicode_str_obj = ::PyUnicode_AsEncodedString(str_rep.get(), "ascii", "strict");
        if (!unicode_str_obj) {
            ::PyErr_SetString(PyExc_RuntimeError, "unable to extract string representation of real");
            bp::throw_error_already_set();
        }
        bp::handle<> unicode_str(unicode_str_obj);
        const char *s = ::PyBytes_AsString(unicode_str.get());
        if (!s) {
            ::PyErr_SetString(PyExc_RuntimeError, "unable to extract string representation of real");
            bp::throw_error_already_set();
        }
#endif
        // NOTE: the search for "'" is due to the string format of mpmath.mpf objects.
        while (*s != '\0' && *s != '\'') {
            ++s;
        }
        if (*s == '\0') {
            ::PyErr_SetString(PyExc_RuntimeError, "invalid string input converting to real");
            bp::throw_error_already_set();
        }
        ++s;
        auto start = s;
        while (*s != '\0' && *s != '\'') {
            ++s;
        }
        if (*s == '\0') {
            ::PyErr_SetString(PyExc_RuntimeError, "invalid string input converting to real");
            bp::throw_error_already_set();
        }
        void *storage
            = reinterpret_cast<bp::converter::rvalue_from_python_storage<piranha::real> *>(data)->storage.bytes;
        ::new (storage) piranha::real(std::string(start, s), prec);
        data->convertible = storage;
    }
};

#endif
}

#endif
