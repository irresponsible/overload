The irresponsible clojure guild present...

# overload

A simple library that deals with dynamic loading of classes and vars

[![Clojars Project](https://img.shields.io/clojars/v/irresponsible/overload.svg)](https://clojars.org/irresponsible/overload)

[![Travis CI](https://travis-ci.org/irresponsible/overload.svg?branch=master)](https://travis-ci.org/irresponsible/overload)

## Usage

```clojure
(require '[irresponsible.overload :refer [overload]])

(overload 'byte)    ;; => [:primitive 'byte]
(overload 'char)    ;; => [:primitive 'char]
(overload 'bytes)   ;; => [:array [:primitive 'byte]]
(overload 'byte<>)  ;; => [:array [:primitive 'byte]]
(overload 'java.lang.Byte)      ;; => [:class java.lang.Byte]
(overload 'clojure.core/str)    ;; => [:var #'clojure.core/str]
(overload 'java.lang.Object<>)  ;; => [:array [:class java.lang.Object]]
```

## Plans

Clojurescript support would be nice but requires careful consideration.

## Contributions

Pull requests and issues welcome, even if it's just doc fixes. We don't bite.

Tests are run with `boot test` or `boot autotest` (reruns on file changes)

## License

Copyright (c) 2016 James Laver

MIT LICENSE

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

