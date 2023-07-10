from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os

maria_user = os.getenv("MARIA_USER")
maria_passwd = os.getenv("MARIA_PASSWD")
maria_host = os.getenv("MARIA_HOST")
maria_port = os.getenv("MARIA_PORT")
maria_db_name = os.getenv("MARIA_DB_NAME")

SQLALCHEMY_DATABASE_URL = f"mysql+pymysql://{maria_user}:{maria_passwd}@{maria_host}:{maria_port}/{maria_db_name}"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
