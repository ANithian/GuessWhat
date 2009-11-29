#!/bin/sh

JRUBY_HOME=C:/applications/jruby-1.4.0
JRUBY_VER=1.4.0

rm -rf tmp_unpack
mkdir tmp_unpack
cd tmp_unpack
jar xf $JRUBY_HOME/jruby-complete*.jar
cd ..
mkdir jruby-core
mv tmp_unpack/org jruby-core/
mv tmp_unpack/com jruby-core/
mv tmp_unpack/jline jruby-core/
mv tmp_unpack/jay jruby-core/
mv tmp_unpack/jruby jruby-core/
cd jruby-core
jar cf ../jruby-core_${JRUBY_VER}.jar .
cd ../tmp_unpack
jar cf ../ruby-stdlib_${JRUBY_VER}.jar .
cd ..
rm -rf jruby-core
rm -rf tmp_unpack
mv jruby-core*.jar war/WEB-INF/lib
mv ruby-*.jar war/WEB-INF/lib