import os
import re
from dotenv import load_dotenv
from pymongo import MongoClient
from pymongo.errors import BulkWriteError
import datetime

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
collection = db['crawlingLog']

result_json = []

# 로그 파일 읽기
with open("crawling.log", "r", encoding='utf-8') as file:
    # 파일의 각 줄을 읽어들여 MongoDB에 저장

    for line in file:
        # 로그 데이터를 딕셔너리 형태로 저장
        words = line.strip().split('|')
        date_time = ''
        day_of_week = ''
        level = ''
        state = ''
        category = ''
        sub_cateogory=''
        running_time = ''
        cnt = ''
        # link = ''
        error_msg = ''
        error_msg2 = ''

        try:
            # date_time = words[0].split(' ')
            # date = datetime.datetime.strptime(date_time[0], '%Y-%m-%d')
            # time = datetime.datetime.strptime(date_time[1], '%H:%M:%S,%f')
            date_time = datetime.datetime.strptime(words[0], "%Y-%m-%d %H:%M:%S,%f")
            time = datetime.datetime.strptime(words[0].split(' ')[1], "%H:%M:%S,%f")
            level = words[1]
            state = words[2]
            category = words[3]

            if level == 'INFO':
                if state == 'start' or state == 'finish':
                    if category != 'tokenize' and category != 'crawling':
                        start_time = datetime.datetime.strptime(words[4], '%H:%M:%S')
                        running_time = (time - start_time).total_seconds()
                elif state == 'success':
                    cnt = int(words[4])
                elif state == 'fail':
                    cnt = int(words[4])
            elif level == 'ERROR':
                if state == 'CRAWLING':
                    sub_cateogory = words[4]
                    error_msg = words[5]
                elif state == 'DB':
                    sub_cateogory = words[4]
                    error_msg = words[5]
                    if error_msg == 'BulkWriteError':
                        error_msg2 = words[6]
                elif state == 'ETC':
                    error_msg = words[3]
                    category = ''

            result = {'date_time':date_time, 'level':level, 'state':state,
                      'category':category, 'sub_cateogory':sub_cateogory, 'running_time':running_time,
                      'cnt':cnt, 'error_msg':error_msg, 'error_msg2':error_msg2}
            # print(result)
            result_json.append(result)
        except Exception as e:
            print(line)
            print(e)
            print(words)
    # MongoDB에 데이터 저장
    # collection.insert_one(log_data)
    # print(result_json)

try:
    collection.insert_many(result_json)
except Exception as e:
    print(e)
