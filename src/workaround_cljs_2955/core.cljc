(ns workaround-cljs-2955.core)

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
