import logging
import datetime
import os
from dotenv import load_dotenv

load_dotenv()

# mongoDB 설정
mongo_host = os.environ.get('MongoHost')
mongo_port = int(os.environ.get('MongoPort'))
mongo_user = os.environ.get('MongoUser')
mongo_passwd = os.environ.get('MongoPasswd')
mongo_db_name = os.environ.get('MongoDbName')
mongo_admin_db = os.environ.get('MongoAdminDb')

class MongoHandler(logging.Handler):
    def __init__(self, level=logging.NOTSET,
                 collectionName='crawlingLog', isCollectionDrop=False,
                 instanceName='default'):
        from pymongo import MongoClient
        import platform

        logging.Handler.__init__(self, level)  # 부모 생성자 호출

        self.conn = MongoClient(host=mongo_host, port=mongo_port, username=mongo_user, password=mongo_passwd) #, authSource=mongo_admin_db)

        self.db = self.conn.get_database(mongo_db_name)  # 데이터베이스를 가져온다

        # 데이터베이스 컬렉션을 가져온다
        if collectionName in self.db.list_collection_names():  # 존재한다면
            if isCollectionDrop:
                self.db.drop_collection(collectionName)  # Drop하고
                self.collection = self.create_collection(collectionName)  # 다시 만든다
            else:
                # 가져온다
                self.collection = self.db.get_collection(collectionName)
        else:  # 없다면
            self.collection = self.create_collection(collectionName)  # 만든다

        self.hostName = platform.node()  # 호스트이름을 저장한다
        self.instanceName = instanceName  # 인스턴스이름을 저장한다

    def create_collection(self, collectionName):
        # 컬렉션이름으로 컬렉션을 만들고 리턴한다
        return self.db.create_collection(collectionName,
                                         capped=True,  # 고정크기 컬렉션
                                         size=10000000)  # 컬렉션 최대크기지정(단위: bytes)

    def emit(self, record):
        self.record = record
        document = \
            {
                'when': datetime.datetime.now(),  # 현재일시
                'localhostName': self.hostName,  # 로컬 호스트명
                'localInstanceName': self.instanceName,  # 로컬 인스턴스명
                'fileName': record.filename,  # 파일명
                'processName': record.processName,  # 프로세스명
                'functionName': record.funcName,  # 함수명
                'levelNumber': record.levelno,  # 로그레벨(ex. 10)
                'levelName': record.levelname,  # 로그레벨명(ex. DEBUG)
                'state' : record.state, # 상태 (start, finish, success, fail 등)
                'category' : record.category, # 카테고리 (뉴스 크롤링, db, etc)
                'link' : record.link, # 크롤링에 실패한 링크
                'message': record.msg,  # 오류 메시지
            }
        self.collection.insert_one(document, bypass_document_validation=False)