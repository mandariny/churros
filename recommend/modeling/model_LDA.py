# 아무래도 벡터값 갱신의 경우, 전체를 갱신하고
# 하나의 유저에 대한 추천의 모델의 경우, 는 아직 작업 중
import os
import pickle
from dotenv import load_dotenv
from pymongo import MongoClient
import numpy as np
import pandas as pd

from gensim import corpora, models, similarities
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

# token 에 index 받아온 값 저장
tokens = [3,4,5]
df = pd.DataFrame([])
# 해당 데이터들은 전체 기사에 대해 적용시켜야 하는 부분이다 즉, 시작시 존재해야 하는 데이터 + 10분 크롤링 이후 적용되어야 하는 것
dictionary = corpora.Dictionary(df.token) # 전체 단어 모음집 (labeling)
corpus = [dictionary.doc2bow(text) for text in df.token] # 기사 encoding 진행

NUM_TOPICS = 30 # 40개의 토픽
project_folder = os.getcwd()
def model_training(): # dictionary, corpus 필요
    models.LdaModel(corpus, num_topics=NUM_TOPICS, random_state=42, update_every=1, passes=10, id2word=dictionary)
    lda_model = models.ldamodel.LdaModel(corpus, num_topics = NUM_TOPICS, id2word=dictionary, passes=15)
    lda_model.save(project_folder+'\\models\\ldamodel.lda')
    index = similarities.MatrixSimilarity(lda_model[corpus])
    similarity_train()
    return True

def similarity_train(): # corpus 필요
    lda_model = models.LdaModel.load(project_folder+'/models/ldamodel.lda')
    index = similarities.MatrixSimilarity(lda_model[corpus])
    index.save(project_folder+'/models/models/index.sim')
    return True

def user_recommend(user_history): # corpus, index 필요
    lda_model = models.LdaModel.load(project_folder+'/models/ldamodel.lda')
    index = similarities.MatrixSimilarity.load(project_folder+'/models/models/index.sim')
    corpus_lda_model = lda_model[corpus]
    # 유저 기록의 주제 관련 평균 계산
    user_topics = np.zeros(len(dictionary))
    for i in user_history:
        single_corpus = corpus_lda_model[i]
        for word in single_corpus:
            user_topics[word[0]] += word[1]
    user_average = user_topics / len(user_history)
    # 유저 평균 기록값과 전체 기사 와의 유사도 계산 ** 유사도 모델 따로 저장해두자
    sims = index[user_average]
    # 유사도 값 리스트
    sim_scores = list(enumerate(sims))

    # 유사도 기준으로 정렬
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

    # 상위 N개의 기사 추천
    N = 5
    top_n_indices = [i[0] for i in sim_scores[0:N+1]]
    recommendations = df.loc[top_n_indices, '_idx'].tolist()
    
    print(f"Top {N} recommended articles based on user history:")
    print(recommendations)
    return recommendations