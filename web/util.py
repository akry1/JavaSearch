# -*- coding: utf-8 -*-
"""
Created on Tue Feb 23 03:26:39 2016

@author: aravind

Simple program to convert response from rest to csv for static page
"""

import requests
import pandas as pd

l = ['result']

inp = pd.read_csv('data.csv',header=0,dtype=str)


for i in inp['text']:
    data = requests.get('http://localhost:8080/search?query='+i)
    l.append(data.text)
        
df = pd.DataFrame(l)
df.to_csv('static.csv',index=None,header=None,encoding='utf8')
        