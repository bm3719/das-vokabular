(ns das-vokabular.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(def dict "data.csv")

(defn csv->seq [f]
  (if-let [r (io/resource f)]
    (with-open [reader (io/reader r)]
      (doall (csv/read-csv reader)))
    (throw (ex-info "Dictionary file not found" {:file f}))))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> [:english :german]
            repeat)
       (rest csv-data)))

(defn read-file [f]
  (csv-data->maps (csv->seq f)))

(defn valid-file?
  "Check an input file to ensure that it has a consistent number of fields.  If
  false, there exists one or more rows that have too few or too many fields." [f]
  (->> (csv->seq f)
       doall
       (map count)
       distinct
       (every? #(= 2 %))))

(defn next-word-article [dict]
  (nth dict (rand-int (count dict))))

(defn score->str
  "Convert the score vector to human readable percentage form." [[right wrong]]
  (if (zero? (+ right wrong))
    "0%"
    (-> (/ right (+ right wrong))
        (* 10000.0)
        Math/round
        (/ 100.0)
        (str "%"))))

(defn -main [& _]
  ;; Before attempting to parse file, ensure it's in valid form.
  (if (valid-file? dict)
    ;; Main interaction loop.
    (let [data (read-file dict)]
      (println "Geben Sie den richtigen deutsche Wort ein (oder „q“ zum Beenden).")
      (loop [score [0 0]]
        (let [english-german (next-word-article data)]
          (println "»" (:english english-german))
          (print "> ")
          (flush)
          (let [ans (read-line)]
            (if (= (s/lower-case ans) "q")
              (do
                (println "Auf Wiedersehen.")
                (println "Score:" (str (first score) "/" (apply + score))
                         (score->str score)))
              (if (= (:german english-german) ans)
                (do (println "Richtig!")
                    (recur [(inc (first score)) (second score)]))
                (do (println (str "Falsch!  Die Antwort war „"
                                  (:german english-german) "“!"))
                    (recur [(first score) (inc (second score))]))))))))
    (println "Dictionary file format invalid.")))
