(ns irresponsible.overload)

;; # overload
;;
;; ## A simple library that deals with dynamic loading of classes and vars
;;
;; ## Cheatsheet
;;
;; ## Functions
;;
;; ### primitive-array-subtype
;;
;; Returns the singular form of aliases for primative arrays.
;;
;; Returns nil otherwise.
;;
;; ```clojure
;; (is (= "byte" (primitive-array-subtype "bytes")))
;; (is (= "char" (primitive-array-subtype "chars")))
;; (is (= nil    (primitive-array-subtype "IDoNotExist")))
;; ```
;;
;;; (primitive-array-subtype "bytes")
(defn primitive-array-subtype
  "If the type names a clojure alias for a primitive array (bytes etc.) returns the singular
   args: [type]
   returns: string or nil"
  [type]
  (when (contains? #{"bytes" "chars" "shorts" "ints" "longs" "floats" "doubles" "booleans"} type)
    (->> type .length dec
         (.substring ^String type 0))))

;; ### array-subtype
;;
;; If the type names an array, returns the singular that the array is composed of.
;;
;; Returns nil otherwise.
;;
;; ```clojure
;; (is (= "foo" (array-subtype "foo<>")))
;; (is (= "bar" (array-subtype "bar<>")))
;; (is (= nil   (array-subtype "NotAnArray")))
;; ```
;;
;;; (array-subtype "foo<>")
(defn array-subtype
  "If the type names an array (ends in <>), returns the preceding portion
   args: [type]
   returns: string or nil"
  [type]
  (let [l (.length type)
        i (.lastIndexOf type "<>")]
    (when (= i (- (.length type) 2))
      (.substring type 0 i))))

;; ### primitive?
;;
;; Returns true if the given string names a primitive
;;
;; ```clojure
;; (is (= true  (primitive? "double"    )))
;; (is (= true  (primitive? "byte"      )))
;; (is (= false (primitive? "jabberwock")))
;; ```
;;
;;; (primitive? "double")
(defn primitive?
  "true if the given string names a primitive
   args: [name]
   returns: boolean"
  [name]
  (contains? #{"byte" "char" "short" "int" "long" "float" "double" "boolean"} name))

;; ### valid-class?
;;
;; Returns true if the given string is a legal descriptor of a class name
;;
;; ```clojure
;; (is (= true  (valid-class? "ClassName.Goes.Here"         )))
;; (is (= false (valid-class? "5NumbersArentLegalFirstChars")))
;; (is (= false (valid-class? "Also ain't valid"            )))
;; ```
;;
;;; (valid-class? "ClassName.Goes.Here")
(defn valid-class?
  "true if the given string names a valid class
   args: [name]
   returns: true or nil"
  [name]
  (when (re-find #"^[a-zA-Z_][a-zA-Z0-9_]*(?:\.[a-zA-Z_][a-zA-Z0-9_]*)*$" name)
    true))

;; ### valid-var?
;;
;; Returns true if the given string is a legal descriptor of a variable
;;
;; ```clojure
;; (is (= true  (valid-var? "byte"          )))
;; (is (= true  (valid-var? "class.name/var")))
;; (is (= false (valid-var? "in/val.id"     )))
;; ```
;;
;;; (valid-var? "foo/bar")
(defn valid-var?
  "true if the given string names a valid clojure var
   args: [name]
   returns: boolean"
  [name]
  ;; This clusterfuck is because unicode!
  (when (re-find #"^(?:[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*(?:\.[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*)*/)?[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*$" name)
    true))

;; ### about
;;
;; Attempts to decode the given type string and returns a vector of decomposed information
;;
;; Returns format:
;; - `[:array     OFTYPE     ]`
;; - `[:primitive PRIMTYPE   ]`
;; - `[:symbol    SYMBOLNAME ]`
;; - `[:class     CLASSSYMBOL]`
;;
;; ```clojure
;; (is (= [:array [:symbol "foo"]]      (about "foo<>")))
;; (is (= [:primitive "byte"]           (about "byte")))
;; (is (= [:symbol "foo"]               (about "foo")))
;; (is (= [:symbol "java.lang.Object"]  (about "java.lang.Object")))
;; (is (nil?                            (about "1`nv@L1d")))
;; ```
;;
;;; (about "foo<>")
(defn about
  "Attempts to parse the given type string and returns a vector of information
   args: [type]
   returns: [type extra] or nil (invalid)
     type:  one of :array, :primitive, :class
     extra: for array, the subtype string (raw), for others, the name)"
  [name]
  (letfn [(scalar [name]
            (cond (primitive? name)        [:primitive name]
                  (or (valid-class? name)
                      (valid-var? name))   [:symbol name]
                  :otherwise               nil))]
    (when (and name (string? name))
      (if-let [subtype (primitive-array-subtype name)]
        [:array [:primitive subtype]]
        (if-let [subtype (array-subtype name)]
          (when-let [r (scalar subtype)]
            [:array r])
          (scalar name))))))

;; ### overload
;;
;; Resolves the given symbol/string and returns a data structure
;; of decomposed information about that symbol/string.
;;
;; Similar to `about`, except uses actual runtime typedata instead of
;; simple cosmetic rules about what it *could* be.
;;
;; ```clojure
;; (is (= [:primitive 'byte]          (overload 'byte   )))
;; (is (= [:primitive 'char]          (overload 'char   )))
;; (is (= [:array [:primitive 'byte]] (overload 'byte<> )))
;; (is (= [:class java.lang.Byte]     (overload 'java.lang.Byte)))
;; (is (= [:var #'clojure.core/str]   (overload 'clojure.core/str)))
;; ```
;;
;;; (overload 'byte)
(defn overload
  "Given a symbol or string, inspects and loads it, returning a data structure tagging it
   with a type. Supports arrays, primitives and vars
   args: [sym]
   returns: [t v] or nil
     t: the type, a keyword. one of :array :primitive :class :var
     v: in case of array, another [t v] vector
        in case of other, the class, primitive or var named"
  [sym]
  (when-let [[t v :as all] (-> sym str about)]
    (letfn [(symbol' [sym]
              (when-let [r (try (resolve sym))]
                  (cond (var? r)   [:var r]
                        (class? r) [:class r]
                        :otherwise nil)))]
      (case t
        :symbol (symbol' (symbol v))
        :array  (let [[t2 v2] v]
                  (case t2
                    :primitive [:array [t2 (symbol v2)]]
                    :symbol    (when-let [r (symbol' (symbol v2))]
                                 [:array r])
                    nil))
        :primitive [:primitive (symbol v)]))))
