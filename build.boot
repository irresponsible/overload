(set-env!
  :project 'irresponsible/overload
  :version "0.1.0"
  :resource-paths #{"src"}
  :source-paths #{"src"}
  :description "Dynamic loading and lookup"
  :url "https://github.com/irresponsible/overload/"
  :scm {:url "https://github.com/irresponsible/overload.git"}
  :license {"MIT" "https://en.wikipedia.org/MIT_License"}
  :dependencies '[[org.clojure/clojure      "1.8.0"  :scope "provided"]
                  [adzerk/boot-test         "1.1.0"  :scope "test"]
                  [org.clojure/tools.nrepl  "0.2.12" :scope "test"]])

(require '[adzerk.boot-test :as boot-test])

(task-options!
  pom {:url         (get-env :url)
       :scm         (get-env :scm)
       :project     (get-env :project)
       :version     (get-env :version)
       :license     (get-env :license)
       :description (get-env :description)
       :developers  (get-env :developers)}
  push {:tag            true
        :ensure-branch  "master"
        :ensure-release true
        :ensure-clean   true
        :gpg-sign       true
        :repo-map [["clojars" {:url "https://clojars.org/repo/"}]]}
  target  {:dir #{"target"}})

(deftask testing []
  (set-env! :source-paths #(conj % "test")))
  
(deftask test []
  (testing)
  (comp (target) (speak) (javac) (boot-test/test)))

(deftask autotest []
  (comp (watch) (test)))

(deftask make-release-jar []
  (comp (target) (pom) (jar)))
