# Unmeta Gradle Plugin for Android

<p>

[![build](https://img.shields.io/github/actions/workflow/status/axzae/unmeta-gradle-plugin/pre-merge.yaml?branch=main)][actions]
[![plugin portal](https://img.shields.io/gradle-plugin-portal/v/com.axzae.unmeta)][pluginportal]

</p>

A gradle plugin for Android Project to remove all Kotlin [`@DebugMetadata`][debugmetadata] annotations from the compiled
classes.

Kotlin Coroutines' [`@DebugMetadata`][debugmetadata] annotations [are not fully processed by ProGuard / R8][1] and
contain un-obfuscated symbol information, both in binary and plain text forms. This information can be used to more
easily reverse engineer your code.

This plugin allows removing all Kotlin [`@DebugMetadata`][debugmetadata] annotations from generated class files in *
*release** build.

## Usage

In order to make Unmeta work with your project you have to apply the Unmeta Gradle plugin to the project. Please notice
that the Unmeta plugin must be applied after the Android plugin.

#### Plugin DSL

```kotlin
// Project build.gradle.kts
plugins {
  id("com.axzae.unmeta") version "1.0.0" apply false
}

// Module(app) build.gradle.kts
plugins {
  id("com.android.application")
  // ...
  id("com.axzae.unmeta")
}

```

#### Legacy Plugin Application

```kotlin
// Project build.gradle.kts
buildscript {
  dependencies {
    classpath("com.axzae.unmeta:com.axzae.unmeta.gradle.plugin:1.0.0")
  }
}
```

## Configuration

```kotlin
unmeta {
  isEnabled.set(true) // Default: true
  verbose.set(true) // Default: false
}
```

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.

## License

    Copyright (c) 2023 Axzae

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

[1]: https://github.com/Kotlin/kotlinx.coroutines/issues/2267#issuecomment-698826645

[debugmetadata]: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/src/kotlin/coroutines/jvm/internal/DebugMetadata.kt
[pluginportal]: https://plugins.gradle.org/plugin/com.axzae.unmeta
[actions]: https://github.com/axzae/unmeta-gradle-plugin/actions
