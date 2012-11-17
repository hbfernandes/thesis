#!/bin/bash

cat $1 | iconv -t ISO-8859-1 -f UTF-8 -c |
ssh hilariof@lince.di.uevora.pt 'txt2visl' |
iconv -t UTF-8 -f ISO-8859-1 -c
