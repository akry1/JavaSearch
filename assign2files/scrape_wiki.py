# -*- coding: utf-8 -*-
import requests,re
from bs4 import BeautifulSoup
import datetime

PATH = 'https://en.wikibooks.org' 

home = PATH + '/wiki/Java_Programming'

content = requests.get(home)
soup = BeautifulSoup(content.text,"lxml")

listOfTopics = []
links = []

div= soup.find('div',id='mw-content-text')
topics = div.find_all('li')

for i in topics:
    topic = i.find_all('a')
    if len(topic)==2:
        listOfTopics.append(topic[1].text)
        links.append(topic[1]['href'])
    
for i,relativeLink in enumerate(links):
    fullpath = PATH + relativeLink
    content = requests.get(fullpath)
    soup = BeautifulSoup(content.text)
    div = soup.find('div',id='mw-content-text')
    text = div.text
    #break the text
    splits=[]
    try:
        text = '\n\n\n'.join(text.strip().split('\n\n\n')[2:-1])
        splits = text.split('[edit]')
    except:
        pass
    removeCharsFromName= re.sub('[^A-Za-z0-9 ]','',listOfTopics[i]).strip()
    if not splits:
        text = text.encode('utf8')
        newfile =  'wiki files/' + removeCharsFromName.strip() + '.txt'
        with open(newfile,'wb') as f:
            f.write(text)
    else:
        for content in splits:            
            content = content.encode('utf8')
            text = content.rsplit('\n',1)
            newfile =  'wiki files/' + re.sub('[^A-Za-z0-9 ]','',removeCharsFromName) + datetime.datetime.now().strftime('%d%H%M%S') + '.txt'
            removeCharsFromName = text[1].strip()
            if(len(text[0].strip()) > 100):
                fileData = re.sub('\n','<br />',text[0].strip())
                with open(newfile,'wb') as f:
                    f.write(fileData)