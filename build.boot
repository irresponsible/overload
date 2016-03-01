(set-env!
  :project 'irresponsible/overload
  :version "0.1.0"
  :resource-paths #{"src"}
  :source-paths #{"src"}
  :description "Dynamic loading and lookup"
  :url "https://github.com/irresponsible/overload/"
  :scm {:url "https://github.com/irresponsible/overload.git"}
  :license {"MIT" "https://en.wikipedia.org/MIT_License"}
  :dependencies '[[org.clojure/clojure "1.8.0"                  :scope "provided"]
                  [adzerk/boot-test "1.1.0"                     :scope "test"]
                  ;; [org.clojure/clojurescript "1.7.228"          :scope "test"]
                  ;; [adzerk/boot-cljs "1.7.228-1"                 :scope "test"]
                  ;; [adzerk/boot-cljs-repl       "0.3.0"          :scope "test"]
                  ;; [adzerk/boot-reload          "0.4.5"          :scope "test"]
                  ;; [pandeiro/boot-http          "0.7.1-SNAPSHOT" :scope "test"]
                  ;; [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                  ;; [weasel                      "0.7.0"          :scope "test"]
                  [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
                  ;; [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  ])

(require '[adzerk.boot-test :as boot-test])
         ;; '[adzerk.boot-cljs :as boot-cljs]
         ;; '[adzerk.boot-cljs-repl :as boot-cljs-repl]
         ;; '[adzerk.boot-reload    :as boot-reload]
         ;; '[crisptrutski.boot-cljs-test :as boot-cljs-test]
         ;; '[pandeiro.boot-http :as boot-http])
         
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

(deftask test-clj []
  (testing)
  (comp (target) (speak) (boot-test/test)))

;; (deftask test-cljs []
;;   (set-env! :source-paths #(conj % "test"))
;;   (comp (target) (speak) (boot-cljs/cljs) (boot-cljs-test/test-cljs)))
  
(deftask test []
  (testing)
  (comp (target) (speak) (javac) (boot-test/test)))

(deftask autotest []
  (comp (watch) (test)))

(deftask make-release-jar []
  (comp (target) (pom) (jar)))
