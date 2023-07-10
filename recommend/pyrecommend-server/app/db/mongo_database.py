import os
from pymongo import MongoClient

# mongoDB 설정
mongo_host = os.getenv("MONGO_HOST")
mongo_port = int(os.getenv("MONGO_PORT"))
mongo_user = os.getenv("MONGO_USER")
mongo_passwd = os.getenv("MONGO_PASSWD")
mongo_db_name = os.getenv("MONGO_DB_NAME")
mongo_admin_db = os.getenv("MONGO_ADMIN_DB")
mongo_client = MongoClient(host=mongo_host, port=mongo_port, username=mongo_user, password=mongo_passwd, authSource=mongo_admin_db)

db = mongo_client[mongo_db_name]
collection = db['newsCol']