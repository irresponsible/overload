; vim: syntax=clojure
(set-env!
  :project 'irresponsible/overload
  :version "0.1.2"
  :resource-paths #{"src" "resources"}
  :source-paths #{"src"}
  :description "Dynamic loading and lookup"
  :url "https://github.com/irresponsible/overload/"
  :scm {:url "https://github.com/irresponsible/overload"}
  :developers {"James Laver" "james@seriesofpipes.com"}
  :license {"MIT" "https://en.wikipedia.org/MIT_License"}
  :dependencies '[[org.clojure/clojure      "1.9.0-alpha15"  :scope "provided"]
                  [adzerk/boot-test         "1.1.2"  :scope "test"]
                  [org.clojure/tools.nrepl  "0.2.12" :scope "test"]])

(require '[adzerk.boot-test :as t])

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
        :repo           "clojars"
        :repo-map [["clojars" {:url "https://clojars.org/repo/"}]]}
  target  {:dir #{"target"}})

(deftask testing []
  (set-env! :resource-paths #(conj % "test"))
  (set-env! :source-paths #(conj % "test")))
  
(deftask test []
  (testing)
  (t/test))

(deftask autotest []
  (comp (watch) (test)))

;; RMG Only stuff
(deftask make-jar []
  (comp (pom) (jar) (target)))

(deftask release []
  (comp (pom) (jar) (push)))

;; Travis Only stuff
(deftask travis []
  (testing)
  (t/test))

(deftask travis-installdeps []
  (testing) identity)
