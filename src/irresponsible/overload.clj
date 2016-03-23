(ns irresponsible.overload)

(defn primitive-array-subtype
  "If the type names a clojure alias for a primitive array (bytes etc.) returns the singular
   args: [type]
   returns: string or nil"
  [type]
  (when (contains? #{"bytes" "chars" "shorts" "ints" "longs" "floats" "doubles" "booleans"} type)
    (->> type .length dec
         (.substring ^String type 0))))

(defn array-subtype
  "If the type names an array (ends in <>), returns the preceding portion
   args: [type]
   returns: string or nil"
  [type]
  (let [l (.length type)
        i (.lastIndexOf type "<>")]
    (when (= i (- (.length type) 2))
      (.substring type 0 i))))

(defn primitive?
  "true if the given string names a primitive
   args: [name]
   returns: boolean"
  [name]
  (contains? #{"byte" "char" "short" "int" "long" "float" "double" "boolean"} name))

(defn valid-class?
  "true if the given string names a valid class
   args: [name]
   returns: true or nil"
  [name]
  (when (re-find #"^[a-zA-Z_][a-zA-Z0-9_]*(?:\.[a-zA-Z_][a-zA-Z0-9_]*)*$" name)
    true))

(defn valid-var?
  "true if the given string names a valid clojure var
   args: [name]
   returns: boolean"
  [name]
  ;; This clusterfuck is because unicode!
  (when (re-find #"^(?:[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*(?:\.[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*)*/)?[\p{IsAlphabetic}\p{IsIdeographic}*+!_'?<>-][\p{IsAlphabetic}\p{IsIdeographic}\p{IsDigit}*+!_'?<>-]*$" name)
    true))

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
    (letfn [(overload' [sym]
              (try (resolve sym)
                   (catch Exception e nil)))
            (symbol' [sym]
              (when-let [r (overload' sym)]
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
