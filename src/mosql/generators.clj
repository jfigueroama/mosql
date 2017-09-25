(ns mosql.generators
  )

(defn gen-field-cast
  "Generate a susbstitute value for the parameter in the operation SQL.
  
  The substitution function is like:
  (fn [field value] ...) and must return a string."
  [casts [field value]]
  (if (nil? (get casts field))
    "?"
    (apply (get casts field) [field value])))



(defn gen-insert
  "Basic insert function.
  
  options can be
  {:new-id :id    ; tells where the generated key will be inserted if any.
                  ; if omitted the generated key will not be inserted.
   :cast {:dob (fn [x] (str \"cast(? as date)\"))}
  "
  ([table data]
   (gen-insert table {} data))
  ([options table data]
   (let [rvalues (or (:cast options) {})
         skeys (keys data)
         fields (clojure.string/join ", " (map name skeys))
         substs (clojure.string/join ", " (map (partial gen-field-cast rvalues) data))
         sql (str "INSERT INTO " (name table) " (" fields ") VALUES(" substs ")")
         sqlvals (vals data)
         ]
     (vec (concat [sql] sqlvals)))))

; (gen-insert {:cast {:dob (fn [f v] "cast(? as date)")}} :person {:fname "jose" :lname "figueroa" :dob "1982-11-20"})

(defn gen-update-field
  [casts [field value]]
  (str (name field) "=" (if (nil? (get casts field))
                          "?"
                          (apply (get casts field) [field value]))))

(defn gen-update
  ([table ids data]
   (gen-update {} table ids data))
  ([options table ids data]
   (let [rvalues (or (:cast options) {})
         fields (clojure.string/join
                  ", "
                  (map (partial gen-update-field rvalues) data))
         where (if (string? ids)
                 (str " WHERE " ids)
                 (if (map? ids)
                   (str " WHERE "
                        (clojure.string/join
                          " AND "
                          (map (partial gen-update-field rvalues) ids)))))
         sql (str "UPDATE " (name table) " SET " fields where)
         ]
     (vec (concat [sql] (vals data) (if (map? ids) (vals ids)))))))

;(gen-update :person {:fname "jose"} {:phone "123456"})
;(gen-update {:dob (fn [f v] "cast(? as date)")} :person {:fname "jose" :dob "1982-11-29"} {:phone "123456" :other "234"})
;(gen-update :person "dob>cast(? as date)" {:fname "jose" :dob "1982-11-29"} )
