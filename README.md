# [Tabular][] [![Apache 2 badge][]](http://www.apache.org/licenses/LICENSE-2.0)

`Tabular` is a way to show data in tabular form.

## Setup

Add this to your sbt build definitions, such as in `build.sbt`:

    libraryDependencies += "com.dwijnand" %% "tabular" % "0.1.0"

For other build systems see the dependency information on Maven Central:

* For Scala 2.11: http://search.maven.org/#artifactdetails|com.dwijnand|tabular_2.11|0.1.0|jar
* For Scala 2.10: http://search.maven.org/#artifactdetails|com.dwijnand|tabular_2.10|0.1.0|jar

## Usage

1. Add `import tabular._`
1. Invoke:
   * `showkv` on your maps to view the key-value pairs
   * `showkvs` on your multimaps (maps where the value is a collection) to view the key-values pairs
   * `tabular` on your collections specifying the columns you want in tabular form
   * `showPs` on your collection of products to view their properties
   * `showM` on your matrix (a collection of collections) to view your matrix

Example:


## Dependencies

* Scala 2.11.x or 2.10.x

## Licence

Copyright 2015-2016 Dale Wijnand

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[tabular]: https://github.com/dwijnand/tabular
[Apache 2 badge]: http://img.shields.io/:license-Apache%202-red.svg
