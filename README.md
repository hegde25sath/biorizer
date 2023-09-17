# biorizer
A text summarizer that also outputs a summary using bionic reading - which highlights certain segments of text to make it faster to read.

# Description

Works with pdf files (and probably only single page) only currently. Needs the PdfBox Java jar to run. Requires pandoc library to be installed. 

May have to change a few lines to work on your machine as it was quickly built for my personal MacOS. 

# Usage

Drag and drog a pdf with text to the root Java project directory named "test.pdf". Make sure pandoc is installed, and run the project. Should result in an "output.pdf" file that contains the summary of the input pdf 

# How it works

The summarizer built in Python uses the NLTK library. A very simple method is used. Text is processed and cleaned, tokenized, and the weighted frequency is found. Based on this, sentences are ranked and n sentences are outputted. 

# Future improvements and extensions 
1. Accept numerous input text files not just pdf
2. refactor code to be more generalizable and efficient, as well as more readable
3. explore abstractive summarization, not just extractive. 
