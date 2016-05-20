filename = '1-2000_6-splitted.txt'
f = open("text_files/" + filename, 'r')

string = f.read()
sentences = string.split('\n\n')

for i in range(len(sentences)):
    sentence = sentences[i]
    f = open('text_files/generated/' + str(i) + '-' + filename, 'w+')
    f.write(sentence)
    f.close()

# python lineBasedFingerPrint.py text_files/generated 0.2 20
