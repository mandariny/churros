from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

maria_user = 'root'
maria_passwd = 'root'
maria_host = 'localhost'
maria_port = '3333'
maria_db_name = 'churros'

SQLALCHEMY_DATABASE_URL = f"mysql+pymysql://{maria_user}:{maria_passwd}@{maria_host}:{maria_port}/{maria_db_name}"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
