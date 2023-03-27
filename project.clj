(defproject ring-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.8.2"]
                 [babashka/fs "0.3.17"]
                 [net.sourceforge.jtransforms/jtransforms "2.4.0"]
                 [com.googlecode.soundlibs/mp3spi "1.9.5-1"]]
  :repl-options {:init-ns ring-app.core}
  :main ring-app.core)
