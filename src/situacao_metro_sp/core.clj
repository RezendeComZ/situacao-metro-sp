(ns situacao-metro-sp.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as string]
            [hickory.core :as html]))

(def VIAQUATRO-URL "https://www.viaquatro.com.br/generic/Main/LineStatus")
(def CPTM-URL      "https://www.cptm.sp.gov.br")

(def mapa-via-quatro
  (let [content (http/get VIAQUATRO-URL)
        body    (:body content)]
    (json/parse-string body true)))

;; CPTM

(def vetor-html-cptm ;; TODO: try and catch
  (let [content (http/get CPTM-URL)
        body    (:body content)
        parse   (html/as-hiccup (html/parse body))]
    (flatten (vec parse))))

(defn situacao-linha-cptm [linha]
  (nth vetor-html-cptm (+ (.indexOf vetor-html-cptm linha) 3)))

(defn mapa-unica-linha-cptm [linha]
  {(keyword (string/lower-case linha)) {:nome linha
                                        :situacao (situacao-linha-cptm linha)}})

(def mapa-todas-linhas-cptm
  (merge (map mapa-unica-linha-cptm ["CORAL" "RUBI" "TURQUESA"
                                     "SAFIRA" "JADE" "LILÁS"
                                     "DIAMANTE" "ESMERALDA"])))

(defn -main
  [& args]
  (prn "Via Quatro:")
  (prn mapa-via-quatro)

  (prn "CPTM:")
  (prn mapa-todas-linhas-cptm))

