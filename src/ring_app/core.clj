(ns ring-app.core
  (:require [clojure.java.shell :as sh]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [babashka.fs :as fs]
            [ring.middleware.resource :as resource]
            [clojure.pprint     :as pprint]))

(defonce server (atom nil))

(defn cents 
  "The pitch command takes a number of cents to shift.
  Outputs the shift for midi note n as a string."
  [n]
  (str  (if (< 59 n)
      (* 100 (- n 60))
      (- (* 100 (- 60 n))))))


(defn pitch
 "Takes an integer and filename (optional)
  and creates a copy transposed to midi note n.
  Outputs a file named `<f>-n.wav`" 
  ([n] 
  (str (:out (sh/sh "sox" "resources/audio/1.wav" 
                    (str "resources/audio/1-" n ".wav")
                    "pitch" (cents n)))
                    (str "wrote 1-" n ".wav" )))
  ([n f] 
  (str (:out (sh/sh "sox" (str "resources/audio/" f ".wav" )
                    (str "resources/audio/" f "-" n ".wav")
                    "pitch" (cents n)))
                    (str "wrote " f "-" n ".wav" ))))


(defn explode! 
  "Takes a `.wav` file and explodes it into a set of
  transpositions n notes before/after 60."
  [f n]
  (doseq [note (range (- 60 n) (+ 60 n))]
        (pitch note f)))

(def n 60)
(def f "1")


(comment
  (pitch n f)
  (explode! f 16)
(sh/sh "ls" "resources/audio")
  )


(defn audio [f]
  (str
  "<div>" f "<audio controls src=\"/" f "\"></audio>"
  "</div>"))

(defn app [req]
  (case (:uri req)
    "/" {:status 200
         :body "<h1>Homepage</h1>
                <ul>
                    <li><a href=\"/sounds\">Sound library</a></li>
                </ul>"
         :headers {"Content-Type" "text/html; charset=UTF-8"}}
    "/sounds" {:status 200
             :body (str "<html><body>" 
                        (apply str (map audio (sort (map fs/file-name (fs/glob "resources/audio" "*.wav")))))
                          "</body></html>")
             :headers {"Content-Type" "text/html; charset=UTF-8"}}
    {:status 404
     :body "Not found."
     :headers {"Content-Type" "text/plain"}}))

(defn start-server []
  (reset! server
          (jetty/run-jetty 
            (resource/wrap-resource
              (fn [req] (app req)) "audio")
              {:port 80 :join? false})))

(defn stop-server []
  (when-some [s @server] ;; check if there is an object in the atom
    (.stop s)            ;; call the .stop method
    (reset! server nil)));; overwrite the atom with nil

(defn -main []
  (start-server))

(comment
  (stop-server)
  )

