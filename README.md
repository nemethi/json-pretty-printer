# JSON Pretty-printer
A small Java library for pretty-printing JSON data. 

## About
This project is free and unencumbered public domain software.
Feel free (and I encourage you) to do anything you want with it. You do not even have to give any credit.

Simply copy and paste the classes into your project. Modify them to your liking and to fit your needs.
The project also contains unit and integration tests which can be useful to you.

Note, that the pretty-printer presumes that the passed JSON data is valid.
It does not perform any validation on the input. 

## Motivation
In one of my hobby projects I generate JSON data, and I wished to pretty-print it.
Because I wanted to keep external dependencies to a minimum, it seemed as an overkill to use any of the big
JSON libraries (e.g. Jackson, Gson, even JSON-Java (org.json)) just for that.
I quickly looked around the web, and I found no general algorithm or small library that I could use.
That is why I created this small utility to fill this gap.

As I mentioned above, the pretty-printer does not validate JSON data. Why? 
Because this library is primarily for my other project where I am generating data: 
and I make sure that only valid data is generated. If I also wanted to validate data I would have to write
a full-fledged JSON parser — which, again, seemed as an overkill. 
(Even generating one with ANTLR or other similar tools.)

## Prerequisites
The project is in Java 11, but with some minor modifications it can be backported to Java 8.
The tests use JUnit 5.

## Contact
Gábor Némethi - [nemethi](https://github.com/nemethi)

Project - https://github.com/nemethi/json-pretty-printer

## (Un)license
This project is released into the public domain. See the [UNLICENSE](UNLICENSE) file for more information.


