 # 아무래도 벡터값 갱신의 경우, 전체를 갱신하고
# 하나의 유저에 대한 추천의 모델의 경우, 는 아직 작업 중
import os
import pickle
from dotenv import load_dotenv
from pymongo import MongoClient
import numpy as np

from gensim.models import Word2Vec
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd

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

# tokens에 token화 시킨 데이터 불러오기
tokens = ["smaple","data"]
# sg = 0 이면 cbow, 1이면 skip-gram, window 는 좌우로 살펴볼 단어의 개수(hyperparameters)
# word2vec 자체를 구현해 보는 것도 하나의 시도일수도, 속도 완전 느림
# word2vec model training
df = pd.DataFrame([])
def training(tokens):
    model = Word2Vec(sentences=tokens, vector_size = 100, window = 8, min_count = 3, workers = 4, sg = 0) 
    model.save('/tmp/my_model.word2vec') # 또는 pickle파일로

    document_embedded = get_document_vectors(df['token'], model)
    with open('document.pickle', 'wb') as f:
        pickle.dump(document_embedded, f, pickle.HIGHEST_PROTOCOL)
# 모델을 저장하는게 아닌, 아니지 document의 벡터값들을 저장해둬야 한다!!
# 모델 갱신시 document에 대한 벡터값을 새로 저장해두고
# 10분마다 새로운 doc들어오면 그에 대한 벡터값 계산 다시해서 저장해주고
# 그렇다면 doc이 들어올때 마다 모든 doc에 대한 벡터를 다시 계산할 필요가 없다. (model은 하루에 한번만 갱신시켜줘도 되기 때문에!) 그건 이따 짜고 일단은 함수로
def get_document_vectors(document_list, model):
    document_embedding_list = []
    # 각 문서에 대해서
    for line in document_list:
        doc2vec = None
        count = 0
        for word in line:
            if word in model.wv.key_to_index:
                count += 1
                # 해당 문서에 있는 모든 단어들의 벡터값을 더한다.
                if doc2vec is None:
                    doc2vec = model.wv[word]
                else:
                    doc2vec = doc2vec + model.wv[word]

        if doc2vec is not None:
            # 단어 벡터를 모두 더한 벡터의 값을 문서 길이로 나눠준다.
            doc2vec = doc2vec / count
            document_embedding_list.append(doc2vec)

    # 각 문서에 대한 문서 벡터 리스트를 리턴
    return document_embedding_list

# 유저 활동기록 평균값 계산
# 현재는 유저를 조회하는 쿼리 가 없음 추가 예정
def aggregate_vectors(read_history, document_embedded):
    history_vec = []
    for i in read_history:
        try:
            history_vec.append(document_embedded[i])
        except KeyError:
            continue
    return np.mean(history_vec, axis=0)

# recommendation
def recommendation(idx):
    # 기사 vector df 불러오기
    books = df[['_idx','title', 'full_text']]
    with open('data.pickle', 'rb') as f:
        document_embedded = pickle.load(f)
        # 해당 유저 vector계산
        user_vector = aggregate_vectors (idx,document_embedded)
        user = user_vector.reshape(1,-1)
        sim_scores = cosine_similarity(document_embedded, user).ravel()
        # 입력된 인덱스와 내용 vector 값이 유사한 6개 추출
        sim_scores = sim_scores.argsort()[::-1][0:6]
        book_indices = sim_scores.tolist()

        # 전체 데이터프레임에서 해당 인덱스의 행만 추출. 6개의 행을 가진다.
        recommend = books.iloc[book_indices].reset_index(drop=True)

    return recommend