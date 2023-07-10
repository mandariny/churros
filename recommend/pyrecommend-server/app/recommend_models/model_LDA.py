import os
import numpy as np
import pickle
from gensim import models, similarities, corpora
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
class LDAmodel():
    def __init__(self, name):
        logging.info(f'LDAmodel init...')
        self.TOPIC_NUM = 30
        self.name = name
        logging.info(f'LDAmodel init 완료!')
        
    def change_model_files(self):
        logging.info(f'파일 읽기 시작')
        project_folder = os.getcwd()
        logging.debug(f"project_folder : {project_folder}")
        self.lda_model = models.LdaModel.load(project_folder + '/data/ldamodels.lda')
        self.index = similarities.MatrixSimilarity.load(project_folder + '/data/ldaindex.sim')
        with open(project_folder + '/data/corpus.pkl', "rb") as fi:
            self.corpus = pickle.load(fi)
        logging.info(f'파일 읽기 종료')
        logging.info(f'기본 추천 용 리스트 생성')
        corpus_lda = [doc for doc in self.lda_model[self.corpus]]
        self.top_docs_by_topic = {topic : None for topic in range(self.TOPIC_NUM)}
        for topic in range(self.TOPIC_NUM):
            top_prob = 0
            top_doc = None
            for doc_idx, doc in enumerate(corpus_lda):
                for topic_prob in doc:
                    if topic_prob[0] == topic and topic_prob[1] > top_prob:
                        top_prob = topic_prob[1]
                        top_doc = doc_idx
            self.top_docs_by_topic[topic] = top_doc
        logging.info(f'추천용 리스트 생성 완료')
    
    def user_recommend(self,user_history:list,dislikes:set,read_idx:list, N:int): # corpus, dictionary 필요
        logging.info(f"Start user recommendation process.")
        corpus_lda_model = self.lda_model[self.corpus]

        # 유저 기록의 주제 관련 평균 계산
        user_topics = np.zeros(self.TOPIC_NUM)
        for i in user_history:
            single_corpus = corpus_lda_model[i]
            for word in single_corpus:
                user_topics[word[0]] += word[1]
        user_average = user_topics / len(user_history)
        logging.info(f"User average topic values: {user_average}")

        # 유저 평균 기록값과 전체 기사 와의 유사도 계산
        sims = self.index[user_average]
        # 유사도 값 리스트
        sim_scores = list(enumerate(sims))

        # 유사도 기준으로 정렬
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

        # 상위 N개의 기사 추천 N개 이상이 될 때까지 반복
        top_n_indices = []
        for i in range(0, len(sim_scores)):
            article_idx = sim_scores[i][0]
            if article_idx not in set(read_idx) and article_idx not in dislikes:
                top_n_indices.append(article_idx)
            if len(top_n_indices) >= N:
                break
        
        logging.debug(f"Top {N} recommended article indices: {top_n_indices[:N]}")
        logging.info(f"User recommendation process completed.")
        return top_n_indices
    
    def sample_article(self,topic_num):
        print("들어온 값", topic_num)
        print("결과 값",self.top_docs_by_topic.get(topic_num))
        print("타입은?", type(topic_num))
        return self.top_docs_by_topic.get(topic_num)