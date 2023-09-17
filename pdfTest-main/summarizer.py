# imports
import nltk
import heapq
import re
import bs4 as bsoup
from pypdf import PdfReader, PdfWriter

#===================
# summarize text -> bionic highlighting
# Java program first takes some input text, then the python script is called and it takes the file and summarizes it
#===================

# extracting text from pdf
reader = PdfReader("test.pdf")
input_text = reader.pages[0].extract_text()

# preprocessing steps
input_text = re.sub(r'\s+', ' ', input_text)
# replacing any non alphabetical char with empty string
formatted_input_text = re.sub('[^a-zA-Z]', ' ', input_text)
formatted_input_text = re.sub(r'\s+', ' ', formatted_input_text)

# tokenization
sentence_list = nltk.sent_tokenize(input_text)

# finding weighted frequency of occurence

# words that can be removed from consideration (stuff like "the")
stopwords = nltk.corpus.stopwords.words('english')

# storing in a dictionary
word_frequencies = {}
for word in nltk.word_tokenize(formatted_input_text):
    if word not in stopwords:
        if word not in word_frequencies.keys():
            word_frequencies[word] = 1
        else:
            word_frequencies[word]+=1

# calculating weighted frequency
max_freq = max(word_frequencies.values())

for word in word_frequencies.keys():
    word_frequencies[word] = (word_frequencies[word]/max_freq)

# calculating sentenc scores for each sentence
sentence_scores = {}
for s in sentence_list:
    for word in nltk.word_tokenize(s.lower()):
        if word in word_frequencies.keys():
            # excluding long sentences from the summary
            if len(s.split(' ')) < 40:
                sentence_scores[s] = word_frequencies[word]
            else:
                sentence_scores[s] += word_frequencies[word]

# obtaining the summary output
summary_sentences = heapq.nlargest(int((len(sentence_scores) / 2.5 )), sentence_scores, key=sentence_scores.get)
summary = ' '.join(summary_sentences)

# saving output
# pdf_writer = PdfWriter()
# for p in reader:
#     pdf_writer.add_page(p)
# pdf_writer.write("output.pdf")

file = open("output.txt", "x")
file.write(summary)
file.close()
