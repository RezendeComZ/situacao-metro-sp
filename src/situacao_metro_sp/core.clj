(ns situacao-metro-sp.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [hickory.core :as html]))

(def VIAQUATRO-URL "https://www.viaquatro.com.br/generic/Main/LineStatus")
(def CPTM-URL      "https://www.cptm.sp.gov.br")

(def mapa-via-quatro
  (let [content (http/get VIAQUATRO-URL)
        body    (:body content)]
    (json/parse-string body true)))


(def mapa-cptm
  (let [content (http/get CPTM-URL)
        body    (:body content)
        parse   (html/as-hiccup (html/parse body))
        cptm    (vec parse)]
    ;; ! Ainda não fechou no escopo da situação das linhas. E ver outra forma de não ficar repetindo o nth, talvez um thread da vida? Ou localizando por string
    ;; ! flattern e "find"?
    (nth (nth (nth (nth (nth (nth (second cptm) 4) 3) 17) 5) 7) 3)))

(defn -main
  [& args]
  (prn "Via Quatro:")
  (prn mapa-via-quatro)

  (prn "CPTM:")
  (prn mapa-cptm))
