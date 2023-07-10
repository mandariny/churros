import os
import sys
import urllib.request
import datetime
import time
import json
from dotenv import load_dotenv
import requests
from bs4 import BeautifulSoup
from pymongo import MongoClient
import datetime

load_dotenv()

# naver search api key 설정
client_id = os.environ.get('ClientId')
client_secret = os.environ.get('ClientSecret')

# mongoDB 설정
mongo_host = os.environ.get('MongoHost')
mongo_port = int(os.environ.get('MongoPort'))
mongo_client = MongoClient(host=mongo_host, port=mongo_port)

db = mongo_client['newsdb']
collection = db['newsCol']
# collections = (db['politics'], db['social'], db['economy'], db['culture'], db['science'])

# [CODE 1]
def getRequestUrl(url):
    req = urllib.request.Request(url)
    req.add_header("X-Naver-Client-Id", client_id)
    req.add_header("X-Naver-Client-Secret", client_secret)

    try:
        response = urllib.request.urlopen(req)
        if response.getcode() == 200:
            print("[%s]Url Request Success" % datetime.datetime.now())
            return response.read().decode('utf-8')
    except Exception as e:
        print(e)
        print("[%s] Error for URL : %s" % (datetime.datetime.now(), url))
        return None


# [CODE 2]
def getNaverSearch(node, srcText, start, display):
    base = "https://openapi.naver.com/v1/search"
    node = "/%s.json" % node
    parameters = "?query=%s&start=%s&display=%s" % (urllib.parse.quote(srcText), start, display)

    url = base + node + parameters
    responseDecode = getRequestUrl(url)  # [CODE 1]

    if (responseDecode == None):
        return None
    else:
        return json.loads(responseDecode)


# [CODE 3]
def getPostData(post, jsonResult, cnt, category, lastidx):
    title = post['title']
    description = post['description']
    org_link = post['originallink']
    link = post['link']
    pDate = post['pubDate']
    full_text = ''
    img_src = ''
    try:
        full_text, img_src = getFullText(link)
    except:
        print("전문 가져오기 실패 : "+link)

    # pDate = datetime.datetime.strptime(post['pubDate'], '%a, %d, %b, %Y, %H:%M:%S+0900')
    # pDate = pDate.strftime('%Y-%m-%d %H:%M:%S')

    jsonResult.append({'_idx': cnt + lastidx, 'title': title, 'description': description,
                       'org_link': org_link, 'link': link, 'pDate': pDate, 'full_text' : full_text, 'img_src' : img_src, 'category' : category})
    return

def getFullText(url):
    news_url = 'https://n.news.naver.com/mnews/article/015/0004816674?sid=101'
    header = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36'}
    r = requests.get(url, headers=header)

    bs = BeautifulSoup(r.text, "lxml")

    elements = bs.select_one('#dic_area')
    img_src = elements.select_one('img')['data-src']

    return elements.text.strip(), img_src

# [CODE 0]
def main():
    keys = ['정치', '경제', '사회', '생활 문화', 'IT 과학']
    node = 'news'  # 크롤링한 대상
    cnt = 0
    length = 5

    last = list(collection.find().sort("_idx", -1))
    lastidx = 0
    lastDate = ''
    if last:
        lastidx = last[0]['_idx']

        # lastDate = datetime.datetime.strptime(last[0]['pDate'], '%a, %m %B %Y %H:%M:%S')
        # print(lastDate)

    for i in range(5):
        srcText = keys[i]
        jsonResult = []
        jsonResponse = getNaverSearch(node, srcText, 1, length)  # [CODE 2]
        total = jsonResponse['total']

        # while ((jsonResponse != None) and (jsonResponse['display'] != 0)):
        list(jsonResponse).reverse()
        for post in jsonResponse['items']:
            cnt += 1
            getPostData(post, jsonResult, cnt, srcText, lastidx)  # [CODE 3]

        # start = jsonResponse['start'] + jsonResponse['display']
            # jsonResponse = getNaverSearch(node, srcText, start, length)  # [CODE 2]

        print('전체 검색 : %d 건' % total)

        # with open('%s_naver_%s.json' % (srcText, node), 'w', encoding='utf8') as outfile:
        #     jsonFile = json.dumps(jsonResult, indent=4, sort_keys=True, ensure_ascii=False)
        #
        #     outfile.write(jsonFile)

        result = collection.insert_many(jsonResult)
        result.inserted_ids

        print("가져온 데이터 : %d 건" % (cnt))
        print('%s_naver_%s.json SAVED' % (srcText, node))


if __name__ == '__main__':
    main()