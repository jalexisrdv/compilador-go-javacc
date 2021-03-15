#!/bin/bash

rm *.class
javacc go.jj
javac Go.java
java Go < /Users/jardv/Documents/Codigos/automatas/codigo.txt
