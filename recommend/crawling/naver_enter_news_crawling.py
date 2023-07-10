import os
import requests
from dotenv import load_dotenv
from bs4 import BeautifulSoup
from pymongo import MongoClient
from pymongo.errors import BulkWriteError
import datetime
# from tokenizer import tokenstart
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s|%(levelname)s|%(message)s')

load_dotenv()

# mongoDB 설정
mongo_host = os.environ.get('MongoHost')
mongo_port = int(os.environ.get('MongoPort'))
mongo_user = os.environ.get('MongoUser')
mongo_passwd = os.environ.get('MongoPasswd')
mongo_db_name = os.environ.get('MongoDbName')
mongo_admin_db = os.environ.get('MongoAdminDb')
mongo_client = MongoClient(host=mongo_host, port=mongo_port, username=mongo_user, password=mongo_passwd, authSource=mongo_admin_db)

db = mongo_client[mongo_db_name]
collection = db['newsCol']
index_collection = db['indexCounter']
# collections = (db['politics'], db['social'], db['economy'], db['culture'], db['science'])

# https://entertain.naver.com/now?sid=221
topics = {
    '연예가화제' : '221',
    '방송/TV' : '224',
    '드라마' : '225',
    '뮤직' : '7a5',
    '해외연예' : '309'
}

weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

def make_log(level, msg):
    now = datetime.datetime.now()
    weeknum = now.weekday()
    log_date = now.strftime("%Y-%m-%d %H:%M:%S ") + weekdays[weeknum] + " " + level + " " + msg
    return log_date

def getRequestResponse(url):
    header = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36'}
    r = requests.get(url, headers=header)

    if (r == None):
        return None
    else:
        return r

def getNaverNewsList(sid1):
    base = "https://entertain.naver.com/now"
    parameters = "?sid=%s" % (sid1)
    url = base + parameters

    return getRequestResponse(url)

def getDetailData(url):
    response = getRequestResponse(url)
    bs = BeautifulSoup(response.text, "lxml")

    title = bs.select_one('.end_tit').text.strip()
    pd = bs.select_one(".article_info > .author").text.split(' ')
    publish_date = datetime.datetime.strptime(pd[0].replace('기사입력', '') + pd[2], '%Y.%m.%d.%H:%M')
    if(pd[1] == '오후'):
        publish_date += datetime.timedelta(hours=12)

    full_text = bs.select_one("#articeBody").text.strip()
    img_src = ''

    # datetime.datetime.strptime(publish_date, '%Y-%m-%d %H:%M:%S')

    try:
        img_src = bs.select_one("#img1")['src']
    except:
        # print("이미지가 존재하지 않는 기사 : ", url)
        pass

    return title, publish_date, full_text, img_src

def getPostData(response, json_result, cat1, cat2, cnt, lastlink, except_count):
    bs = BeautifulSoup(response.text, "lxml")

    for news in bs.select_one('#newsWrp > ul').select('li'):
        try:
            description = news.select_one('.tit_area').select_one('.summary').text.strip()
            press = news.select_one('.tit_area').select_one('.press').text.split('\t')[0].strip()
            link = 'https://entertain.naver.com/' + news.select_one('.tit_area').select_one('a')['href']

            if lastlink == link:
                break

            title, publish_date, full_text, img_src = getDetailData(link)

            json_result.append({'_idx': 0, 'cat1': cat1, 'cat2': cat2, 'title': title, 'description': description,
                                'press': press, 'link': link, 'publish_date': publish_date, 'full_text': full_text,
                                'img_src': img_src})
        except Exception as e:
            # print("크롤링 중 예외 발생 : ", e)
            # msg = "CRAWLING " + cat1 + " " + cat2 + " " + link
            # print(make_log("ERROR", msg), '[', e, ']')
            logging.error(f'CRAWLING|{cat1}|{cat2}|{e}')
            except_count += 1

    json_result.reverse()
    for result in json_result:
        cnt += 1
        result['_idx'] = cnt

    return cnt, except_count

def crawlingGeneralNews(lastcounter, except_count):
    for topic in topics:
        # start_time = datetime.datetime.now()
        # start_count = lastcounter
        sid1 = topics[topic]

        json_result = []
        response = getNaverNewsList(sid1)

        if not response:
            continue

        lastlink = ''
        lastindex = index_collection.find_one({"cat1": "연예", "cat2": topic})

        if lastindex:
            topic_last_data = list(collection.find({"_idx": lastindex['counter']}))
        else:
            topic_last_data = list(collection.find({"cat1": "연예", "cat2": topic}).sort("_idx", -1))

        if topic_last_data:
            lastlink = topic_last_data[0]['link']

        lastcounter, except_count = getPostData(response, json_result, '연예', topic, lastcounter, lastlink, except_count)
        #
        # with open('naver_enter_news.json', 'w', encoding='utf8') as outfile:
        #     jsonFile = json.dumps(json_result, indent=4, sort_keys=True, ensure_ascii=False)
        #
        #     outfile.write(jsonFile)

        if json_result:
            try:
                if lastindex:
                    index_collection.update_one({"cat1": "연예", "cat2": topic}, {"$set":{"counter": lastcounter}})
                else:
                    index_collection.insert_one({"cat1": "연예", "cat2": topic, "counter" : lastcounter})

                result = collection.insert_many(json_result)
                result.inserted_ids
                # print(make_log("INFO", "start 연예 " + topic + " " + start_time.strftime("%H:%M:%S")))
                # print(make_log("INFO", "success 연예 " + topic + " " + str(lastcounter - start_count)))
                # print(make_log("INFO", "fail 연예 " + topic + " " + str(except_count)))
            except BulkWriteError as bwe:
                # print(make_log("ERROR", "DB 연예 " + topic+ " BulkWriteError"), '[', bwe.details, ']')
                logging.error(f'DB|연예|{topic}|BulkWriteError|{bwe.details}')
            except Exception as e:
                # print(make_log("ERROR", "DB 연예 " + topic), '[', e, ']')
                logging.error(f'DB|연예|{topic}|{e}')
    return lastcounter, except_count

def main():
    start_time = datetime.datetime.now()
    lastcounter = collection.estimated_document_count()
    startcounter = lastcounter
    except_count = 0

    lastcounter, except_count = crawlingGeneralNews(lastcounter, except_count)
    # print(make_log("INFO", "start 연예 " + start_time.strftime("%H:%M:%S")))
    # print(make_log("INFO", "success 연예 " + str(lastcounter - startcounter)))
    # print(make_log("INFO", "fail 연예 " + str(except_count)))
    logging.info(f'start|연예|{start_time.strftime("%H:%M:%S")}')
    logging.info(f'success|연예|{lastcounter - startcounter}')
    logging.info(f'fail|연예|{except_count}')
    # if startcounter < lastcounter:
    #     tokenstart(startcounter, lastcounter)

if __name__ == '__main__':
    main()
