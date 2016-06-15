# -*- coding: utf-8 -*-
import requests,re
from bs4 import BeautifulSoup

PATH = 'https://docs.oracle.com/javase/tutorial/' 

home = PATH 

content = requests.get(home)
soup = BeautifulSoup(content.text)


links = []

ul= soup.find_all('ul','BlueArrows')

for parts in ul:
    topics = parts.find_all('li')
    for i in topics:
        topic = i.find('a')
        if topic:            
            link = topic['href'].split('/')
            links.append(link[0])
    
for relativeLink in links:
    try:
        fullpath = PATH + relativeLink + '/'
        content = requests.get(fullpath)
        soup = BeautifulSoup(content.text)
        div = soup.find('div',id='PageContent')
        links = div.find_all('p')
    except:
        print Exception.message
        continue
    for a in links:
        try:
            aTag1 = a.find('a')
            if not aTag1: continue
            ref = aTag1['href'].split('/')
            thirdLevel = fullpath + ref[0] + '/'
            lessons = requests.get(thirdLevel)
            soup1 = BeautifulSoup(lessons.text)
            tutorials = soup1.find('div',id='PageContent')
            tutorial_links = tutorials.find_all('li')
            for b in tutorial_links:
                try:
                    aTag2 = b.find('a')
                    if not aTag2: continue
                    ref2 = aTag2['href']            
                    finalLevel = thirdLevel + ref2
                    finalContent = requests.get(finalLevel)
                    soup2 = BeautifulSoup(finalContent.text)
                    data = soup2.find('div',id='PageContent')    
                    text = data.text.encode('utf8')
                    title = soup2.find('div',id='PageTitle').text
                    removeCharsFromName= re.sub('[^A-Za-z0-9 ]','',title) 
                    newfile =  'oracle files/' + removeCharsFromName.strip() + '.txt'
                    with open(newfile,'wb') as f:
                        f.write(text)
                except: 
                    print Exception.message
                    continue
        except: 
            print Exception.message
            continue
