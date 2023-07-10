from konlpy.tag import Okt
import re
import os
import pandas as pd
from dotenv import load_dotenv, find_dotenv
from pymongo import MongoClient
from pymongo.errors import BulkWriteError

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
newscollection = db['newsCol']
tokencollection = db['newsToken']
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s|%(levelname)s|%(message)s')

def tokenstart(startidx, endidx):
    logging.info('start|tokenize')
    data = newscollection.find().skip(startidx).limit(endidx-startidx)
    data_list = list(data)
    df = pd.DataFrame(data_list)
    df["token"] = df["full_text"].apply(token)
    df_saving = df[["_idx", "token"]]
    records = df_saving.to_dict("records")
    tokencollection.insert_many(records)
    logging.info('finish|tokenize')
    logging.info(f'success|tokenize|{len(df)}')

def token(txt):
    okt = Okt()
    # 아무튼 기사 전문을 변수에 저장
    text = clean(txt)
    text = clean_str(text)
    word_tokens = okt.pos(text, join="/", stem=True)
    result = delete_stop_words(word_tokens)
    # 이 결과값이 token된 값
    return result

# 아래는 전처리 함수 (추가 수정 예정)
# punctuation 삭제
punct = "/-'?!.,#$%\'()*+-/:;<=>@[\\]^_`{|}~" + '""“”’' + '∞θ÷α•à−β∅³π‘₹´°£€\×™√²—–&'
punct_mapping = {"‘": "'", "₹": "e", "´": "'", "°": "", "€": "e", "™": "tm", "√": " sqrt ", "×": "x", "²": "2", "—": "-", "–": "-", "’": "'", "_": "-", "`": "'", '“': '"', '”': '"', '“': '"', "£": "e", '∞': 'infinity', 'θ': 'theta', '÷': '/', 'α': 'alpha', '•': '.', 'à': 'a', '−': '-', 'β': 'beta', '∅': '', '³': '3', 'π': 'pi', } 
def clean(text):
    for p in punct_mapping:
        text = text.replace(p, punct_mapping[p])
    
    for p in punct:
        text = text.replace(p, f' {p} ')
    
    specials = {'\u200b': ' ', '…': ' ... ', '\ufeff': '', 'करना': '', 'है': ''}
    for s in specials:
        text = text.replace(s, specials[s])
    
    return text.strip()

# 불필요한 부분 삭제(이메일, url등)
def clean_str(text):
    pattern = r'([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+)' # E-mail제거
    text = re.sub(pattern=pattern, repl='', string=text)
    pattern = r'(http|ftp|https)://(?:[-\w.]|(?:%[\da-fA-F]{2}))+' # URL제거
    text = re.sub(pattern=pattern, repl='', string=text)
    pattern = r'([ㄱ-ㅎㅏ-ㅣ]+)'  # 한글 자음, 모음 제거
    text = re.sub(pattern=pattern, repl='', string=text)
    pattern = r'<[^>]*>'         # HTML 태그 제거
    text = re.sub(pattern=pattern, repl='', string=text)
    pattern = r'[^\w\s\n]'         # 특수기호제거
    text = re.sub(pattern=pattern, repl='', string=text)
    text = re.sub(r'[-=+,#/\?:^$.@*\"※~&%ㆍ!』\\‘|\(\)\[\]\<\>`\'…》]','', string=text)
    text = re.sub(r'\n', '.', string=text)
    return text


# 불용어 제거
word_file = open("/home/ubuntu/S08P22A503/recommend/crawling/stopwords.txt", "r", encoding="utf-8")
words = word_file.read()
stop_words = set(words.split('\n'))
lemmatization = {'Adjective', 'Adverb', 'Alpha', 'Exclamation', 'Foreign', 'Noun', 'Number',  'Unknown', 'Verb'} # 동사와 명사 형용사 및 기타 의미가 존재하는 형태소만을 남김
def delete_stop_words(text):
    result =  [word for word in text if (not (word.split('/')[0] in stop_words) and word.split('/')[1] in lemmatization)]
    return result
