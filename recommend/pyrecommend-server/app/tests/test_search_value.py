from fastapi.testclient import TestClient
from app.api.main import app

client = TestClient(app)

def test_search_value():
    response = client.get("/recommend/search/윤석열")
    assert response.status_code == 200
    assert response.json()["result"] == 1004