Inkling
=======

Inkling is a stand-alone version of [sbt]'s incremental compiler.

[sbt]: http://github.com/harrah/xsbt


Build
-----

Inkling is built using sbt 0.11.3. To create the full distribution:

    sbt dist/create

Published distributions can be found in the [inkling repo].

[inkling repo]: http://repo.typesafe.com/typesafe/inkling/com/typesafe/inkling/dist/


Options
-------

To get information about options run ``inkling -help``.

### Compile

As for ``scalac`` the main options for compiling are ``-classpath`` for
specifying the classpath elements, and ``-d`` for selecting the output
directory. Anything passed on the command-line that is not an option is
considered to be a source file.

### Scala

To specify which Scala version to compile with, use either the ``-scala-home`` or
``-scala-path`` options.

Using ``-scala-home`` point to the base directory of a Scala distribution (which
needs to contain a ``lib`` directory with the Scala jars).

Using ``-scala-path`` the compiler, library, and any extra jars (like scala-reflect)
can be listed directly as a path.

If no options are passed to locate a version of Scala then Scala 2.9.2 is used
by default (which is bundled with inkling).

To pass options to scalac simply prefix with ``-S``. For example, deprecation
warnings can be enabled with ``-S-deprecation``.

### Java

To select a different ``javac`` to compile Java sources, use the ``-java-home``
option. To pass options to javac, prefix with ``-J``.

If only Java sources are being compiled then the ``-java-only`` option can be
added to avoid the Scala library jar being automatically added to the classpath.

If mixed Java and Scala sources are being compiled then the compile order can be
specified with ``-compile-order``, where the available orders are ``Mixed``,
``JavaThenScala``, or ``ScalaThenJava``. The default order is ``Mixed``.

### Nailed

Inkling comes with built-in [Nailgun] integration. Running with Nailgun provides
inkling as a server, communicating commands via a client, keeping cached
compilers in a warm running JVM and avoiding startup and load times.

To run inkling as a build daemon add the ``-nailed`` option to all commands, or
``alias inkling="inkling -nailed"``.

Nailgun client binaries for common platforms are bundled with inkling. If an
``ng`` client is on the current path then this will be used instead.

To shutdown the inkling server run ``inkling -nailed -shutdown``. To list
currently cached inkling compilers use ``inkling -nailed -status``.

[Nailgun]: http://www.martiansoftware.com/nailgun

### Logging

The log level can be set directly with ``-log-level debug|info|warn|error``. Or
to set to debug use ``-debug``. To silence all logging use ``-quiet``.

### Analysis

The analysis used to determine which files to incrementally recompile is stored
in a file. The default location for the analysis cache is relative to the output
directory. To specify a different location for the analysis cache use the
``-analysis-cache`` option. When compiling multiple projects, and the analysis
cache is not at the default location, then a mapping from output directory to
cache file for any upstreams projects should also be provided with the
``-analysis-map`` option.


License
-------

Copyright 2012 Typesafe, Inc.

Licensed under the [Apache License, Version 2.0][apache2] (the "License"); you
may not use this software except in compliance with the License. You may obtain
a copy of the License at:

[http://www.apache.org/licenses/LICENSE-2.0][apache2]

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

[apache2]: http://www.apache.org/licenses/LICENSE-2.0