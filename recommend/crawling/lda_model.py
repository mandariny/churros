import os
import pandas as pd
from dotenv import load_dotenv
from pymongo import MongoClient
from gensim import corpora, models, similarities


load_dotenv()

# mongoDB 설정
mongo_host = os.environ.get('MongoHost')
mongo_port = int(os.environ.get('MongoPort'))
mongo_user = os.environ.get('MongoUser')
mongo_passwd = os.environ.get('MongoPasswd')
mongo_db_name = os.environ.get('MongoDbName')
mongo_admin_db = os.environ.get('MongoAdminDb')
mongo_client = MongoClient(
    host=mongo_host, port=mongo_port, username=mongo_user, password=mongo_passwd)

db = mongo_client[mongo_db_name]
newscollection = db['newsCol']
tokencollection = db['newsToken']


# 절대경로
project_folder = os.getcwd()


# token dataframe 조회
def token_dataframe():
    logging.info('token_dataframe recall start')
    data = list(tokencollection.find())
    df = pd.DataFrame(data)
    logging.info('token_dataframe recall end')
    return df

# doc2bow 실시 (dictionary, doc2bow저장)


def doc2bow(df):
    logging.info('start|doc2bow')
    dictionary = corpora.Dictionary(df.token)
    corpus = [dictionary.doc2bow(text) for text in df.token]
    logging.info('Saving corpus start')
    # corpora.MmCorpus.serialize('home/ubuntu/recommend/data/corpus.mm', corpus)
    corpora.MmCorpus.serialize('./corpus.mm', corpus)
    logging.info('Saving corpus end')
    return dictionary, corpus

def model_train(dictionary, corpus):
    logging.info('finish|doc2bow')
    NUM_TOPICS = 30
    # num topics, passes 추후 수정
    ldamodel = models.ldamulticore.LdaMulticore(corpus, num_topics=NUM_TOPICS, random_state=42, chunksize=10000,passes=1,eta='auto',id2word=dictionary, workers=4, minimum_probability=0.01)
    # ldamodel.save('home/ubuntu/recommend/data/ldamodels.lda')
    ldamodel.save('./ldamodels.lda')
    index = similarities.MatrixSimilarity(ldamodel[corpus],num_features=NUM_TOPICS)
    # index.save('home/ubuntu/recommend/data/ldaindex.sim')
    index.save('./ldaindex.sim')

import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s|%(levelname)s|%(message)s')

def main():
    try:
        logging.info('start|model_train')
        df = token_dataframe()
        dictionary, corpus = doc2bow(df)
        model_train(dictionary, corpus)
        logging.info('finish|model_train')
    except Exception as e:
        logging.error(f'MODELING{e}')


if __name__ == '__main__':
    main()
