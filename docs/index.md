---
description: A scala (and zio) implementation of ULID specification

hide:
  - toc
  - navigation
---

# ZIO-ULID

*A Scala (and [ZIO](https://zio.dev)) implementation of [ULID specification](https://github.com/ulid/spec)*

![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.bilal-fazlani/zio-ulid_3?color=%23099C05&label=STABLE%20VERSION&server=https%3A%2F%2Foss.sonatype.org&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.bilal-fazlani/zio-ulid_3?color=skyblue&label=SNAPSHOT%20VERSION&logo=SNAPSHOT%20VERSION&server=https%3A%2F%2Foss.sonatype.org&style=for-the-badge)



## What is ULID?

ULID stands for "Universally Unique Lexicographically Sortable Identifier". Read more on [Official specification](https://github.com/ulid/spec) or [ZIO-ULID documentation](documentation/structure-encoding/)


## Installation

```scala
libraryDependencies += "com.bilal-fazlani" %% "zio-ulid" % "<VERSION>"
```

## Why ZIO-ULID?

While there are already a few implementations of ULID in Scala, I could not find one that fits natively in ZIO ecosystem. This library has the core logic from [airframe-ulid](https://wvlet.org/airframe/docs/airframe-ulid) implemented using FP and ZIO constructs.

## Features

- Pure. No exceptions or any other side effects
- Uses ZIO `Clock` for timestamps
- Uses ZIO `Random` for randomness
- Uses ZIO `Ref` for thread safety of state
- Uses `ZLayer` for dependency injection
- Returns `ZIO[ULIDGen, Nothing, ULID]`

---

[:material-book: Documentation](documentation/){ .md-button .md-button--primary }
[:material-github: Github](https://github.com/bilal-fazlani/zio-ulid){ .md-button }

---

#### Inspirations
- https://wvlet.org/airframe/docs/airframe-ulid (core logic)
- https://github.com/petitviolet/ulid4s

#### Tools and references used

- http://www.crockford.com/base32.html
- https://ulidgenerator.com/
- https://www.ulidtools.com/
- https://cryptii.com/pipes/crockford-base32
- https://currentmillis.com/
- https://stackoverflow.com/a/10813256/1365053
