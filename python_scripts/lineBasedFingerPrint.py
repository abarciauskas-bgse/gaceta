"""
lineBasedFingerPrint.py written by Raphael Cohen, Computer Science Department ,Ben-Gurion University

This module takes a directory and produces a subset of the files in that directory (in a list) with an upper bound on similarity between two files.
USAGE: python lineBasedFingerPrint.py [MaxSimilarity] [FingerPrintLen]


"""

import os
import re
import os.path
from collections import defaultdict
import sys


"""
get arguments - order is fixed
"""

if len(sys.argv) < 3:
	print "USAGE: python lineBasedFingerPrint.py [directory] [MaxSimilarity:default-0.2] [FingerPrintLen:default-30]"

dir = sys.argv[1]
try:
	similarityMax=float(sys.argv[2])
except:
	similarityMax = 0.2

try:
	fingerprintLen = int(sys.argv[3])
except:
	fingerprintLen = 30 #20-40 are good lengths

print "running on dir:",dir,"maximum similarity:",similarityMax,"Length of fingerprint:",fingerprintLen

figerprints = {}
fileVectors = defaultdict(list)
buckets = defaultdict(list)

counter = 0
files = os.listdir(dir)
fingerCoutner = 0
for file in files:
    counter+=1
    data = open(os.path.normpath(dir+"/"+file),"r").readlines() 
    for l in data:
        chunks = len(l)/fingerprintLen
        for i in range(0,chunks):
            beginF = i*fingerprintLen
            endF = beginF + fingerprintLen
            fprint = l[beginF:endF]
            if not fprint  in figerprints:
                figerprints[fprint] = [fingerCoutner,0]
                fingerCoutner += 1
            figerprints[fprint][1]+= 1
            fileVectors[file].append(figerprints[fprint][0])


rejects = 0
try:
    counter =0
    for file in fileVectors:
        fileVectors[file] = list(set(fileVectors[file]))

except Exception as inst:
    pass


fout = open("document-pairs-"+str(similarityMax)+"-"+str(fingerprintLen),"w")

slist = []    
for file in fileVectors:
    slist.append([os.path.getsize(os.path.normpath(dir+"/"+file)),file])
slist.sort(reverse=True)
for item in slist:
    currFile = item[1]
    myFingers = set(fileVectors[currFile])
    suspects = defaultdict(int)
    for bucket in myFingers:
        if len(buckets[bucket]) > 0:
            for item in buckets[bucket]:
                suspects[item] +=1
    flag = True
    for suspect in suspects:
        if suspects[suspect] > len(myFingers) * similarityMax:
            fout.write(str(currFile)+"\t"+suspect+"\t"+str(float(suspects[suspect])/len(myFingers))+"\n")
            flag = False
            rejects +=1
            break
    if flag:
        for bucket in myFingers:
            buckets[bucket].append(currFile)
    counter+=1
myCorpus = {}
for bucket in buckets:
    for item in buckets[bucket]:
        myCorpus[item] = 1


fout2 = open("corpusSubset-"+str(similarityMax)+"-"+str(fingerprintLen),"w")
for item in myCorpus:
	fout2.write(str(item)+"\n")

  
