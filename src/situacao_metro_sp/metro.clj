(ns situacao-metro-sp.metro
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def VIAQUATRO-URL "https://www.viaquatro.com.br/generic/Main/LineStatus")

(def mapa-via-quatro
  (try
    (let [content (http/get VIAQUATRO-URL)
          body    (:body content)]
      (json/parse-string body true))
    (catch Exception e
      (str "Erro ao capturar dados do Metrô: " e))))

(def mapa-linha-amarela
  {:nome     "Amarela" 
   :numero   4
   :situacao (:Status (:CurrentLineStatus mapa-via-quatro))})

(defn linha-metro-padronizada [linha]
  {:nome     (:Color linha)
   :numero   (Integer/parseInt (:Id linha))
   :situacao (:StatusMetro linha)})

(def mapa-demais-linhas
  (map linha-metro-padronizada (:ListLineStatus (:StatusMetro mapa-via-quatro))))

(def mapa-padronizado-metro
  (remove nil? (conj mapa-demais-linhas
        (when (:situacao mapa-linha-amarela)
          mapa-linha-amarela))))