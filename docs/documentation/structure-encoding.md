---
hide:
  - toc

description: Structure and Encoding of 128 bit ULID
---

# Structure and Encoding

ULID is a 128 bit number represented as a 26 character string. It is a combination of a timestamp and a random part.

## Binary Structure

![ULID Structure](../img/ulid-byte-structure.png#only-light){.m-img}
![ULID Structure](../img/ulid-byte-structure-dark.png#only-dark){.m-img}


![48-bit-structure](../img/48-bit-structure.png#only-light){.xs-img}
![48-bit-structure](../img/48-bit-structure-dark.png#only-dark){.xs-img}
![80-bit-structure](../img/80-bit-structure.png#only-light){.s-img}
![80-bit-structure](../img/80-bit-structure-dark.png#only-dark){.s-img}


The 48 bits form an Integer value and it's a reprensentation of the timestamp in milliseconds. The maximum value it can hold is 281474976710655 (2^48 - 1). That year 10,889 AD.

80 bits are for the random component.

## Encoding

These 128 bits are represented as string using a 5 bit encoding to form 26 characters. This encoding is called [Crockford's Base32](https://www.crockford.com/base32.html).

![Encoding](../img/string-structure.png#only-light){.m-img}
![Encoding](../img/string-structure-dark.png#only-dark){.m-img}

There are no special characters in the encoding. The encoding is case insensitive. Decoding of lower case letters is supported. At the time or encoding, all characters and ecoded as upper case. To avoid mistakes, characters `i`, `l`, `o` and `u` are not used while encoding.

While decoding, `o` and `O` are treated as `0`. Characters `i`, `I`, `l` and `L` are treated as `1`. `u` / `U` is considered invalid.

| Char              | Binary  | Decimal | HEX  |
| ----------------- | ------- | ------- | ---- |
| `0`, `o/O`        | `00000` | `0 `    | `0`  |
| `1`, `i/I`, `l/L` | `00001` | `1 `    | `1`  |
| `2`               | `00010` | `2 `    | `2`  |
| `3`               | `00011` | `3 `    | `3`  |
| `4`               | `00100` | `4 `    | `4`  |
| `5`               | `00101` | `5 `    | `5`  |
| `6`               | `00110` | `6 `    | `6`  |
| `7`               | `00111` | `7 `    | `7`  |
| `8`               | `01000` | `8 `    | `8`  |
| `9`               | `01001` | `9 `    | `9`  |
| `a/A`             | `01010` | `10`    | `A`  |
| `b/B`             | `01011` | `11`    | `B`  |
| `c/C`             | `01100` | `12`    | `C`  |
| `d/D`             | `01101` | `13`    | `D`  |
| `e/E`             | `01110` | `14`    | `E`  |
| `f/F`             | `01111` | `15`    | `F`  |
| `g/G`             | `10000` | `16`    | `10` |
| `h/H`             | `10001` | `17`    | `11` |
| `j/J`             | `10010` | `18`    | `12` |
| `k/K`             | `10011` | `19`    | `13` |
| `m/M`             | `10100` | `20`    | `14` |
| `n/N`             | `10101` | `21`    | `15` |
| `p/P`             | `10110` | `22`    | `16` |
| `q/Q`             | `10111` | `23`    | `17` |
| `r/R`             | `11000` | `24`    | `18` |
| `s/S`             | `11001` | `25`    | `19` |
| `t/T`             | `11010` | `26`    | `1A` |
| `v/V`             | `11011` | `27`    | `1B` |
| `w/W`             | `11100` | `28`    | `1C` |
| `x/X`             | `11101` | `29`    | `1D` |
| `y/Y`             | `11110` | `30`    | `1E` |
| `z/Z`             | `11111` | `31`    | `1F` |

