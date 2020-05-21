(ns number-format-repro.core
  (:import (goog.i18n NumberFormat NumberFormat.Format
                      NumberFormatSymbols
                      NumberFormatSymbols_eu)))

(defn decimal-pattern
  "Creates a decimal pattern for the number formatter based on number of decimals set by user"
  [num-decimals]
  (cond-> "#,##0"
    (not (zero? num-decimals))
    (str "." (apply str (repeat num-decimals "#")))))

;; Number formatter
;; FIXME: https://stackoverflow.com/questions/55462576/how-can-i-prevent-the-closure-compiler-from-minifying-certain-methods-in-clojure
(defn ^NumberFormat chart-number-format [{:keys [num-decimals thousand-separator? currency column-type]}]
  #_(prn "test" NumberFormatSymbols NumberFormatSymbols_eu)
  (let [currency (or currency "en-US")
        num-format-symbols (case currency
                             :de-DE NumberFormatSymbols_eu
                             NumberFormatSymbols)
        number-formatter (case column-type
                           :currency (.-CURRENCY ^js/Object NumberFormat.Format)
                           (.-DECIMAL ^js/Object NumberFormat.Format))
        _ (prn num-format-symbols)
        number-format (-> num-format-symbols
                          (js->clj)
                          (assoc "DECIMAL_PATTERN" (decimal-pattern num-decimals))
                          (cond->
                              (not thousand-separator?) (assoc "GROUP_SEP" ""))
                          (clj->js))
        _ (prn number-format)
        ]
    (NumberFormat. number-formatter
                   nil nil
                   number-format)))

(defn format-chart-number [content-format ^number x]
  (let [number-formatter (chart-number-format content-format)]
    (.format number-formatter x)))

(decimal-pattern 4)

(doseq [fmt [{:num-decimals 2
              :thousand-separator? true
              :currency :de-DE
              :column-type :currency}
             {:num-decimals 3
              :thousand-separator? true
              :currency :en-US
              :column-type :currency}
             {:num-decimals 4
              :currency :de-DE}]]

  (js/console.log (pr-str fmt))
  (js/console.log (format-chart-number fmt 12345.34456)))
