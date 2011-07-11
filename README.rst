==============
whatthehex.clj
==============
:Info: A hexadcimal color-guessing game written in Clojure
:Authors: Steve Challis (http://schallis.com)
:Requires: Leiningen, Compojure, Hiccup
:License: Distributed under the Eclipse Public License, the same as Clojure.

Usage
=====
To start playing straightaway, run a Ring server from Leiningen with::

$ lein ring server

Alternatively, you can interactively run the program from a REPL/Slime. You'll just need to load the source e.g. ::

user> (load whatthehex/core)
user> (ns whatthehex.core)
whatthehex.core> (gen-level {})

