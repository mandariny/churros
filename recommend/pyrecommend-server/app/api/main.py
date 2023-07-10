import random
from datetime import datetime, timedelta
from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy.orm import Session
from app.db.database import SessionLocal
# from app.db.mock_database import SessionLocal
# from app.db.mongo_database import collection
from app.common.crud import read_user
from app.recommend_models.model_LDA import LDAmodel
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

app = FastAPI()
green = LDAmodel('green')
blue = LDAmodel('blue')
green.change_model_files()

models = [green, blue]

flag = 0
remodel = models[flag]

_RECOMMEND_ARTICLE_CNT = 12
_SLICING_NUM = 5
_MIN_VLUE = 2

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get("/recommend/remodel")
def remodel_recommend_model():
    global flag
    global remodel

    logging.info(f"현재 사용중인 모델 {remodel.name}")

    flag = (flag + 1) % 2
    models[flag].change_model_files()
    remodel = models[flag]

    logging.info(f"새로 할당된 모델 {remodel.name}")

    return {"result" : "success"}

@app.get("/recommend/sample")
async def get_sample_articles():
    logging.info(f"현재 사용중인 모델 {remodel.name}")
    recommendList = []
    samplelist = random.sample(range(0,30), 6)
    print("뽑아낸 렌덤 값",samplelist)
    for i in range(6):
        recommendList.append(remodel.sample_article(samplelist[i]))
    return {"recommendList":recommendList}

# @app.get("/recommend/search/{value}")
# async def search_value(value: str):
#     start_time = datetime.now()
#     logging.info(f"시작 시간 : {start_time}")
#
#     data = list(collection.find({"$text": {"$search": value}}))
#
#     end_time = datetime.now()
#     logging.info(f"종료 시간 : {end_time}")
#     logging.info(f"소요 시간 : {end_time - start_time}")
#     logging.info(f"결과 : {len(data)}")
#
#     return {"result" : len(data)}

@app.get("/recommend/{user_id}")
async def get_recommend_articles(user_id: int, db: Session = Depends(get_db)):
    logging.info(f"현재 사용중인 모델 {remodel.name}")
    db_user = read_user(db, user_id)

    if not db_user:
        logging.info("user 정보가 존재하지 않습니다.")
        raise HTTPException(status_code=400, detail="user 정보가 존재하지 않습니다.")

    logging.debug(f"user_id조회 결과 : [idx : {db_user.user_idx}, email : {db_user.user_email}, name : {db_user.user_name}, 읽은 기사 개수 : {len(db_user.articles)}]")

    if not db_user.articles:
        logging.info("읽은 기사 정보가 존재하지 않습니다.")
        raise HTTPException(status_code=400, detail="읽은 기사 정보가 존재하지 않습니다.")

    # 싫어요를 누른 기사의 중복 제거
    dislikes = set(re.likes_idx for re in db_user.dislikes)

    # 2주 안에 읽은 데이터들을 최신 날짜 순으로 정렬
    today = datetime.now()
    two_weeks_ago = today - timedelta(days=14)

    read_articles = [ra for ra in db_user.articles
                    if ra.read_date >= two_weeks_ago]
    sorted_read_articles = sorted(read_articles, key=lambda x : x.read_date, reverse=True)
    read_idx = [sra.read_idx for sra in sorted_read_articles]
    logging.debug(f'최근 읽은 기사 인덱스 : {read_idx}')

    # 이후 읽은 순서에 따라 우선 탐색...
    recommendations = []
    N = (_RECOMMEND_ARTICLE_CNT//((len(read_idx)//_SLICING_NUM)+1))
    if N < _MIN_VLUE:
        N = _MIN_VLUE
    for i in range(0, len(read_idx), _SLICING_NUM):
        cur_articles = read_idx[0 : i + _SLICING_NUM]
        cur_recommendations:list = remodel.user_recommend(cur_articles, dislikes, read_idx+recommendations, N)
        recommendations += cur_recommendations
        if len(recommendations) >= _RECOMMEND_ARTICLE_CNT:
            break

    return {"recommendList": recommendations[:_RECOMMEND_ARTICLE_CNT]}
