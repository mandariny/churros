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

topics = {
    '정치': '100',
    '경제': '101',
    '사회': '102',
    '생활/문화': '103',
    'IT/과학': '105',
}

topic_detail = {
    '정치': {
        '대통령실': '264',
        '국회/정당': '265',
        '북한': '268',
        '행정': '266',
        '국방/외교': '267',
        '정치일반': '269'
    },
    '경제': {
        '금융': '259',
        '증권': '258',
        '산업/재계': '261',
        '증기/벤처': '771',
        '부동산': '260',
        '글로벌 경제': '262',
        '생활경제': '310',
        '경제 일반': '263'
    },
    '사회': {
        '사건사고': '249',
        '교육': '250',
        '노동': '251',
        '언론': '254',
        '환경': '252',
        '지역': '256',
        '인물': '276',
        '사회 일반': '257',
    },
    '생활/문화': {
        '생활문화 일반': '245',
    },
    'IT/과학': {
        '모바일': '731',
        '인터넷/SNS': '226',
        '통신/뉴미디어': '227',
        'IT 일반': '230',
        '보안/해킹': '732',
        '컴퓨터': '283',
        '게임/리뷰': '229',
        '과학 일반': '228'
    }
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

def getNaverNewsList(sid1, sid2):
    base = "https://news.naver.com/main/list.naver"
    parameters = "?mode=LS2D&mid=shm&sid1=%s&sid2=%s" % (sid1, sid2)
    url = base + parameters

    return getRequestResponse(url)

def getDetailData(url):
    response = getRequestResponse(url)
    bs = BeautifulSoup(response.text, "lxml")

    title = bs.select_one('#title_area').text.strip()
    publish_date = bs.select_one('.media_end_head_info_datestamp_time')['data-date-time']
    publish_date = datetime.datetime.strptime(publish_date, '%Y-%m-%d %H:%M:%S')
    full_text = bs.select_one('#dic_area').text.strip()
    img_src = ''

    try:
        img_src = bs.select_one('#img1')['data-src']
    except:
        # print("이미지가 존재하지 않는 기사 : ", url)
        pass

    return title, publish_date, full_text, img_src

def getPostData(response, json_result, cat1, cat2, cnt, lastlink, except_count):
    bs = BeautifulSoup(response.text, "lxml")
    flag = False
    for type in ['.type06_headline', '.type06']:
        if flag : break
        for news in bs.select_one(type).select('li'):
            try:
                description = news.select_one('dd').select_one('.lede').text
                press = news.select_one('dd').select_one('.writing').text
                link = news.select_one('dt').select_one('a')['href']

                if lastlink == link:
                    flag = True
                    break

                title, publish_date, full_text, img_src = getDetailData(link)

                json_result.append({'_idx': 0, 'cat1': cat1, 'cat2': cat2, 'title': title, 'description': description,
                                    'press': press, 'link': link, 'publish_date': publish_date, 'full_text': full_text,
                                    'img_src': img_src})
            except Exception as e:
                # msg = "CRAWLING " + cat1 + " " + cat2 + " " + link
                # print(make_log("ERROR", msg), '[', e, ']')
                logging.error(f'CRAWLING|{cat1}|{cat2}|{e}')
                except_count += 1

    json_result.reverse()
    for result in json_result:
        cnt += 1
        result['_idx'] = cnt

    return cnt, except_count

def crawlingGeneralNews(lastcounter):
    for topic in topics.keys():
        sid1 = topics[topic]
        # print(f"[{topic}] : 크롤링 시작...")
        start_time = datetime.datetime.now()
        startcounter = lastcounter
        except_count = 0

        for detail in topic_detail[topic]:
            # print(f"[{topic} - {detail}] : 크롤링 시작...")
            # start_time = datetime.datetime.now()
            # start_count = lastcounter

            json_result = []
            sid2 = topic_detail[topic][detail]

            response = getNaverNewsList(sid1, sid2)

            if not response:
                continue

            lastlink = ''
            lastindex = index_collection.find_one({"cat1" : topic, "cat2" : detail})

            if lastindex:
                topic_last_data = list(collection.find({"_idx": lastindex['counter']}))
            else :
                topic_last_data = list(collection.find({"cat1": topic, "cat2": detail}).sort("_idx", -1))

            if topic_last_data:
                lastlink = topic_last_data[0]['link']

            lastcounter, except_count = getPostData(response, json_result, topic, detail, lastcounter, lastlink, except_count)


            # print(f"[{topic} - {detail}] : {newidx-lastidx}개 크롤링 완료...")

            if json_result:
                try:
                    if lastindex:
                        index_collection.update_one({"cat1": topic, "cat2": detail}, {"$set": {"counter": lastcounter}})
                    else:
                        index_collection.insert_one({"cat1": topic, "cat2": detail, "counter" : lastcounter})
                    result = collection.insert_many(json_result)
                    result.inserted_ids
                    # print(make_log("INFO", "start " + topic + " " + detail + " " + start_time.strftime("%H:%M:%S")))
                    # print(make_log("INFO", "success " + topic + " " + detail + " " + str(lastcounter - start_count)))
                    # print(make_log("INFO", "fail " + topic + " " + detail + " " + str(except_count)))
                except BulkWriteError as bwe:
                    # print(make_log("ERROR", "DB " + topic + " " + detail + " BulkWriteError"), '[', bwe.details, ']')
                    logging.error(f'DB|{topic}|{detail}|BulkWriteError|{bwe.details}')
                except Exception as e:
                    # print(make_log("ERROR", "DB " + topic + " " + detail), '[', e, ']')
                    logging.error(f'DB|{topic}|{detail}|{e}')

        # print(make_log("INFO", "start " + topic + " " + start_time.strftime("%H:%M:%S")))
        # print(make_log("INFO", "success " + topic + " " + str(lastcounter - startcounter)))
        # print(make_log("INFO", "fail " + topic + " " + str(except_count)))
        logging.info(f'start|{topic}|{start_time.strftime("%H:%M:%S")}')
        logging.info(f'success|{topic}|{lastcounter - startcounter}')
        logging.info(f'fail|{topic}|{except_count}')
    return lastcounter

def main():
    lastcounter = collection.estimated_document_count()
    lastcounter_new = crawlingGeneralNews(lastcounter)
    # if lastcounter < lastcounter_new:
    #     tokenstart(lastcounter, lastcounter_new)

if __name__ == '__main__':
    main()
