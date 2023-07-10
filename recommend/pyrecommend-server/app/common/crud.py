from sqlalchemy.orm import Session
from app.models.models import User

def read_user(db: Session, id : int):
    db_user = db.query(User).filter(User.user_idx == id)

    if not db_user:
        return None

    return db_user.first()
