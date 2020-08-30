(ns babashka.impl.spec
  {:no-doc true}
  (:require [babashka.impl.clojure.spec.alpha :as s]
            [babashka.impl.clojure.spec.gen.alpha :as gen]
            [clojure.core :as c]
            [sci.core :as sci :refer [copy-var]]
            [sci.impl.vars :as vars]))

(def tns (vars/->SciNamespace 'clojure.spec.alpha nil))
(def gns (vars/->SciNamespace 'clojure.spec.gen.alpha nil))

(defn- ns-qualify
  "Qualify symbol s by resolving it or using the current *ns*."
  [s]
  (if-let [ns-sym (some-> s namespace symbol)]
    (c/or (some-> (get (ns-aliases *ns*) ns-sym) str (symbol (name s)))
          s)
    (symbol (str (.name *ns*)) (str s))))

(c/defn def
  "Given a namespace-qualified keyword or resolvable symbol k, and a
  spec, spec-name, predicate or regex-op makes an entry in the
  registry mapping k to the spec. Use nil to remove an entry in
  the registry for k."
  [_ _ k spec-form]
  (let [k (if (symbol? k) (ns-qualify k) k)]
    `(clojure.spec.alpha/def-impl '~k '~(s/res spec-form) ~spec-form)))

(def spec-namespace
  {'def (sci/copy-var s/def tns)
   'def-impl (copy-var s/def-impl tns)
   'valid? (copy-var s/valid? tns)
   'gen (copy-var s/gen tns)
   'cat (copy-var s/cat tns)
   'cat-impl (copy-var s/cat-impl tns)
   #_#_'explain-data (copy-var s/explain-data tns)})

(def gen-namespace
  {'generate (copy-var gen/generate gns)})

;; def-impl
;; -> spec? ;; OK
;;    regex?
;;    spec-impl
;;    with-name
