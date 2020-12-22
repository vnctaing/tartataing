(defproject tartataing "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.10.0"]
                 [cljs-ajax "0.8.0"]
                 [clojure.java-time "0.3.2"]
                 [com.cognitect/transit-clj "1.0.324"]
                 [clj-commons/cljss "1.6.4"]
                 [com.fasterxml.jackson.core/jackson-core "2.11.2"]
                 [com.fasterxml.jackson.core/jackson-databind "2.11.2"]
                 [com.google.javascript/closure-compiler-unshaded "v20200504" :scope "provided"]
                 [cprop "0.1.17"]
                 [expound "0.8.5"]
                 [funcool/struct "1.4.0"]
                 [luminus-transit "0.1.2"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [luminus-jetty "0.2.0"]
                 [markdown-clj "1.10.5"]
                 [metosin/jsonista "0.2.6"]
                 [metosin/muuntaja "0.6.7"]
                 [metosin/reitit "0.5.5"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.8.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773" :scope "provided"]
                 [org.clojure/core.async "1.2.603"]
                 [org.clojure/google-closure-library "0.0-20191016-6ae1f72f" :scope "provided"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.40"]
                 [reagent "0.10.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-servlet "1.7.1"]
                 [selmer "1.12.28"]
                 [thheller/shadow-cljs "2.10.17" :scope "provided"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot tartataing.core

  :plugins [[lein-shadow "0.2.0"]
            [lein-uberwar "0.2.1"]]
  :sassc    [{:src          "src/scss/main.scss"
              :output-to    "dist/main.css"
              :style        "compressed"}]
  :tailwind {:tailwind-dir "src/scss/tailwind"
             :output-dir   "src/scss"
             :tailwind-config  "tailwind.config.js" ;; tailwind.config.js is the default value 
             :styles [{:src "main.scss"
                       :dst "main.scss"}]}
  :uberwar {:handler tartataing.handler/app
   :init    tartataing.handler/init
   :destroy tartataing.handler/destroy
   :name    "tartataing.war"}

  :clean-targets ^{:protect false}
  [:target-path "target/cljsbuild"]
  :shadow-cljs
  {:nrepl {:port 7002}
   :builds
   {:app
    {:target     :browser
     :output-dir "target/cljsbuild/public/js"
     :asset-path "/js"
     :modules    {:app {:entries [tartataing.app]}}
     :devtools   {:watch-dir "resources/public"}}
    :test
    {:target    :node-test
     :output-to "target/test/test.js"
     :autorun   true}}}

  :npm-deps []
  :npm-dev-deps [[xmlhttprequest "1.8.0"]]

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks  ["compile" ["shadow" "release" "app"]]

             :aot            :all
             :uberjar-name   "tartataing.jar"
             :source-paths   ["env/prod/clj"  "env/prod/cljs"]
             :resource-paths ["env/prod/resources"]}

   :dev  [:project/dev :profiles/dev]
   :test [:project/dev :project/test :profiles/test]

   :project/dev {:jvm-opts     ["-Dconf=dev-config.edn"]
                 :dependencies [[binaryage/devtools "1.0.2"]
                                [cider/piggieback "0.5.0"]
                                [directory-naming/naming-java "0.8"]
                                [pjstadig/humane-test-output "0.10.0"]
                                [prone "2020-01-17"]
                                [ring/ring-devel "1.8.1"]
                                [ring/ring-mock "0.4.0"]]
                 :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                [jonase/eastwood "0.3.5"]]

                 :source-paths   ["env/dev/clj"  "env/dev/cljs" "test/cljs"]
                 :resource-paths ["env/dev/resources"]
                 :repl-options   {:init-ns user
                                  :timeout 120000}
                 :injections     [(require 'pjstadig.humane-test-output)
                                  (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts       ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}

   :profiles/dev  {}
   :profiles/test {}})
