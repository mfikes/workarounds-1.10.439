(ns workarounds-1-10-439.core
  (:require
   [cljs.spec.test.alpha]))

(defn- eval-form
  [form ns]
  (when-not (find-ns ns)
    #?(:clj  (create-ns ns)
       :cljs (eval `(~'ns ~ns))))
  (binding #?(:clj  [*ns* (the-ns ns)]
              :cljs [*ns* (find-ns ns)])
    (#?(:clj do :cljs try)
      (eval `(do
               (clojure.core/refer-clojure)
               ~form))
      #?(:cljs (catch :default e (throw (ex-cause e)))))))

(defn- macros-ns [sym]
  #?(:clj  sym
     :cljs (symbol (str sym "$macros"))))

#?(:cljs
   (defn checkable-syms*
     ([]
      (checkable-syms* nil))
     ([opts]
      (reduce into #{}
        [(filter js/cljs.spec.test.alpha$macros.fn_spec_name_QMARK_ (keys @js/cljs.spec.alpha$macros.registry_ref))
         (keys (:spec opts))]))))

#?(:cljs
   (defmacro monkey-patch-check []
     (set! js/cljs.spec.test.alpha$macros.checkable_syms_STAR_ checkable-syms*))
   :clj
   (defmacro monkey-patch-check []))

(defmacro monkey-patch-with-instrument-disabled []
  (eval-form
    '(defmacro with-instrument-disabled [& body]
       `(let [orig# (.-*instrument-enabled* js/cljs.spec.test.alpha)]
          (set! cljs.spec.test.alpha/*instrument-enabled* nil)
          (try
            ~@body
            (finally
              (set! cljs.spec.test.alpha/*instrument-enabled* orig#)))))
    (macros-ns 'cljs.spec.test.alpha)))
