(ns irresponsible.overload-test
  (:require [clojure.test :refer [deftest is]]
            [irresponsible.overload :as o]))

(deftest primitive-array-subtype
  (let [in  ["bytes" "longs" "foo" "foo.bar.baz" "foo/baz"]
        out ["byte" "long" nil nil nil]]
    (is (= out (mapv o/primitive-array-subtype in)))))

(deftest array-subtype
  (let [in  ["foo<>" "foo.bar.baz<>" "foo" "foo.bar.baz"]
        out ["foo" "foo.bar.baz" nil nil]]
    (is (= out (mapv o/array-subtype in)))))

(deftest primitive?
  (let [in  ["byte" "char" "short" "foo" "foo.bar" "foo/bar"]
        out [true true true false false false]]
    (is (= out (mapv o/primitive? in)))))

(deftest valid-class?
  (let [in  ["byte" "java.lang.Object" "clojure.core/str" "invalid-name"]
        out [true true nil nil]]
    (is (= out (mapv o/valid-class? in)))))

(deftest valid-var?
  (let [in  ["byte" "clojure.core/str" "foo/bar" "foo" "java.lang.Object"
             "really.super.long/name" "inv/al.id"]
        out [true true true true nil true nil]]
    (is (= out (mapv o/valid-var? in)))))

(deftest about
  (is (= [:array [:symbol "foo"]] (o/about "foo<>")))
  (is (= [:array [:symbol "java.lang.Object"]] (o/about "java.lang.Object<>")))
  (is (= [:primitive "byte"] (o/about "byte")))
  (is (= [:symbol "foo"] (o/about "foo")))
  (is (= [:symbol "java.lang.Object"] (o/about "java.lang.Object")))
  (is (nil? (o/about "1`nv@L1d"))))

(deftest overload
  (is (= [:primitive 'byte]               (o/overload 'byte)))
  (is (= [:class java.lang.Byte]          (o/overload 'java.lang.Byte)))
  (is (= [:array [:primitive 'byte]]      (o/overload 'bytes)))
  (is (= [:array [:primitive 'byte]]      (o/overload 'byte<>)))
  (is (= [:array [:class java.lang.Byte]] (o/overload 'java.lang.Byte<>)))
  (is (= [:var #'clojure.core/str]        (o/overload 'clojure.core/str)))
  (is (nil? (o/about 'nonexistent.class)))
  (is (nil? (o/about 'nonexistent/symbol)))
  (is (nil? (o/about nil)))
  (is (nil? (o/about ""))))
